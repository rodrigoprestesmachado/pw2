---
layout: default
title: Fault Tolerance
parent: Micro Serviços Intermediário
nav_order: 10
---

# Fault Tolerance 🆘

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/fault/slides/index.html#/" title="Fault Tolerance" width="90%" height="500" style="border:none;"></iframe>
</center>

De forma geral, os serviços dependem da estrutura de rede para funcionarem de maneira adequada. Porém, a rede é um ponto crítico para o bom funcionamento de um serviço uma vez que podem apresentar diversos problemas, tais como: saturação, mudança de topologia inesperada, atualizações, falhas de hardware, entre outros.

Por essa razão, o [Microprofile](https://github.com/eclipse/microprofile-fault-tolerance/) implementou um conjunto de anotações para que você possa tentar tornar um serviço um pouco mais resiliente quando uma falha ocorrer. A implementação concreta das dessas anotações projetadas no Microprofile ficam ao encargo do [SmallRye Fault Tolerance](https://github.com/smallrye/smallrye-fault-tolerance/).

As principais anotações para aumento da resiliência do seu serviço são: `@Retry`, `@Fallback`, `@Timeout` e `@CircuitBreaker`.

* `@Retry` – Tentar novamente, trata-se da forma mais simples e efetiva para que um serviço se recupere de um problema de rede.
* `@Fallback` – Invoca um método quando algum erro ocorrer.
* `@Timeout` – evita que a execução do serviço espere para sempre.
* `@Bulkhead` - O padrão bulkhead limita as operações que podem ser executadas ao mesmo tempo, mantendo as novas solicitações em espera, até que as solicitações de execução atuais possam termina.
* `@CircuitBreaker` - Evita realizar chamadas desnecessárias se um erro ocorrer.

# Configurações

Inicialmente, crie um projeto que tenha suporte para tolerância a falhas:

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:2.9.0.Final:create \
    -DprojectGroupId=dev.pw2 \
    -DprojectArtifactId=fault-tolerance \
    -Dextensions="quarkus-smallrye-fault-tolerance" \
    -DclassName="dev.pw2.FaultService" \
    -Dpath="/fault"

code fault-tolerance
```

## Retry

Como dito anteriormente, a anotação `@Retry` irá tentar executar novamente o método de um serviço. Como exemplo, observe o trecho de código abaixo:

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

Se o método `getName` receber a String `error` como parâmetro de entrada, então, a exceção  `WebApplicationException` será lançada. Porém, a anotação `@Retry` irá fazer com que o método `getName` seja executado novamente por três vezes (*maxRetries*) num intervalo de tempo de dois segundos (*delay*).

## Fallback

Caso um método não consiga se recuperar de uma falha, podemos implementar um métodos que tome alguma atitude no lugar do método original. Desa forma, podemos adicionar um método de *fallback* por meio da anotação `@Fallback` como mostra o exemplo abaixo:

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

🚨 Um detalhe importante, o método de *fallback* deve ter a mesma assinatura do método original, ou seja, mesmo tipo de retorno, mesmo nome de método e também mesma lista de parâmetros. No exemplo, observe que o método `recover` possui a mesma assinatura do método `getName`.

## Timeout

 Como o próprio nome já induz, a anotação `@Timeout` aguarda a execução completa de um método por um tempo pré-determinado. Assim, caso um método não consiga terminar no tempo estipulado, uma exceção será lançada.

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

## Bulkhead

A anotação `@Bulkhead` limita as operações que podem ser executadas ao mesmo tempo. O trecho de código do exemplo abaixo mostra o uso da anotação `@Bulkhead`, nesse caso, o método `bulkhead` irá permitir que duas requisições possam ser processadas simultaneamente, assim, se por um acaso chegar uma terceira requisição, essa será descartada.

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

Quando `@Bulkhead` é usado sem a anotação `@Asynchronous`, a abordagem de isolamento será de [`semáforo`](https://download.eclipse.org/microprofile/microprofile-fault-tolerance-4.0/microprofile-fault-tolerance-spec-4.0.html#_semaphore_style_bulkhead), ou seja, permite apenas o número concomitante de requisições. Porém, quando `@Bulkhead` for usado com `@Asynchronous`, a abordagem de isolamento de será [`thread pool`](https://download.eclipse.org/microprofile/microprofile-fault-tolerance-4.0/microprofile-fault-tolerance-spec-4.0.html#_thread_pool_style_bulkhead), permitindo configurar as solicitações simultâneas junto com um tamanho da fila de espera, por exemplo:

```java
// máximo de 2 requisições concorrentes serão permitidas
// máximo de 5 requisições serão permitidas na fila de espera
@Asynchronous
@Bulkhead(value = 2, waitingTaskQueue = 5)
```

Para testar a anotação `@Bulkhead` instale a ferramenta [k6](https://k6.io/docs/). O k6 é capaz de simular o disparo de requisições HTTP por clientes distintos. Observe o exemplo:

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

A configuração acima faz com que o k6 crie 10 unidades virtuais (vu) que irão disparar requisições HTTP com um intervalo de 1 segundo dentro de um tempo de 10 segundos. 🚨 Um detalhe, o objeto `exec` pode ser utilizado para identificar qual vu que está realizando a requisição (`exec.vu.idInTest`).

Para rodar o k6 com a configuração acima, crie um arquivo .js e depois execute o commando `run` do `k6`, por exemplo:

    k6 run k6.js

## Circuit Breaker

A anotação `@CircuitBreaker` evita realizar chamadas desnecessárias se um erro
ocorrer. O trecho de código abaixo mostra o uso da anotação `@CircuitBreaker`.

O circuito será fechado novamente após um tempo de espera (pr padrão 5 segundos).
Caso o método anotado com o `circuitBreaker` volte a falhar, o circuito será
aberto novamente. Observe o [exemplo](https://pt.quarkus.io/guides/smallrye-fault-tolerance#adding-resiliency-circuit-breaker) abaixo:

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

O disjuntor começa fechado. Nesse estado, o disjuntor mantém uma janela
deslizante (_rolling window_) das invocações recentes. Para cada invocação, a
janela deslizante registra se ela foi concluída com sucesso ou falhou.

A janela deslizante deve estar cheia para tomar qualquer decisão de transição
de estado. Por exemplo, se a janela deslizante tiver tamanho 10, um disjuntor
fechado sempre permite pelo menos 10 invocações.

Se a janela deslizante contiver um número de falhas maior do que a taxa
configurada, um disjuntor fechado muda para o estado aberto. Quando o disjuntor
estiver aberto, as invocações não são permitidas. Em vez disso, o disjuntor
falha rapidamente e lança a exceção CircuitBreakerOpenException.

Por exemplo, se a janela deslizante tiver tamanho 10 e a taxa de falha for de
0,5, isso significa que 5 invocações das últimas 10 invocações devem falhar para
que o disjuntor mude para o estado aberto.

Após algum tempo, um disjuntor aberto passa para o estado meio-aberto para
determinar se a falha rápida ainda é apropriada. Um disjuntor meio-aberto
permite que algumas tentativas prossigam. Se todas elas tiverem sucesso, o
disjuntor retorna ao estado fechado e as invocações são permitidas novamente.
Se algumas invocações de sonda falharem, o disjuntor volta ao estado aberto e
as invocações são impedidas.


# Código 💡

Um código de exemplo sobre Fault Tolerance está disponível no Github:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
code pw2/exemplos/fault-tolerance
```

# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* SmallRye Fault Tolerance. Disponível em: [https://github.com/smallrye/smallrye-fault-tolerance/](https://github.com/smallrye/smallrye-fault-tolerance/).

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>