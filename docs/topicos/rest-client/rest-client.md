---
layout: default
title: Rest Client
nav_order: 4
---

# Rest Client

O [MicroProfile Rest Client](https://github.com/eclipse/microprofile-rest-client) fornece uma maneira para invocarmos serviços RESTful sobre HTTP. O MicroProfile Rest Client tenta usar APIs [Jakarta RESTful Web Services 2.1](https://jakarta.ee/specifications/restful-ws/2.1/) para manter compatibilidades e melhorar as questões de reuso.

Para criar um projeto Quarkus com suporte ao Rest Cliente utilize o seguinte comando:

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:2.5.1.Final:create \
    -DprojectGroupId=org.acme \
    -DprojectArtifactId=service \
    -DclassName="dev.rpmhub.Client" \
    -Dpath="/client" \
    -Dextensions="resteasy,resteasy-jackson,rest-client,rest-client-jackson"
cd client
```

Note que as extensões `rest-client` e `rest-client-jackson` (JSON *binding*) foram adicionadas ao projeto.

## Como implementar?

Imagine que temos que nos comunicar um um RESTful Web Service que realiza operações matemáticas simples, a operação de soma poderia ser algo parecido com:

```java
@GET
@Path("/sum/{a}/{b}")
@Produces(MediaType.TEXT_PLAIN)
public long sum(@PathParam("a") long a, @PathParam("b") long b) {
    return a + b;
}
```

Para criarmos um Rest Client que se comunique com a operação de soma de um RESTful Web Service temos que primeiro declarar uma interface, assim, observe o trecho de código abaixo:

```java
// 1 - Registrando um cliente rest
@RegisterRestClient(baseUri = "http://localhost:8080/service")
public interface MyRemoteService {

    // 2 - Declaração do método da interface
    @GET
    @Path("/sum/{a}/{b}")
    @Produces(MediaType.TEXT_PLAIN)
    public long sum(@PathParam("a") long a, @PathParam("b") long b);
}
```

Na declaração da interface, temos que registrar nosso Rest cliente no CDI (*Contexts and Dependency Injection*) por meio da anotação `@RegisterRestClient`. Logo, podemos indicar a URL base do serviço que iremos nos comunicar usando o atributo `baseUri`. Alternativamente, também podemos definir a URL base do serviço por meio de uma propriedade do arquivo *application.properties*. Nesse caso, temos que utilizar o seguinte formato:

    {nome da classe com o pacote}/mp-rest/url={url base}

Depois disso, note que a declaração dos métodos da interface do cliente é bastante parecido com o método do serviço que desejamos nos comunicar.

Finalmente, para utilizarmos o nosso Rest Cliente utilizamos a injeção de dependência em um bean, por exemplo:

```java
@Inject
@RestClient
MyRemoteService service;
```

## Código 💡

Um exemplo de código simples (serviço e Rest client) pode ser acessado por intermédio do Github:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
# Rest cliente
code pw2/exemplos/rest-client/client
# serviço
code pw2/exemplos/rest-client/service
```

# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* SmallReye Metrics. Disponível em: [https://quarkus.io/guides/rest-client](https://quarkus.io/guides/rest-client)

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>