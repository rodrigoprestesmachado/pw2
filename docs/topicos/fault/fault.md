---
layout: default
title: Fault Tolerance
parent: Micro Serviços Intermediário
nav_order: 11
---

# Fault Tolerance 🆘

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/fault/slides/index.html#/" title="Fault Tolerance" width="90%" height="500" style="border:none;"></iframe>
</center>

## Por que precisamos nos preocupar com falhas? 🤔

Imagine que você está em casa esperando uma encomenda. O entregador depende de
várias coisas para chegar até você: o caminhão precisa funcionar, o trânsito
precisa fluir, o endereço precisa estar correto, e o porteiro precisa atender.
Se qualquer um desses elementos falhar, a entrega não acontece.
{: .fs-3 }

Com microsserviços, a situação é parecida. Os serviços conversam entre si pela
rede e, **inevitavelmente**, algo vai dar errado em algum momento: a rede pode
ficar saturada, um servidor pode reiniciar para atualização, um hardware pode
falhar, ou a topologia da rede pode mudar inesperadamente. A pergunta não é
*se* uma falha vai ocorrer, mas *quando*.
{: .fs-3 }

É aqui que entra o conceito de **tolerância a falhas** (*fault tolerance*):
projetar nossos serviços para que sobrevivam, e até se recuperem, quando
algo der errado, sem derrubar todo o sistema.
{: .fs-3 }

Pensando nisso, o [Microprofile](https://github.com/eclipse/microprofile-fault-tolerance/)
criou um conjunto de anotações que tornam essa tarefa muito mais simples. Você
não precisa escrever a lógica de recuperação manualmente: basta anotar seus
métodos e o framework cuida do resto. A implementação concreta dessas anotações
fica a cargo do [SmallRye Fault Tolerance](https://github.com/smallrye/smallrye-fault-tolerance/).
{: .fs-3 }

## As cinco ferramentas do nosso "kit de sobrevivência" 🧰

Vamos conhecer as anotações que estudaremos, com uma analogia para cada uma:
{: .fs-3 }

* **`@Retry`** (*"Tenta de novo!"*): como quando o WhatsApp não envia uma
mensagem e você toca em "tentar novamente". A maioria das falhas de rede é
temporária, e simplesmente tentar de novo resolve o problema.
* **`@Fallback`** (*"Plano B"*): se mesmo tentando não der certo, executa
uma alternativa. Como quando o cartão de crédito é recusado e você paga em
dinheiro.
* **`@Timeout`** (*"Não vou esperar para sempre"*): define um tempo máximo
de espera. Se passar disso, desistimos e seguimos em frente.
* **`@Bulkhead`** (*"Compartimentos estanques"*): limita quantas operações
podem rodar simultaneamente. O nome vem dos navios, que têm divisórias para
que, se uma parte alaga, o navio inteiro não afunda.
* **`@CircuitBreaker`** (*"Disjuntor elétrico"*): se um serviço está
claramente quebrado, paramos de tentar por um tempo, evitando sobrecarregar
ainda mais o sistema com chamadas que vão falhar de qualquer forma.
{: .fs-3 }

## Preparando o ambiente 🛠️

Antes de explorar cada anotação, vamos criar um projeto com suporte para
tolerância a falhas:
{: .fs-3 }

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:3.40.1.2:create \
    -DprojectGroupId=dev.pw2 \
    -DprojectArtifactId=fault-tolerance \
    -Dextensions='rest,smallrye-fault-tolerance' \
    -DclassName="dev.pw2.FaultService" \
    -Dpath="/fault"

code fault-tolerance
```

Note que a extensão `quarkus-smallrye-fault-tolerance` é a peça-chave: é ela
que disponibiliza todas as anotações que veremos a seguir. A extensão `rest`
traz o suporte a endpoints Jakarta REST no Quarkus atual.
{: .fs-3 }

## 1. Retry: "Se não deu certo, tenta de novo" 🔄

A anotação `@Retry` é a mais simples e, surpreendentemente, uma das mais
efetivas. A lógica é direta: se o método lançar uma exceção, o framework o
executa novamente até atingir o número máximo de novas tentativas configurado.
{: .fs-3 }

**Quando usar?** Falhas transitórias de rede, picos momentâneos de carga em
outro serviço, ou qualquer situação em que "esperar um pouco e tentar de novo"
tenda a funcionar.
{: .fs-3 }

Observe o exemplo abaixo:
{: .fs-3 }

```java
@GET
@Path("/{name}")
@Produces(MediaType.TEXT_PLAIN)
@Retry(maxRetries = 3, delay = 2000)
public String getName(@PathParam("name") String name) {

    if (name.equalsIgnoreCase("error")) {
        Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("The requested was an error")
                .build();
        throw new WebApplicationException(response);
    }

    return name;
}
```

Vamos decifrar o que acontece passo a passo:
{: .fs-3 }

1. Se o método `getName` recebe a String `error`, ele lança uma `WebApplicationException`.
2. A anotação `@Retry` intercepta essa exceção.
3. Em vez de propagar o erro imediatamente, ela espera 2 segundos (`delay = 2000`).
4. Executa o método de novo. Com `maxRetries = 3`, o framework faz **até 3 novas tentativas** além da execução inicial (no total, **até 4 execuções**).
5. Se todas falharem, **aí sim** a exceção é propagada para quem chamou o serviço.
{: .fs-3 }

⚠️ **Atenção:** `@Retry` só faz sentido para erros que podem se resolver com
o tempo. Não adianta ficar tentando se a falha é por causa de um bug no código
ou um dado inválido: você só vai gastar processamento e atrasar a resposta de
erro.
{: .fs-3 }

## 2. Fallback: "Plano B quando tudo dá errado" 🅱️

E se mesmo depois de tentar várias vezes o método continuar falhando? Em vez
de simplesmente devolver um erro feio ao usuário, podemos ter uma **estratégia
alternativa**: um método que retorna uma resposta padrão, busca dados de um
cache, ou faz qualquer outra coisa que mantenha a aplicação útil.
{: .fs-3 }

Para isso, usamos a anotação `@Fallback` indicando qual método deve ser
chamado quando a falha não puder ser contornada:
{: .fs-3 }

```java
@GET
@Path("/{name}")
@Produces(MediaType.TEXT_PLAIN)
@Retry(maxRetries = 3, delay = 2000)
@Fallback(fallbackMethod = "recover")
public String getName(@PathParam("name") String name) {
    // 🚨 o código do método do exemplo anterior foi suprimido
}

// Método que irá ser executado caso o método getName não se recupere da falha
public String recover(String name) {
    return FALL_BACK_MESSAGE;
}
```

Observe a sequência de eventos:
{: .fs-3 }

1. O método `getName` falha.
2. `@Retry` faz até 3 novas tentativas e falha novamente.
3. Agora `@Fallback` entra em ação e chama o método `recover`.
4. O usuário recebe `FALL_BACK_MESSAGE` em vez de uma exceção.
{: .fs-3 }

🚨 **Regra de ouro do fallback:** o método de recuperação deve ficar na
**mesma classe**, receber os **mesmos parâmetros** (mesmos tipos e mesma ordem)
e ter um tipo de retorno **atribuível** ao tipo de retorno do método original
(por exemplo, o mesmo tipo ou um subtipo). No exemplo, tanto `getName`
quanto `recover` retornam `String` e recebem uma `String` como parâmetro. Se
você quebrar essa regra, o Quarkus reclama na inicialização.
{: .fs-3 }

## 3. Timeout: "Tempo é dinheiro" ⏱️

Imagine que seu serviço chama outro serviço pela rede, e esse outro serviço
simplesmente... trava. Sem timeout, seu serviço fica esperando indefinidamente,
consumindo memória, threads e, eventualmente, derrubando tudo.
{: .fs-3 }

A anotação `@Timeout` resolve isso definindo um tempo máximo de espera.
Passou disso, uma exceção é lançada e o método é interrompido:
{: .fs-3 }

```java
@GET
@Path("/{name}")
@Produces(MediaType.TEXT_PLAIN)
@Retry(maxRetries = 3, delay = 2000)
@Fallback(fallbackMethod = "recover")
@Timeout(7000)
public String getName(@PathParam("name") String name) {
    // 🚨 o código do método do exemplo anterior foi suprimido
}
```

No exemplo, o método tem 7 segundos (7000 ms) para terminar sua execução.
Note como as anotações **compõem-se naturalmente**:
{: .fs-3 }

* `@Timeout` garante que cada tentativa não passe de 7 segundos.
* `@Retry` cuida das tentativas adicionais se houver timeout ou outra falha.
* `@Fallback` é acionado caso tudo dê errado.
{: .fs-3 }

Essa combinação cria uma "rede de proteção" em camadas que protege seu
serviço de uma variedade enorme de problemas.
{: .fs-3 }

## 4. Bulkhead: "Divisórias para não afundar o navio" 🚢

O nome dessa anotação vem da engenharia naval: navios são divididos em
*compartimentos estanques* (bulkheads), de forma que, se um compartimento
alaga, os outros ficam isolados e o navio continua flutuando.
{: .fs-3 }

No nosso caso, o "afogamento" seria seu serviço receber tantas requisições
simultâneas que esgota memória, threads ou conexões de banco de dados,
derrubando toda a aplicação. Com `@Bulkhead`, **limitamos quantas requisições
podem rodar ao mesmo tempo**:
{: .fs-3 }

```java
@GET
@Path("/bulkhead/{name}")
@Produces(MediaType.TEXT_PLAIN)
@Bulkhead(2)
public String bulkhead(@PathParam("name") String name) {
    LOGGER.info(name);
    return name;
}
```

Nesse exemplo, no máximo **2 requisições** podem ser processadas
simultaneamente. Se chegar uma terceira enquanto as duas primeiras ainda
estão executando, ela será **rejeitada** (recebe uma exceção).
{: .fs-3 }

Para visualizar melhor como o `@Bulkhead` funciona, dê uma olhada no
seguinte link: [https://pw2.rpmhub.dev/topicos/fault/bulkhead/](https://pw2.rpmhub.dev/topicos/fault/bulkhead/)
{: .fs-3 }

### Dois modos de operação do Bulkhead

O comportamento de `@Bulkhead` muda dependendo de estar ou não acompanhado de
`@Asynchronous`:
{: .fs-3 }

**Modo Semáforo** (sem `@Asynchronous`): apenas controla quantas requisições
rodam ao mesmo tempo. Excedentes são rejeitadas imediatamente. Veja a
especificação: [`semáforo`](https://download.eclipse.org/microprofile/microprofile-fault-tolerance-4.0/microprofile-fault-tolerance-spec-4.0.html#_semaphore_style_bulkhead).
{: .fs-3 }

**Modo Thread Pool** (com `@Asynchronous`): além de controlar a concorrência,
mantém uma **fila de espera** para requisições excedentes. Veja a
especificação: [`thread pool`](https://download.eclipse.org/microprofile/microprofile-fault-tolerance-4.0/microprofile-fault-tolerance-spec-4.0.html#_thread_pool_style_bulkhead).
Exemplo:
{: .fs-3 }

```java
// máximo de 2 requisições concorrentes serão permitidas
// máximo de 5 requisições serão permitidas na fila de espera
@Asynchronous
@Bulkhead(value = 2, waitingTaskQueue = 5)
```

Pense assim: o modo semáforo é uma porta com um segurança que diz "está
cheio, volte depois". O modo thread pool é um restaurante com fila de espera:
você pode aguardar até abrir uma mesa, mas só até certo limite.
{: .fs-3 }

### Testando com k6

Para testar a anotação `@Bulkhead`, precisamos simular **múltiplos clientes
simultâneos**. A ferramenta [k6](https://k6.io/docs/) é perfeita para isso:
ela permite disparar requisições HTTP em paralelo, como se vários usuários
estivessem acessando seu serviço ao mesmo tempo.
{: .fs-3 }

Observe um script de exemplo:
{: .fs-3 }

```js
import exec from 'k6/execution';
import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 10,
    duration: '10s',
    thresholds: {
        // Como teste, os erros de HTTP devem ser menor do que 5%
	    http_req_failed: ['rate<0.05'],
	},
};

export default function () {
    http.get('http://localhost:8080/fault/bulkhead/' + exec.vu.idInTest);
    sleep(1);
}
```

O que esse script faz, em palavras simples:
{: .fs-3 }

* `vus: 10` → cria **10 usuários virtuais** (*virtual users*) rodando em paralelo.
* `duration: '10s'` → o teste dura **10 segundos**.
* `sleep(1)` → cada usuário espera 1 segundo entre as requisições.
* `http_req_failed: ['rate<0.05']` → o teste passa se menos de 5% das requisições falharem.
* `exec.vu.idInTest` → identifica qual usuário virtual está fazendo a requisição (útil para os logs).
{: .fs-3 }

Para rodar o teste, salve o arquivo (por exemplo, `k6.js`) e execute:
{: .fs-3 }

    k6 run k6.js

Depois, **brinque com os números**: aumente `vus`, mude o valor do `@Bulkhead`,
e observe como a taxa de erros varia. Essa experimentação prática é a melhor
forma de internalizar o comportamento dessas anotações.
{: .fs-3 }

## 5. Circuit Breaker: "Quando insistir só piora as coisas" ⚡

Imagine que um serviço que você depende está **completamente fora do ar**.
Continuar fazendo chamadas para ele é desperdício duplo: você gasta seus
próprios recursos (threads, memória, conexões) e ainda atrasa a resposta para
seus usuários, que esperam o timeout para receberem um erro.
{: .fs-3 }

O `@CircuitBreaker` resolve isso inspirado em um **disjuntor elétrico**:
quando há muita corrente (muitas falhas), o disjuntor "abre" e corta o
fluxo. Depois de um tempo, ele tenta religar para ver se o problema passou.
{: .fs-3 }

Uma visão geral do funcionamento do disjuntor pode ser vista na figura no
link a seguir: [https://pw2.rpmhub.dev/topicos/fault/circuit/](https://pw2.rpmhub.dev/topicos/fault/circuit/).
{: .fs-3 }

### Os três estados do disjuntor

Antes de ver o código, é fundamental entender que o `@CircuitBreaker` opera
como uma **máquina de estados** com três situações distintas:
{: .fs-3 }

1. **🟢 Fechado (Closed):** estado normal. As chamadas passam livremente, mas
o disjuntor está **monitorando** quantas falham.
2. **🔴 Aberto (Open):** a taxa de falhas na janela ultrapassou o limite. As
chamadas **não são executadas**: uma `CircuitBreakerOpenException` é lançada
imediatamente, poupando recursos.
3. **🟡 Meio-aberto (Half-Open):** depois de um tempo no estado aberto, o
disjuntor permite algumas chamadas de teste. Se elas funcionam, volta para
fechado. Se falham, volta para aberto.
{: .fs-3 }

### Vendo o circuit breaker em ação

Observe o [exemplo](https://pt.quarkus.io/guides/smallrye-fault-tolerance#adding-resiliency-circuit-breaker)
adaptado da documentação oficial. Primeiro, o serviço que **simula falhas
intermitentes**:
{: .fs-3 }

```java
@ApplicationScoped
public class CoffeeRepositoryService {

    private AtomicLong counter = new AtomicLong(0);

    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    public Integer getAvailability(Coffee coffee) {
        maybeFail();
        return new Random().nextInt(30);
    }

    private void maybeFail() {
        final Long invocationNumber = counter.getAndIncrement();
        // Alterna 2 sucessos e 2 falhas
        if (invocationNumber % 4 > 1) {
            throw new RuntimeException("Service failed.");
        }
    }

    public Coffee getCoffeeById(int id) {
        Coffee coffee = new Coffee();
        coffee.setId(id);
        return coffee;
    }
}
```

Repare no método `maybeFail`: ele alterna entre **2 sucessos e 2 falhas**,
simulando um serviço instável. É um cenário típico do mundo real, em que um
serviço fica intermitente antes de cair de vez. A classe é um bean CDI
(`@ApplicationScoped`), necessário para que o interceptor de tolerância a
falhas atue sobre o método.
{: .fs-3 }

Agora, o recurso REST que consome esse serviço:
{: .fs-3 }

```java
@Path("/circuit")
public class CoffeeResource {

    private Long counter = 0L;

    @Inject
    CoffeeRepositoryService coffeeRepository;

    Logger LOGGER = Logger.getLogger(CoffeeResource.class.getName());

    @GET
    @Path("/{id}/availability")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response availability(@PathParam("id") int id) {

        final Long invocationNumber = counter++;

        Coffee coffee = coffeeRepository.getCoffeeById(id);
        if (coffee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            Integer availability = coffeeRepository.getAvailability(coffee);
            LOGGER.log(Level.INFO, () -> "Sucesso: " + invocationNumber);
            return Response.ok(availability).build();
        } catch (RuntimeException e) {
            String message = String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
            LOGGER.log(Level.SEVERE, () -> "Falha:" + invocationNumber + " " + message);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(message)
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .build();
        }
    }
}
```

### Como o disjuntor toma decisões: a janela deslizante

O conceito-chave para entender o `@CircuitBreaker` é a **janela deslizante**
(*rolling window*). Pense nela como uma "memória curta" das últimas
invocações: o disjuntor anota quais foram bem-sucedidas e quais falharam.
{: .fs-3 }

**Estado fechado (operação normal):** o disjuntor mantém essa janela e, para
cada nova invocação, atualiza o registro. Para tomar uma decisão de mudar de
estado, a janela precisa estar **cheia**. Por padrão, ela tem tamanho 20
(`requestVolumeThreshold = 20`) e a taxa de falha aceitável é 0,5
(`failureRatio = 0.5`, ou 50%).
{: .fs-3 }

⚠️ Isso significa que um disjuntor fechado **sempre permite pelo menos N
invocações** (onde N é o tamanho da janela) antes de qualquer decisão.
{: .fs-3 }

**Exemplo concreto:** se a janela tem tamanho 4 e a taxa de falha é 0,5,
basta que 2 das últimas 4 invocações falhem para o disjuntor abrir. O critério
é a **taxa de falhas dentro da janela**, não uma sequência rígida de falhas
seguidas.
{: .fs-3 }

**Estado aberto:** uma vez aberto, o disjuntor não deixa nenhuma chamada
passar. Em vez de executar o método, ele lança `CircuitBreakerOpenException`
imediatamente. É a "falha rápida" (*fail fast*): em vez de gastar tempo e
recursos, você sabe na hora que algo está errado.
{: .fs-3 }

**Estado meio-aberto:** depois de algum tempo (`delay = 5000` ms por padrão,
ou seja, 5 segundos), o disjuntor passa para meio-aberto. Nesse modo, ele
deixa passar chamadas de sonda até atingir `successThreshold` sucessos
(padrão: **1**):
{: .fs-3 }

* Se as sondas tiverem sucesso → volta para **fechado** (problema resolvido).
* Se alguma falhar → volta para **aberto** (ainda está com problema).
{: .fs-3 }

### Resumo do ciclo de vida

Visualizando o fluxo completo:
{: .fs-3 }

```
   🟢 FECHADO  ──(taxa de falhas alta)──▶  🔴 ABERTO
       ▲                                       │
       │                                (passa o delay)
   (sondas OK)                                 │
       │                                       ▼
       └────────────────────────────────  🟡 MEIO-ABERTO
                  (alguma sonda falha)
                          │
                          └──▶ volta para 🔴 ABERTO
```

## Combinando as anotações: o cenário ideal 🎯

Você não precisa usar apenas uma anotação por método. Na verdade, a beleza
dessas ferramentas está em **combiná-las** para criar uma estratégia robusta
de tolerância a falhas:
{: .fs-3 }

```java
@Timeout(3000)                        // 1️⃣ não espera mais que 3s
@Retry(maxRetries = 3, delay = 500)   // 2️⃣ até 3 novas tentativas
@CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5)  // 3️⃣ disjuntor
@Fallback(fallbackMethod = "respostaCache")  // 4️⃣ último recurso
public Resposta consultarServico() { ... }
```

A sequência de defesa fica assim:
{: .fs-3 }

1. Cada chamada tem **3 segundos** para responder, ou é interrompida.
2. Se falhar, faz até **3 novas tentativas** com intervalo de 500 ms.
3. Se a **taxa de falhas na janela** ultrapassar o limite, o **disjuntor abre** e poupa recursos.
4. Em qualquer cenário de falha final, o método de **fallback** entrega uma resposta útil.
{: .fs-3 }

## Código 💡

Os exemplos sobre Fault Tolerance estão disponíveis no Github:
{: .fs-3 }

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
code pw2/exemplos/fault-tolerance
code pw2/exemplos/banco-fault-tolerance
```

## Exercícios de Fixação 🏦

Estes exercícios são **independentes** do restante da disciplina. Você vai
criar um projeto Quarkus novo, colar um código de partida que simula um core
bancário instável e, em cada exercício, **apenas adicionar** as anotações de
tolerância a falhas.
{: .fs-3 }

### Criando o projeto

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:3.40.1.2:create \
    -DprojectGroupId=dev.pw2 \
    -DprojectArtifactId=banco-fault-tolerance \
    -Dextensions='rest,smallrye-fault-tolerance' \
    -DclassName="dev.pw2.banco.BancoResource" \
    -Dpath="/banco"

cd banco-fault-tolerance
```

### Código de partida (cole e use em todos os exercícios)

Crie a classe `BancoInstavelService` (não altere a lógica dela nos exercícios;
ela existe para falhar de propósito):
{: .fs-3 }

```java
package dev.pw2.banco;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BancoInstavelService {

    private static final Logger LOGGER = Logger.getLogger(BancoInstavelService.class);

    private final AtomicInteger tentativasSaldo = new AtomicInteger(0);
    private final AtomicInteger chamadasCircuito = new AtomicInteger(0);
    private final AtomicInteger tentativasTransferencia = new AtomicInteger(0);

    public BigDecimal consultarSaldo(String conta) {
        int n = tentativasSaldo.incrementAndGet();
        LOGGER.info("consultarSaldo conta=" + conta + " tentativa=" + n);
        if (n % 4 != 0) {
            throw new RuntimeException("Core bancario indisponivel");
        }
        return new BigDecimal("1500.00");
    }

    public BigDecimal consultarSaldoSempreFalha(String conta) {
        LOGGER.info("consultarSaldoSempreFalha conta=" + conta);
        throw new RuntimeException("Core bancario fora do ar");
    }

    public BigDecimal consultarSaldoLento(String conta) throws InterruptedException {
        LOGGER.info("consultarSaldoLento inicio conta=" + conta);
        Thread.sleep(5000);
        LOGGER.info("consultarSaldoLento fim conta=" + conta);
        return new BigDecimal("1500.00");
    }

    public String processarTransferenciaLenta(String id) throws InterruptedException {
        LOGGER.info("processarTransferenciaLenta inicio id=" + id);
        Thread.sleep(3000);
        LOGGER.info("processarTransferenciaLenta fim id=" + id);
        return "transferencia-" + id + "-ok";
    }

    public BigDecimal consultarSaldoIntermitente(String conta) {
        long n = chamadasCircuito.getAndIncrement();
        LOGGER.info("consultarSaldoIntermitente conta=" + conta + " chamada=" + n);
        if (n % 4 > 1) {
            throw new RuntimeException("Falha intermitente no core");
        }
        return new BigDecimal("2200.00");
    }

    public String transferir(String origem, String destino, String valor) {
        int n = tentativasTransferencia.incrementAndGet();
        LOGGER.info("transferir " + origem + "->" + destino + " valor=" + valor
                + " tentativa=" + n);
        throw new RuntimeException("Transferencia rejeitada pelo core");
    }
}
```

Substitua o conteúdo de `BancoResource` pelo esqueleto abaixo (**ainda sem**
anotações de tolerância a falhas). Em cada exercício você anota só o método
pedido:
{: .fs-3 }

```java
package dev.pw2.banco;

import java.math.BigDecimal;

import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/banco")
public class BancoResource {

    private static final Logger LOGGER = Logger.getLogger(BancoResource.class);

    @Inject
    BancoInstavelService core;

    @GET
    @Path("/saldo/{conta}")
    @Produces(MediaType.TEXT_PLAIN)
    public String saldo(@PathParam("conta") String conta) {
        BigDecimal saldo = core.consultarSaldo(conta);
        return "Saldo da conta " + conta + ": " + saldo;
    }

    @GET
    @Path("/saldo-cache/{conta}")
    @Produces(MediaType.TEXT_PLAIN)
    public String saldoComFallback(@PathParam("conta") String conta) {
        BigDecimal saldo = core.consultarSaldoSempreFalha(conta);
        return "Saldo da conta " + conta + ": " + saldo;
    }

    public String saldoEmCache(String conta) {
        LOGGER.warn("Fallback acionado para conta=" + conta);
        return "Saldo em cache da conta " + conta + ": 1000.00";
    }

    @GET
    @Path("/saldo-lento/{conta}")
    @Produces(MediaType.TEXT_PLAIN)
    public String saldoLento(@PathParam("conta") String conta) throws InterruptedException {
        BigDecimal saldo = core.consultarSaldoLento(conta);
        return "Saldo da conta " + conta + ": " + saldo;
    }

    @GET
    @Path("/transferencia-lenta/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String transferenciaLenta(@PathParam("id") String id) throws InterruptedException {
        return core.processarTransferenciaLenta(id);
    }

    @GET
    @Path("/saldo-circuito/{conta}")
    @Produces(MediaType.TEXT_PLAIN)
    public String saldoCircuito(@PathParam("conta") String conta) {
        BigDecimal saldo = core.consultarSaldoIntermitente(conta);
        return "Saldo da conta " + conta + ": " + saldo;
    }

    @GET
    @Path("/transferir")
    @Produces(MediaType.TEXT_PLAIN)
    public String transferir(
            @QueryParam("origem") String origem,
            @QueryParam("destino") String destino,
            @QueryParam("valor") String valor) {
        return core.transferir(origem, destino, valor);
    }

    public String transferenciaAgendada(String origem, String destino, String valor) {
        LOGGER.warn("Fallback de transferencia acionado");
        return "Transferencia agendada para processamento posterior: "
                + origem + " -> " + destino + " valor=" + valor;
    }
}
```

Suba a aplicação:
{: .fs-3 }

```sh
./mvnw quarkus:dev
```

### Exercício 1: `@Retry` 🔄

**Objetivo:** fazer a consulta de saldo sobreviver a falhas transitórias do core.
{: .fs-3 }

**Código de partida (trecho):** o método `saldo` em `BancoResource`, sem anotação.
{: .fs-3 }

**Implemente:** anote `saldo` com `@Retry(maxRetries = 3, delay = 500)`.
{: .fs-3 }

**Teste:**
{: .fs-3 }

```sh
curl -i http://localhost:8080/banco/saldo/123
```

**Resultado esperado:** a resposta HTTP 200 contém `Saldo da conta 123: 1500.00`.
Nos logs aparecem quatro linhas `consultarSaldo ... tentativa=1` até
`tentativa=4` (1 execução inicial + 3 novas tentativas). Sem o `@Retry`, a
primeira chamada falharia imediatamente.
{: .fs-3 }

### Exercício 2: `@Fallback` 🅱️

**Objetivo:** devolver um saldo em cache quando o core não recupera.
{: .fs-3 }

**Código de partida (trecho):** `saldoComFallback` e o método auxiliar
`saldoEmCache` (já presente no esqueleto).
{: .fs-3 }

**Implemente:** anote `saldoComFallback` com
`@Retry(maxRetries = 2, delay = 200)` e
`@Fallback(fallbackMethod = "saldoEmCache")`.
{: .fs-3 }

**Teste:**
{: .fs-3 }

```sh
curl -i http://localhost:8080/banco/saldo-cache/123
```

**Resultado esperado:** HTTP 200 com
`Saldo em cache da conta 123: 1000.00`. Nos logs: três chamadas a
`consultarSaldoSempreFalha` e em seguida `Fallback acionado para conta=123`.
{: .fs-3 }

### Exercício 3: `@Timeout` ⏱️

**Objetivo:** interromper uma consulta que demora demais.
{: .fs-3 }

**Código de partida (trecho):** o método `saldoLento`.
{: .fs-3 }

**Implemente:** anote `saldoLento` com `@Timeout(2000)`.
{: .fs-3 }

**Teste:**
{: .fs-3 }

```sh
curl -i http://localhost:8080/banco/saldo-lento/123
```

**Resultado esperado:** a resposta falha em cerca de 2 segundos (não espera os
5 segundos do `Thread.sleep`). Nos logs do Quarkus aparece
`TimeoutException` mencionando `BancoResource#saldoLento timed out`.
{: .fs-3 }

### Exercício 4: `@Bulkhead` 🚢

**Objetivo:** limitar transferências lentas concorrentes para não esgotar o
serviço.
{: .fs-3 }

**Código de partida (trecho):** o método `transferenciaLenta`.
{: .fs-3 }

**Implemente:** anote `transferenciaLenta` com `@Bulkhead(2)`.
{: .fs-3 }

**Teste** (com k6, ou 8 `curl` em paralelo):
{: .fs-3 }

```js
import exec from 'k6/execution';
import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 8,
    duration: '8s',
};

export default function () {
    http.get('http://localhost:8080/banco/transferencia-lenta/' + exec.vu.idInTest);
    sleep(0.5);
}
```

```sh
k6 run k6-bulkhead.js
```

**Resultado esperado:** só até 2 requisições concluem com sucesso enquanto as
slots estão ocupadas. As demais falham com `BulkheadException` (HTTP 500). A
taxa de erro no k6 sobe claramente acima de zero.
{: .fs-3 }

### Exercício 5: `@CircuitBreaker` ⚡

**Objetivo:** isolar o core quando a taxa de falhas na janela fica alta demais.
{: .fs-3 }

**Código de partida (trecho):** o método `saldoCircuito`.
{: .fs-3 }

**Implemente:** anote `saldoCircuito` com
`@CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)`.
{: .fs-3 }

**Teste:**
{: .fs-3 }

```sh
for i in 1 2 3 4 5 6; do
  echo "req $i"
  curl -s http://localhost:8080/banco/saldo-circuito/999
  echo
done
```

**Resultado esperado:** as duas primeiras respostas trazem saldo `2200.00`; as
duas seguintes falham com `RuntimeException` (falha intermitente). A partir daí
aparece `CircuitBreakerOpenException` (o disjuntor abriu porque 50% das 4
chamadas da janela falharam). Sem novas chamadas bem-sucedidas, o isolamento
permanece pelos próximos 5 segundos (`delay`).
{: .fs-3 }

### Exercício 6: combinando anotações 🎯

**Objetivo:** proteger a transferência com timeout, retry, circuit breaker e
fallback.
{: .fs-3 }

**Código de partida (trecho):** o método `transferir` e o auxiliar
`transferenciaAgendada`.
{: .fs-3 }

**Implemente:** anote `transferir` com:
{: .fs-3 }

```java
@Timeout(3000)
@Retry(maxRetries = 3, delay = 300)
@CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
@Fallback(fallbackMethod = "transferenciaAgendada")
```

**Teste:**
{: .fs-3 }

```sh
curl -i "http://localhost:8080/banco/transferir?origem=111&destino=222&valor=50"
```

**Resultado esperado:** HTTP 200 com
`Transferencia agendada para processamento posterior: 111 -> 222 valor=50`.
Nos logs: quatro tentativas de `transferir` e depois
`Fallback de transferencia acionado`.
{: .fs-3 }

💡 Se os seis exercícios acima funcionaram, você já pratica as cinco anotações
isoladamente e em combinação. O desafio abaixo é opcional e usa a arquitetura
de livros da disciplina.
{: .fs-3 }

## Desafio opcional: sistema de livros 🏋️

Hora de aplicar tolerância a falhas na aplicação de
[gerenciamento de livros](https://github.com/rpmhubdev/pw2-books):
{: .fs-3 }

1. **Serviço `users`:** adicione `@Retry` no endpoint `/users/getJwt`.
*Pergunte-se: quais valores de `maxRetries` e `delay` fazem sentido para uma operação de autenticação?*
2. **Serviço `management`:** adicione `@CircuitBreaker` e `@Timeout` no endpoint `/bookManagement/listBooks`.
*Pergunte-se: quanto tempo é razoável esperar para listar livros? E qual taxa de falhas na janela deve abrir o circuito?*
{: .fs-3 }

Para realizar o desafio, você pode abrir diretamente no Codespaces:
{: .fs-3 }

[![Open in Codespaces](https://github.com/codespaces/badge.svg)](https://github.com/codespaces/new?hide_repo_select=true&repo=rpmhubdev/pw2-books)

Alternativamente, você pode fazer um `fork` do projeto para a sua conta e,
posteriormente, clonar para a sua máquina.
{: .fs-3 }

💡 **Extra:** depois de adicionar as anotações, use o `k6` para simular
carga sobre os endpoints e **observe os logs** quando o circuito abrir. Nada
ensina mais sobre tolerância a falhas do que vê-la funcionando ao vivo.
{: .fs-3 }

## Teste seus conhecimentos 🧠

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/fault/questions.html"
        title="Fault Tolerance"
        width="90%" height="500"
        style="border:none;">
    </iframe>
</center>

## Referências 📚

* SmallRye Fault Tolerance. Disponível em: [https://github.com/smallrye/smallrye-fault-tolerance/](https://github.com/smallrye/smallrye-fault-tolerance/).
* Guia Quarkus: [https://quarkus.io/guides/smallrye-fault-tolerance](https://quarkus.io/guides/smallrye-fault-tolerance)
{: .fs-3 }

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
