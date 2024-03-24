---
layout: default
title: JSON Web Token
nav_order: 6
---

# JSON Web Token 🔑

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/jwt/slides/index.html#/"
        title="JSON Web Token" width="90%" height="500" style="border:none;">
    </iframe>
</center>

Um JSON Web Token (JWT) é um [padrão](https://datatracker.ietf.org/doc/html/rfc7519)
para a criação de tokens, sequências de caracteres normalmente criptografadas,
capazes de transportar dados no formato JSON. A principal utilização desse
padrão se da na geração de tokens para controlar o acesso aos métodos de
serviços. Do ponto de vista prático, um JWT é uma String codificada que possui
três trechos separados por um ponto (.): cabeçalho, carga (_payload_) de
declarações (*claims*) e assinatura do JWT.

O cabeçalho normalmente contém duas informações, o tipo do token (nesse caso
JWT) e o algoritmo de assinatura que está sendo utilizado, como por exemplo,
[HMAC](https://pt.wikipedia.org/wiki/HMAC), [SHA256](https://pt.wikipedia.org/wiki/SHA-2)
ou [RSA](https://pt.wikipedia.org/wiki/RSA_(sistema_criptográfico)). A carga
(_payload_) de declarações (*claims*) é a segunda parte de um token. As
declarações são dados específicos do sistema em questão, como por exemplo,
declarações sobre um usuário, nome, e-mail, papel (_role_), entre outros.
Finalmente, a assinatura se constitui como a terceira parte de um JWT, trata-se
 da concatenação de hashes gerados a partir do cabeçalho e da carga com o
 objetivo de garantir a integridade do token.

💡 Para saber mais e também conseguir visualizar as três partes de um JWT de
forma prática visite o site [jwt.io](https://jwt.io/#debugger-io) e assista ao
[vídeo](https://www.youtube.com/watch?v=_XbXkVdoG_0). Além disso, existe um
segundo [vídeo](https://www.youtube.com/watch?v=soGRyl9ztjI) que compara, por
meio de analogias, os métodos de autenticação por sessão e token (se necessitar,
 coloque as legendas em português e assista aos vídeos pausadamente).

## Como funciona? 🤔

A [Figura 1](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/funcionamento.puml) ilustra o funcionamento básico de um JWT. Inicialmente, o
cliente envia suas credenciais para o servidor, que por sua vez, verifica as
credenciais e gera um JWT. O JWT é enviado de volta ao cliente, que o envia em
cada requisição subsequente. O servidor verifica a assinatura do JWT para
autorizar as solicitações.

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/funcionamento.puml">
        <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/funcionamento.puml" alt="Funcionamento do JWT" width="60%" height="60%"/>
    </a>
    <br/>
    Figura 1 - Funcionamento básico de um JWT.
</center>

## Por que utilizar JWT? 🤔

- **Segurança**: o JWT é um padrão seguro e amplamente utilizado para
autenticação e autorização de usuários.

- **Escalabilidade**: o JWT é um padrão leve e eficiente, que pode ser
facilmente integrado em qualquer aplicação.

- **Interoperabilidade**: o JWT é um padrão aberto e amplamente suportado
por diversas linguagens de programação e _frameworks_.

- **Flexibilidade**: o JWT permite a inclusão de informações adicionais no
token, como por exemplo, o nome do usuário, o papel (_role_) e outras
informações específicas da aplicação.

## Formato de um JWT 📝

Um JWT é uma String codificada que possui três partes separadas por um ponto
(.): cabeçalho, carga (_payload_) de declarações (*claims*) e assinatura do JWT.

Um JWT tem o seguinte formato:

```
    xxxxx.yyyyy.zzzzz
```

* **Cabeçalho**: contém duas informações, o tipo do token (nesse caso JWT) e o
algoritmo de assinatura que está sendo utilizado.

* **Carga (_payload_) de declarações (*claims*)**: contém informações específicas
do sistema em questão, como por exemplo, declarações sobre um usuário, nome,
e-mail, papel (_role_), entre outros.

* **Assinatura**: é a concatenação de hashes gerados a partir do cabeçalho e da
carga com o objetivo de garantir a integridade do token.


## Como implementar no Quarkus? 🤓

Para criar um serviço no Quarkus com suporte ao JWT necessitamos de duas
extensões `smallrye-jwt` e `smallrye-jwt-build`, por exemplo:

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:2.5.1.Final:create \
    -DprojectGroupId=dev.rpmhub \
    -DprojectArtifactId=jwt \
    -DclassName="dev.rpmhub.TokenSecuredResource" \
    -Dpath="/secured" \
    -Dextensions="resteasy,resteasy-jackson,smallrye-jwt,smallrye-jwt-build"
```

* `smallrye-jwt`: fornece suporte para a validação de tokens JWT.
* `smallrye-jwt-build`: fornece suporte para a construção de tokens JWT.

## Chaves públicas e privadas 🔐

Inicialmente é necessário gerar um par de chaves pública e privada para poder
assinar e validar os tokens. Para isso, podemos utilizar o [OpenSSL](https://www.openssl.org),
que forneces um conjunto de ferramentas de código aberto para criptografia.
No caso do JWT, a assinatura é feita por meio de chaves assimétricas, ou seja,
a chave privada é utilizada para assinar o token e a chave pública é utilizada
para validar a assinatura.

💡 Veja o [vídeo](https://www.youtube.com/watch?v=AQDCe585Lnc) para entender
mais sobre criptografia assimétrica.

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

Depois de gerar as chaves, devemos indicar a chave privada por meio da
propriedade `smallrye.jwt.sign.key.location` no arquivo de
`application.properties`, veja o exemplo abaixo:

```sh
    smallrye.jwt.sign.key.location=privateKey.pem
```

## Gerando um JSON Web Token (JWT) 🏭

Depois de criarmos e configurarmos as chaves, podemos escrever um código para
gerar um JWT. Como visto anteriormente, um JWT nada mais é que uma String
codificada que possui três: cabeçalho,  carga (_payload_) de declarações
(*claims*) e assinatura. Para gerar e assinar um token podemos utiliza a classe
`io.smallrye.jwt.build.Jwt`, veja um exemplo:

```java
@POST
@Path("/getJwt")
@PermitAll
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
public String generate(final String fullName) {
    return Jwt.issuer("http://localhost:8080")
        .upn("rodrigo@rpmhub.dev")
        .groups(new HashSet<>(Arrays.asList("User", "Admin")))
        .claim(Claims.full_name, fullName)
        .sign();
}
```

No exemplo acima o token é construído por meio do método `issuer`, o assunto ou
usuário (`upn`), os papeis do usuário (`groups`) e um conjunto de propriedades
específicas da aplicação (*Claim*). Note, o método `sign` é utilizado no final
da criação do token para assinar
e efetivamente construir o token.

🚨 Note que o método do exemplo acima utiliza a anotação `@PermitAll` para
liberar o acesso ao método.

## Restringindo o Acesso 🚪

Para restringir o acesso a um método devemos utilizar a anotação
`@RolesAllowed`. Logo, temos que informar quais são as *roles* que poderão
acessar aquele método, observe o exemplo abaixo:

```java
@Inject
@RestClient
IPayment paymentService;

@POST
@Path("/buy")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("User")
public Invoice buy(@FormParam("cardNumber") String cardNumber,
                        @FormParam("value") String value){
    logger.info("Confirms the payment");

    return paymentService.pay(cardNumber, value);
}
```

No exemplo, podemos também observar que as informações contidas no token podem
ser recuperadas por intermédio da anotação `@Claim`. Além disso, o método `buy`
foi decorado com a anotação `@RolesAllowed({ "User" })`, assim, o método está
estrito para requisições que encaminhem tokens que contenham o papel "User".
Apesar do exemplo não mostrar, também é possível injetar o token diretamente
por meio de um objeto da classe `org.eclipse.microprofile.jwt.JsonWebToken` que,
por sua vez, possui métodos para você recuperar informações sobre o token,
como por exemplo, o nome de um usuário: `token.getName()`.

💡 Para saber mais sobre recuperação de informações de um JWT acesse:
[Using the JsonWebToken and Claim Injection](https://quarkus.io/guides/security-jwt#using-the-jsonwebtoken-and-claim-injection)

## Validando um JWT

Quando um serviço deseja validar um token, ele deve saber quem é o emissor
(*Issuer*) do JWT. Assim, no Quarkus/Microprofile devemos que adicionar nos
serviços que recebem os tokens duas configurações no arquivo
`application.properties`: (1) `mp.jwt.verify.issuer` - que indica a url do
emissor do token e (2) `mp.jwt.verify.publickey.location` - que indica a chave
pública, veja o exemplo abaixo:

```sh
    mp.jwt.verify.issuer=http://localhost:8080
    mp.jwt.verify.publickey.location=publicKey.pem
```

🚨 Uma observação importante, no caso de desenvolvimento de um serviço nativo
([GraalVM](https://www.graalvm.org)) a propriedade
`mp.jwt.verify.publickey.location` deve ser substituída por
`quarkus.native.resources.includes=publicKey.pem`.

## Sign e Encrypt

Quando o _payload_ (_claims_) possuir dados sensíveis, como por exemplo, um
número de cartão de crédito, é recomendável criptografar o JWT. Neste caso, o
JWT pode assinado e criptografado, o que garante a integridade e a
confidencialidade, por meio dos métodos `innerSign()` e `encrypt()`. O método
`innerSign()` é utilizado para assinar o token e o método `encrypt()` é
usado para criptografar o token. Observem o exemplo abaixo:

```java
@POST
@Path("/getJwt")
@PermitAll
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
public String generate(final String fullName) {
    return Jwt.issuer("http://localhost:8080")
        .upn("rodrigo@rpmhub.dev")
        .groups(new HashSet<>(Arrays.asList("User", "Admin")))
        .claim(Claims.full_name, fullName)
        .innerSign()
        .encrypt();
}

```

Para gerar um JWT com esses métodos `innerSign()` e `encrypt()` se faz
necessário configurar o Quarkus com as seguintes propriedades:

    smallrye.jwt.sign.key.location=privateKey.pem
    smallrye.jwt.encrypt.key.location=publicKey.pem

Por outro lado, para poder validar o JWT e também descriptografar:

    mp.jwt.verify.publickey.location=publicKey.pem
    mp.jwt.decrypt.key.location=privateKey.pem

No momento que você configura o Quarkus com essas propriedades, o JWT é gerado
com a assinatura e criptografia. Por outro lado, o Quarkus, por meio das
propriedades `mp.jwt.verify.publickey.location` e `mp.jwt.decrypt.key.location`,
consegue validar e descriptografar o token.

🚨 Para saber mais detalhes, sobre esse processo de assinatura e criptografia,
por favor acesse: [https://smallrye.io/docs/smallrye-jwt/generate-jwt.html](https://smallrye.io/docs/smallrye-jwt/generate-jwt.html)

## Propagação de JWT 🔌

Em uma arquitetura de micro serviços, é bastante comum que necessitemos propagar
os tokens entre os serviços de maneira automática. Para fazermos isso no Quarkus
inicialmente temos que adicionar a extensão `quarkus-oidc-token-propagation` no
arquivo `pom.xml`. Em seguida, devemos anotar o Rest Client com `@AccessToken`,
pois, isto irá permitir que os Rest Clients reencaminhe os tokens recebidos de
um serviço para o outro. Veja o exemplo abaixo:

```java
@RegisterRestClient(baseUri = "https://localhost:8445/payment")
@AccessToken
public interface IPayment {
```

# Hyper Text Transfer Protocol Secure (HTTPS)

Um dos problemas do JWT é que o token pode ser capturado, nesse caso, se faz
necessário utilizar _Hyper Text Transfer Protocol Secure_ (HTTPS) para fazer
com que o JWT trafegue sempre numa conexão criptografada. Assim, pare gerar uma
chave privada e um certificado utilize o comando:

```sh
    keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 365 -keysize 2048
```

🚨 Nota, o formato keystore.jks armazena tanto o certificado quanto a sua chave
privada.

Para informar o caminho do arquivo keystore.jks adicione a seguinte propriedades
 do arquivo `application.properties` do Quarkus:

```
    quarkus.http.ssl.certificate.key-store-file=keystore.jks
    quarkus.http.ssl-port=8443 // porta padrão para HTTPS
```

🚨 Nota, quando você estiver utilizando Rest Client se faz necessário utilizar a
propriedade `quarkus.tls.trust-all` para que o cliente confie em certificados
não homologados por uma unidade certificadora. Assim, adicione a seguinte linha
no arquivo de properties do serviço que utiliza um Rest Client:

```
    quarkus.tls.trust-all=true
```

## Exemplo de código 🖥️

O código do exemplo abaixo, ilustra um trecho de uma arquitetura de micro
serviços para um sistema de comércio eletrônico. Nesse caso, temos um serviço
de "_Users_", que é responsável por gerar um token JWT, e dois serviços,
"_Chekout_" e "_Payment_", que são protegidos por esse token. Como exemplo,
o diagrama de componentes da [Figura 2](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/jwt.puml) ilustra os serviços e suas relações.

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/jwt.puml">
        <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/jwt.puml" alt="Exemplo de arquitetura de micro serviços" width="40%" height="40%"/>
    </a>
    <br/>
    Figura 2 - Exemplo de arquitetura de micro serviços.
</center>

O JWT do exemplo é utilizado para proteger os métodos dos serviços dos serviços
"Checkout" e "Payment". Desta maneira, é necessário se obter um token por meio
do serviço de "Users" para depois conseguir acessar os demais serviços.
Para baixar o código desse exemplo utilize os seguintes comandos:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
cd pw2/exemplos/store
```

## Referências 📚

* Usando JWT RBAC. Disponível em: [https://quarkus.io/guides/security-jwt](https://quarkus.io/guides/security-jwt)

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>