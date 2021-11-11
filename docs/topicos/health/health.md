# Health 🩺

As verificações de Health checks são usadas para verificar o estado de um serviço. Esse tipo de recurso visa é propício para ambientes de infraestrutura em nuvem onde processos automatizados mantêm o estado de nós de computação (kubernetes por exemplo).

Nesse contexto, as verificações de integridade são usadas para determinar se um nó de computação precisa ser descartado/encerado e/ou eventualmente substituído por outra instância. Assim, o Health checks não se destina (embora possa ser usado) como uma solução de monitoramento de serviços para operadores humanos.

Por padrão, o Quarkus utiliza a extensão [SmallRye Health](https://github.com/smallrye/smallrye-health/) como uma implementação da especificação [Microprofile Health](https://github.com/eclipse/microprofile-health).

# Como implementar?

Para se criar um projeto Quarkus com recursos de Health checks abra um terminal de digite (linux, unix):

    mvn io.quarkus.platform:quarkus-maven-plugin:2.4.1.Final:create \
        -DprojectGroupId=dev.pw2 \
        -DprojectArtifactId=health \
        -Dextensions="smallrye-health"

    code health

Se você já tiver um projeto Quarkus e quiser instalar o `smallrye-health` digite na raiz do projeto:

    ./mvnw quarkus:add-extension -Dextensions="smallrye-health"

Outra opção é adicionar no pom.xml a seguinte dependência:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-health</artifactId>
</dependency>
```

A extensão `smallrye-health` expõe diretamente três endpoints em REST:

* `/q/health/live` - o serviço está instalado e funcionando.

* `/q/health/ready` - o serviço está pronto para atender às solicitações.

* `/q/health` - acumula todos os procedimentos de verificação de integridade do serviço.

Todos os endpoint REST retornam um objeto JSON com apenas dois campos:

* status - o resultado geral de todos os procedimentos de verificação de saúde

* checks - uma série de verificações individuais

Depois de abrir o vscode verifique a classe `MyLivenessCheck.java`:

```java
@Liveness
public class MyLivenessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("alive");
    }
}
```

Uma classe que implementa uma verificação de health (saúde) deve ser um bean decorada com `@ApplicationScoped` ou `@Singleton` para que uma única instância do bean seja usada em todas as solicitações de verificação de saúde. Se uma classe do bean não for decorada, então o escopo `@Singleton` é usado automaticamente (caso do exemplo acima).

Como você pode ver, os procedimentos de verificação de integridade são definidos por meio de um beans que implementam a interface `HealthCheck` e são anotados com uma das anotações de verificação de integridade, tais como:

* `@Liveness` - a verificação de atividade acessível em `/q/health /live`
* `@Readiness` - a verificação de prontidão acessível em `/q/health/ready`

Assim, para atender ao endpoint `/q/health/ready` você pode implementar a classe `MyReadinessCheck.java` conforme o exemplo abaixo:

```java
@Readiness
public class MyReadinessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder response = HealthCheckResponse.named("Database connection health check");

        response.up().withData("key", "value");

        // Para responder que o serviço está down
        // response.down();

        return response.build();

    }
}
```

Lembre-se de que a classe `MyReadinessCheck` é um bean Singleton pois não foi anotada. 🚨 Uma observação importante, javax.ejb.Singleton são transacionais, porém, javax.inject.Singleton não são, ou seja, não suportam acessos concorrentes.

No exemplo da classe `MyReadinessCheck` utilizamos a um objeto `HealthCheckResponseBuilder` para verificar se o serviço está UP ou DOWN (métodos `up()` e `down()` do objeto `HealthCheckResponseBuilder`). Além disso, utilizamos o método `.withData` para adicionar alguma informação sobre a situação do serviço. Assim, de posse desses recursos, podemos realizar uma verificação na saúde do serviço, como por exemplo, verificar se a conexão com um banco de dados está ativa, e decidir informar se o serviço está com uma saúde em dia para processar informações.

A URL `/q/health-ui` permite que você veja suas verificações de saúde em uma interface Web. A extensão Quarkus smallrye-health vem com a URL `/q/health-ui` e a habilita por padrão nos modos de desenvolvimento e teste, mas também pode ser explicitamente configurada para o modo de produção (quarkus.smallrye-health.ui.enable=true)

    http://localhost:8080/q/health-ui/

## Código 💡

O código desse tutorial está disponível no Github:

    git clone -b dev https://github.com/rodrigoprestesmachado/pw2
    code pw2/exemplos/health

# Referências 📚

* SMALLRYE HEALTH disponível em: https://quarkus.io/guides/smallrye-health