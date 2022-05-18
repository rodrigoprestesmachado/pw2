<!-- .slide: data-background-opacity="0.3" data-background-image="https://res.cloudinary.com/dotcom-prod/images/c_fill,f_auto,g_faces:center,q_auto,w_1920/v1/wt-cms-assets/2020/08/emayhqxbsu48vsdeqfl0/wtheadlessmicroservices1920x1440.jpg"
data-transition="convex"
-->
# Fault Tolerance
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Fault Tolerance
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Os serviços dependem da estrutura de rede para funcionarem de maneira adequada.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Porém, a rede é um ponto crítico uma vez que podem apresentar diversos problemas, tais como:
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

  * saturação
  <!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

  * mudança de topologia inesperada,
  <!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

  * atualizações,
  <!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

  * falhas de hardware, entre outros.
  <!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Anotações
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Configuração das anotações
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Para implementar no Quarkus uma solução de tolerância a falhas instale a extensão smallrye-fault-tolerance.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

```sh
./mvnw quarkus:add-extension -Dextensions="quarkus-smallrye-fault-tolerance"
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:2.9.0.Final:create \
    -DprojectGroupId=dev.pw2 \
    -DprojectArtifactId=fault-tolerance \
    -Dextensions="quarkus-smallrye-fault-tolerance" \
    -DclassName="dev.pw2.FaultService" \
    -Dpath="/fault"

code fault-tolerance
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Anotações
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* As principais anotações para aumento da resiliência do seu serviço são: `@Retry`, `@Fallback`, `@Timeout` e `@CircuitBreaker`.
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* `@Retry` – Tentar novamente, trata-se da forma mais simples e efetiva para que um serviço se recupere de um problema de rede.
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* `@Fallback` – Invoca um método quando algum erro ocorrer.
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* `@Timeout` – evita que a execução do serviço espere para sempre.
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* `@Bulkhead` - O padrão bulkhead limita as operações que podem ser executadas ao mesmo tempo, mantendo as novas solicitações em espera, até que as solicitações de execução atuais possam termina.
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* `@CircuitBreaker` - Evita realizar chamadas desnecessárias se um erro ocorrer.
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Retry
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
## @Fallback
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
## @Timeout
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
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## @Bulkhead
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
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## K6
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## K6
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* O k6 é capaz de simular o disparo de requisições HTTP por clientes distintos, vejamos um exemplo:
<!-- .element: style="margin-bottom:10px; font-size: 25px; color:white" -->

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
<!-- .element: style="margin-bottom:40px; font-size: 14px; font-family: arial; color:black; background-color: #F2FAF3;" -->

Para rodar o k6 com a configuração acima:
<!-- .element: style="margin-bottom:10px; font-size: 25px; color:white" -->

```sh
    k6 run k6.js
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## K6
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* O exemplo do slide anterior faz com que o k6 crie 10 unidades virtuais (vu) que irão disparar requisições HTTP com um intervalo de 1 segundo dentro de um tempo de 10 segundos.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

* 🚨 Um detalhe, o objeto `exec` pode ser utilizado para identificar qual vu que está realizando a requisição (`exec.vu.idInTest`).
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Referências
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* SMALLRYE FAULT TOLERANCE disponível em: [https://quarkus.io/guides/smallrye-fault-tolerance](https://quarkus.io/guides/smallrye-fault-tolerance)
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>