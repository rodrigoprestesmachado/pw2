---
layout: default
title: Primeira Avaliação
nav_order: 10
---

# Simulado: Primeira Avaliação


## Exemplos de Questões Práticas

1. Criar a classe Product com os atributos: id, name, category.
    1. Implementar o recurso ProductResource com os endpoints:
    * GET /products → retorna todos os produtos.
    * GET /products?category=Eletronicos → retorna apenas os produtos da categoria informada.

2. Escreva um exemplo de interface REST Client para consumir a API pública de usuários (GET /users).

3. Como você configuraria o endpoint "https://api.exemplo.com/v1" em application.properties para o cliente acima?

4. Escreva um código no Quarkus, para restringir o acesso a um endpoint apenas para usuários com a role “admin”.

## Exemplos de Questões Teóricas

1) Qual anotação em Jakarta REST define o caminho base de um recurso REST em Quarkus?
* A) @Produces
* B) @Path
* C) @Consumes
* D) @RestController


2) Qual das opções a seguir representa corretamente um recurso REST em Quarkus que responde em /hello com um GET retornando texto simples?
* A)
```java
@Path("/hello")
public class HelloResource {
    @GET
    public String hello() {
        return "Olá, Quarkus!";
    }
}
```

* B)
```java
@RestController
@RequestMapping("/hello")
public class HelloResource {
    @GetMapping
    public String hello() {
        return "Olá, Quarkus!";
    }
}
```

C)
```java
@Path("/hello")
public class HelloResource {
    @POST
    public String hello() {
        return "Olá, Quarkus!";
    }
}
```

D)
```java
@Controller
public class HelloResource {
    @RequestMapping("/hello")
    public String hello() {
        return "Olá, Quarkus!";
    }
}
```

3) Se um endpoint REST em Quarkus precisar retornar dados em JSON, qual anotação deve ser usada para indicar o tipo de mídia de saída?
* A) @Path
* B) @Produces(MediaType.APPLICATION_JSON)
* C) @Consumes(MediaType.APPLICATION_JSON)
* D) @ResponseBody

4) Em um recurso REST no Quarkus, qual anotação é usada para indicar que um método deve processar requisições POST?
* A) @PUT
* B) @POST
* C) @PathParam
* D) @RequestBody

5) Em Quarkus/Jakarta REST, qual anotação é usada para capturar um valor diretamente do path da URL, como em /users/{id}?
* A) @QueryParam
* B) @PathParam
* C) @RequestParam
* D) @HeaderParam

6) Se quisermos capturar um parâmetro de query em uma requisição REST do tipo /users?active=true, qual anotação do Jakarta REST devemos usar no método do recurso?
* A) @QueryParam
* B) @PathParam
* C) @RequestParam
* D) @FormParam

7) O que é o REST Client no Quarkus?
* A) Uma API para expor endpoints HTTP
* B) Uma biblioteca para substituir JAX-RS
* C) Uma forma declarativa de consumir serviços REST externos
* D) Um módulo de banco de dados embutido

8) Qual anotação registra uma interface como cliente REST?
* A) @RestEndpoint
* B) @RegisterRestClient
* C) @RestClient
* D) @RemoteService

9) Qual a vantagem de usar REST Client com CDI e injeção de dependência?
* A) Permite executar chamadas assíncronas automaticamente
* B) Facilita testes, reuso e integração com o ecossistema Quarkus
* C) Substitui o Hibernate para chamadas de banco de dados
* D) Dispensa configuração em application.properties

10) Considere a interface REST Client abaixo:

```java
@RegisterRestClient(configKey = "payments-api")
public interface PaymentClient {
    @GET
    @Path("/transactions")
    List<Transaction> listTransactions();
}
```

12) Para configurar a URL base para esse cliente via application.properties, qual propriedade está correta?
* A) quarkus.rest-client.PaymentClient.url=https://api.exemplo.com/
* B) payments-api/mp-rest/url=https://api.exemplo.com/
* C) payments-api/mp-rest/uri=https://api.exemplo.com/
* D) org.acme.PaymentClient/mp-rest/url=https://api.exemplo.com/

13) Qual das seguintes partes NÃO faz parte da estrutura básica de um JWT?
* A) Cabeçalho (Header)
* B) Carga de declarações (Payload / Claims)
* C) Assinatura (Signature)
* D) Cabeçalho de sessão (Session Header)

14) Para implementar JWT no Quarkus, quais extensões são necessárias segundo o conteúdo?
* A) smallrye-jwt e quarkus-oauth2
* B) smallrye-jwt e smallrye-jwt-build
* C) smallrye-jwt-build e quarkus-security-jwt
* D) smallrye-jwt, smallrye-jwt-build e quarkus-oidc

15) Qual propriedade do application.properties indica onde está a chave pública
usada para verificar tokens no Quarkus / MicroProfile JWT?
* A) mp.jwt.verify.publickey.location
* B) smallrye.jwt.sign.key.location
* C) smallrye.jwt.verify.key.location
* D) jwt.public.key.path

16) Se quisermos que um método do serviço só seja acessível por usuários com
certo papel (role), qual anotação é usada para isso?
* A) @PermitAll
* B) @Authenticated
* C) @RolesAllowed("User")
* D) @PermitRole("User")

17) Qual claim é obrigatório em um JWT compatível com a especificação MicroProfile JWT?
* A) aud (Audience)
* B) iss (Issuer)
* C) upn (User Principal Name)
* D) jti (JWT ID)

18) Se quisermos permitir acesso irrestrito a um endpoint em uma aplicação
protegida por JWT, qual anotação devemos usar?
* A) @DenyAll
* B) @PermitAll
* C) @Unsecured
* D) @AllowAnonymous

19) Qual é a função da anotação @AccessToken em um cliente REST no Quarkus?
* A) Indica que o método deve retornar diretamente o conteúdo do JWT.
* B) Configura automaticamente o cliente REST para incluir o token de acesso (Authorization: Bearer) nas requisições.
* C) Faz a validação do token JWT antes de executar o método.
* D) Substitui a necessidade de @RolesAllowed nos recursos protegidos.

20) Quando você quer que seu serviço Quarkus ofereça endpoints protegidos por JWT e garanta que as comunicações sejam feitas via HTTPS, qual das alternativas abaixo representa uma configuração correta e segura?
* A) Definir quarkus.http.ssl-port=8443, fornecer o keystore ou certificado SSL (arquivo .jks ou .pem) com suas credenciais no application.properties, e obrigar que o cliente use https:// nas URLs de comunicação.
* B) Usar quarkus.jwt.require-https=true, sem necessidade de configurar certificado SSL, pois o Quarkus irá gerar um certificado autoassinado automaticamente e confiar nele por padrão.
* C) Adicionar @Secure na classe de recurso REST, para que todas as requisições a esse recurso usem HTTPS, mesmo se o Quarkus estiver executando apenas em HTTP.
* D) Configurar smallrye.jwt.sign.key.location apontando para um certificado SSL, o que automaticamente habilita HTTPS para todas requisições REST e valida que o token JWT venha criptografado.


<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
