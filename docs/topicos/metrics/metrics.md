---
layout: default
title: Metrics
parent: Micro Serviços Intermediário
nav_order: 10
---

# Metrics 📏

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/metrics/slides/index.html#/" title="Microprofile Metrics" width="90%" height="500" style="border:none;"></iframe>
</center>

O [Micrometer](https://micrometer.io) é a abordagem recomendada pelo Quarkus para coleta de métricas. Ele fornece uma camada de abstração para diferentes sistemas de monitoramento (Prometheus, Datadog, InfluxDB, etc.), definindo uma API comum para tipos básicos de medidores: contadores, gauges, timers e resumos de distribuição.

A extensão `quarkus-micrometer-registry-prometheus` adiciona suporte ao Micrometer com exportação no formato Prometheus. Com ela, o Quarkus expõe automaticamente um endpoint de métricas em `http://localhost:8080/q/metrics` no formato [OpenMetrics](https://github.com/OpenObservability/OpenMetrics/blob/main/specification/OpenMetrics.md), pronto para ser coletado pelo [Prometheus](https://prometheus.io).

Ao contrário da abordagem anterior (MicroProfile Metrics), o Micrometer não divide as métricas em prefixos `base`, `vendor` e `application`. Em vez disso, utiliza **tags dimensionais** (pares chave/valor) para enriquecer os dados e um único endpoint consolidado. Os nomes de métricas usam pontos como separador (`http.server.requests`) e são automaticamente convertidos para o formato Prometheus com underscores (`http_server_requests_duration_seconds`).

Alguns valores comuns monitorados em microsserviços:

* Tempo de CPU
* Memória ocupada (heap e direct memory)
* Desempenho de endpoints HTTP
* Métricas de negócio (por exemplo, número de pagamentos por segundo)
* Recursos da JVM
* Métricas de pools de conexão (banco de dados, Redis, etc.)
* Saúde geral do cluster

# Como implementar?

## Adicionando a extensão

Para adicionar suporte a métricas Prometheus em um projeto Quarkus, adicione a extensão `micrometer-registry-prometheus`:

```bash
./mvnw quarkus:add-extension -Dextensions='micrometer-registry-prometheus'
```

Ou adicione diretamente no `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
</dependency>
```

## Obtendo o MeterRegistry

Para registrar métricas, é necessário obter uma referência ao `MeterRegistry`. O Micrometer disponibiliza três formas via CDI:

```java
// 1. Injeção via construtor (recomendada)
@Path("/example")
public class ExampleResource {
    private final MeterRegistry registry;

    ExampleResource(MeterRegistry registry) {
        this.registry = registry;
    }
}

// 2. Injeção via campo
@Inject
MeterRegistry registry;

// 3. Registro global
MeterRegistry registry = Metrics.globalRegistry;
```

## Exemplo completo

O exemplo abaixo implementa um serviço que verifica se um número é primo, usando `Counter`, `Gauge` e `@Timed`:

```java
package dev.pw2;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/example")
@Produces(MediaType.TEXT_PLAIN)
public class PrimeResource {

    private final MeterRegistry registry;
    private final Counter primeCounter;
    private final AtomicLong highestPrime = new AtomicLong(0);

    PrimeResource(MeterRegistry registry) {
        this.registry = registry;

        // Contador de números primos encontrados
        this.primeCounter = registry.counter("example.prime.number", "type", "prime");

        // Gauge que expõe o maior primo encontrado
        Gauge.builder("example.prime.highest", highestPrime, AtomicLong::get)
                .description("The highest prime number found so far")
                .register(registry);
    }

    @GET
    @Path("/prime/{number}")
    @Timed(value = "example.prime.check", description = "Time spent checking if a number is prime")
    public String checkIfPrime(@PathParam("number") long number) {
        if (number < 1) {
            return number + ": must be greater than 0";
        }
        if (number == 1) {
            return number + ": not prime";
        }
        if (number == 2 || number % 2 == 0) {
            return number + ": even";
        }
        if (testPrime(number)) {
            primeCounter.increment();
            if (number > highestPrime.get()) {
                highestPrime.set(number);
            }
            return number + ": prime";
        }
        return number + ": not prime";
    }

    private boolean testPrime(long number) {
        for (long i = 3; i < Math.floor(Math.sqrt(number)) + 1; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
```

Após executar o método `checkIfPrime` algumas vezes, as métricas ficam disponíveis em `http://localhost:8080/q/metrics`. O Prometheus as expõe com nomes como:

* `example_prime_number_total` — total de primos encontrados (Counter)
* `example_prime_highest` — maior primo encontrado (Gauge)
* `example_prime_check_seconds_count/sum/max` — estatísticas de tempo (@Timed)

> **Nota:** As métricas são construídas de forma *lazy*. Elas só aparecem no endpoint após a primeira invocação do método instrumentado.

# Tipos de Métricas

## Counter

Contadores medem valores que **só aumentam**. Use para contar eventos como requisições, erros ou operações concluídas.

```java
// Via MeterRegistry
registry.counter("example.prime.number", "type", "prime").increment();

// Via builder (com descrição e unidade)
Counter.builder("payments.processed")
    .baseUnit("payments")
    .description("Total payments processed")
    .tags("method", "credit_card")
    .register(registry)
    .increment();

// Via anotação em método
@Counted(value = "orders.created", extraTags = {"source", "api"})
public Order createOrder(OrderRequest request) { ... }
```

## Gauge

Gauges medem valores que **sobem e descem**, como o tamanho de uma fila, uso de memória ou número de conexões ativas.

```java
// Monitorar o tamanho de uma coleção
List<String> pendingJobs = registry.gaugeCollectionSize(
    "jobs.pending",
    Tags.of("type", "import"),
    new ArrayList<>()
);

// Via builder com função
Gauge.builder("cache.size", cache, Cache::size)
    .description("Current cache size")
    .register(registry);
```

> Use Gauge somente quando não for possível usar um Counter. Se o valor apenas incrementa, prefira um Counter.

## Timer

Timers medem **latências e frequência de ocorrência**. São otimizados para medir tempo e convertem automaticamente para a unidade requerida pelo backend de monitoramento.

```java
// Via MeterRegistry
registry.timer("http.requests", "uri", "/api/orders").record(Duration.ofMillis(50));

// Via builder
Timer.builder("db.query")
    .description("Database query duration")
    .tags("table", "orders")
    .register(registry)
    .record(() -> executeQuery());

// Via anotação
@Timed(value = "payment.processing", extraTags = {"gateway", "stripe"})
public PaymentResult processPayment(Payment payment) { ... }

// Via Sample (para paths complexos)
Timer.Sample sample = Timer.start(registry);
// ... lógica de negócio ...
sample.stop(registry.timer("checkout.flow", "step", "payment"));
```

O Timer armazena internamente: soma dos valores, contagem e o maior valor em uma janela de tempo decrescente.

## DistributionSummary

Resumos de distribuição registram **valores arbitrários** (não necessariamente tempo), como tamanho de requisições HTTP em bytes ou valor de transações financeiras.

```java
// Via MeterRegistry
registry.summary("http.response.size", "uri", "/api/products");

// Via builder
DistributionSummary.builder("order.value")
    .baseUnit("BRL")
    .description("Order value in Brazilian Real")
    .tags("channel", "web")
    .register(registry)
    .record(orderValue);
```

### Histogramas e Percentis

Tanto Timers quanto DistributionSummaries podem ser configurados para emitir dados de histograma e percentis calculados:

```java
Timer.builder("api.latency")
    .publishPercentiles(0.5, 0.95, 0.99)    // percentis pré-calculados (não agregáveis)
    .publishPercentileHistogram()             // buckets de histograma (para histogram_quantile do Prometheus)
    .register(registry);
```

# Métricas Automáticas

O Micrometer instrui automaticamente diversas partes do Quarkus sem nenhuma configuração adicional:

## HTTP Server

O Micrometer registra automaticamente o tempo de todas as requisições HTTP. No Prometheus, procure por:

* `http_server_requests_seconds_count` — número de requisições
* `http_server_requests_seconds_sum` — tempo total acumulado
* `http_server_requests_seconds_max` — maior tempo observado

As métricas incluem tags para `uri`, `method` (GET, POST, etc.), `status` (200, 404, etc.) e `outcome`. URIs com parâmetros de path são normalizadas automaticamente (ex: `/prime/7919` aparece como `uri=/example/prime/{number}`).

Para ignorar determinados endpoints:

```properties
quarkus.micrometer.binder.http-server.ignore-patterns=/q/health.*
```

## JVM e Sistema

Métricas da JVM (memória, threads, GC) e do sistema operacional são coletadas automaticamente pelos binders do Micrometer.

## Netty (Memória Nativa)

Como o Quarkus é construído sobre o Eclipse Vert.x (que usa Netty), há métricas específicas de gerenciamento de memória Netty disponíveis:

* `allocator_memory_used` — memória atualmente usada (heap e direct)
* `allocator_memory_pinned` — memória fixada que não pode ser reutilizada
* `allocator_pooled_arenas` — número de arenas de memória

> Evite configurar `-XX:MaxRAMPercentage` com valores muito altos, pois o Netty aloca memória *fora* do heap da JVM (direct memory). Essas métricas ajudam a dimensionar esse espaço.

## Extensões instrumentadas automaticamente

Entre as extensões Quarkus com instrumentação automática estão: `quarkus-rest`, `quarkus-hibernate-orm`, `quarkus-agroal`, `quarkus-redis-client`, `quarkus-cache`, `quarkus-grpc`, `quarkus-messaging` (Kafka, RabbitMQ, etc.), entre outras.

# Prometheus

Para rodar o [Prometheus](https://prometheus.io), utilize o arquivo `docker-compose.yml` abaixo:

```yml
volumes:
    metrics:
services:
    prometheus:
      image: prom/prometheus:v3.11.3
      container_name: prometheus
      ports:
        - 9090:9090
      volumes:
        - metrics:/etc/prometheus
        - ./prometheus.yml:/etc/prometheus/prometheus.yml
      extra_hosts:
        - "host.docker.internal:host-gateway"
```

A entrada `extra_hosts` garante que o hostname `host.docker.internal` funcione no **Linux** (no Mac e Windows já é resolvido nativamente pelo Docker).

O arquivo de configuração `prometheus.yml` referenciado acima deve ter o seguinte conteúdo:

```yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

alerting:
  alertmanagers:
    - static_configs:
        - targets:

rule_files:

scrape_configs:
  - job_name: "quarkus-app"
    metrics_path: "/q/metrics"
    # `host.docker.internal` resolve para o host da máquina no Mac e Windows.
    # No Linux isso não funciona nativamente — adicione ao docker-compose.yml:
    #
    #   extra_hosts:
    #     - "host.docker.internal:host-gateway"
    #
    # Assim o mesmo target abaixo funciona em todos os SOs.
    static_configs:
      - targets: ["host.docker.internal:8080"]
      # Linux (sem extra_hosts): use o IP da interface docker0, ex: 172.17.0.1:8080
      # - targets: ["172.17.0.1:8080"]
```

Uma vez que o Prometheus esteja rodando, ele irá coletar periodicamente as métricas da aplicação via o endpoint `/q/metrics`, conforme configurado nos `targets`.

## Interface de Gerenciamento (opcional)

Por padrão, as métricas são expostas no servidor HTTP principal. Para expô-las em uma porta separada, configure:

```properties
quarkus.management.enabled=true
```

Com essa configuração, as métricas ficam disponíveis em `http://0.0.0.0:9000/q/metrics`.

## Código 💡

Um código de exemplo sobre esse tópico está disponível no Github:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
code pw2/exemplos/metrics
```

# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* Micrometer Metrics. Disponível em: [https://quarkus.io/guides/telemetry-micrometer](https://quarkus.io/guides/telemetry-micrometer)

* Micrometer Documentation. Disponível em: [https://micrometer.io/docs](https://micrometer.io/docs)

<center>
    <a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a>
    <br/>
    <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
