<!-- .slide:  data-background-opacity="0.2" data-background-image="img/java.jpg" 
data-transition="convex"
-->
# Web Services
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white;" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white;" -->




# O que é um Web Services? 🕸️


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## O que é um Web Services?
<!-- .element: style="margin-bottom:60px; font-size: 60px; color:white; font-family: Marker Felt;" -->

* Um Web Service provê uma maneira padrão de interoperabilidade entre aplicações cliente/servidor por meio do protocolo HTTP
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white; font-family: arial;" -->

* Um Web Service também permite que aplicações com operações complexas obtenham um baixo acoplamento
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white; font-family: arial;" -->

* Assim, os Web Services permitem a construção de serviços que podem interagir uns com os outros a fim de oferecer um valor agregado sofisticado
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## Tipos de Web Services
<!-- .element: style="margin-bottom:40px; font-size: 60px; color:white; font-family: Marker Felt;" -->

* No nível conceitual, um serviço é um componente de software fornecido através de um ponto acessível na rede
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

* Assim, mensagens são trocadas entre os clientes e o serviço para obter informações sobre as invocações de requisições e respostas
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

* No nível técnico, um Web Service pode ser implementado de duas maneiras:
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white; font-family: arial;" -->

    * RESTful Web Services — foco desta disciplina
    <!-- .element: style="margin-bottom:30px; font-size: 25px; color:white; font-family: fantasy;" -->

    * Web Services baseados em XML (SOAP/WSDL)
    <!-- .element: style="margin-bottom:30px; font-size: 25px; color:white; font-family: fantasy;" -->



# RESTful Web Services 🕸️

[Jakarta RESTful Web Services](https://jakarta.ee/specifications/restful-ws/)
<!-- .element: style="margin-bottom:40px; font-size: 10px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## RESTful Web Services
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* No Jakarta EE, o JAX-RS (hoje *Jakarta RESTful Web Services*) provê a funcionalidade para Web Services baseados em REST
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->

* O Quarkus implementa essa especificação com a extensão `resteasy-reactive`
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->

* Os RESTful Web Services utilizam normas consolidadas: HTTP, URI e MIME
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->

* Assim, eles permitem que os serviços sejam construídos com uma barreira muito baixa para adoção
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## RESTful Web Services
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Um RESTful Web Service pode ser apropriado quando as seguintes condições forem atendidas:
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white; font-family: arial;" -->

    * O produtor e o consumidor têm uma compreensão mútua do contexto e conteúdo que está sendo repassado
    <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white; font-family: arial;" -->

    * O serviço pode ser stateless
    <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white; font-family: arial;" -->

    * A largura de banda for limitada
    <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white; font-family: arial;" -->

    * O serviço pode ser agregado a um site existente
    <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## Jakarta REST no Quarkus
<!-- .element: style="margin-bottom:50px; font-size: 45px; color:white; font-family: Marker Felt;" -->

* JAX-RS utiliza anotações para simplificar o desenvolvimento de RESTful Web Services
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->

* É possível decorar uma classe Java para definir recursos e ações sobre estes recursos
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->

* Extensões: `resteasy-reactive` (API REST) e `resteasy-reactive-jackson` (JSON)
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->

* Pacote das anotações: `jakarta.ws.rs`
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## Sem `@ApplicationPath`
<!-- .element: style="margin-bottom:50px; font-size: 45px; color:white; font-family: Marker Felt;" -->

* No Jakarta EE tradicional, era comum estender `Application` com `@ApplicationPath`
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->

* No Quarkus, essa classe **não é necessária**
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->

* Basta anotar uma classe com `@Path`: o *framework* descobre e registra o recurso automaticamente
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->

* A URI fica disponível em `http://localhost:8080` + o valor de `@Path`
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## Anotações principais
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* `@Path` — URI do recurso (*endpoint*); aceita *templates*, por exemplo `/produtos/{id}`
<!-- .element: style="margin-bottom:35px; font-size: 22px; color:white; font-family: arial;" -->

* `@GET`, `@POST`, `@PUT`, `@DELETE` — associam o método a um verbo HTTP
<!-- .element: style="margin-bottom:35px; font-size: 22px; color:white; font-family: arial;" -->

* `@PathParam` / `@QueryParam` — injetam valores da URI ou da *query string*
<!-- .element: style="margin-bottom:35px; font-size: 22px; color:white; font-family: arial;" -->

* `@Consumes` — tipo MIME que o método **recebe** do cliente
<!-- .element: style="margin-bottom:35px; font-size: 22px; color:white; font-family: arial;" -->

* `@Produces` — tipo MIME que o método **envia** ao cliente
<!-- .element: style="margin-bottom:35px; font-size: 22px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#F5F5F5" data-transition="zoom" -->
## Recurso REST básico
<!-- .element: style="margin-bottom:40px; font-size: 40px; font-family: Marker Felt;" -->

```java
@Path("/produtos")
public class ProdutoResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ola() {
        return "Bem-vindo ao catálogo de produtos!";
    }
}
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: Courier New;" -->

Acesso: `http://localhost:8080/produtos`
<!-- .element: style="margin-bottom:30px; font-size: 20px; font-family: arial;" -->


<!-- .slide: data-background="#F5F5F5" data-transition="zoom" -->
## `@PathParam`
<!-- .element: style="margin-bottom:40px; font-size: 40px; font-family: Marker Felt;" -->

* URI: `/produtos/1` — o `{id}` é uma variável de *template*
<!-- .element: style="margin-bottom:25px; font-size: 20px; font-family: arial;" -->

```java
@GET
@Path("/{id}")
@Produces(MediaType.APPLICATION_JSON)
public Produto buscarPorId(@PathParam("id") Long id) {
    return catalogo.get(id);
}
```
<!-- .element: style="margin-bottom:25px; font-size: 18px; font-family: Courier New;" -->

```java
public record Produto(Long id, String nome,
        String categoria, double preco) {}
```
<!-- .element: style="margin-bottom:25px; font-size: 18px; font-family: Courier New;" -->


<!-- .slide: data-background="#F5F5F5" data-transition="zoom" -->
## `@QueryParam`
<!-- .element: style="margin-bottom:40px; font-size: 40px; font-family: Marker Felt;" -->

* URI: `/produtos?categoria=eletronicos` — parâmetro **opcional**
<!-- .element: style="margin-bottom:25px; font-size: 20px; font-family: arial;" -->

```java
@GET
@Produces(MediaType.APPLICATION_JSON)
public List<Produto> listar(
        @QueryParam("categoria") String categoria) {
    if (categoria == null) {
        return catalogo.values().stream().toList();
    }
    return catalogo.values().stream()
        .filter(p -> p.categoria()
            .equalsIgnoreCase(categoria))
        .toList();
}
```
<!-- .element: style="margin-bottom:25px; font-size: 16px; font-family: Courier New;" -->


<!-- .slide: data-background="#F5F5F5" data-transition="zoom" -->
## Corpo simples (`@Consumes`)
<!-- .element: style="margin-bottom:40px; font-size: 40px; font-family: Marker Felt;" -->

* Corpo com um valor simples (texto/número), sem JSON estruturado
<!-- .element: style="margin-bottom:25px; font-size: 20px; font-family: arial;" -->

```java
@POST
@Path("/desconto")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public String aplicarDesconto(double preco) {
    double comDesconto = preco * 0.9;
    return Double.toString(comDesconto);
}
```
<!-- .element: style="margin-bottom:25px; font-size: 18px; font-family: Courier New;" -->

Mesmo padrão do exercício de conversão km/h → mi/h
<!-- .element: style="margin-bottom:25px; font-size: 18px; font-family: arial;" -->


<!-- .slide: data-background="#F5F5F5" data-transition="zoom" -->
## Corpo JSON
<!-- .element: style="margin-bottom:40px; font-size: 40px; font-family: Marker Felt;" -->

* Sem anotação no parâmetro → o JAX-RS preenche a partir do *body*
<!-- .element: style="margin-bottom:25px; font-size: 20px; font-family: arial;" -->

```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response criar(Produto produto) {
    catalogo.put(produto.id(), produto);
    return Response.status(Response.Status.CREATED)
        .entity(produto).build();
}
```
<!-- .element: style="margin-bottom:25px; font-size: 18px; font-family: Courier New;" -->

🚨 `@Consumes` = o que o servidor **aceita**; `@Produces` = o que **envia**
<!-- .element: style="margin-bottom:25px; font-size: 18px; color:#900; font-family: arial;" -->


<!-- .slide: data-background="#F5F5F5" data-transition="zoom" -->
## `Response` e status HTTP
<!-- .element: style="margin-bottom:40px; font-size: 40px; font-family: Marker Felt;" -->

```java
@DELETE
@Path("/{id}")
public Response remover(@PathParam("id") Long id) {
    if (catalogo.remove(id) == null) {
        return Response.status(
            Response.Status.NOT_FOUND).build();
    }
    return Response.noContent().build(); // 204
}
```
<!-- .element: style="margin-bottom:25px; font-size: 18px; font-family: Courier New;" -->

* `CREATED` → 201 · `ok()` → 200 · `noContent()` → 204 · `NOT_FOUND` → 404
<!-- .element: style="margin-bottom:25px; font-size: 20px; font-family: arial;" -->


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## Cliente REST
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Para **consumir** um serviço REST no Quarkus, use o MicroProfile Rest Client
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->

* Anotações como `@RegisterRestClient`, `@Path`, `@GET` na interface do cliente
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->

* Detalhes no tópico [Rest Client](../../rest-client/rest-client.html)
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->



# Web Services baseados em XML 🕸️


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## XML / SOAP (visão geral)
<!-- .element: style="margin-bottom:40px; font-size: 45px; color:white; font-family: Marker Felt;" -->

* Outro estilo de Web Service usa SOAP e WSDL (mensagens e contratos em XML)
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->

* SOAP (_Simple Object Access Protocol_) — padrão XML para troca de mensagens
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->

* WSDL (_Web Services Description Language_) — descreve operações e acesso ao serviço
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->

* Mais complexos e pesados; o **foco desta disciplina é REST** com Quarkus
<!-- .element: style="margin-bottom:45px; font-size: 23px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## XML / SOAP
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

<center>
<img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/master/docs/topicos/webservices/slides/webservices.puml" alt="XML Web Service" width="40%" height="40%">
</center>



<!-- .slide: data-background="#222c44" data-transition="zoom" -->
# Referência
<!-- .element: style="margin-bottom:60px; font-size: 60px; color:white; font-family: Marker Felt;" -->

[Jakarta RESTful Web Services](https://jakarta.ee/specifications/restful-ws/)
<!-- .element: style="margin-bottom:30px; font-size: 20px;" -->

[Writing JSON REST services — Quarkus](https://quarkus.io/guides/rest-json)
<!-- .element: style="margin-bottom:30px; font-size: 20px;" -->

[The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#web-services)
<!-- .element: style="margin-bottom:50px; font-size: 20px;" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
