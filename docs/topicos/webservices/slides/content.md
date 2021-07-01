<!-- .slide: data-background-image="img/java.jpg" 
data-transition="convex"
-->
# Web Services
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white;" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white;" -->




# O que é um Web Services?


<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
## O que é um Web Services?
<!-- .element: style="margin-bottom:60px; font-size: 60px; color:white; font-family: Marker Felt;" -->

* Um Web Service provê uma maneira padrão de interoperabilidade entre aplicações cliente/servidor por meio do protocolo HTTP
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white; font-family: arial;" -->

* Um Web Service também permite que aplicações com operações complexas obtenham um baixo acoplamento
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white; font-family: arial;" -->

* Assim, os Web Services permitem a construção de serviços podem interagir uns com os outros a fim de oferecer um valor agregado sofisticado
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
## Tipos de Web Services
<!-- .element: style="margin-bottom:40px; font-size: 60px; color:white; font-family: Marker Felt;" -->

* No nível conceitual, um serviço é um componente de software fornecido através de um ponto acessível na rede
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

* Assim, mensagens são trocadas entre os clientes e o serviço para obter informações sobre as invocações de requisições e respostas
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

* No nível técnico, um Web Service pode ser implementado de duas maneiras no Jakarta EE:
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white; font-family: arial;" -->

    * RESTful Web Services
    <!-- .element: style="margin-bottom:30px; font-size: 25px; color:white; font-family: fantasy;" -->

    * Web Services baseados em XML (_Extensible Markup Language_)
    <!-- .element: style="margin-bottom:30px; font-size: 25px; color:white; font-family: fantasy;" -->



# RESTful Web Services

[Building RESTful Web Services with Jakarta REST](https://eclipse-ee4j.github.io/jakartaee-tutorial/#building-restful-web-services-with-jakarta-rest)
<!-- .element: style="margin-bottom:40px; font-size: 10px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
## RESTful Web Services
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* No Jakarta EE, o JAX-RS provê a funcionalidade para Web Services baseados REST (_Representational State Transfer_)
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white; font-family: arial;" -->

* Os RESTful Web Services utilizam normas bastante consolidadas como: HTTP, XML, URI (_Uniform Resource Identifier_), MIME (_Multipurpose Internet Mail Extensions_)
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white; font-family: arial;" -->

* Assim, eles permitem que os serviços sejam construídos com uma barreira muito baixa para adoção
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
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

    * O serviço deve ser agregado a um site existente
    <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
## RESTful Web Services
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* JAX-RS é a API projetada para tonar fácil o desenvolvimento de aplicações que utilizem a arquitetura REST
<!-- .element: style="margin-bottom:60px; font-size: 23px; color:white; font-family: arial;" -->

* JAX-RS utiliza anotações para simplificar o desenvolvimento de RESTful Web Services
<!-- .element: style="margin-bottom:60px; font-size: 23px; color:white; font-family: arial;" -->

* Assim, é possível decorar uma classe Java para definir recursos e ações que podem ser executadas sobre estes recursos
<!-- .element: style="margin-bottom:60px; font-size: 23px; color:white; font-family: arial;" -->

* As anotações Java irão gerar os artefatos para construir o serviço
<!-- .element: style="margin-bottom:60px; font-size: 23px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
## RESTful Web Services
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* `@ApplicationPath` - define a URI base de todo o serviço
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->

* `@Path` – URI que indica o _endpoint_  do serviço. Você também pode inserir variáveis na URI para construir um template, por exemplo, `/resource/{username}`
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->

* `@GET`, `@POST`, `@PUT`, `@DELETE`, `@HEAD` – são anotações utilizadas em métodos Java que correspondem as requisições HTTP
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->

* `@Consumes` - usado para especificar os tipos [MIME](https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types) que foram enviados pelo cliente
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->

* `@Produces` – utilizado para especificar os tipos [MIME](https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types) que um recurso pode produzir e enviar para o cliente
<!-- .element: style="margin-bottom:50px; font-size: 23px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## RESTful Web Services: exemplo
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

```java
@ApplicationPath("/data")
public class DemoRestApplication extends Application {
    
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Adiciona as que formam o serviço
        classes.add(ServiceController.class);
        return classes;
    }
}
```
<!-- .element: style="margin-bottom:50px; font-size: 20px; font-family: Courier New;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## RESTful Web Services: exemplo
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

```java
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

@Path("/exemplo")
public class Exemplo {
    
    @GET
    @Produces("text/plain")
    public String getUser() {
        return "Rodrigo Prestes Machado";
    }

}      
```
<!-- .element: style="margin-bottom:50px; font-size: 20px; font-family: Courier New;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## RESTful Web Services: exemplo
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

```java
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/exemplo/{username}")
public class Exemplo {
    
    @GET
    public String getUser(@PathParam("username") String userName) {
        return userName;
    }

} 
```
<!-- .element: style="margin-bottom:50px; font-size: 20px; font-family: Courier New;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## RESTful Web Services: exemplo
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

```java
@Path("/exemplo/{username}/{age}")
public class Exemplo {
    
    @GET
    public String getUser(@PathParam("username") String userName, 
        @PathParam("age") String age ) {
        return userName + ":" + age;
    }
    
}    
```
<!-- .element: style="margin-bottom:50px; font-size: 20px; font-family: Courier New;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## RESTful Web Services: exemplo
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

```java
@Path("/exemplo")
public class Exemplo {
    
    @POST
    @Consumes("application/x-www-form-urlencoded")
    public void getUser(@FormParam("userName") String userName) {
        return userName;
    }

}      
```
<!-- .element: style="margin-bottom:50px; font-size: 20px; font-family: Courier New;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## RESTful Web Services: exemplo
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

```java
@Path("/exemplo")
public class Exemplo {
    
    @GET
    public String getUser(@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        return queryParams.getFirst("userName");
    }   
}    
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: Courier New;" -->

```java
@Path("/exemplo")
public class Exemplo {
    
    @POST
    @Consumes("application/x-www-form-urlencoded")
    public void getUser(MultivaluedMap<String, String> formParams) {
        return formParams.getFirst("userName");
    } 

} 
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: Courier New;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## RESTful Web Services: exemplo
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

* Para obter os cookies e o cabeçalho do HTTP:

```java
@Path("/exemplo")
public class Exemplo {
    
    @GET
    public String get(@Context HttpHeaders hh) {
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
    }
} 
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: Courier New;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## RESTful Web Services: exemplo
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

* Implementação de um cliente REST escrito com jax-rs:
<!-- .element: style="margin-bottom:50px; font-size: 23px; font-family: arial;" -->

```java
@WebServlet("/go")
public class UI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Client client = ClientBuilder.newClient();
        WebTarget myResource = client.target("http://localhost:9081/data/client/service/Rodrigo");
        String response = myResource.request(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(response);
    }
}
```
<!-- .element: style="margin-bottom:50px; font-size: 16px; font-family: Courier New;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## RESTful Web Services: exemplo
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

* Conversão de JSON para objeto Java num cliente REST escrito com jax-rs:
<!-- .element: style="margin-bottom:50px; font-size: 23px; font-family: arial;" -->

```java
@WebServlet("/go")
public class UI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Client client = ClientBuilder.newClient();
        WebTarget myResource = client.target("http://localhost:9081/data/client/service/Rodrigo");
        User user = myResource.request(MediaType.APPLICATION_JSON).get(User.class);
        System.out.println(user.getName());
    }
}
```
<!-- .element: style="margin-bottom:50px; font-size: 16px; font-family: Courier New;" -->



# Web Services baseados em XML

[Building Web Services with Jakarta XML Web Services](https://eclipse-ee4j.github.io/jakartaee-tutorial/#building-web-services-with-jakarta-xml-web-services)
<!-- .element: style="margin-bottom:40px; font-size: 10px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
## Web Services baseados em XML
<!-- .element: style="margin-bottom:40px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* No Jakarta EE, o JAX-WS provê a funcionalidade para Web Services baseados em XML
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white; font-family: arial;" -->

* Nesta especificação, existem padrões importantes para comunicação entre clientes e os serviços: SOAP e WSDL
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white; font-family: arial;" -->

* O SOAP (_Simple Object Access Protocol_) é um padrão em XML para a troca de mensagens
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white; font-family: arial;" -->

* WSDL (_Web Services Description Language_) descreve as operações e a forma de acesso de um serviço
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
## Web Services baseados em XML
<!-- .element: style="margin-bottom:100px; font-size: 50px; color:white; font-family: Marker Felt;" -->

<center>
<iframe src="https://pw2.rpmhub.dev/topicos/webservices/slides/webservice.html#/" title="Web Services" width="90%" height="500" style="border:none;"></iframe>
</center>


<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
## Web Services baseados em XML
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Para implementar um serviço, é necessário criar um contrato formal que descreve a interface que o serviço oferece
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

* Neste caso, o WSDL pode ser utilizado para descrever os detalhes deste contrato
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

* A implementação de um Web Service pode abordar requisitos não-funcionais, por exemplo, transações, segurança, coordenação, etc.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

* Além disso, pode ser necessário a implementação de operações assíncronas. Nesse caso, utiliza-se a infra-estrutura fornecida pelo padrão WSRM (Web Services Reliable Messaging) e a API JAX-WS
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## Web Services baseados em XML
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

```java
@WebService
public class Math {
    
    @WebMethod
    public int sum(int x, int y){
        return x+y;
    }
    
}
```
<!-- .element: style="margin-bottom:50px; font-size: 20px; font-family: Courier New;" -->


<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
## Web Services baseados em XML
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Para criar um Web Service, basta decorar uma classe Java com a anotação `@WebService` e, indicar qual método faz parte da interface por meio da anotação `@WebMethod`
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

* Para visualizar o WSDL da aplicação, utilize a URL:
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

    * `http://host:porta/aplicação/NomeDaClasse + Service?wsdl`
     <!-- .element: style="margin-bottom:30px; font-size: 23px; color:white; font-family: fantasy;" -->

    * Exemplo: `http://localhost:8080/Web/MathService?wsdl`
     <!-- .element: style="margin-bottom:30px; font-size: 23px; color:white; font-family: fantasy;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="zoom" -->
## Web Services baseados em XML
<!-- .element: style="margin-bottom:60px; font-size: 40px; font-family: Marker Felt;" -->

* Podemos utilizar a anotação `@WebServiceRef` para criarmos um cliente do serviço, por exemplo:
<!-- .element: style="margin-bottom:50px; font-size: 25px; font-family: arial;" -->

```java
@WebServiceRef
MathService service;
    
// Retorna a porta (interface) do serviço
edu.ifrs.ws.Math port = service.getMathPort();

// Invoca o serviço
port.sum(2, 2);        
```
<!-- .element: style="margin-bottom:50px; font-size: 20px; font-family: Courier New;" -->



<!-- .slide: data-background="#455FA4" data-transition="zoom" -->
# Referência
<!-- .element: style="margin-bottom:60px; font-size: 60px; color:white; font-family: Marker Felt;" -->

[The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#web-services)
<!-- .element: style="margin-bottom:50px; font-size: 20px;" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>