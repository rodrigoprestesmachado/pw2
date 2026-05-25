---
layout: default
title: Configuration
parent: Micro Serviços Intermediário
nav_order: 8
---

# Configuration 🛠️

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/configuration/slides/index.html#/"
        title="Configuration"
        width="90%" height="500" style="border:none;">
    </iframe>
</center>

## Objetivos

Ao final deste tópico, você será capaz de:

- declarar propriedades em `application.properties`
- injetá-las no código com `@ConfigProperty`
- usar perfis (`dev`, `test`, `prod`) e sobrescrever valores em runtime
- migrar configuração para YAML

O Quarkus utiliza a especificação
[MicroProfile Config](https://github.com/eclipse/microprofile-config),
implementada com o
[SmallRye Config](https://github.com/smallrye/smallrye-config). As
configurações podem vir de várias
[fontes](https://quarkus.io/guides/config-reference#configuration-sources):
arquivos de propriedades, variáveis de ambiente, parâmetros `-D`, arquivos
`.env`, YAML, entre outros.

## Onde ficam as configurações

Por padrão, as propriedades ficam em `src/main/resources/application.properties`.
Esse arquivo é a fonte mais comum no dia a dia, mas o Quarkus também aceita
valores definidos em:

- **variáveis de ambiente** — por exemplo, `PW2_MESSAGE` corresponde a
  `pw2.message`
- **parâmetros `-D`** na linha de comando do Maven ou da JVM
- **arquivo `.env`** na raiz do projeto
- **arquivo YAML** (`application.yaml` ou `application.yml`), com extensão
  adicional (veja mais adiante)

## Injetando configurações com `@ConfigProperty`

Imagine a declaração de duas propriedades no arquivo `application.properties`:

```properties
pw2.message=hello
pw2.name=world
```

Para injetar esses valores em um bean CDI, utilize a anotação `@ConfigProperty`:

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

Os imports necessários incluem `javax.inject.Inject`,
`org.eclipse.microprofile.config.inject.ConfigProperty`, anotações JAX-RS e
`java.util.Optional`.

### Quando a propriedade não existe

| Situação | Comportamento |
|---|---|
| `@ConfigProperty` sem valor e sem `defaultValue` | falha na inicialização (`DeploymentException`) |
| com `defaultValue = "..."` | usa o padrão informado |
| tipo `Optional<T>` | injeta `Optional.empty()` se ausente |

No exemplo acima, `message` recebe uma string vazia quando `pw2.message` não
está definida. Já `name` aceita a ausência da propriedade e o método `hello()`
usa `"world"` como fallback com `orElse`.

### Acesso programático

O MicroProfile Config também permite ler valores fora da injeção CDI. Use
`ConfigProvider` quando o código não é um bean gerenciado ou quando a chave é
obtida de forma dinâmica:

```java
String databaseName = ConfigProvider.getConfig()
    .getValue("database.name", String.class);
Optional<String> maybeDatabaseName = ConfigProvider.getConfig()
    .getOptionalValue("database.name", String.class);
```

## Sobrescrevendo em runtime

As propriedades não precisam estar apenas em `application.properties`. Você
pode atribuir valores na execução do serviço com o parâmetro `-D`:

```shell
./mvnw compile quarkus:dev -Dpw2.message=hello
```

Variáveis de ambiente seguem a mesma lógica: `PW2_MESSAGE=hello` sobrescreve
`pw2.message` em runtime.

## Namespace `quarkus.` e configurações de build

🚨 O próprio Quarkus é configurado pelo mesmo mecanismo do seu serviço. O
framework reserva o namespace `quarkus.` para sua própria configuração. Por
exemplo, para definir a porta HTTP, use `quarkus.http.port` no
`application.properties`. Nunca utilize o prefixo `quarkus.` nas suas
variáveis de aplicação.

Algumas configurações do Quarkus só têm efeito durante o tempo de construção
(*build*), o que significa que não é possível alterá-las no tempo de execução.
Essas configurações ainda estarão disponíveis em runtime, mas somente leitura.
As opções de build são marcadas com um ícone de cadeado (🔒) na
[lista](https://quarkus.io/guides/all-config) de todas as configurações. Uma
mudança em qualquer uma delas exige reconstruir o serviço (*rebuild*).

## Perfis

Podemos criar [perfis](https://quarkus.io/guides/config-reference#profiles) de
configuração específicos para cada fase do desenvolvimento de um serviço. Por
padrão, o Quarkus oferece três perfis: `dev`, `test` e `prod`. No arquivo
`application.properties`, separamos as configurações de cada perfil com o
seletor `%`:

```properties
pw2.jdbc=jdbc:mysql://localhost:3306/pw2
%prod.pw2.jdbc=jdbc:mysql://rpmhub.dev:3307/pw2
```

Propriedades sem prefixo `%` valem para todos os perfis, exceto quando um perfil
define um override — como `%prod.pw2.jdbc` acima.

O exemplo ilustra uma situação comum: endereços distintos para desenvolvimento
e produção. Note que não foi necessário usar `%dev` ou `%test` na primeira
linha; ela vale para desenvolvimento e teste por padrão.

Se o serviço tiver muitas configurações, você pode dividir os perfis em
arquivos distintos (`application-{nome do perfil}.properties`), sem o prefixo
`%` dentro do arquivo específico. Por exemplo, para um perfil de teste, crie
`application-test.properties`.

Também é possível criar
[perfis customizados](https://quarkus.io/guides/config-reference#custom-profiles).
Imagine um perfil chamado `build`:

```properties
quarkus.http.port=9090
%build.quarkus.http.port=9999
```

Para ativar um perfil customizado:

1. Defina as propriedades com o prefixo `%nome-do-perfil` no
   `application.properties` (ou em `application-{nome-do-perfil}.properties`).
2. Execute o serviço informando `quarkus.profile`:

```shell
./mvnw compile quarkus:dev -Dquarkus.profile=build
```

## Arquivos `.env`

O arquivo [`.env`](https://quarkus.io/guides/config-reference#env-file) é útil para
guardar **segredos e ajustes locais** (senhas, tokens, URLs de máquina de
desenvolvimento) sem versioná-los no Git. O Quarkus carrega esse arquivo
automaticamente em `quarkus:dev`; em runtime, os valores do `.env` têm
**prioridade** sobre o `application.properties` — ou seja, sobrescrevem
propriedade a propriedade.

### Onde colocar o `.env`

Diferente do `application.properties`, o `.env` fica na **raiz do projeto**
(mesmo nível do `pom.xml`), não em `src/main/resources`:

```text
meu-projeto/
├── .env                          ← aqui
├── pom.xml
└── src/main/resources/
    └── application.properties    ← configuração versionada
```

Adicione `.env` ao `.gitignore` para evitar commit acidental de credenciais.

### Formato e correspondência com `application.properties`

Cada linha do `.env` usa `CHAVE=valor`. A chave segue a mesma convenção das
**variáveis de ambiente**: pontos viram underscores e tudo fica em maiúsculas.
Assim, a propriedade `pw2.message` do `application.properties` corresponde a
`PW2_MESSAGE` no `.env`; `quarkus.http.port` corresponde a `QUARKUS_HTTP_PORT`.

Veja o mesmo serviço configurado nos dois arquivos:

`src/main/resources/application.properties` — valores padrão, versionados:

```properties
pw2.message=hello
pw2.name=world
pw2.jdbc.url=jdbc:mysql://localhost:3306/pw2
pw2.jdbc.password=${DB_PASSWORD:changeme}
quarkus.http.port=8080
```

`.env` na raiz do projeto — sobrescreve ou complementa em desenvolvimento local:

```properties
PW2_MESSAGE=hello from .env
PW2_JDBC_URL=jdbc:mysql://localhost:3307/pw2
DB_PASSWORD=secret
QUARKUS_HTTP_PORT=9090
```

Com essa combinação:

| Propriedade | Valor efetivo | Origem |
|---|---|---|
| `pw2.message` | `hello from .env` | `.env` (`PW2_MESSAGE`) |
| `pw2.name` | `world` | `application.properties` (sem entrada no `.env`) |
| `pw2.jdbc.url` | `jdbc:mysql://localhost:3307/pw2` | `.env` (`PW2_JDBC_URL`) |
| `pw2.jdbc.password` | `secret` | `.env` via expressão `${DB_PASSWORD:...}` |
| `quarkus.http.port` | `9090` | `.env` (`QUARKUS_HTTP_PORT`) |

A linha `pw2.jdbc.password=${DB_PASSWORD:changeme}` no `application.properties`
usa uma **expressão de propriedade**: o Quarkus lê a variável `DB_PASSWORD`
(definida no `.env` ou no sistema operacional) e, se ela não existir, usa
`changeme` como fallback. Esse padrão é comum para senhas: o arquivo
versionado declara *de onde* vem o valor, e o `.env` local traz o segredo.

### Referenciando variáveis de ambiente no `application.properties`

Além do `.env`, você pode referenciar variáveis do sistema operacional
diretamente no `application.properties`:

```properties
pw2.jdbc.url=${PW2_JDBC_URL:jdbc:mysql://localhost:3306/pw2}
application.host=${HOST:localhost}
```

A sintaxe `${NOME:valor-padrao}` expande `NOME` na inicialização. Se a variável
não estiver definida (nem no `.env`, nem no ambiente), o valor após `:` é
utilizado.

## Arquivos YAML

O Quarkus também pode trabalhar com arquivos
[YAML](https://quarkus.io/guides/config-yaml). Para isso, instale a dependência
`quarkus-config-yaml` no `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-config-yaml</artifactId>
</dependency>
```

Depois disso, crie `application.yaml` ou `application.yml` em
`src/main/resources`. O comportamento é equivalente ao de
`application.properties`. Compare os dois formatos:

```properties
pw2.message=hello
pw2.name=world
```

```yaml
pw2:
  message: hello
  name: world
```

## Exercício 🏋️

Transforme a configuração do serviço `users` do projeto Books em YAML.

Projeto Books:

```shell
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
code pw2/exemplos/books/users
```

O arquivo atual em `src/main/resources/application.properties` contém:

```properties
#HTTP
quarkus.http.port=8080

# HTTPS
quarkus.http.ssl-port=8443
quarkus.http.ssl.certificate.key-store-file=keystore.jks
quarkus.http.ssl.certificate.key-store-password=password
quarkus.tls.trust-all=true

# JWT Sign Key Location
smallrye.jwt.sign.key.location=privateKey.pem
```

Siga os passos:

1. Abra o projeto `exemplos/books/users`.
2. Adicione a dependência `quarkus-config-yaml` no `pom.xml`.
3. Crie `src/main/resources/application.yml` com as propriedades equivalentes
   ao trecho acima.
4. Remova ou esvazie `application.properties` (mantenha apenas um formato
   ativo).
5. Valide com `./mvnw quarkus:dev`.

Gabarito esperado para o `application.yml`:

```yaml
quarkus:
  http:
    port: 8080
    ssl-port: 8443
    ssl:
      certificate:
        key-store-file: keystore.jks
        key-store-password: password
  tls:
    trust-all: true

smallrye:
  jwt:
    sign:
      key:
        location: privateKey.pem
```

## Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* SmallRye Config. Disponível em: [https://quarkus.io/guides/config-reference](https://quarkus.io/guides/config-reference)

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
