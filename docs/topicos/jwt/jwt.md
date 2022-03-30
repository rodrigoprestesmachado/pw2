# JSON Web Token

Um JSON Web Token (JWT) é um padrão da Internet para a criação de um token (sequência de caracteres) normalmente criptografado cujo seu corpo contém o JSON com um conjunto de declarações específicas de uma aplicação, como por exemplo, nome de um usuário, e-mail e papéis.

## Como implementar?

Para criar a aplicação com as extenões `smallrye-jwt` e `smallrye-jwt-build`:

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:2.5.1.Final:create \
    -DprojectGroupId=dev.rpmhub \
    -DprojectArtifactId=jwt \
    -DclassName="dev.rpmhub.TokenSecuredResource" \
    -Dpath="/secured" \
    -Dextensions="resteasy,resteasy-jackson,smallrye-jwt,smallrye-jwt-build"
cd jwt
```

## Gerando chaves públicas e privadas com OpenSSL

```sh
# Para criar uma chave privada
openssl genrsa -out rsaPrivateKey.pem 2048

# Converter a chave privada para o formato PKCS#8
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem

# Para criar uma chave pública
openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
```

🚨 Uma observação, atualmente o JWT suporta chaves no formato:

* Public Key Cryptography Standards #8 (PKCS#8) PEM
* JSON Web Key (JWK)
* JSON Web Key Set (JWKS)
* JSON Web Key (JWK) Base64 URL encoded
* JSON Web Key Set (JWKS) Base64 URL encoded

Depois de gerar as chaves, devemos indicar a chave privada por meio da propriedade `smallrye.jwt.sign.key.location` no arquivo de `application.properties`, veja o exemplo abaixo:

```sh
    smallrye.jwt.sign.key.location=privateKey.pem
```

## Gerando um JSON Web Token (JWT)

Um JWT nada mais é que uma string codificada que possui 3 partes separadas por um ponto (.): cabeçalho, declarações (*claims*) e assinatura JWT. Para gerar um token podemos utiliza a classe `io.smallrye.jwt.build.Jwt`, veja um exemplo:

```java
@GET
@Path("/jwt")
@PermitAll
@Produces(MediaType.TEXT_PLAIN)
public String generate(@Context SecurityContext ctx) {
    return Jwt.issuer("http://localhost:8080")
            .upn("rodrigo@rpmhub.dev")
            .groups(new HashSet<>(Arrays.asList("User", "Admin")))
            .claim(Claims.full_name, "Rodrigo Prestes Machado")
            .sign();
}
```

No exemplo acima o token é construído por meio do método `issuer`, o assunto ou usuário (`upn`), os papeis do usuário (`groups`) e um conjunto de propriedades específicas da aplicação (*Claim*). Note, o método `sign` é utilizado no final da criação do token para assinar (chave privada) e efetivamente construir o token.

🚨 Note que o método do exemplo utiliza a anotação `@PermitAll` para permitir um acesso livre ao método.

## Configurando o Acesso

Para restringir o acesso a um método devemos utilizar a anotação `@RolesAllowed`. Logo, temos que informar quais são as *roles* que poderão acessar aquele método, observe o exemplo abaixo:

```java
/* Recuperando uma informação do token */
@Inject
@Claim(standard = Claims.full_name)
String fullName;

@GET
@Path("/sum/{a}/{b}")
@RolesAllowed({ "User" })
@Produces(MediaType.TEXT_PLAIN)
public long sum(@Context SecurityContext ctx, @PathParam("a") long a, @PathParam("b") long b) {
    return a + b;
}
```

No exemplo, podemos também observar que as informações contidas no token podem ser recuperadas por intermédio da anotação `@Claim`. Apesar do exemplo não mostrar, também é possível injetar o token diretamente por meio de um objeto da classe `org.eclipse.microprofile.jwt.JsonWebToken` que, por sua vez, possui métodos para você recuperar informações sobre o token, como por exemplo, o usuário:  `token.getName()`. Para saber mais, por favor acesse: [Using the JsonWebToken and Claim Injection](https://quarkus.io/guides/security-jwt#using-the-jsonwebtoken-and-claim-injection)

## Validando um token

Quando um serviço deseja validar um token, ele deve saber quem é o emissor (*issuer*) do JWT. Assim, no Quarkus/Microprofile devemos que adicionar nos serviços que recebem os tokens duas configurações no arquivo `application.properties`: (1) `mp.jwt.verify.issuer` - que indica a url do emissor do token e (2) `mp.jwt.verify.publickey.location` - que indica a chave pública, veja o exemplo abaixo:

```sh
    mp.jwt.verify.issuer=http://localhost:8080
    mp.jwt.verify.publickey.location=publicKey.pem
```

🚨 Uma observação importante, no caso de desenvolvimento de um serviço nativo ([GraalVM](https://www.graalvm.org)) a propriedade `mp.jwt.verify.publickey.location` deve ser substituída por `quarkus.native.resources.includes=publicKey.pem`.

# Propagação de JSON Web Token

Em uma arquitetura de micro serviços, é bastante comum que necessitemos propagar os tokens entre os serviços, assim, para transmitir tokens de maneira automática, devemos primeiro importar a extensão `quarkus-oidc-token-propagation`. Logo, devemos anotar o Rest Client com `@AccessToken`, pois, isto irá permitir que os Rest Clients reencaminhe os tokens recebidos de um serviço para o outro.

## Exemplo de código 💡

O código do exemplo abaixo, apresenta uma arquitetura de micro serviços para suportar um _front-end_, normalmente chamada de _Back-end for Front-end_(BFF). O diagrama de componentes da Figura 1 ilustra os serviços e suas relações.

<center>
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/jwt.puml" alt="Diagrama de classes" width="60%" height="60%"/> <br/>
    Figura 1 - Back-end for Front-end (BFF)
</center>

O JWT nesse exemplo é utilizado para proteger os métodos dos serviços "BFF" e "Backend". Desta maneira, é necessário se obter um token por meio do serviço de "usuários" para depois conseguir acessar os demais serviços. Para baixar o código desse pequeno exemplo utilize os seguintes comandos:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
cd pw2/exemplos/bff
```

# Referências 📚

* Usando JWT RBAC. Disponível em: [https://quarkus.io/guides/security-jwt](https://quarkus.io/guides/security-jwt)

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>