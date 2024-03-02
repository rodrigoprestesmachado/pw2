---
layout: default
title: Metrics
nav_order: 11
---

# Metrics 📏

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/metrics/slides/index.html#/" title="Microprofile Metrics" width="90%" height="500" style="border:none;"></iframe>
</center>

A especificação [Metrics](https://github.com/eclipse/microprofile-metrics/) do Microprofile fornece uma maneira de construir e expor métricas do seu serviço para ferramentas de monitoramento, como por exemplo, o [Prometheus](https://prometheus.io)/[OpenMetrics](https://github.com/OpenObservability/OpenMetrics/blob/main/specification/OpenMetrics.md).

Para utiilzar o MicroProfile Metrics em um aplicativo Quarkus, você precisa importar a extensão `quarkus-smallrye-metrics` ([SmallRye Metrics](https://github.com/smallrye/smallrye-metrics/)). Com a extensão `quarkus-smallrye-metrics` adicionada no projeto, o Quarkus fornece um endpoint padrão (ex.: http://localhost:8080/q/metrics) no formato Prometheus. Entretanto, o formato pode ser alterado para JSON trocando-se o cabeçalho da requisição HTTP Accept para `application/json`.

Se você olhar para a saída do endpoint padrão (http://localhost:8080/q/metrics), verás diversos parâmetros prefixados:

* base (`q/metrics/base`) - informações essenciais do servidor, como por exemplo, o número de classes carregadas por uma máquina virtual, ou o número de processos rodando na máquina virtual, etc.
* vendor  (`q/metrics/vendor`) - informações especificas sobre um fornecedor, como por exemplo, o tempo de CPU utilizado por cada processo, ou o uso recente de CPU por todo os sistema, etc.
* application (`q/metrics/application`) - informações personalizadas desenvolvidas por meio do mecanismo de extensão do MicroProfile Metrics.

Como pode ser observado, em um serviço existem muitos valores possíveis para serem monitorados, os mais comuns são:

* Tempo de CPU
* Memória ocupada
* Espaço em disco
* Desempenho de métodos críticos
* Métricas de negócios (por exemplo, o número de pagamentos por segundo)
* Quesões de Rede
* Recursos ocupados pela JVM
* Saúde geral do seu cluster

O MicroProfile Metrics possui um conjunto de anotações que podem serem usadas para criamos métricas espefíficas (`q/metrics/application`) para o serviço, são elas:

* @Counted - Conta o número de invocações de um método
* @Timed - Monitora a duração de uma invocação
* @SimplyTimed - Monitora a duração das invocações sem considerar cálculos como média e distribuição, ou seja, trata-se de uma versão simplificada do @Timed.
* @Metered - Monitora a frequência de invocações
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

Você pode obter informações de qualquer métrica consultando um endpoint específico usando o método OPTION do HTTP. Os metadados são expostos por padrão em `q/metrics/escope/metric-name`, onde o `escope` pode ser: base, vendor ou application e metric-name é o nome propriamente dito da métrica (no caso de um aplicativo, aquele definido no atributo name).

## Prometheus

Para rodar o [Prometheus](https://prometheus.io), utilize, por exemplo, o arquivo `docker-compose.yml` `abaixo:

```yml
version: "3.9"
volumes:
    metrics:
services:
    prometheus:
      image: bitnami/prometheus:latest
      container_name: prometheus
      ports:
        - 9090:9090
      volumes:
        - metrics:/etc/prometheus
        - ./prometheus.yml:/etc/prometheus/prometheus.yml
```

A última linha do arquivo `docker-compose.yml` mostra que o prometheus necessita de um arquivo de configuração chamado `prometheus.yml`. Um exemplo de arquivo `prometheus.yml` pode ser observado abaixo:

```yml
# my global config
global:
  scrape_interval: 15s # Set the scrape interval to every 15 seconds. Default is every 1 minute.
  evaluation_interval: 15s # Evaluate rules every 15 seconds. The default is every 1 minute.
  # scrape_timeout is set to the global default (10s).

# Alertmanager configuration
alerting:
  alertmanagers:
    - static_configs:
        - targets:
          # - alertmanager:9093

# Load rules once and periodically evaluate them according to the global 'evaluation_interval'.
rule_files:
  # - "first_rules.yml"
  # - "second_rules.yml"

# A scrape configuration containing exactly one endpoint to scrape:
# Here it's Prometheus itself.
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: "prometheus"

    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.

    metrics_path: "/q/metrics"

    # No Windows utilize `host.docker.internal` ao invés de `docker.for.mac`
    # para acessar um serviço externo ao container
    # https://stackoverflow.com/questions/24319662/from-inside-of-a-docker-container-how-do-i-connect-to-the-localhost-of-the-mach
    static_configs:
      - targets: ["docker.for.mac.localhost:8080"]
```

Uma vez que o Prometheus esteja rodando, ele irá realizar a leitura dos endpoints da aplicação, por meio da sua configuração de "*targets*".

## Código 💡

um código de exemplo sobre esse tópico está disponível no Github:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
code pw2/exemplos/metrics
```

# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* SmallReye Metrics. Disponível em: [https://quarkus.io/guides/smallrye-metrics](https://quarkus.io/guides/smallrye-metrics)

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>