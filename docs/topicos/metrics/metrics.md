---
layout: default
title: Metrics
parent: Micro Serviços Intermediário
nav_order: 10
---

# Metrics 📏

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/metrics/slides/index.html#/" title="Metrics" width="90%" height="500" style="border:none;"></iframe>
</center>

## Por que precisamos nos preocupar com métricas? 🤔

Imagine pilotar um avião comercial à noite ou dirigir um carro em uma rodovia
movimentada sem nenhum painel de instrumentos. Você não tem velocímetro, não tem
marcador de combustível, não tem indicador de temperatura do motor nem altímetro.
Se o combustível estiver acabando ou o motor começar a superaquecer, você só
descobrirá quando o motor parar de repente no meio do caminho!
{: .fs-3 }

Com microsserviços em produção, a situação é exatamente essa. Quando seu serviço
está rodando na nuvem atendendo milhares de clientes simultâneos, não é possível
"abrir a IDE e colocar um breakpoint". Precisamos de **observabilidade** e
**métricas em tempo real**:
{: .fs-3 }

* Quantas requisições por segundo cada endpoint está recebendo?
* O tempo de resposta está rápido ou os usuários estão esperando demais?
* Quantos erros HTTP 500 estão acontecendo agora?
* Quanta memória RAM a aplicação está consumindo?
* O banco de dados está sobrecarregado?
{: .fs-3 }

Para resolver isso de forma padronizada, o Quarkus adota o
[Micrometer](https://micrometer.io) como sua biblioteca oficial de métricas. O
Micrometer funciona como uma camada de abstração (uma fachada) para medição: você
escreve suas métricas uma única vez usando uma API limpa e amigável e ele as
exporta no formato ideal para sistemas de monitoramento como o
[Prometheus](https://prometheus.io), Datadog, InfluxDB e outros.
{: .fs-3 }

## As quatro ferramentas do nosso kit de medidores 🧰

O Micrometer organiza o monitoramento em quatro tipos fundamentais de medidores
(*meters*), cada um com um propósito específico:
{: .fs-3 }

* **`Counter`** (*"A catraca ou o odômetro"*): um medidor acumulador que **só aumenta** (ou reinicia em caso de reinicialização da aplicação). Ideal para contar eventos: total de pedidos, total de logins realizados, total de erros.
* **`Gauge`** (*"O velocímetro ou marcador de combustível"*): um valor instantâneo que **sobe e desce** dinamicamente. Ideal para medir o estado atual: quantidade de itens em uma fila, conexões ativas no banco de dados, memória em uso.
* **`Timer`** (*"O cronômetro inteligente"*): mede **latência (duração de tempo) e frequência** ao mesmo tempo. Ideal para medir quanto tempo leva uma requisição HTTP ou consulta ao banco.
* **`DistributionSummary`** (*"A balança de bagagens"*): analisa a distribuição estatística de valores numéricos que **não** são tempo. Ideal para medir tamanho de requisições em bytes, peso de pacotes ou valores financeiros de compras.
{: .fs-3 }

Além desses, o Micrometer oferece anotações convenientes como **`@Timed`** e
**`@Counted`**, que instrumentam métodos inteiros com uma única linha!
{: .fs-3 }

## Preparando o ambiente 🛠️

Para criar um novo projeto com suporte a métricas formatadas para o Prometheus:
{: .fs-3 }

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:3.40.1.2:create \
    -DprojectGroupId=dev.pw2 \
    -DprojectArtifactId=metrics-quickstart \
    -Dextensions='rest,micrometer-registry-prometheus' \
    -DclassName="dev.pw2.ItemResource" \
    -Dpath="/itens"

cd metrics-quickstart
code .
```

Se você já possui um projeto Quarkus existente, basta adicionar a extensão
`micrometer-registry-prometheus`:
{: .fs-3 }

```sh
./mvnw quarkus:add-extension -Dextensions='micrometer-registry-prometheus'
```

Com essa extensão, o Quarkus passa a expor automaticamente todas as métricas
da aplicação no endpoint:
{: .fs-3 }

```text
http://localhost:8080/q/metrics
```

Esse endpoint fornece os dados no formato aberto **OpenMetrics/Prometheus**, pronto
para ser lido por qualquer ferramenta de coleta.
{: .fs-3 }

## 1. Obtendo o `MeterRegistry` 💉

O ponto de entrada para criar qualquer métrica programática no Micrometer é a
classe `MeterRegistry`. No Quarkus, você obtém uma instância simplesmente
injetando-a via construtor (forma recomendada) ou campo:
{: .fs-3 }

```java
package dev.pw2;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.ws.rs.Path;

@Path("/itens")
public class ItemResource {

    private final MeterRegistry registry;

    // Injeção via construtor (boa prática recomendada)
    public ItemResource(MeterRegistry registry) {
        this.registry = registry;
    }
}
```

## 2. Counter: registrando eventos que só aumentam 🔢

O contador (`Counter`) registra grandezas cumulativas. Você nunca subtrai de um
contador: ele apenas incrementa.
{: .fs-3 }

**Quando usar?** Número total de compras efetuadas, total de acessos a uma página,
tentativas de autenticação falhas ou exceções capturadas.
{: .fs-3 }

Observe o exemplo abaixo:
{: .fs-3 }

```java
package dev.pw2;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/itens")
@Produces(MediaType.TEXT_PLAIN)
public class ItemResource {

    private final Counter contadorAcessos;

    public ItemResource(MeterRegistry registry) {
        // Criando e registrando um contador com tags
        this.contadorAcessos = registry.counter("itens.consultas.total", "tipo", "geral");
    }

    @GET
    public String listarItens() {
        // Incrementa o contador em 1
        contadorAcessos.increment();
        return "Lista de itens: Livro, Notebook, Caneta";
    }
}
```

Vamos decifrar o que acontece passo a passo:
{: .fs-3 }

1. No construtor, invocamos `registry.counter("itens.consultas.total", "tipo", "geral")`.
2. A cada chamada ao método `listarItens()`, executamos `contadorAcessos.increment()`.
3. Ao acessar `http://localhost:8080/q/metrics`, o Prometheus exibirá a métrica convertida com underscores:
{: .fs-3 }

```text
itens_consultas_total{tipo="geral"} 1.0
```

### O atalho com `@Counted` ⚡

Se você só precisa contar quantas vezes um método foi executado, não precisa nem
instanciar o `Counter` manualmente. Basta anotar o método:
{: .fs-3 }

```java
@GET
@Path("/rapido")
@Counted(value = "itens.rapido.execucoes", description = "Total de chamadas ao endpoint rapido")
public String endpointRapido() {
    return "Executado!";
}
```

## 3. Timer: medindo latência e duração de operações ⏱️

O `Timer` é um dos medidores mais valiosos na arquitetura de microsserviços. Ele
armazena automaticamente três informações essenciais:
{: .fs-3 }

* Quantas vezes a operação foi executada (`_count`).
* O tempo total acumulado gasto por todas as execuções (`_sum`).
* O maior tempo observado na janela recente (`_max`).
{: .fs-3 }

**Quando usar?** Tempo de resposta de endpoints REST, duração de consultas SQL,
tempo de chamada a APIs externas via REST Client.
{: .fs-3 }

A forma mais simples e produtiva de usar um Timer é com a anotação **`@Timed`**:
{: .fs-3 }

```java
@GET
@Path("/processar")
@Timed(value = "itens.processar.duracao", description = "Tempo gasto no processamento de itens")
public String processarItem() throws InterruptedException {
    // Simulando um processamento que leva 150 milissegundos
    Thread.sleep(150);
    return "Item processado com sucesso!";
}
```

No endpoint `/q/metrics`, o Micrometer expõe automaticamente:
{: .fs-3 }

* `itens_processar_duracao_seconds_count`: número total de execuções.
* `itens_processar_duracao_seconds_sum`: soma total de segundos gastos.
* `itens_processar_duracao_seconds_max`: duração máxima registrada em segundos.
{: .fs-3 }

Você também pode medir trechos específicos de código programaticamente:
{: .fs-3 }

```java
// Medindo a execucao de um Runnable / Lambda
registry.timer("operacao.demorada").record(() -> {
    fazerCalculoPesado();
});
```

## 4. Gauge: medindo valores que sobem e descem 🌡️

Ao contrário do contador, o `Gauge` reflete o **estado atual instantâneo** de uma
variável. Ele observa o valor de um objeto vivo e relata seu valor no momento
exato da coleta de métricas.
{: .fs-3 }

**Quando usar?** Quantidade de usuários logados no momento, tamanho de uma fila
de mensagens pendentes, quantidade de itens disponíveis em estoque.
{: .fs-3 }

Observe o exemplo com `AtomicInteger`:
{: .fs-3 }

```java
package dev.pw2;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/estoque")
public class EstoqueResource {

    private final AtomicInteger itensEmEstoque = new AtomicInteger(10);

    public EstoqueResource(MeterRegistry registry) {
        // Registra o Gauge observando o metodo get() de itensEmEstoque
        Gauge.builder("loja.estoque.atual", itensEmEstoque, AtomicInteger::get)
            .description("Quantidade atual de itens em estoque")
            .register(registry);
    }

    @GET
    @Path("/comprar")
    public String comprar() {
        int restante = itensEmEstoque.decrementAndGet();
        return "Item vendido! Estoque restante: " + restante;
    }

    @GET
    @Path("/repor")
    public String repor() {
        int restante = itensEmEstoque.addAndGet(5);
        return "Estoque reposto! Novo estoque: " + restante;
    }
}
```

⚠️ **Atenção:** Use `Gauge` **apenas** quando o valor subir e descer. Se o valor
apenas cresce ao longo do tempo (como total de requisições), use sempre `Counter`.
O Prometheus calcula taxas de variação por segundo (derivadas com a função `rate()`)
especificamente sobre Contadores, o que não funciona de forma correta com Gauges.
{: .fs-3 }

## 5. DistributionSummary: valores que não são tempo ⚖️

Quando você precisa analisar a distribuição de grandezas escalares (que não
medem tempo em segundos), o medidor correto é o `DistributionSummary`.
{: .fs-3 }

**Quando usar?** Tamanho de arquivos recebidos por upload (em bytes), valor total
financeiro das compras (em reais), número de itens por carrinho de supermercado.
{: .fs-3 }

```java
DistributionSummary resumoPagamentos = DistributionSummary
    .builder("vendas.valor.pagamento")
    .baseUnit("BRL")
    .description("Valores dos pagamentos efetuados")
    .register(registry);

// Registrando o valor de um pagamento
resumoPagamentos.record(149.90);
```

## 6. Tags dimensionais: filtrando dados com riqueza 🏷️

O Micrometer não separa métricas em pastas rígidas (como fazia a especificação
antiga MicroProfile Metrics). Em vez disso, ele utiliza **tags dimensionais**
(pares chave/valor).
{: .fs-3 }

Isso permite que você tenha uma única métrica (por exemplo, `vendas.total`) e use
tags para fatiar as informações no painel de monitoramento:
{: .fs-3 }

```java
registry.counter("vendas.total", "forma_pagamento", "pix").increment();
registry.counter("vendas.total", "forma_pagamento", "cartao_credito").increment();
```

No Prometheus, isso vira:
{: .fs-3 }

```text
vendas_total{forma_pagamento="pix"} 1.0
vendas_total{forma_pagamento="cartao_credito"} 1.0
```

Assim, no Grafana ou Prometheus, você pode consultar o total geral somando tudo,
ou filtrar apenas os pagamentos realizados por Pix.
{: .fs-3 }

## 7. Métricas automáticas do Quarkus 🤖

A melhor parte do suporte ao Micrometer no Quarkus é que você ganha dezenas de
métricas vitais sem precisar escrever uma linha sequer de código Java! O Quarkus
já vem com instrumentação automática para:
{: .fs-3 }

* **Servidor HTTP:** Todas as requisições HTTP recebidas são medidas automaticamente
sob `http_server_requests_seconds_count/sum/max`, incluindo tags para o path da URI,
o método HTTP (GET/POST) e o status code (200, 404, 500).
* **Recursos da JVM:** Memória heap e direct, contagem de threads ativas, pausas do
Garbage Collector (GC) e tempo de CPU.
* **Alocação de Memória Netty:** Como o Quarkus roda sobre o motor de alta performance
Netty/Vert.x, métricas como `allocator_memory_used` e `allocator_memory_pinned`
ajudam a garantir que a memória fora do heap (direct memory) não cause vazamentos.
* **Extensões integradas:** Pools de conexão de banco (`quarkus-agroal`), cache
(`quarkus-cache`), clientes gRPC e mensagens Kafka também emitem métricas
automaticamente quando presentes no projeto.
{: .fs-3 }

💡 **Nota sobre inicialização *lazy*:** As métricas de negócio criadas na sua
aplicação só aparecem no endpoint `/q/metrics` após a primeira invocação do
método correspondente.
{: .fs-3 }

## 8. Coletando métricas com Prometheus e Docker 📊

O [Prometheus](https://prometheus.io) é o servidor de monitoramento padrão da
nuvem. Ele opera no modelo **pull** (*scraping*): de tempos em tempos (ex: a
cada 15 segundos), ele faz uma requisição HTTP ao `/q/metrics` da sua aplicação,
baixa todas as métricas e armazena em um banco de dados de séries temporais.
{: .fs-3 }

Para subir o Prometheus em contêiner na sua máquina, crie o arquivo
`docker-compose.yml`:
{: .fs-3 }

```yaml
services:
  prometheus:
    image: prom/prometheus:v3.11.3
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    extra_hosts:
      - "host.docker.internal:host-gateway"
```

A diretiva `extra_hosts` garante que o contêiner do Prometheus consiga se conectar
à porta 8080 do Quarkus rodando na máquina host no **Linux** (no macOS e Windows
isso já é resolvido nativamente pelo Docker Desktop).
{: .fs-3 }

Crie também o arquivo de configuração `prometheus.yml` no mesmo diretório:
{: .fs-3 }

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: "quarkus-app"
    metrics_path: "/q/metrics"
    static_configs:
      - targets: ["host.docker.internal:8080"]
```

Inicie o Prometheus com:
{: .fs-3 }

```sh
docker compose up -d
```

Agora acesse `http://localhost:9090` no seu navegador para ver o painel do
Prometheus e executar consultas interativas!
{: .fs-3 }

---

## Colocando tudo em prática: observabilidade na prática 🎯

Vamos praticar a instrumentação passo a passo com exercícios simples e diretos,
garantindo que você compreenda e veja os resultados no terminal com facilidade.
{: .fs-3 }

### Exercício 1: Seu primeiro contador com `Counter` 🟢

**Objetivo:** Criar um contador de acessos e verificar seu valor no endpoint `/q/metrics`.
{: .fs-3 }

**Passo 1:** No seu projeto Quarkus com `micrometer-registry-prometheus`, crie a classe `src/main/java/dev/pw2/CatalogoResource.java`:
{: .fs-3 }

```java
package dev.pw2;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/catalogo")
public class CatalogoResource {

    private final Counter contadorBuscas;

    public CatalogoResource(MeterRegistry registry) {
        this.contadorBuscas = registry.counter("catalogo.buscas.total");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String buscar() {
        contadorBuscas.increment();
        return "Busca realizada no catalogo!";
    }
}
```

**Passo 2:** Inicie o serviço com `./mvnw quarkus:dev` e faça duas requisições ao endpoint:
{: .fs-3 }

```sh
curl http://localhost:8080/catalogo
curl http://localhost:8080/catalogo
```

**Teste:**
Inspecione as métricas no endpoint `/q/metrics` filtrando pelo nome do contador:
{: .fs-3 }

```sh
curl -s http://localhost:8080/q/metrics | grep catalogo_buscas_total
```

**Resultado esperado:**
{: .fs-3 }

```text
catalogo_buscas_total 2.0
```

---

### Exercício 2: Medindo a duração de uma operação com `@Timed` ⏱️

**Objetivo:** Medir automaticamente a latência de um método de negócio sem escrever código de contagem manual.
{: .fs-3 }

**Passo 1:** Adicione um novo método no `CatalogoResource.java`:
{: .fs-3 }

```java
    @GET
    @Path("/relatorio")
    @Produces(MediaType.TEXT_PLAIN)
    @io.micrometer.core.annotation.Timed(value = "catalogo.relatorio.tempo")
    public String gerarRelatorio() throws InterruptedException {
        // Simula um atraso de 100ms
        Thread.sleep(100);
        return "Relatorio gerado com sucesso!";
    }
```

**Passo 2:** Faça uma requisição ao endpoint:
{: .fs-3 }

```sh
curl http://localhost:8080/catalogo/relatorio
```

**Teste:**
Consulte as métricas do timer gerado:
{: .fs-3 }

```sh
curl -s http://localhost:8080/q/metrics | grep catalogo_relatorio_tempo
```

**Resultado esperado:**
Você verá as três séries temporais criadas automaticamente pelo Micrometer:
{: .fs-3 }

* `catalogo_relatorio_tempo_seconds_count 1.0` (executado 1 vez)
* `catalogo_relatorio_tempo_seconds_sum ...` (tempo acumulado próximo a 0.1s)
* `catalogo_relatorio_tempo_seconds_max ...` (maior duração registrada)
{: .fs-3 }

---

### Exercício 3: Monitorando estado dinâmico com `Gauge` 🌡️

**Objetivo:** Criar um medidor para acompanhar uma variável que pode aumentar e diminuir.
{: .fs-3 }

**Passo 1:** Crie a classe `src/main/java/dev/pw2/FilaResource.java`:
{: .fs-3 }

```java
package dev.pw2;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/fila")
@Produces(MediaType.TEXT_PLAIN)
public class FilaResource {

    private final AtomicInteger tamanhoFila = new AtomicInteger(0);

    public FilaResource(MeterRegistry registry) {
        Gauge.builder("fila.tarefas.pendentes", tamanhoFila, AtomicInteger::get)
            .description("Numero de tarefas pendentes na fila")
            .register(registry);
    }

    @GET
    @Path("/adicionar")
    public String adicionar() {
        return "Fila atual: " + tamanhoFila.incrementAndGet();
    }

    @GET
    @Path("/concluir")
    public String concluir() {
        return "Fila atual: " + tamanhoFila.decrementAndGet();
    }
}
```

**Passo 2:** Adicione 2 tarefas e conclua 1:
{: .fs-3 }

```sh
curl http://localhost:8080/fila/adicionar
curl http://localhost:8080/fila/adicionar
curl http://localhost:8080/fila/concluir
```

**Teste:**
Verifique o valor atual registrado no endpoint de métricas:
{: .fs-3 }

```sh
curl -s http://localhost:8080/q/metrics | grep fila_tarefas_pendentes
```

**Resultado esperado:**
{: .fs-3 }

```text
fila_tarefas_pendentes 1.0
```

---

### Exercício 4: Enriquecendo métricas com Tags Dimensionais 🏷️

**Objetivo:** Usar tags para permitir que os dados sejam filtrados por categoria.
{: .fs-3 }

**Passo 1:** Crie o endpoint com tags no `CatalogoResource.java`:
{: .fs-3 }

```java
    @GET
    @Path("/pedido/{tipo}")
    @Produces(MediaType.TEXT_PLAIN)
    public String registrarPedido(@jakarta.ws.rs.PathParam("tipo") String tipo) {
        // Incrementa o contador com a tag correspondente ao tipo do pedido
        registry.counter("pedidos.recebidos.total", "tipo", tipo).increment();
        return "Pedido registrado do tipo: " + tipo;
    }
```

**Passo 2:** Execute pedidos de tipos diferentes:
{: .fs-3 }

```sh
curl http://localhost:8080/catalogo/pedido/digital
curl http://localhost:8080/catalogo/pedido/digital
curl http://localhost:8080/catalogo/pedido/fisico
```

**Teste:**
Consulte o endpoint de métricas:
{: .fs-3 }

```sh
curl -s http://localhost:8080/q/metrics | grep pedidos_recebidos_total
```

**Resultado esperado:**
O Prometheus exibirá duas linhas separadas pela tag `tipo`:
{: .fs-3 }

```text
pedidos_recebidos_total{tipo="digital"} 2.0
pedidos_recebidos_total{tipo="fisico"} 1.0
```

---

### Exercício 5: Explorando as métricas automáticas do HTTP 🤖

**Objetivo:** Observar as métricas que o Quarkus gera sozinho para todas as requisições HTTP.
{: .fs-3 }

**Teste:**
Execute o comando para filtrar as métricas do servidor HTTP:
{: .fs-3 }

```sh
curl -s http://localhost:8080/q/metrics | grep http_server_requests_seconds_count
```

**Resultado esperado:**
Você verá linhas listando cada endpoint chamado, acompanhadas de tags como `method="GET"`, `outcome="SUCCESS"`, `status="200"` e `uri="/catalogo"` sem que você precise ter escrito código para isso!
{: .fs-3 }

---

## Teste seus conhecimentos 🧠

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/metrics/questions.html"
        title="Metrics"
        width="90%" height="500"
        style="border:none;">
    </iframe>
</center>

## Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.
* Guia Oficial de Métricas do Quarkus com Micrometer: [https://quarkus.io/guides/telemetry-micrometer](https://quarkus.io/guides/telemetry-micrometer)
* Documentação Oficial do Micrometer: [https://micrometer.io/docs](https://micrometer.io/docs)
* Documentação do Prometheus: [https://prometheus.io/docs/introduction/overview/](https://prometheus.io/docs/introduction/overview/)
{: .fs-3 }

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
