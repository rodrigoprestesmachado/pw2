<!-- .slide: data-background-opacity="0.3" data-background-image="img/title.jpg" data-transition="convex" -->
# Configuration 🛠️
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Por que configuração? 🤔
<!-- .element: style="margin-bottom:40px; font-size: 48px; color:white; font-family: Marker Felt;" -->

* O mesmo artefato roda em dev, teste e produção
<!-- .element: style="margin-bottom:35px; font-size: 26px; color:white" -->

* O que muda é a configuração injetada pelo ambiente, não o código
<!-- .element: style="margin-bottom:35px; font-size: 26px; color:white" -->

* Princípio do [Twelve-Factor App](https://12factor.net/pt_br/config)
<!-- .element: style="margin-bottom:35px; font-size: 26px; color:white" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## MicroProfile Config
<!-- .element: style="margin-bottom:40px; font-size: 48px; color:white; font-family: Marker Felt;" -->

* O Quarkus usa a especificação [MicroProfile Config](https://github.com/eclipse/microprofile-config)
<!-- .element: style="margin-bottom:35px; font-size: 26px; color:white" -->

* Implementação: [SmallRye Config](https://github.com/smallrye/smallrye-config)
<!-- .element: style="margin-bottom:35px; font-size: 26px; color:white" -->

* Fontes: `application.properties`, variáveis de ambiente, `-D`, `.env` e YAML
<!-- .element: style="margin-bottom:35px; font-size: 26px; color:white" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Kit de ferramentas 🧰
<!-- .element: style="margin-bottom:35px; font-size: 48px; color:white; font-family: Marker Felt;" -->

* 💉 `@ConfigProperty` — injeta a chave já convertida (String, int, boolean)
<!-- .element: style="margin-bottom:22px; font-size: 23px; color:white" -->

* 📁 `application.properties` — valores versionados com o código
<!-- .element: style="margin-bottom:22px; font-size: 23px; color:white" -->

* 🎭 Perfis `%dev`, `%test`, `%prod` — um valor para cada fase
<!-- .element: style="margin-bottom:22px; font-size: 23px; color:white" -->

* 🔐 `.env` e variáveis de ambiente — segredos e ajustes fora do Git
<!-- .element: style="margin-bottom:22px; font-size: 23px; color:white" -->

* 📄 YAML — a mesma configuração em árvore
<!-- .element: style="margin-bottom:22px; font-size: 23px; color:white" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Onde ficam as configurações? 📁
<!-- .element: style="margin-bottom:40px; font-size: 44px; color:white; font-family: Marker Felt;" -->

```text
src/main/resources/application.properties
```
<!-- .element: style="margin-bottom:25px; font-size: 22px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```properties
pw2.mensagem=Ola, bem-vindo ao mundo dos microsservicos!
pw2.autor=Rodrigo
```
<!-- .element: style="margin-bottom:20px; font-size: 20px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* Esse arquivo vai dentro do JAR
<!-- .element: style="margin-bottom:18px; font-size: 24px; color:white" -->



<!-- .slide: data-background="white" data-transition="convex" -->
## @ConfigProperty 💉
<!-- .element: style="margin-bottom:20px; font-size: 46px; color:black; font-family: Marker Felt;" -->

```java
@Inject
@ConfigProperty(name = "pw2.mensagem")
String mensagem;

@Inject
@ConfigProperty(name = "pw2.autor", defaultValue = "Anonimo")
String autor;

@Inject
@ConfigProperty(name = "pw2.versao")
Optional<String> versao;
```
<!-- .element: style="margin-bottom:16px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* Pacote: `org.eclipse.microprofile.config.inject.ConfigProperty`
<!-- .element: style="margin-bottom:12px; font-size: 20px; color:black" -->

* Com `@ConfigProperty` no campo, o `@Inject` é opcional no Quarkus
<!-- .element: style="margin-bottom:12px; font-size: 20px; color:black" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Se a chave não existe 💥
<!-- .element: style="margin-bottom:40px; font-size: 46px; color:white; font-family: Marker Felt;" -->

* Sem valor e sem `defaultValue` → a aplicação não sobe (`DeploymentException`)
<!-- .element: style="margin-bottom:35px; font-size: 24px; color:white" -->

* `defaultValue = "Anonimo"` → usa esse texto
<!-- .element: style="margin-bottom:35px; font-size: 24px; color:white" -->

* `Optional<T>` → injeta `Optional.empty()`
<!-- .element: style="margin-bottom:35px; font-size: 24px; color:white" -->



<!-- .slide: data-background="white" data-transition="convex" -->
## Leitura programática 🔍
<!-- .element: style="margin-bottom:30px; font-size: 46px; color:black; font-family: Marker Felt;" -->

```java
String dbHost = ConfigProvider.getConfig()
    .getValue("banco.host", String.class);

Optional<Integer> dbPorta = ConfigProvider.getConfig()
    .getOptionalValue("banco.porta", Integer.class);
```
<!-- .element: style="margin-bottom:20px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* `getValue` lança `NoSuchElementException` se a chave falta
<!-- .element: style="margin-bottom:16px; font-size: 22px; color:black" -->

* `getOptionalValue` devolve `Optional.empty()`
<!-- .element: style="margin-bottom:16px; font-size: 22px; color:black" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Quem vence? 🏆
<!-- .element: style="margin-bottom:28px; font-size: 48px; color:white; font-family: Marker Felt;" -->

* 400 — `-Dchave=valor`
<!-- .element: style="margin-bottom:16px; font-size: 24px; color:white" -->

* 300 — variável de ambiente (`PW2_MENSAGEM`)
<!-- .element: style="margin-bottom:16px; font-size: 24px; color:white" -->

* 295 — `.env` no diretório de trabalho
<!-- .element: style="margin-bottom:16px; font-size: 24px; color:white" -->

* 260 — `config/application.properties` no diretório de trabalho
<!-- .element: style="margin-bottom:16px; font-size: 24px; color:white" -->

* 250 — `src/main/resources/application.properties` (dentro do JAR)
<!-- .element: style="margin-bottom:16px; font-size: 24px; color:white" -->

* No mesmo arquivo, `%prod.chave` vale quando o perfil ativo é `prod`
<!-- .element: style="margin-bottom:14px; font-size: 22px; color:#ffdd99" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Sobrescrevendo em runtime ⚡
<!-- .element: style="margin-bottom:30px; font-size: 44px; color:white; font-family: Marker Felt;" -->

```sh
./mvnw quarkus:dev -Dpw2.mensagem="Ola direto da linha de comando"
```
<!-- .element: style="margin-bottom:18px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```sh
java -Dpw2.mensagem="Ola direto da linha de comando" -jar target/quarkus-app/quarkus-run.jar
```
<!-- .element: style="margin-bottom:18px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```sh
PW2_MENSAGEM="Ola via variavel de ambiente" ./mvnw quarkus:dev
```
<!-- .element: style="margin-bottom:16px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->



<!-- .slide: data-background="white" data-transition="convex" -->
## Perfis 🎭
<!-- .element: style="margin-bottom:25px; font-size: 48px; color:black; font-family: Marker Felt;" -->

* `dev` no `quarkus:dev`, `test` no `./mvnw test`, `prod` no `java -jar`
<!-- .element: style="margin-bottom:16px; font-size: 22px; color:black" -->

```properties
pw2.banco.url=jdbc:postgresql://localhost:5432/meubanco_dev
%prod.pw2.banco.url=jdbc:postgresql://db.minhaempresa.com:5432/meubanco_prod
%test.pw2.banco.url=jdbc:h2:mem:testdb
```
<!-- .element: style="margin-bottom:16px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```properties
quarkus.http.port=8080
%homologacao.quarkus.http.port=8090
```
<!-- .element: style="margin-bottom:14px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```sh
./mvnw quarkus:dev -Dquarkus.profile=homologacao
```
<!-- .element: style="margin-bottom:12px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Arquivo do perfil
<!-- .element: style="margin-bottom:30px; font-size: 46px; color:white; font-family: Marker Felt;" -->

* `application-homologacao.properties`, no mesmo diretório do `application.properties`
<!-- .element: style="margin-bottom:25px; font-size: 23px; color:white" -->

* As chaves desse arquivo ficam sem o prefixo `%homologacao.`
<!-- .element: style="margin-bottom:25px; font-size: 23px; color:white" -->

* Ele vence as chaves `%homologacao.` do arquivo principal
<!-- .element: style="margin-bottom:25px; font-size: 23px; color:white" -->

* O `application.properties` precisa existir, mesmo vazio
<!-- .element: style="margin-bottom:25px; font-size: 23px; color:white" -->

* Ative com `-Dquarkus.profile=homologacao` ou `QUARKUS_PROFILE=homologacao`
<!-- .element: style="margin-bottom:22px; font-size: 23px; color:#ffdd99" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Arquivo `.env` 🔐
<!-- .element: style="margin-bottom:30px; font-size: 48px; color:white; font-family: Marker Felt;" -->

* Fica na raiz do projeto, ao lado do `pom.xml`, e entra no `.gitignore`
<!-- .element: style="margin-bottom:22px; font-size: 23px; color:white" -->

* Lido do diretório de trabalho: no `quarkus:dev` e também no `java -jar`
<!-- .element: style="margin-bottom:22px; font-size: 23px; color:white" -->

* Ordinal 295: acima do `application.properties`, abaixo da variável de ambiente e do `-D`
<!-- .element: style="margin-bottom:22px; font-size: 23px; color:white" -->

```properties
PW2_MENSAGEM=Mensagem geral
_DEV_PW2_MENSAGEM=Mensagem apenas no perfil dev
QUARKUS_HTTP_PORT=9090
```
<!-- .element: style="margin-bottom:14px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->



<!-- .slide: data-background="white" data-transition="convex" -->
## Nomes no ambiente
<!-- .element: style="margin-bottom:28px; font-size: 46px; color:black; font-family: Marker Felt;" -->

Para `pw2.mensagem`, o Quarkus procura:
<!-- .element: style="margin-bottom:16px; font-size: 24px; color:black" -->

* `pw2.mensagem`
<!-- .element: style="margin-bottom:14px; font-size: 24px; color:black" -->

* `pw2_mensagem`
<!-- .element: style="margin-bottom:14px; font-size: 24px; color:black" -->

* `PW2_MENSAGEM`
<!-- .element: style="margin-bottom:18px; font-size: 24px; color:black" -->

* `quarkus.http.port` → `QUARKUS_HTTP_PORT`
<!-- .element: style="margin-bottom:12px; font-size: 22px; color:black" -->

* `app.boas-vindas` → `APP_BOAS_VINDAS`
<!-- .element: style="margin-bottom:12px; font-size: 22px; color:black" -->



<!-- .slide: data-background="white" data-transition="convex" -->
## Expressões `${chave:padrao}`
<!-- .element: style="margin-bottom:30px; font-size: 42px; color:black; font-family: Marker Felt;" -->

```properties
banco.senha=${DB_SENHA:senha_padrao_dev}
servico.externo.url=${URL_SERVICO:http://localhost:8081}
```
<!-- .element: style="margin-bottom:20px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* A busca da chave acontece em todas as fontes
<!-- .element: style="margin-bottom:16px; font-size: 22px; color:black" -->

* Sem a chave e sem o padrão depois dos dois-pontos, a leitura falha
<!-- .element: style="margin-bottom:16px; font-size: 22px; color:black" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Namespace `quarkus.` 🚨
<!-- .element: style="margin-bottom:35px; font-size: 46px; color:white; font-family: Marker Felt;" -->

```properties
quarkus.http.port=8082
quarkus.log.console.level=DEBUG
```
<!-- .element: style="margin-bottom:25px; font-size: 20px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* `quarkus.` configura o framework e as extensões
<!-- .element: style="margin-bottom:22px; font-size: 24px; color:white" -->

* Propriedade de negócio usa outro prefixo, como `pw2.` ou `app.`
<!-- .element: style="margin-bottom:22px; font-size: 24px; color:white" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Configuração de build 🔒
<!-- .element: style="margin-bottom:40px; font-size: 46px; color:white; font-family: Marker Felt;" -->

* O cadeado na [lista oficial](https://quarkus.io/guides/all-config) marca o que só vale no build
<!-- .element: style="margin-bottom:35px; font-size: 24px; color:white" -->

* Em execução o valor pode ser lido, e não muda o comportamento do framework
<!-- .element: style="margin-bottom:35px; font-size: 24px; color:white" -->

* Para passar a valer, o artefato é gerado de novo
<!-- .element: style="margin-bottom:35px; font-size: 24px; color:white" -->



<!-- .slide: data-background="white" data-transition="convex" -->
## YAML 📄
<!-- .element: style="margin-bottom:20px; font-size: 48px; color:black; font-family: Marker Felt;" -->

```sh
./mvnw quarkus:add-extension -Dextensions='quarkus-config-yaml'
```
<!-- .element: style="margin-bottom:14px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```yaml
pw2:
  servico:
    nome: Notificacoes
    timeout: 5000
quarkus:
  http:
    port: 8080
```
<!-- .element: style="margin-bottom:12px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* Arquivo: `application.yml` ou `application.yaml`, em `src/main/resources`
<!-- .element: style="margin-bottom:10px; font-size: 20px; color:black" -->

* Mesma posição do `application.properties` do mesmo diretório. Uma chave, um formato
<!-- .element: style="margin-bottom:10px; font-size: 20px; color:black" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Exercícios de fixação 🎯
<!-- .element: style="margin-bottom:28px; font-size: 44px; color:white; font-family: Marker Felt;" -->

* 🟢 **Ex. 1:** `app.boas-vindas` com `@ConfigProperty`
<!-- .element: style="margin-bottom:14px; font-size: 22px; color:white" -->

* 🛡️ **Ex. 2:** `defaultValue` e `Optional`
<!-- .element: style="margin-bottom:14px; font-size: 22px; color:white" -->

* ⚡ **Ex. 3:** sobrescrever com `-D`
<!-- .element: style="margin-bottom:14px; font-size: 22px; color:white" -->

* 🎭 **Ex. 4:** `%dev` e `%prod` em `app.ambiente`
<!-- .element: style="margin-bottom:14px; font-size: 22px; color:white" -->

* 🔐 **Ex. 5:** `.env` com `APP_BOAS_VINDAS` e `APP_CUPOM`
<!-- .element: style="margin-bottom:14px; font-size: 22px; color:white" -->

* 🏋️ Desafio: `exemplos/books/users` em `application.yml`
<!-- .element: style="margin-bottom:16px; font-size: 22px; color:#FFD580" -->

  [Exercícios na página](https://pw2.rpmhub.dev/topicos/configuration/configuration.html#colocando-tudo-em-prática-dominando-a-configuração-)
<!-- .element: style="margin-bottom:12px; font-size: 18px; color:#FFD580" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Referências 📚
<!-- .element: style="margin-bottom:40px; font-size: 48px; color:white; font-family: Marker Felt;" -->

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) O'Reilly Media, 2020.
<!-- .element: style="margin-bottom:28px; font-size: 22px; color:white" -->

* Configuração no Quarkus: [https://quarkus.io/guides/config-reference](https://quarkus.io/guides/config-reference)
<!-- .element: style="margin-bottom:28px; font-size: 22px; color:white" -->

* MicroProfile Config: [https://github.com/eclipse/microprofile-config](https://github.com/eclipse/microprofile-config)
<!-- .element: style="margin-bottom:28px; font-size: 22px; color:white" -->

* SmallRye Config: [https://github.com/smallrye/smallrye-config](https://github.com/smallrye/smallrye-config)
<!-- .element: style="margin-bottom:28px; font-size: 22px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
