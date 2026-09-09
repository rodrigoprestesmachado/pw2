---
layout: default
title: JSON Web Token
parent: Micro Serviços Básico
nav_order: 7
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

🎫 **Uma analogia simples**: pense no JWT como a pulseira de identificação de
um festival de música. Na entrada, você mostra seu ingresso (as credenciais,
usuário e senha) e recebe uma pulseira (o token). A partir daí, você não
precisa mostrar o ingresso novamente: basta exibir a pulseira em cada área do
festival (cada requisição). Os seguranças (o servidor) não guardam quem é
você, eles apenas verificam se a pulseira é autêntica (a assinatura) e o que
ela permite acessar (as *claims*, por exemplo, área VIP ou pista comum).
{: .fs-3 }

## Como funciona? 🤔

A [Figura 1](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/funcionamento.puml) ilustra o funcionamento básico de um JWT. Inicialmente, o
cliente envia suas credenciais para o servidor, que por sua vez, verifica as
credenciais e gera um JWT. O JWT é enviado de volta ao cliente, que o envia em
cada requisição subsequente. O servidor verifica a assinatura do JWT para
autorizar as solicitações.

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/funcionamento.puml">
        <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/funcionamento.puml" alt="Funcionamento do JWT" width="70%" height="70%"/>
    </a>
    <br/>
    Figura 1 - Funcionamento básico de um JWT.
</center>

Em resumo, o fluxo pode ser dividido em 4 passos simples:
{: .fs-3 }

1. **Login**: o cliente envia usuário e senha para um serviço de autenticação.
2. **Emissão**: o servidor valida as credenciais e gera (assina) um JWT.
3. **Uso**: o cliente guarda o token e o envia no *header* `Authorization` de
   cada requisição seguinte (`Authorization: Bearer <token>`).
4. **Verificação**: cada serviço que recebe a requisição valida a assinatura
   do token (sem precisar consultar um banco de dados ou "telefonar" para o
   serviço de autenticação) e decide se autoriza ou não o acesso.
{: .fs-3 }

💡 É justamente por não precisar consultar nada externo para validar o token
(basta verificar a assinatura com a chave pública) que o JWT é considerado
leve e escalável: qualquer serviço pode validar o token de forma independente.
{: .fs-3 }

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

Uma dica para visualizar as três partes de um JWT de forma prática é visitar o
site [jwt.io](https://jwt.io/#debugger-io).


## Como implementar no Quarkus? 🤓

Para criarmos serviços no Quarkus com suporte ao JWT necessitamos de duas
extensões `smallrye-jwt` e `smallrye-jwt-build`.

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

Se você tiver dificuldades para gerar as chaves, por favor, baixe as chaves de
exemplo: [privateKey.pem](https://github.com/rodrigoprestesmachado/pw2/blob/dev/exemplos/store/users/src/main/resources/privateKey.pem) e
[publicKey.pem](https://github.com/rodrigoprestesmachado/pw2/blob/dev/exemplos/store/users/src/main/resources/publicKey.pem).

Atualmente o JWT suporta chaves no formato:

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
public String generate(final String fullName, final String email) {
    return Jwt.issuer(ISSUER)
            .upn(email)
            .groups(new HashSet<>(Arrays.asList("User", "Admin")))
            .claim(Claims.full_name, fullName)
            .sign();
}
```

No exemplo acima os tokens são construídos por meio do método `issuer`, a
identificação do usuário ou _User Principal Name_ (`upn`), os papeis/grupos do
usuário (`groups`) e um conjunto de propriedades específicas da aplicação
(*Claim*). Note, o método `sign` é utilizado no final da criação do token para
assinar e efetivamente construir o token.

🚨 Note que o método do exemplo acima utiliza a anotação `@PermitAll` para
liberar o acesso ao método.

## Restringindo o Acesso 🚪

Para restringir o acesso a um método devemos utilizar a anotação
`@RolesAllowed`. Logo, temos que informar quais são as *roles* que poderão
acessar aquele método, observe o exemplo abaixo:

💡 O exemplo a seguir (e os das próximas seções) faz parte de um cenário
maior de comércio eletrônico com três serviços: "_Users_" (gera o JWT),
"_Checkout_" (recebe o pedido de compra) e "_Payment_" (processa o
pagamento). O `IPayment` abaixo é o **Rest Client** que o serviço
"_Checkout_" usa para consumir o serviço "_Payment_" (o mesmo padrão visto
na página [Rest Client](../rest-client/rest-client.html)). Esse cenário
completo, com o diagrama de componentes, é detalhado mais adiante na seção
[Exemplo de código](#exemplo-de-código-).
{: .fs-3 }

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
estrito para requisições que encaminhem tokens que contenham o papel "_User_".
Apesar do exemplo não mostrar, também é possível injetar o token diretamente
por meio de um objeto da classe `org.eclipse.microprofile.jwt.JsonWebToken` que,
por sua vez, possui métodos para você recuperar informações sobre o token,
como por exemplo, o nome de um usuário: `token.getName()`.

💡 Para saber mais sobre recuperação de informações de um JWT acesse:
[Using the JsonWebToken and Claim Injection](https://quarkus.io/guides/security-jwt#using-the-jsonwebtoken-and-claim-injection)

Para fixar as ideias, veja um resumo das principais anotações usadas para
proteger e ler informações de um método:
{: .fs-3 }

| Anotação | Onde é usada | Função |
|----------|--------------|--------|
| `@PermitAll` | Método | Libera o acesso a qualquer requisição, sem exigir token |
| `@RolesAllowed({"User"})` | Método | Restringe o acesso apenas a tokens que possuam o(s) papel(is)/grupo(s) informado(s) |
| `@Inject JsonWebToken token` | Atributo da classe | Injeta o token recebido para leitura de informações, por exemplo, `token.getName()` |
| `@Claim("full_name")` | Atributo/parâmetro | Injeta diretamente o valor de uma *claim* específica do token |
{: .fs-3 }

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

Até aqui, vimos apenas a **assinatura** (`sign()`) do JWT, que garante que o
token não foi alterado, mas não impede que qualquer pessoa leia o conteúdo do
*payload* (basta decodificar o token em Base64, como no [jwt.io](https://jwt.io/#debugger-io)).
Isso é aceitável na maioria dos casos, já que o *payload* normalmente contém
apenas informações não sensíveis, como nome de usuário e papéis.
{: .fs-3 }

💡 Pense assim: **assinar** é como lacrar um envelope transparente com um
selo. Qualquer um consegue ler o conteúdo, mas ninguém consegue alterá-lo sem
quebrar o selo. **Criptografar** é usar um envelope opaco: além de lacrado,
ninguém consegue ler o conteúdo sem a chave correta.
{: .fs-3 }

Quando o _payload_ (_claims_) possuir dados sensíveis, como por exemplo, um
número de cartão de crédito, é recomendável também **criptografar** o JWT
(além de assinar), o que garante tanto a integridade quanto a
confidencialidade do conteúdo. Para isso, utilizamos os métodos
`innerSign()` (assina o token) e `encrypt()` (criptografa o token
já assinado), em sequência. Observe o exemplo abaixo:

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

Imagine o cenário: um serviço "_Checkout_" recebe uma requisição já
autenticada (com um JWT válido no *header*) e, para completar a operação,
precisa chamar um serviço "_Payment_" usando um Rest Client (visto na
página anterior sobre [Rest Client](../rest-client/rest-client.html)). Nesse
caso, o serviço "_Payment_" também exige um token válido, então precisamos
**reencaminhar** o mesmo JWT recebido pelo "_Checkout_" na chamada feita ao
"_Payment_". É isso que chamamos de **propagação de JWT**.
{: .fs-3 }

Fazer isso manualmente (extrair o token da requisição recebida e adicioná-lo
manualmente no *header* da chamada ao Rest Client) seria repetitivo. Por
isso, no Quarkus, basta: (1) adicionar a extensão
`quarkus-rest-client-oidc-token-propagation` no arquivo `pom.xml` e (2)
anotar a interface do Rest Client com `@AccessToken`. Com isso, o próprio
*framework* reencaminha automaticamente o token recebido para o serviço
seguinte. Veja o exemplo abaixo:

```java
@RegisterRestClient(baseUri = "https://localhost:8445/payment")
@AccessToken
public interface IPayment {
```

🚨 Por padrão, a extensão quarkus-rest-client-oidc-token-propagation inicia um
container com o Keycloak se o Docker estiver disponível, pois assume que a
propagação de tokens ocorre via OIDC (OpenID Connect). No entanto, como não
estamos utilizando um serviço de openId oficial, devemos desabilitar o contêiner
do Keycloak. Para isso, basta adicionar a seguinte configuração no arquivo
application.properties:

```properties
quarkus.keycloak.devservices.enabled=false
```

## Hyper Text Transfer Protocol Secure (HTTPS)

Vimos na seção anterior que é possível criptografar o *payload* do JWT. Mas
existe um problema que a criptografia do token, por si só, não resolve: se a
conexão entre cliente e servidor não for segura, alguém "no meio do caminho"
(um ataque conhecido como *man in the middle*) pode **capturar** o token
inteiro (criptografado ou não) enquanto ele trafega pela rede e reutilizá-lo
para se passar pelo usuário original, sem nem precisar saber o que há
dentro do token.
{: .fs-3 }

Por isso, é fundamental utilizar _Hyper Text Transfer Protocol Secure_
(HTTPS) para que o JWT trafegue sempre numa conexão criptografada de
ponta a ponta, dificultando essa captura. Para gerar uma chave privada e um
certificado autoassinado (suficiente para desenvolvimento/estudo), utilize o
comando:

```sh
    keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 365 -keysize 2048
```

Se você tiver dificuldades para gerar o certificado, por favor, baixe o
[certificado](https://github.com/rodrigoprestesmachado/pw2/blob/dev/exemplos/store/users/src/main/resources/keystore.jks) de exemplo.

🚨 Nota, o formato keystore.jks armazena tanto o certificado quanto a sua chave
privada.

Para informar o caminho do arquivo keystore.jks adicione a seguinte propriedades
 do arquivo `application.properties` do Quarkus:

```properties
    quarkus.http.ssl-port=8443
    quarkus.http.ssl.certificate.key-store-file=keystore.jks
    quarkus.http.ssl.certificate.key-store-password=password
```

A propriedade `quarkus.http.ssl-port` é utilizada para informar a porta que o
serviço irá escutar as requisições HTTPS que por padrão é a porta 8443. Para
acessar o serviço utilize o endereço `https://localhost:8443`. A propriedade
`quarkus.http.ssl.certificate.key-store-file` é utilizada para informar o
caminho do arquivo keystore.jks e a propriedade
`quarkus.http.ssl.certificate.key-store-password` é utilizada para informar a
senha do arquivo keystore.jks, que foi informada no momento da criação do
certificado.

🚨 Nota, quando você estiver utilizando Rest Client se faz necessário utilizar a
propriedade `quarkus.tls.trust-all` para que o cliente confie em certificados
não homologados por uma unidade certificadora. Assim, adicione a seguinte linha
no arquivo de properties do serviço que utiliza um Rest Client:

```properties
    quarkus.tls.trust-all=true
```

## Exemplo de código 🖥️

O código do exemplo abaixo, ilustra um trecho de uma arquitetura de micro
serviços para um sistema de comércio eletrônico. Nesse caso, temos um serviço
de "_Users_", que é responsável por gerar um token JWT, e dois serviços,
"_Checkout_" e "_Payment_", que são protegidos por esse token. Como exemplo,
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

## Exercício de Fixação Prático 🏋️

Antes de partir para o exercício principal, resolva os três exercícios
simples abaixo. Eles são propositalmente pequenos e diretos, o objetivo é
ganhar confiança com a geração, proteção e leitura de um JWT antes de
combinar tudo em uma arquitetura maior.
{: .fs-3 }

**1) Gerando seu primeiro token.** Crie um endpoint `POST /token` que recebe
um nome de usuário (`String`, em texto simples) e retorna um JWT assinado
contendo esse nome como *claim* `upn`. Não é necessário validar nada ainda,
apenas gerar e retornar o token (use `@PermitAll`, como no exemplo da seção
[Gerando um JSON Web Token](#gerando-um-json-web-token-jwt-)). Para testar,
copie o token gerado e cole em [jwt.io](https://jwt.io/#debugger-io) para
visualizar as três partes (cabeçalho, *payload* e assinatura).
{: .fs-3 }

**2) Protegendo um endpoint.** Crie um endpoint `GET /hello` que só pode ser
acessado por quem enviar um token válido com o papel (*role*) `"User"`. Use
a anotação `@RolesAllowed("User")` (veja a seção [Restringindo o Acesso](#restringindo-o-acesso-)).
Teste duas vezes: uma sem enviar o token (deve falhar) e outra enviando, no
*header* `Authorization`, o token gerado no exercício anterior no formato
`Bearer <token>` (deve funcionar, desde que o token contenha o *role*
`"User"`).
{: .fs-3 }

**3) Lendo dados do token.** Modifique o endpoint `/hello` do exercício
anterior para retornar uma saudação personalizada, por exemplo
`"Olá, <nome>!"`, onde `<nome>` é obtido diretamente do token recebido. Para
isso, injete o token com `@Inject JsonWebToken token` e utilize
`token.getName()` (ou `@Claim` para uma *claim* específica) para recuperar a
informação, em vez de recebê-la novamente como parâmetro.
{: .fs-3 }

💡 Se os três exercícios acima funcionaram, você já domina o essencial de
JWT no Quarkus: gerar, proteger e ler um token. O exercício abaixo apenas
aplica essas mesmas ideias em uma arquitetura com múltiplos serviços.
{: .fs-3 }

---

Com base no exercício [anterior](https://pw2.rpmhub.dev/topicos/rest-client/rest-client.html#exercício-de-fixação-), sobre da arquitetura de micro serviços para
uma rede social de empréstimo de livros, adicione a segurança por meio de JWT.
Para isso, crie um serviço "_Users_" que seja responsável por gerar um tokens
JWT.

## Teste seus conhecimentos 🧠

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/jwt/questions.html"
        title="JSON Web Token" width="90%" height="500"
        style="border:none;background-color:white;">
    </iframe>
</center>

## Referências 📚

* Usando JWT RBAC. Disponível em: [https://quarkus.io/guides/security-jwt](https://quarkus.io/guides/security-jwt)

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

<center>
    <a
        href="https://rpmhub.dev"
        target="blanck"
    >
        <img
            src="../../imgs/logo.png"
            alt="Rodrigo Prestes Machado"
            width="3%" height="3%"
            border=0 style="border:0;
            text-decoration:none;
            outline:none"
        >
    </a>
    <br>
    <a
        rel="license"
        href="http://creativecommons.org/licenses/by/4.0/">
        CC BY 4.0 DEED
    </a>
</center>