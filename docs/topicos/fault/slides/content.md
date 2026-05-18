<!-- .slide: data-background-opacity="0.3" data-background-image="https://pw2.rpmhub.dev/topicos/fault/slides/img/title.jpg" data-transition="convex" -->
# Fault Tolerance
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Por que se preocupar?
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Imagine esperar uma encomenda 📦
<!-- .element: style="margin-bottom:30px; font-size: 28px; color:white" -->

* O entregador depende de **várias coisas**: caminhão, trânsito, endereço, porteiro...
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Se **qualquer uma** falhar, a entrega não acontece
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Com microsserviços, é a mesma coisa: eles dependem da rede para conversar entre si
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## A rede vai falhar
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* A pergunta não é **se**, mas **quando** uma falha vai ocorrer:
<!-- .element: style="margin-bottom:40px; font-size: 28px; color:white" -->

  * 🌐 saturação de rede
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

  * 🔀 mudança de topologia inesperada
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

  * 🔄 atualizações de serviços
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

  * 💥 falhas de hardware
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* **Solução:** projetar serviços que **sobrevivam** às falhas
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Nosso kit de sobrevivência 🧰
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Em vez de escrever lógica de recuperação na mão...
<!-- .element: style="margin-bottom:30px; font-size: 28px; color:white" -->

* ...basta **anotar os métodos** e o framework cuida do resto
<!-- .element: style="margin-bottom:30px; font-size: 28px; color:white" -->

* Vamos conhecer **5 anotações**, cada uma com sua função
<!-- .element: style="margin-bottom:30px; font-size: 28px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## As 5 anotações
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* 🔄 `@Retry` — *"Tenta de novo!"*
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* 🅱️ `@Fallback` — *"Plano B"*
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* ⏱️ `@Timeout` — *"Não vou esperar para sempre"*
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* 🚢 `@Bulkhead` — *"Compartimentos estanques"*
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* ⚡ `@CircuitBreaker` — *"Disjuntor elétrico"*
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Preparando o ambiente
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* A extensão `quarkus-smallrye-fault-tolerance` é a peça-chave
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

Em um projeto existente:
<!-- .element: style="margin-bottom:10px; font-size: 22px; color:white" -->

```sh
./mvnw quarkus:add-extension -Dextensions="quarkus-smallrye-fault-tolerance"
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

Criando do zero:
<!-- .element: style="margin-bottom:10px; font-size: 22px; color:white" -->

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:2.9.0.Final:create \
    -DprojectGroupId=dev.pw2 \
    -DprojectArtifactId=fault-tolerance \
    -Dextensions="quarkus-smallrye-fault-tolerance" \
    -DclassName="dev.pw2.FaultService" \
    -Dpath="/fault"

code fault-tolerance
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Retry 🔄
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Lembra do WhatsApp quando a mensagem não envia?
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Você toca em "tentar novamente" e... funciona ✅
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* `@Retry` faz exatamente isso: **automatiza** as novas tentativas
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Ideal para **falhas transitórias** de rede
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Retry — exemplo
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

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
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Retry — o que acontece?
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* 1️⃣ Se receber `"error"`, lança `WebApplicationException`
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* 2️⃣ `@Retry` **intercepta** a exceção
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* 3️⃣ Espera **2 segundos** (`delay = 2000`)
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* 4️⃣ Tenta de novo — até **3 vezes** (`maxRetries = 3`)
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* 5️⃣ Se tudo falhar, **aí sim** propaga a exceção
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* ⚠️ Cuidado: não adianta com bugs ou dados inválidos
<!-- .element: style="margin-bottom:25px; font-size: 22px; color:#FFD580" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Fallback 🅱️
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* E se mesmo tentando várias vezes, não funcionar?
<!-- .element: style="margin-bottom:30px; font-size: 28px; color:white" -->

* Em vez de devolver erro... temos um **plano B**
<!-- .element: style="margin-bottom:30px; font-size: 28px; color:white" -->

* É como pagar em dinheiro quando o cartão é recusado 💳➡️💵
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Fallback — exemplo
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

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
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Fallback — regra de ouro
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* O método de fallback deve ter a **mesma assinatura** do original:
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  * ✅ Mesmo tipo de retorno
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

  * ✅ Mesma lista de parâmetros (ordem e tipos)
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* Se quebrar essa regra, o Quarkus reclama na **inicialização** 🚨
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:#FFD580" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Timeout ⏱️
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Imagine chamar um serviço que **trava** 🥶
<!-- .element: style="margin-bottom:30px; font-size: 28px; color:white" -->

* Sem timeout: seu serviço espera **para sempre**
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Resultado: threads, memória e conexões esgotadas 💀
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* `@Timeout` define um **tempo máximo** — passou disso, lança exceção
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Timeout — exemplo
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

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
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* Cada tentativa tem **7 segundos** para terminar
<!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->

* Note como as anotações **se compõem** naturalmente!
<!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Bulkhead 🚢
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* O nome vem da **engenharia naval** ⚓
<!-- .element: style="margin-bottom:30px; font-size: 28px; color:white" -->

* Navios têm **compartimentos estanques**: se um alaga, os outros ficam isolados
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* No nosso caso: limitar quantas requisições rodam **ao mesmo tempo**
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Evita esgotar memória, threads e conexões 🛡️
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Bulkhead — exemplo
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

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
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* No máximo **2 requisições** simultâneas
<!-- .element: style="margin-bottom:20px; font-size: 25px; color:white" -->

* A terceira que chegar... é **descartada** 🚫
<!-- .element: style="margin-bottom:20px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Bulkhead — dois modos
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* 🚪 **Modo Semáforo** (sem `@Asynchronous`):
<!-- .element: style="margin-bottom:15px; font-size: 25px; color:white" -->

  * É um segurança na porta: "está cheio, volte depois"
  <!-- .element: style="margin-bottom:30px; font-size: 22px; color:white" -->

* 🍽️ **Modo Thread Pool** (com `@Asynchronous`):
<!-- .element: style="margin-bottom:15px; font-size: 25px; color:white" -->

  * É um restaurante com **fila de espera**
  <!-- .element: style="margin-bottom:30px; font-size: 22px; color:white" -->

```java
// 2 concorrentes + fila de até 5 esperando
@Asynchronous
@Bulkhead(value = 2, waitingTaskQueue = 5)
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## ⚡ Circuit Breaker
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Imagine um serviço **completamente fora do ar** 💀
<!-- .element: style="margin-bottom:30px; font-size: 28px; color:white" -->

* Continuar tentando é **desperdício duplo**:
<!-- .element: style="margin-bottom:20px; font-size: 25px; color:white" -->

  * Gasta seus próprios recursos
  <!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->

  * Atrasa a resposta de erro ao usuário
  <!-- .element: style="margin-bottom:30px; font-size: 22px; color:white" -->

* Inspiração: **disjuntor elétrico** ⚡
<!-- .element: style="margin-bottom:20px; font-size: 25px; color:white" -->

  * Muita corrente → disjuntor abre → corta o fluxo
  <!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Os 3 estados do disjuntor
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* 🟢 **Fechado** — operação normal, mas monitorando falhas
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white" -->

* 🔴 **Aberto** — muitas falhas! Chamadas falham **imediatamente**
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white" -->

* 🟡 **Meio-aberto** — depois de um tempo, permite chamadas de teste
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Ciclo de vida visual
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

<img src="https://smallrye.io/docs/smallrye-fault-tolerance/6.2.6/_images/circuit-breaker-d4de8eed326379e7fdfe50126a827e4a7d0db05a.svg"/>
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## A janela deslizante
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* O disjuntor tem uma **"memória curta"** das últimas invocações
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Por padrão: tamanho **20**, taxa de falha **0,5** (50%)
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* **Exemplo:** janela = 10, taxa = 0,5
<!-- .element: style="margin-bottom:20px; font-size: 25px; color:white" -->

  * Se **5 das últimas 10** chamadas falharem...
  <!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->

  * ...o disjuntor **abre** 🔴
  <!-- .element: style="margin-bottom:30px; font-size: 22px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Circuit Breaker — código
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

```java

@CircuitBreaker(requestVolumeThreshold = 2)
public Integer getAvailability(Coffee coffee) {
    maybeFail();
    return new Random().nextInt(30);
}

private void maybeFail() {
    // introduce some artificial failures
    final Long invocationNumber = counter.getAndIncrement();
    // alternate 2 successful and 2 failing invocations
    if (invocationNumber % 4 > 1) {
        throw new RuntimeException("Service failed.");
    }
}

```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* `maybeFail` simula instabilidade: **2 sucessos** seguidos de **2 falhas** 🔄
<!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Combinando tudo 🎯
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

```java
@Timeout(3000)
@Retry(maxRetries = 3, delay = 500)
@CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5)
@Fallback(fallbackMethod = "respostaCache")
public Resposta consultarServico() { ... }
```
<!-- .element: style="margin-bottom:40px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

Camadas de defesa em sequência:
<!-- .element: style="margin-bottom:15px; font-size: 22px; color:white" -->

* 1️⃣ Cada chamada: máx. **3 segundos**
<!-- .element: style="margin-bottom:15px; font-size: 22px; color:white" -->

* 2️⃣ Falhou? Tenta **mais 3 vezes**
<!-- .element: style="margin-bottom:15px; font-size: 22px; color:white" -->

* 3️⃣ Muitas falhas? **Disjuntor abre** e poupa recursos
<!-- .element: style="margin-bottom:15px; font-size: 22px; color:white" -->

* 4️⃣ Em qualquer cenário ruim: **fallback** entrega resposta útil
<!-- .element: style="margin-bottom:15px; font-size: 22px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Como testar? 🧪
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Precisamos simular **muitos clientes ao mesmo tempo**
<!-- .element: style="margin-bottom:40px; font-size: 28px; color:white" -->

* Ferramenta ideal: **k6** ⚡
<!-- .element: style="margin-bottom:40px; font-size: 28px; color:white" -->

* Dispara requisições HTTP em **paralelo**, como se vários usuários estivessem acessando
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## K6 — script de exemplo
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

```javascript
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
<!-- .element: style="margin-bottom:30px; font-size: 14px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```sh
    k6 run k6.js
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## K6 — decifrando o script
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* `vus: 10` → 👥 cria **10 usuários virtuais** em paralelo
<!-- .element: style="margin-bottom:25px; font-size: 24px; color:white" -->

* `duration: '10s'` → ⏰ teste dura **10 segundos**
<!-- .element: style="margin-bottom:25px; font-size: 24px; color:white" -->

* `sleep(1)` → 💤 1 segundo entre requisições
<!-- .element: style="margin-bottom:25px; font-size: 24px; color:white" -->

* `rate<0.05` → ✅ passa se **menos de 5%** falharem
<!-- .element: style="margin-bottom:25px; font-size: 24px; color:white" -->

* 🚨 `exec.vu.idInTest` → identifica qual usuário fez a requisição
<!-- .element: style="margin-bottom:25px; font-size: 24px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Experimente! 🔬
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Aumente o `vus` e veja a taxa de erro subir
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white" -->

* Mude o valor do `@Bulkhead` e observe a diferença
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white" -->

* Acompanhe os **logs** quando o circuito abrir 📋
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white" -->

* 💡 Nada ensina mais do que **ver funcionando ao vivo**
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:#FFD580" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Recapitulando
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* 🔄 `@Retry` → tenta novamente
<!-- .element: style="margin-bottom:20px; font-size: 24px; color:white" -->

* 🅱️ `@Fallback` → plano B
<!-- .element: style="margin-bottom:20px; font-size: 24px; color:white" -->

* ⏱️ `@Timeout` → tempo máximo de espera
<!-- .element: style="margin-bottom:20px; font-size: 24px; color:white" -->

* 🚢 `@Bulkhead` → limita concorrência
<!-- .element: style="margin-bottom:20px; font-size: 24px; color:white" -->

* ⚡ `@CircuitBreaker` → para de tentar quando claramente está quebrado
<!-- .element: style="margin-bottom:20px; font-size: 24px; color:white" -->

* 🎯 Combine-as para **defesa em camadas**
<!-- .element: style="margin-bottom:20px; font-size: 24px; color:#FFD580" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Referências
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* SMALLRYE FAULT TOLERANCE disponível em: [https://quarkus.io/guides/smallrye-fault-tolerance](https://quarkus.io/guides/smallrye-fault-tolerance)
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
