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
projetar nossos serviços para que sobrevivam — e até se recuperem — quando
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

* **`@Retry`** – *"Tenta de novo!"* — Como quando o WhatsApp não envia uma
mensagem e você toca em "tentar novamente". A maioria das falhas de rede é
temporária, e simplesmente tentar de novo resolve o problema.
* **`@Fallback`** – *"Plano B"* — Se mesmo tentando não der certo, executa
uma alternativa. Como quando o cartão de crédito é recusado e você paga em
dinheiro.
* **`@Timeout`** – *"Não vou esperar para sempre"* — Define um tempo máximo
de espera. Se passar disso, desistimos e seguimos em frente.
* **`@Bulkhead`** – *"Compartimentos estanques"* — Limita quantas operações
podem rodar simultaneamente. O nome vem dos navios, que têm divisórias para
que, se uma parte alaga, o navio inteiro não afunda.
* **`@CircuitBreaker`** – *"Disjuntor elétrico"* — Se um serviço está
claramente quebrado, paramos de tentar por um tempo, evitando sobrecarregar
ainda mais o sistema com chamadas que vão falhar de qualquer forma.
{: .fs-3 }

## Preparando o ambiente 🛠️

Antes de explorar cada anotação, vamos criar um projeto com suporte para
tolerância a falhas:
{: .fs-3 }

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:2.9.0.Final:create \
    -DprojectGroupId=dev.pw2 \
    -DprojectArtifactId=fault-tolerance \
    -Dextensions="quarkus-smallrye-fault-tolerance" \
    -DclassName="dev.pw2.FaultService" \
    -Dpath="/fault"

code fault-tolerance
```

Note que a extensão `quarkus-smallrye-fault-tolerance` é a peça-chave: é ela
que disponibiliza todas as anotações que veremos a seguir.
{: .fs-3 }

## 1. Retry — "Se não deu certo, tenta de novo" 🔄

A anotação `@Retry` é a mais simples e, surpreendentemente, uma das mais
efetivas. A lógica é direta: se o método lançar uma exceção, o framework o
executa novamente até atingir o número máximo de tentativas configurado.
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
        ResponseBuilderImpl builder = new ResponseBuilderImpl();
        builder.status(Response.Status.INTERNAL_SERVER_ERROR);
        builder.entity("The requested was an error");
        Response response = builder.build();
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
4. Tenta executar o método novamente — e assim por diante, até **3 tentativas** (`maxRetries = 3`).
5. Se todas falharem, **aí sim** a exceção é propagada para quem chamou o serviço.
{: .fs-3 }

⚠️ **Atenção:** `@Retry` só faz sentido para erros que podem se resolver com
o tempo. Não adianta ficar tentando se a falha é por causa de um bug no código
ou um dado inválido — você só vai gastar processamento e atrasar a resposta de
erro.
{: .fs-3 }

## 2. Fallback — "Plano B quando tudo dá errado" 🅱️

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
2. `@Retry` tenta mais 3 vezes — e falha novamente.
3. Agora `@Fallback` entra em ação e chama o método `recover`.
4. O usuário recebe `FALL_BACK_MESSAGE` em vez de uma exceção.
{: .fs-3 }

🚨 **Regra de ouro do fallback:** o método de recuperação **deve ter a mesma
assinatura** do método original. Isso significa: mesmo tipo de retorno, mesma
lista de parâmetros (na mesma ordem e tipos). No exemplo, tanto `getName`
quanto `recover` retornam `String` e recebem uma `String` como parâmetro. Se
você quebrar essa regra, o Quarkus reclama na inicialização.
{: .fs-3 }

## 3. Timeout — "Tempo é dinheiro" ⏱️

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

## 4. Bulkhead — "Divisórias para não afundar o navio" 🚢

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
estão executando, ela será **descartada** (recebe uma exceção).
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
cheio, volte depois". O modo thread pool é um restaurante com fila de espera
— você pode aguardar até abrir uma mesa, mas só até certo limite.
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

## 5. Circuit Breaker — "Quando insistir só piora as coisas" ⚡

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
2. **🔴 Aberto (Open):** muitas falhas detectadas. As chamadas **não são
executadas** — uma `CircuitBreakerOpenException` é lançada imediatamente,
poupando recursos.
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
public class CoffeeRepositoryService {

    private AtomicLong counter = new AtomicLong(0);

    /**
     * Returns the availability of a coffee.
     *
     * @param coffee The coffee to check availability for.
     * @return An integer representing the availability of the coffee.
     */
    @CircuitBreaker(requestVolumeThreshold = 2)
    public Integer getAvailability(Coffee coffee) {
        maybeFail();
        // Java expression that generates a random integer between 0 (inclusive)
        // and 30 (exclusive)
        return new Random().nextInt(30);
    }

    /**
     * This method introduces artificial failures in the service. It throws a
     * RuntimeException every other invocation, alternating between 2 successful
     * and 2 failing invocations.
     */
    private void maybeFail() {
        // introduce some artificial failures
        final Long invocationNumber = counter.getAndIncrement();
        // alternate 2 successful and 2 failing invocations
        if (invocationNumber % 4 > 1) {
            throw new RuntimeException("Service failed.");
        }
    }
```

Repare no método `maybeFail`: ele alterna entre **2 sucessos e 2 falhas**,
simulando um serviço instável. É um cenário típico do mundo real, em que um
serviço fica intermitente antes de cair de vez.
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
        // check that coffee with given id exists, return 404 if not
        if (coffee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            Integer availability = null;
            if (coffee != null) {
                availability = coffeeRepository.getAvailability(coffee);
            }

            if (availability != null) {
                LOGGER.log(Level.INFO, () -> "Sucesso: " + invocationNumber);
                return Response.ok(availability).build();
            } else {
                LOGGER.log(Level.SEVERE, () -> "Falha, coffee nulo:" + invocationNumber);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Coffee is null")
                        .type(MediaType.TEXT_PLAIN_TYPE)
                        .build();
            }
        } catch (RuntimeException e) {
            String message = String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
            LOGGER.log(Level.SEVERE, () -> "Falha:" + invocationNumber);
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
estado, a janela precisa estar **cheia**. Por padrão, ela tem tamanho 20 e a
taxa de falha aceitável é 0,5 (50%).
{: .fs-3 }

⚠️ Isso significa que um disjuntor fechado **sempre permite pelo menos N
invocações** (onde N é o tamanho da janela) antes de qualquer decisão.
{: .fs-3 }

**Exemplo concreto:** se a janela tem tamanho 10 e a taxa de falha é 0,5,
basta que 5 das últimas 10 invocações falhem para o disjuntor abrir.
{: .fs-3 }

**Estado aberto:** uma vez aberto, o disjuntor não deixa nenhuma chamada
passar. Em vez de executar o método, ele lança `CircuitBreakerOpenException`
imediatamente. É a "falha rápida" (*fail fast*) — em vez de gastar tempo e
recursos, você sabe na hora que algo está errado.
{: .fs-3 }

**Estado meio-aberto:** depois de algum tempo (5 segundos por padrão), o
disjuntor passa para meio-aberto. Nesse modo, ele deixa algumas **chamadas
de sonda** (*probe calls*) passarem:
{: .fs-3 }

* Se todas tiverem sucesso → volta para **fechado** (problema resolvido, vida normal).
* Se alguma falhar → volta para **aberto** (ainda está com problema, melhor esperar mais).
{: .fs-3 }

### Resumo do ciclo de vida

Visualizando o fluxo completo:
{: .fs-3 }

```
   🟢 FECHADO  ──(muitas falhas)──▶  🔴 ABERTO
       ▲                                 │
       │                          (passa tempo)
   (todas sondas OK)                     │
       │                                 ▼
       └──────────────────────────  🟡 MEIO-ABERTO
                  (alguma sonda falha)
                          │
                          └──▶ volta para 🔴 ABERTO
```

## Código 💡

Os exemplos sobre Fault Tolerance estão disponíveis no Github:
{: .fs-3 }

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
code pw2/exemplos/fault-tolerance
```

## Combinando as anotações: o cenário ideal 🎯

Você não precisa usar apenas uma anotação por método. Na verdade, a beleza
dessas ferramentas está em **combiná-las** para criar uma estratégia robusta
de tolerância a falhas:
{: .fs-3 }

```java
@Timeout(3000)                        // 1️⃣ não espera mais que 3s
@Retry(maxRetries = 3, delay = 500)   // 2️⃣ tenta 3x se falhar
@CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5)  // 3️⃣ disjuntor
@Fallback(fallbackMethod = "respostaCache")  // 4️⃣ último recurso
public Resposta consultarServico() { ... }
```

A sequência de defesa fica assim:
{: .fs-3 }

1. Cada chamada tem **3 segundos** para responder, ou é interrompida.
2. Se falhar, tenta **mais 3 vezes** com intervalo de 500ms.
3. Se houver muitas falhas seguidas, o **disjuntor abre** e poupa recursos.
4. Em qualquer cenário de falha final, o método de **fallback** entrega uma resposta útil.
{: .fs-3 }

## Exercício Prático 🏋️

Hora de colocar a mão na massa! Na aplicação de [gerenciamento de livros](https://github.com/rpmhubdev/pw2-books),
faça as seguintes modificações:
{: .fs-3 }

1. **Serviço `users`:** adicione `@Retry` no endpoint `/users/getJwt`.
*Pergunte-se: quais valores de `maxRetries` e `delay` fazem sentido para uma operação de autenticação?*
2. **Serviço `management`:** adicione `@CircuitBreaker` e `@Timeout` no endpoint `/bookManagement/listBooks`.
*Pergunte-se: quanto tempo é razoável esperar para listar livros? E quantas falhas seguidas devem abrir o circuito?*
{: .fs-3 }

Para realizar o exercício prático, você pode abrir diretamente no Codespaces:
{: .fs-3 }

[![Open in Codespaces](https://github.com/codespaces/badge.svg)](https://github.com/codespaces/new?hide_repo_select=true&repo=rpmhubdev/pw2-books)

Alternativamente, você pode fazer um `fork` do projeto para a sua conta e,
posteriormente, clonar para a sua máquina.
{: .fs-3 }

💡 **Desafio extra:** depois de adicionar as anotações, use o `k6` para simular
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
{: .fs-3 }

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
