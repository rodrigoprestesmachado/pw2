# Fault Tolerance 🆘

De forma geral, os serviços dependem da estrutura de rede para funcionarem de forma adequada. Porém, a rede é um ponto crítico para o bom funcionamento de um serviço uma vez que podem apresentar diversos problemas, tais como: saturação, mudança de topologia inesperada, atualizações, falhas de hardware, entre outros.

Por essa razão, o [Micro profile](https://github.com/eclipse/microprofile-fault-tolerance/) implementou um conjunto de anotações para que você possa tentar tornar um serviço um pouco mais tolerante quando ocorrer uma falha. Novamente, a implementação dessas anotações ficam ao encargo do [SmallRye Fault Tolerance](https://github.com/smallrye/smallrye-fault-tolerance/).

As principais anotações para aumento da resiliência do seu serviço são: `@Retry`, `@Fallback`, `@Timeout` e `@CircuitBreaker`.

* `@Retry` – Trata-se de uma das formas mais simples e efetivas para que um serviço se recupere de um problema de rede é tentar novamente a mesma operação.
* `@Fallback` – Invoca um método quando algum erro ocorrer.
* `@Timeout` – evita que a execução do serviço espere para sempre.
* `@Bulkhead` - O padrão bulkhead limita as operações que podem ser executadas ao mesmo tempo, mantendo as novas solicitações em espera, até que as solicitações de execução atuais possam termina.
* `@CircuitBreaker` - Evita realizar chamadas desnecessárias se um erro ocorrer.

# Como implementar?

Inicialmente, crie um projeto que tenha suporte para tolerância a falhas:

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:2.4.1.Final:create \
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

Se o método `getName` receber a String `error` como parâmetro de entrada, então, a exceção  `WebApplicationException` será lançada. Porém, a anotação `@Retry` irá fazer com que o método `getName` seja executado novamente por três vezes (*maxRetries*) e com um intervalo de tempo de dois segundos (*delay*).

## Fallback

Caso um método não consiga se recuperar de uma falha, podemos implementar um métodos que tome alguma atitude no lugar do método original. Desa forma, podemos adicionar um método de *fallback* por meio da anotação `@Fallback` como mostra o exemplo abaixo:

```java
@GET
@Path("/{name}")
@Produces(MediaType.TEXT_PLAIN)
@Retry(maxRetries = 3, delay = 2000)
@Fallback(fallbackMethod = "recover")
public String getName(@PathParam("name") String name) {
    // 🚨 o código do método anterior foi suprimido, pois,
    // não existem alterações nesse trecho
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
    // 🚨 o código do método anterior foi suprimido, pois,
    // não existem alterações nesse trecho
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

Quando `@Bulkhead` é usado sem a anotação `@Asynchronous`, a abordagem de isolamento será de [`semáforo`](https://download.eclipse.org/microprofile/microprofile-fault-tolerance-4.0/microprofile-fault-tolerance-spec-4.0.html#_semaphore_style_bulkhead), ou seja, permite apenas o número concomitante de configuração de solicitações. Porém, quando `@Bulkhead` for usado com `@Asynchronous`, a abordagem de isolamento de será [`thread pool`](https://download.eclipse.org/microprofile/microprofile-fault-tolerance-4.0/microprofile-fault-tolerance-spec-4.0.html#_thread_pool_style_bulkhead), permitindo configurar as solicitações simultâneas junto com um tamanho da fila de espera, por exemplo:

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

# Código 💡

Um código de exemplo sobre Fault Tolerance está disponível no Github:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
code pw2/exemplos/fault-tolerance
```

# Referências 📚

* SmallRye Fault Tolerance. Disponível em: [https://github.com/smallrye/smallrye-fault-tolerance/](https://github.com/smallrye/smallrye-fault-tolerance/).

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>