<!-- .slide: data-background-opacity="0.3" data-background-image="https://pw2.rpmhub.dev/topicos/rest-client/slides/img/title.jpg" data-transition="convex" -->
# Rest Client 🕸️
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Servidor x Cliente 🔄
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Até agora criamos Web Services REST: aplicações que **oferecem** recursos
<!-- .element: style="margin-bottom:45px; font-size: 27px; color:white" -->

- Em micro serviços, um serviço muitas vezes também precisa **consumir**
outro serviço (ex.: carrinho de compras → serviço de pagamento)
<!-- .element: style="margin-bottom:45px; font-size: 27px; color:white" -->

- Fazer isso "na mão" (montar requisição HTTP, cabeçalhos, JSON) gera
código repetitivo (*boilerplate*)
<!-- .element: style="margin-bottom:45px; font-size: 27px; color:white" -->

- O **MicroProfile Rest Client** resolve esse problema
<!-- .element: style="margin-bottom:45px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Introdução ao MicroProfile Rest Client 📏
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- O MicroProfile é uma iniciativa comunitária para desenvolver especificações
para micro serviços em Java.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- O MicroProfile Rest Client é uma das especificações do MicroProfile, projetado
para simplificar a comunicação com serviços RESTful em aplicações Java.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- Reutiliza as **mesmas anotações do JAX-RS** (`@Path`, `@GET`, `@POST`...)
já usadas para criar recursos REST, reduzindo a curva de aprendizado
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- Ele oferece uma maneira elegante de consumir APIs REST, reduzindo a quantidade
de código boilerplate e facilitando a integração de serviços externos.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Principais Características
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- **Interface baseada em anotações:** as anotações como `@Path`, `@GET`, `@POST`,
  etc., são utilizadas para definir serviços RESTful de forma declarativa.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- **Tratamento de tipos de mídia:** lida automaticamente com tipos de mídia,
  como JSON ou XML, nas respostas das chamadas REST.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- **Parâmetros de requisição:** facilita a passagem de parâmetros de requisição,
  como caminhos, consultas e cabeçalhos.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- **Configuração simplificada:** as propriedades de clientes REST podem ser
  configuradas em arquivos de configuração.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Como Usar o MicroProfile Rest Client?
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Adicione as dependências do MicroProfile Rest Client no seu projeto.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- Defina interfaces para os serviços RESTful usando anotações como `@Path`,
  `@GET`, `@POST`, etc.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- Configure propriedades específicas do cliente REST em arquivos de
  configuração, como baseUri, timeouts, headers, etc.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## Cenário: carrinho de compras → pagamento
<!-- .element: style="margin-bottom:40px; font-size: 40px; color:black; font-family: Marker Felt;" -->

- O serviço de **checkout** (cliente) precisa confirmar um pagamento no
serviço de **payment** (servidor)
<!-- .element: style="margin-bottom:30px; font-size: 24px; color:black" -->

- Para isso, criamos uma interface Java que descreve a operação remota
<!-- .element: style="margin-bottom:30px; font-size: 24px; color:black" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## Passo 1: declarar a interface
<!-- .element: style="margin-bottom:40px; font-size: 40px; color:black; font-family: Marker Felt;" -->

  ```java
@RegisterRestClient(baseUri = "https://localhost:8444/")
public interface IPayment {

    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Invoice confirmPayment(
        @FormParam("cardNumber") String cardNumber,
        @FormParam("value") String value);
}
  ```
<!-- .element: style="margin-bottom:30px; font-size: 18px; background-color: white; " -->

`@RegisterRestClient` registra o cliente no CDI · as demais anotações são
as mesmas do JAX-RS, só que descrevem uma requisição **enviada**
<!-- .element: style="margin-bottom:30px; font-size: 20px; color:black" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## Passo 2: configurar a URL (alternativa)
<!-- .element: style="margin-bottom:40px; font-size: 40px; color:black; font-family: Marker Felt;" -->

Em vez de fixar `baseUri` na anotação, configure no
`application.properties`:
<!-- .element: style="margin-bottom:30px; font-size: 24px;" -->

```properties
{nome da classe com o pacote}/mp-rest/url={url base}
```
<!-- .element: style="margin-bottom:50px; font-size: 20px;" -->

Útil quando a URL do serviço varia entre ambientes (dev, teste, produção)
<!-- .element: style="margin-bottom:20px; font-size: 22px; color:black" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## Passo 3: injetar e usar
<!-- .element: style="margin-bottom:40px; font-size: 40px; color:black; font-family: Marker Felt;" -->

Injeção do cliente REST em uma classe de serviço:
<!-- .element: style="margin-bottom:20px; font-size: 27px;" -->

  ```java
  @Inject
  @RestClient
  IPayment paymentService;
  ```
<!-- .element: style="margin-bottom:30px; font-size: 20px;" -->

`paymentService.confirmPayment(...)` já monta e envia o `POST /payment`
<!-- .element: style="margin-bottom:20px; font-size: 22px; color:black" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Benefícios do MicroProfile Rest Client
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- **Redução de código:** O MicroProfile Rest Client elimina a
  necessidade de escrever código _boilerplate_ (repetitivo) para consumir
  serviços RESTful.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- **Facilidade de integração:** Facilita a integração de serviços RESTful em
  aplicações Java, reduzindo a quantidade de código necessário.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- **Configuração simplificada:** Permite configurar facilmente propriedades
  de clientes REST em arquivos de configuração, ao invés de configurar
  manualmente cada chamada REST.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Considerações de Segurança
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- O MicroProfile Rest Client suporta a especificação MicroProfile JWT, que
  permite a autenticação e autorização de chamadas REST usando tokens JWT.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- Ele também suporta a especificação MicroProfile Rest Client JWT, que permite
  a configuração de propriedades de clientes REST usando tokens JWT.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Resumo em 4 passos 🧭
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

1. Adicionar as extensões `rest-client` e `rest-client-jackson`
<!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

2. Declarar uma interface anotada com `@RegisterRestClient`
<!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

3. Injetar a interface com `@Inject` + `@RestClient`
<!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

4. Chamar os métodos da interface como se fossem locais 🎉
<!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Conclusão
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- O MicroProfile Rest Client é uma especificação do MicroProfile projetada para
  simplificar a comunicação com serviços RESTful em aplicações Java.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- Oferece uma maneira elegante de consumir APIs REST, reduzindo a quantidade
  de código boilerplate e facilitando a integração de serviços externos.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

- Suporta métodos HTTP padrão, tratamento de tipos de mídia, parâmetros de
  requisição e configuração simplificada de clientes REST.
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Referências 📚
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.
* <!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* SmallReye Metrics. Disponível em: [https://quarkus.io/guides/smallrye-metrics](https://quarkus.io/guides/smallrye-metrics)
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>