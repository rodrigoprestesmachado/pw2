# Metrics no Microprofile/Quarkus 📏

A especificação [Metrics](https://github.com/eclipse/microprofile-metrics/) do Microprofile fornece uma maneira de construir e expor métricas do seu serviço para ferramentas de monitoramento, como por exemplo, o [Prometheus](https://prometheus.io)/[OpenMetrics](https://github.com/OpenObservability/OpenMetrics/blob/main/specification/OpenMetrics.md).

Para habilitar MicroProfile Metrics em um aplicativo Quarkus, você precisa importar a extensão `quarkus-smallrye-metrics` ([SmallRye Metrics](https://github.com/smallrye/smallrye-metrics/)). Com a extensão `quarkus-smallrye-metrics` adicionada no projeto, o Quarkus fornece um endpoint padrão (ex.: http://localhost:8080/q/metrics) no formato Prometheus. Entretanto, o formato pode ser alterado para JSON trocando-se o cabeçalho da requisição HTTP Accept para `application/json`.

Se você olhar para a saída do endpoint padrão (http://localhost:8080/q/metrics), verás diversos parâmetros prefixados:

* base (`q/metrics/base`) - informações essenciais do servidor, como por exemplo, o número de classes carregadas por uma máquina virtual, ou o número de processos rodando na máquina virtual, etc.
* vendor  (`q/metrics/vendor`) - informações especificar sobre um fornecedor, como por exemplo, o tempo de CPU utilizado por cada processo, ou o uso recente de CPU por todo os sistema, etc.
* application (`q/metrics/application`) - Informações personalizadas desenvolvidas por meio do mecanismo de extensão do MicroProfile Metrics.

Como pode ser observado, em um serviço existem muitos valores possíveis para serem monitorados, os mais comuns são:

* Memória
* Espaço em disco
* Rede
* Recursos da JVM
* Desempenho de métodos críticos
* Métricas de negócios (por exemplo, o número de pagamentos por segundo)
* Saúde geral do seu cluster

O MicroProfile Metrics possui um conjunto de anotações que podem serem usadas para criamos métricas espefíficas (`q/metrics/application`) para o serviço, são elas:

* @Counted - Conta o número de invocações
* @Timed - Rastreia a duração de uma invocação
* @SimplyTimed - Rastreia a duração das invocações sem cálculos de média e distribuição. Uma versão simplificada do @Timed.
* @Metered - Rastreia a frequência de invocações
* @Gauge - Expõe o valor de retorno do método anotado como uma métrica
* @ConcurrenceGauge - Conta as invocações paralelas

# Como implementar?

Vamos implementar um exemplo de métricas em um micro serviço escrito com o Quarkus.

Primeiro, abra um terminal e crie um projeto com o seguinte comando:

    mvn io.quarkus:quarkus-maven-plugin:2.1.3.Final:create \
        -DprojectGroupId=dev.pw2 \
        -DprojectArtifactId=metrics \
        -DclassName="dev.pw2.Checker" \
        -Dpath="/" \
        -Dextensions="resteasy,smallrye-metrics"

    code metrics

Como ilustração iremos utilizar um serviço que indica se um número é par ou ímpar como exemplo. Observe o trecho de código abaixo:

```java
@Path("/")
public class Checker {

    // Injeta um objeto da classe Histogram
    @Inject
    @Metric(name = "histogram", absolute = true)
    Histogram histogram;

    // Armazena o maior número par encontrado
    private long highestEven = 1;

    @GET
    @Path("/{number}")
    @Produces(MediaType.TEXT_PLAIN)
    @Counted(name = "counter", description = "The number of execution of the check method")
    @Timed(name = "timer", description = "A measure of how long it takes to  execute the check method", unit = MetricUnits.MICROSECONDS)
    public String check(@PathParam("number") long number) {
        // Histogram
        this.histogram.update(number);
        if (number < 1) {
            return "The number must be > 1";
        }
        if (number % 2 == 1) {
            return number + ": Odd";
        }
        if (number > highestEven) {
            this.highestEven = number;
        }
        return number + ": Even";
    }

    // Retorna o maior número par encontrado como uma métrica
    @Gauge(name = "highestEven", unit = MetricUnits.NONE, description = "Highest even so far")
    public Long highestEven() {
        return this.highestEven;
    }
}
```

Como pode ser observado no código, a classe acima implementa uma métrica chamada `counter` que é incrementada cada vez que o método `check` é executado. Além disso, a métrica `timer` registra o tempo em micro segundos gasto na execução do método `check`. Finalmente a métrica `highestEven` informa o maior número par que foi encontrado pelo serviço. Para visualizar as métricas, é necessário executar o método check e depois conferir os resultados no end-point `/q/metrics/application`

## Histograma

Existe também uma métrica, que não possui uma anotação, chamada de Histograma. Um histograma armazena dados ao longo do tempo e, com isso, gera valores como mínimo, máximo, média, desvio padrão, entre outros. No exemplo anterior, declaramos um histograma chamado "histogram" da seguinte maneira:

```java
    @Inject
    @Metric(name = "histogram", absolute = true)
    Histogram histogram;
```

Por sua vez, no método `check` utilizamos o código `this.histogram.update(number);` para armazenar os  valores e guardar o histórico de dados do método.

## Consultas 🔎

Você pode obter informações de qualquer métrica consultando um endpoint específico usando o método OPTION HTTP. Os metadados são expostos por padrão em `q/metrics/escope/metric-name`, onde o `escope` pode ser: base, vendor ou application e metric-name é o nome propriamente dito da métrica (no caso de um aplicativo, aquele definido no atributo name).
## Código 💡

O código desse tutorial está disponível no Github:

    git clone -b dev https://github.com/rodrigoprestesmachado/pw2
    code pw2/exemplos/metrics
# Referências 📚

SMALLRYE METRICS: Disponível em: [https://quarkus.io/guides/smallrye-metrics](https://quarkus.io/guides/smallrye-metrics)

Alex Soto, Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions](https://www.amazon.com.br/Quarkus-Cookbook-Alex-Soto/dp/1492062650). Editora: O'Reilly Media.