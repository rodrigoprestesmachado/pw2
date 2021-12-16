# JSON Web Token

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
# Chave privada
openssl genrsa -out rsaPrivateKey.pem 2048

# Chave pública
openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem

# Convertendo a chave privada para o formato PKCS#8
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem
```

🚨 Uma observação, atualmente o JWT suporta chaves no formato:

* Public Key Cryptography Standards #8 (PKCS#8) PEM
* JSON Web Key (JWK)
* JSON Web Key Set (JWKS)
* JSON Web Key (JWK) Base64 URL encoded
* JSON Web Key Set (JWKS) Base64 URL encoded

Depois de gerar as chaves, indique a chave privada por meio da propriedade `smallrye.jwt.sign.key.location` e a chave pública com as propriedades `smallrye.jwt.sign.key.location`e  `quarkus.native.resources.includes` (código nativo), veja o exemplo abaixo:

```sh
    smallrye.jwt.sign.key.location=privateKey.pem
    mp.jwt.verify.publickey.location=publicKey.pem
    quarkus.native.resources.includes=publicKey.pem
```

## Configurando o Emissor do Token

O emissor (*issuer*) deve ser configurado para que, posteriormente, possa validar os tokens. No Quarkus/Microprofile podemos fazer isso com a propriedade `mp.jwt.verify.issuer` no `application.properties`, por exemplo:

    mp.jwt.verify.issuer=http://localhost:8080

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
            .groups(new HashSet<>(Arrays.asList("User")))
            .claim(Claims.birthdate.name(), "2001-07-13")
            .sign();
}
```

No exemplo acima o token é construido com indicando o `issuer`, o assunto ou usuário (`upn`), os papeis do usuário (`groups`) e um conjunto de propriedades específicas da aplicação (*Claim*). Note, o método `sign` é utilizado no final da criação do token para criptografar e efetivamente construir o token.

🚨 Note que o método do exemplo utiliza a anotação `@PermitAll` para permitir um acesso livre ao método.


## Configurando o Acesso

Para configurar o acesso a um método devemos utilizar a anotação `@RolesAllowed`. Logo, temos que informar quais são as *roles* que poderão acessar aquele método, veja o exemplo abaixo:

```java
@Inject
JsonWebToken token;

@GET
@Path("/sum/{a}/{b}")
@RolesAllowed({ "User" })
@Produces(MediaType.TEXT_PLAIN)
public long sum(@Context SecurityContext ctx, @PathParam("a") long a, @PathParam("b") long b) {
    return a + b;
}
```

No exemplo, podemos também observar que o token pode ser injetado por intermédio da classe `org.eclipse.microprofile.jwt.JsonWebToken`. Apesar do exemplo não mostrar, um objeto da classe `JsonWebToken` possui métodos para você recuperar informações sobre o token, como por exemplo, o usuário:  `token.getName()`.

# Propagação de JSON Web Token

Podemos utilizar a extensão `quarkus-oidc-token-propagation` para enviar de forma automática o token quando um Rest Cliente necessitar realizar uma chamada para um outro serviço. Esse tipo de situação se mostra bastante corriqueira quando um sistema é concebido numa arquitetura orientada a micro serviços.

Nesse caso, devemos anotar o Rest Client com `@AccessToken`. Isto permite que o Rest Client reencaminhe o token recebido para um próximo serviço.

## Código 💡

O código desse tutorial está disponível no Github:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
cd pw2/exemplos/jwt
```

# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* Usando JWT RBAC. Disponível em: [https://quarkus.io/guides/security-jwt#generating-a-jwt](https://quarkus.io/guides/security-jwt#generating-a-jwt)

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>