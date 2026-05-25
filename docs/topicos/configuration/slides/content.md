<!-- .slide: data-background-opacity="0.3" data-background-image="img/title.jpg" data-transition="convex" -->
# Configuration
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Introdução
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Quarkus é um framework Java projetado para aplicativos nativos da nuvem.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- Como esses aplicativos são executados em contêineres, a configuração é
  essencial para parametrizar o serviço sem alterar o código.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- O Quarkus suporta configuração baseada em propriedades e injeção de
  configuração via CDI.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Objetivos
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Declarar propriedades em `application.properties`
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- Injetar propriedades com `@ConfigProperty`
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- Usar perfis (`dev`, `test`, `prod`) e sobrescrever valores em runtime
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- Trabalhar com arquivos `.env` e YAML
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## MicroProfile Config
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- O Quarkus implementa a especificação
  [MicroProfile Config](https://github.com/eclipse/microprofile-config),
  através do [SmallRye Config](https://github.com/smallrye/smallrye-config).
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- As configurações podem vir de várias **fontes**: arquivo de propriedades,
  variáveis de ambiente, parâmetros `-D`, arquivos `.env`, YAML, entre outros.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Onde ficam as configurações?
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Caminho padrão: `src/main/resources/application.properties`
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

- Variáveis de ambiente: `PW2_MESSAGE` ↔ `pw2.message`
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

- Parâmetros `-D` na linha de comando do Maven ou da JVM
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

- Arquivo `.env` na raiz do projeto
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

- Arquivo YAML (`application.yaml` ou `application.yml`)
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Propriedades comuns
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- `quarkus.http.port`: Define a porta HTTP do servidor.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- `quarkus.datasource.jdbc.url`: URL da fonte de dados para integração com
  banco de dados.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- `quarkus.log.console.enable`: Ativa ou desativa a saída de log no console.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Injeção de Configuração
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- O Quarkus suporta injeção de configuração em beans CDI (_Contexts and
  Dependency Injection_).
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- Use a anotação `@ConfigProperty` para injetar propriedades configuradas.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## Exemplo com `@ConfigProperty`
<!-- .element: style="margin-bottom:40px; font-size: 50px; color:black; font-family: Marker Felt;" -->

```properties
pw2.message=hello
pw2.name=world
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```java
@Path("/hello")
@RequestScoped
public class HelloResource {

    @Inject
    @ConfigProperty(name = "pw2.message", defaultValue = "")
    String message;

    @Inject
    @ConfigProperty(name = "pw2.name")
    Optional<String> name;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return message + " " + name.orElse("world");
    }
}
```
<!-- .element: style="margin-bottom:30px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Quando a propriedade não existe
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Sem valor e sem `defaultValue` → falha na inicialização
  (`DeploymentException`).
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- Com `defaultValue = "..."` → usa o padrão informado.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- Tipo `Optional<T>` → injeta `Optional.empty()` se ausente.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## Acesso programático
<!-- .element: style="margin-bottom:40px; font-size: 50px; color:black; font-family: Marker Felt;" -->

- Use `ConfigProvider` quando o código não é um bean CDI ou quando a chave é
  dinâmica.
<!-- .element: style="margin-bottom:30px; font-size: 22px; color:black" -->

```java
String databaseName = ConfigProvider.getConfig()
    .getValue("database.name", String.class);

Optional<String> maybeDatabaseName = ConfigProvider.getConfig()
    .getOptionalValue("database.name", String.class);
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Sobrescrevendo em runtime
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Parâmetro `-D` na execução do serviço:
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

```sh
./mvnw compile quarkus:dev -Dpw2.message=hello
```
<!-- .element: style="margin-bottom:40px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

- Variáveis de ambiente seguem a mesma lógica:
  `PW2_MESSAGE=hello` sobrescreve `pw2.message`.
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Namespace `quarkus.`
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- 🚨 O Quarkus reserva o namespace `quarkus.` para sua própria configuração.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- Ex.: `quarkus.http.port` define a porta HTTP.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- **Nunca** use o prefixo `quarkus.` nas suas variáveis de aplicação.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Configurações de build
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Algumas configurações do Quarkus só têm efeito durante o *build*.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- Estão disponíveis em runtime, mas como **somente leitura**.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- Marcadas com 🔒 na
  [lista oficial](https://quarkus.io/guides/all-config) — alterá-las exige
  reconstruir o serviço (*rebuild*).
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Profiles
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Permitem configurações distintas para cada ambiente.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- Quarkus oferece três por padrão: `dev`, `test` e `prod`.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

- Duas formas de organizar:
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

  - Prefixo `%perfil` no mesmo `application.properties`.
  <!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

  - Arquivos separados: `application-dev.properties`,
    `application-prod.properties`.
  <!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## Profiles — Exemplo
<!-- .element: style="margin-bottom:40px; font-size: 50px; color:black; font-family: Marker Felt;" -->

```properties
pw2.jdbc=jdbc:mysql://localhost:3306/pw2
%prod.pw2.jdbc=jdbc:mysql://rpmhub.dev:3307/pw2
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

- Linhas sem prefixo valem para todos os perfis.
<!-- .element: style="margin-bottom:30px; font-size: 22px; color:black" -->

- `%prod.pw2.jdbc` sobrescreve o valor padrão quando o perfil ativo é `prod`.
<!-- .element: style="margin-bottom:30px; font-size: 22px; color:black" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## Profile customizado
<!-- .element: style="margin-bottom:40px; font-size: 50px; color:black; font-family: Marker Felt;" -->

```properties
quarkus.http.port=9090
%build.quarkus.http.port=9999
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

- Ative o perfil informando `quarkus.profile`:
<!-- .element: style="margin-bottom:30px; font-size: 22px; color:black" -->

```sh
./mvnw compile quarkus:dev -Dquarkus.profile=build
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Arquivos `.env`
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Útil para **segredos e ajustes locais** (senhas, tokens, URLs de DEV).
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

- Carregado automaticamente em `quarkus:dev`.
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

- Tem **prioridade** sobre o `application.properties` em runtime.
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

- Fica na **raiz** do projeto (mesmo nível do `pom.xml`) — adicione ao
  `.gitignore`.
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## `.env` ↔ `application.properties`
<!-- .element: style="margin-bottom:40px; font-size: 45px; color:black; font-family: Marker Felt;" -->

`application.properties`
<!-- .element: style="margin-bottom:10px; font-size: 20px; color:black" -->

```properties
pw2.message=hello
pw2.jdbc.url=jdbc:mysql://localhost:3306/pw2
pw2.jdbc.password=${DB_PASSWORD:changeme}
quarkus.http.port=8080
```
<!-- .element: style="margin-bottom:25px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->

`.env` (raiz do projeto)
<!-- .element: style="margin-bottom:10px; font-size: 20px; color:black" -->

```properties
PW2_MESSAGE=hello from .env
PW2_JDBC_URL=jdbc:mysql://localhost:3307/pw2
DB_PASSWORD=secret
QUARKUS_HTTP_PORT=9090
```
<!-- .element: style="margin-bottom:25px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->

Pontos viram underscores; tudo em maiúsculas.
<!-- .element: style="margin-bottom:20px; font-size: 20px; color:black" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## Expressões de propriedade
<!-- .element: style="margin-bottom:40px; font-size: 50px; color:black; font-family: Marker Felt;" -->

- Você pode referenciar variáveis no próprio `application.properties`:
<!-- .element: style="margin-bottom:30px; font-size: 22px; color:black" -->

```properties
pw2.jdbc.url=${PW2_JDBC_URL:jdbc:mysql://localhost:3306/pw2}
application.host=${HOST:localhost}
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

- Sintaxe `${NOME:valor-padrao}` — usa o valor padrão se `NOME` não existir.
<!-- .element: style="margin-bottom:30px; font-size: 22px; color:black" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Arquivos YAML
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Instale a extensão `quarkus-config-yaml`:
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-config-yaml</artifactId>
</dependency>
```
<!-- .element: style="margin-bottom:40px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

- Crie `application.yaml` ou `application.yml` em `src/main/resources`.
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## `properties` ↔ `yaml`
<!-- .element: style="margin-bottom:40px; font-size: 50px; color:black; font-family: Marker Felt;" -->

`application.properties`
<!-- .element: style="margin-bottom:10px; font-size: 20px; color:black" -->

```properties
pw2.message=hello
pw2.name=world
```
<!-- .element: style="margin-bottom:25px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

`application.yml`
<!-- .element: style="margin-bottom:10px; font-size: 20px; color:black" -->

```yaml
pw2:
  message: hello
  name: world
```
<!-- .element: style="margin-bottom:25px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Exercício 🏋️
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Transforme a configuração do serviço `users` do projeto Books em YAML.
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

1. Abra `exemplos/books/users`.
<!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->

2. Adicione `quarkus-config-yaml` no `pom.xml`.
<!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->

3. Crie `src/main/resources/application.yml` com as propriedades equivalentes.
<!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->

4. Esvazie o `application.properties` (mantenha apenas um formato).
<!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->

5. Valide com `./mvnw quarkus:dev`.
<!-- .element: style="margin-bottom:20px; font-size: 22px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Referências
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

* Configuration Reference Guide. Disponível em: [https://quarkus.io/guides/config-reference](https://quarkus.io/guides/config-reference)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

* SmallRye Config. Disponível em: [https://github.com/smallrye/smallrye-config](https://github.com/smallrye/smallrye-config)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
