# Microprofile Configuration

O sistema de configuração de um Quarkus utiliza a especificação [Microprofile Config.](https://github.com/eclipse/microprofile-config) implementada com o (SmallRye Config)(https://github.com/smallrye/smallrye-config). As configurações são provenientes de várias [fontes](https://quarkus.io/guides/config-reference#environment-variables).

## O arquivo application.properties

Imagine a declaração de duas propriedades no arquivo `application.properties`:

```yaml
    pw2.message = hello
    pw2.name = world
```

Para injetar no serviço o valor da propriedade `pw2.message` utilizamos a anotação `@ConfigProperty`:

```java
@ConfigProperty(name = "pw2.message",  defaultValue="" )
String message;

@ConfigProperty(name = "pw2.name")
Optional<String> name;
```

Se você não fornecer um valor para um propriedade, a inicialização do serviço lançará uma exceção: `javax.enterprise.inject.spi.DeploymentException`. Nesse caso, é possível codificar um valor padrão para a variável `message` (uma String vazia). O exemplo também mostra que uma classe Optional vazio também é injetado se a configuração não fornecer um valor para a variável `name`. Veja um exemplo de utilização de inicialização do atributo `name`.

```java
@GET
@Produces(MediaType.TEXT_PLAIN)
public String hello() {
    return message + " " + name.orElse("world");
}
```

O Microprofile Config. também permite acessar valores de configuração de maneira programática. No exemplo abaixo, a classe `ConfigProvider` permite que você acesse, o valor da chave `database.name`.

```java
    String databaseName = ConfigProvider.getConfig().getValue("database.name", String.class);
    Optional<String> maybeDatabaseName = ConfigProvider.getConfig().getOptionalValue("database.name", String.class);
```

🚨 O próprio Quarkus é configurado por meio do mesmo mecanismo do ser serviço. Quarkus reserva o  namespace `quarkus.` para sua própria configuração. Por exemplo, para configurar a porta do servidor HTTP, você pode definir `quarkus.http.port` no arquivo application.properties. Assim, nunca utilize o prefixo `quarkus.`como prefixo das suas variáveis.

Algumas configurações do Quarkus só têm efeito durante o tempo de construção (*build*), o que significa que não é possível alterá-las no tempo de execução. Essas configurações ainda estarão disponíveis em tempo de execução, mas, para somente leitura. As configurações de tempo de construção são marcadas com um ícone de cadeado (🔒) na [lista](https://quarkus.io/guides/all-config) de todas as opções de configuração. Uma mudança em qualquer uma dessas configurações de contstrução requer uma reconstrução do seu serviço (*rebuild*).
