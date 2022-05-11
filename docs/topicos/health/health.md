# Health 🩺

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/health/slides/index.html#/" title="Microprofile Health" width="90%" height="500" style="border:none;"></iframe>
</center>

As verificações de Health checks são usadas para verificar o estado de um serviço. Esse tipo de recurso é propício para ambientes de infraestrutura em nuvem onde processos automatizados mantêm o estado de nós de computação (kubernetes por exemplo).

Nesse contexto, as verificações de integridade são usadas para determinar se um nó de computação precisa ser descartado/encerado e/ou eventualmente substituído por outra instância. Assim, o Health checks não se destina (embora possa ser usado) como uma solução de monitoramento de serviços para operadores humanos.

Por padrão, o Quarkus utiliza a extensão [SmallRye Health](https://github.com/smallrye/smallrye-health/) como uma implementação da especificação [Microprofile Health](https://github.com/eclipse/microprofile-health).

# Configuração no Quarkus

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

# Implementação

A extensão `smallrye-health` irá habilitar _endpoints_, são eles:

* `/q/health/live` - indica se o serviço está rodando (vivo).

* `/q/health/ready` - informa se o serviço está pronto para atender às solicitações (_requests_).

* `/q/health` - indica se o serviço está vivo e também pronto para atender às solicitações.

Os _endpoints_ retornam um objeto JSON contendo duas propriedades:

* status - o resultado geral de todos os procedimentos de verificação de saúde.

* checks - uma série de verificações individuais.

Um exemplo dos dados retornados no _endpoint_ `/q/health` :

```json
{
    "status": "UP",
    "checks": [
        {
            "name": "I'm alive",
            "status": "UP"
        },
        {
            "name": "Database connection health check",
            "status": "UP",
            "data": {
                "key": "some information"
            }
        }
    ]
}
```

Uma classe que implementa uma verificação de _health_ deve ser decorada com `@ApplicationScoped` ou `@Singleton`. Estas anotações fazem com que seja criado uma única instância de um _bean_ que irá responder a todas as requisições de verificação de saúde. Se uma classe do bean não for decorada, então o escopo `@Singleton` é usado automaticamente, por exemplo:

```java
@Liveness
public class MyLivenessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("alive");
    }
}
```

Como você pode ver, os procedimentos de verificação de integridade são definidos por meio de _beans_ que implementam a interface `HealthCheck` e que são decorados com uma das anotações de verificação de integridade, tais como:

* `@Liveness` - faz com o que o bean responda no _endpoint_  `/q/health /live` e indique que o serviço está vivo(rodando).
* `@Readiness` - faz com o que o bean responda no _endpoint_  `/q/health/ready` e indique que o serviço está pronto para receber requisições.

Assim, para atender ao endpoint `/q/health/ready` você pode implementar, por exemplo, a classe `MyReadinessCheck.java` conforme o trecho de código abaixo:

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

Lembre-se de que a classe `MyReadinessCheck` é um bean Singleton pois não foi anotada. 🚨 Uma observação interessante, `javax.ejb.Singleton` são transacionais, porém, `javax.inject.Singleton` não são, ou seja, não suportam acessos concorrentes.

No exemplo da classe `MyReadinessCheck` utilizamos a um objeto `HealthCheckResponseBuilder` para verificar se o serviço está UP ou DOWN (métodos `up()` e `down()` do objeto `HealthCheckResponseBuilder`). Além disso, utilizamos o método `.withData` para adicionar alguma informação sobre a situação do serviço. Assim, de posse desses recursos, podemos realizar uma verificação na saúde do serviço, como por exemplo, verificar se a conexão com um banco de dados está ativa, e decidir informar se o serviço está com uma saúde em dia para processar informações.

A extensão `smallrye-health` também pode criar uma URL `/q/health-ui` para que você observe as  verificações de saúde por meio de uma interface Web, conforme ilustra a Figura 1. A URL `/q/health-ui` é habilita por padrão nos modos de desenvolvimento e teste, porém, pode ser explicitamente configurada para o modo de produção se a propriedade do Quarkus `quarkus.smallrye-health.ui.enable=true` receber o valor `true`.

    http://localhost:8080/q/health-ui/

<center>
    <img src="slides/img/health-ui.png" width="50%" height="50%"/><br/>
    Figura 1 - Health UI
</center>

## Código 💡

Um código de exemplo desse documento pode ser encontrado no Github:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
code pw2/exemplos/health
```
# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* SMALLRYE HEALTH disponível em: [https://quarkus.io/guides/smallrye-health](https://quarkus.io/guides/smallrye-health)

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>