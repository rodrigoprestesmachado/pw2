---
layout: default
title: Trace e Log
parent: Micro Serviços Intermediário
nav_order: 12
---

# Trace e Log

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/logging/slides/index.html#/"
        title="Trace e Log" width="90%" height="500" style="border:none;">
    </iframe>
</center>

Em microsserviços, uma única requisição do usuário pode passar por vários
serviços — API Gateway, autenticação, pedidos, pagamento, estoque. Quando algo
dá errado ou fica lento, a pergunta é sempre a mesma: **onde** e **por quê**?

Duas ferramentas respondem perguntas diferentes:

| Ferramenta | Pergunta que responde | Exemplo |
|------------|----------------------|---------|
| **Trace** (Jaeger) | *Qual o caminho da requisição e onde está o gargalo?* | "O checkout demorou 2 s porque o serviço de pagamento levou 1,8 s" |
| **Log** (Graylog) | *O que aconteceu em cada ponto?* | "Pedido #1234 rejeitado: saldo insuficiente" |

Este tutorial mostra como configurar o [Jaeger](https://www.jaegertracing.io)
para *trace* distribuído e o [Graylog](https://www.graylog.org) para
centralizar *logs* — ambos integrados a um projeto Quarkus.

## O que você vai aprender

Ao final deste material, você será capaz de:

1. Explicar a diferença entre *trace* e *log* em sistemas distribuídos.
1. Subir o Jaeger e o Graylog com Docker Compose.
1. Configurar um projeto Quarkus para enviar *traces* e *logs* a essas ferramentas.
1. Correlacionar *logs* e *traces* pelo `traceId`.

---

## Conceitos essenciais

Antes de configurar as ferramentas, vale fixar três ideias:

**Trace** — o registro completo de uma requisição de ponta a ponta. Pense nele
como o "raio-X" de um pedido: mostra por quais serviços passou e quanto tempo
cada etapa levou.

**Span** — cada etapa dentro de um *trace*. Por exemplo, um *trace* de checkout
pode ter *spans* para "validar carrinho", "reservar estoque" e "processar
pagamento".

**Log** — uma mensagem textual registrada em um ponto específico da aplicação.
Diferente do *trace*, o *log* descreve *o que aconteceu* (erro, aviso, evento de
negócio), não necessariamente o tempo de cada etapa.

```
Requisição do usuário
        │
        ▼
   ┌─────────┐     ┌─────────┐     ┌────────-─┐
   │ API     │────▶│ Pedidos │────▶│ Pagamento│
   └─────────┘     └─────────┘     └────────-─┘
        │               │               │
     span 1          span 2          span 3
        └───────────────┴───────────────┘
                    = 1 trace
```

O poder real aparece quando você **liga os dois**: um *log* com `traceId`
permite saltar do texto do erro direto para o *trace* visual no Jaeger.

---

## Jaeger — rastreamento distribuído

Inspirado no [Dapper](https://research.google/pubs/pub36356/) e no
[OpenZipkin](https://zipkin.io/), o Jaeger foi desenvolvido pela Uber e
implementa a especificação [OpenTelemetry](https://opentelemetry.io/). Ele
coleta *spans* de cada serviço e monta o fluxo completo da requisição.

### Principais funcionalidades

* Rastreamento de requisições entre serviços
* Visualização do fluxo de trabalho (diagrama de *spans*)
* Análise de desempenho e identificação de gargalos
* Armazenamento e busca de *traces* históricos
* Integração com OpenTelemetry e outras ferramentas de observabilidade

### Pontos de atenção

Como toda ferramenta de observabilidade, o Jaeger tem custos:

* **Overhead** — coletar *spans* consome CPU e memória.
* **Armazenamento** — *traces* acumulam dados ao longo do tempo.
* **Curva de aprendizado** — interpretar *traces* exige prática.

Na prática, isso raramente impede o uso em desenvolvimento e homologação; em
produção, costuma-se usar *sampling* (amostragem) para reduzir o volume.

### Passo 1 — Subir o Jaeger com Docker Compose

Crie um arquivo `docker-compose.yml` com o conteúdo abaixo:

```yml
version: '3.9'
services:

  jaeger:
    image: jaegertracing/all-in-one:latest
    ports:
      - "16686:16686" # Jaeger UI
      - "14268:14268" # Receive legacy OpenTracing traces, optional
      - "4317:4317"   # OTLP gRPC receiver
      - "4318:4318"   # OTLP HTTP receiver, optional
      - "14250:14250" # Receive from external otel-collector, optional
    environment:
      - COLLECTOR_OTLP_ENABLED=true
```

No diretório do arquivo, execute:

```bash
docker-compose up -d
```

Para parar e remover os contêineres:

```bash
docker-compose down
```

Abra o navegador em `http://localhost:16686` para acessar a interface do
Jaeger. Nela você busca *traces* por serviço, operação e duração.

### Passo 2 — Configurar o Jaeger no Quarkus

**2.1. Adicione a extensão OpenTelemetry**

```bash
./mvnw quarkus:add-extension -Dextensions='opentelemetry'
```

Ou no `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-opentelemetry</artifactId>
</dependency>
```

**2.2. Configure o `application.properties`**

```properties
quarkus.otel.service.name=myservice
quarkus.otel.exporter.otlp.traces.endpoint=http://localhost:4317
quarkus.log.console.format=%d{HH:mm:ss} %-5p traceId=%X{traceId}, parentId=%X{parentId}, spanId=%X{spanId}, sampled=%X{sampled} [%c{2.}] (%t) %s%e%n
```

O que cada linha faz:

| Propriedade | Função |
|-------------|--------|
| `quarkus.otel.service.name` | Nome do serviço exibido no Jaeger (ex.: `myservice`) |
| `quarkus.otel.exporter.otlp.traces.endpoint` | Endereço do coletor OTLP do Jaeger (`localhost:4317`) |
| `quarkus.log.console.format` | Inclui `traceId` e `spanId` nos *logs* do console |

A terceira linha é importante: ela permite **correlacionar** um *log* no
console (ou no Graylog) com o *trace* correspondente no Jaeger.

**2.3. Teste**

1. Inicie o Jaeger (`docker-compose up -d`).
1. Execute o projeto Quarkus (`./mvnw quarkus:dev`).
1. Faça algumas requisições HTTP à aplicação.
1. Abra `http://localhost:16686`, selecione o serviço `myservice` e clique em
   **Find Traces**.

Você deve ver *traces* listados com a duração de cada requisição e os *spans*
internos.

---

## Graylog — centralização de logs

Enquanto o Jaeger mostra *onde* e *quanto tempo*, o Graylog responde *o que
aconteceu*. Ele coleta *logs* de várias fontes (aplicações, SO, rede),
armazena em um índice pesquisável e oferece interface web para busca, alertas
e dashboards.

### Principais funcionalidades

* Coleta de *logs* de múltiplas fontes
* Busca e filtros na interface web
* Alertas quando eventos importantes ocorrem
* Gráficos e métricas derivadas dos *logs*
* Integrações com Kafka, Prometheus e outras ferramentas

### Pontos de atenção

* **Configuração inicial** — exige Elasticsearch, MongoDB e ajuste de *inputs*.
* **Consumo de recursos** — Elasticsearch e Graylog demandam memória.
* **Conhecimento técnico** — filtros, *pipelines* e alertas exigem prática.

Para aprendizado local, o `docker-compose` abaixo é suficiente.

### Passo 1 — Subir o Graylog com Docker Compose

```yml
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.10.2
    ports:
      - "9200:9200"
    environment:
      ES_JAVA_OPTS: "-Xms512m -Xmx512m"
      discovery.type: "single-node"
    networks:
      - graylog

  mongo:
    image: mongo:6.0
    networks:
      - graylog

  graylog:
    image: graylog/graylog:5.1
    ports:
      - "9000:9000"   # Interface web
      - "12201:12201/udp" # GELF UDP
      - "1514:1514"   # GELF TCP
    environment:
      GRAYLOG_HTTP_EXTERNAL_URI: "http://127.0.0.1:9000/"
      GRAYLOG_PASSWORD_SECRET: "forpasswordencryption"
      GRAYLOG_ROOT_PASSWORD_SHA2: "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
    networks:
      - graylog
    depends_on:
      - elasticsearch
      - mongo

networks:
  graylog:
    driver: bridge
```

Execute `docker-compose up -d` e acesse `http://localhost:9000`.

* **Usuário:** `admin`
* **Senha:** `admin`

### Passo 2 — Criar um input GELF

O Graylog precisa de um *input* — uma "porta de entrada" que define como os
*logs* chegam. Vamos usar **GELF UDP** na porta `12201`.

**Opção A — pela interface web**

1. Acesse `http://localhost:9000` e faça login.
1. Vá em **System → Inputs**.
1. Selecione **GELF UDP** e clique em **Launch new input**.
1. Defina a porta `12201` e salve.

**Opção B — via linha de comando**

```sh
curl -H "Content-Type: application/json" \
     -H "Authorization: Basic YWRtaW46YWRtaW4=" \
     -H "X-Requested-By: curl" \
     -X POST \
     -d '{"title":"Application log input","configuration":{"recv_buffer_size":262144,"bind_address":"0.0.0.0","port":12201,"decompress_size_limit":8388608},"type":"org.graylog2.inputs.gelf.udp.GELFUDPInput","global":true}' \
     http://localhost:9000/api/system/inputs
```

### O que é GELF?

**GELF** (*Graylog Extended Log Format*) é um formato JSON que estrutura os
*logs* com campos extras — nível, host, *timestamp*, campos customizados. Isso
facilita buscas como "todos os erros do serviço X nas últimas 2 horas".

### Passo 3 — Configurar o Graylog no Quarkus

**3.1. Adicione a extensão GELF**

```bash
./mvnw quarkus:add-extension -Dextensions='logging-gelf'
```

Ou no `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-logging-gelf</artifactId>
</dependency>
```

**3.2. Configure o `application.properties`**

```properties
quarkus.log.handler.gelf.enabled=true
quarkus.log.handler.gelf.host=localhost
quarkus.log.handler.gelf.port=12201
```

Com isso, o Quarkus envia *logs* em formato GELF para o Graylog na porta
`12201`.

**3.3. Teste**

1. Certifique-se de que o Graylog está rodando e o *input* GELF UDP está ativo.
1. Execute o projeto Quarkus.
1. Faça requisições à aplicação.
1. No Graylog, vá em **Search** e busque por mensagens recentes.

Você deve ver os *logs* da aplicação aparecendo em tempo real.

---

## Colocando tudo junto

Para observabilidade completa em desenvolvimento, use Jaeger e Graylog ao
mesmo tempo. A configuração combinada no `application.properties` fica assim:

```properties
# Jaeger (trace)
quarkus.otel.service.name=myservice
quarkus.otel.exporter.otlp.traces.endpoint=http://localhost:4317

# Graylog (log)
quarkus.log.handler.gelf.enabled=true
quarkus.log.handler.gelf.host=localhost
quarkus.log.handler.gelf.port=12201

# Correlacionar logs com traces
quarkus.log.console.format=%d{HH:mm:ss} %-5p traceId=%X{traceId}, parentId=%X{parentId}, spanId=%X{spanId}, sampled=%X{sampled} [%c{2.}] (%t) %s%e%n
```

### Fluxo de depuração típico

1. Um usuário reporta lentidão no checkout.
1. Você busca no **Graylog** por erros ou avisos recentes.
1. Copia o `traceId` de um *log* suspeito.
1. Cola o `traceId` no **Jaeger** e visualiza qual *span* demorou mais.
1. Com o serviço identificado, volta ao código ou aos *logs* daquele *span*.

Esse fluxo — **log → traceId → trace → causa** — é o padrão mais usado em
produção para depurar microsserviços.

### Checklist rápido

| Etapa | Jaeger | Graylog |
|-------|--------|---------|
| Docker Compose rodando | `docker-compose up -d` | `docker-compose up -d` |
| Interface acessível | `http://localhost:16686` | `http://localhost:9000` |
| Extensão Quarkus | `quarkus-opentelemetry` | `quarkus-logging-gelf` |
| Porta de comunicação | `4317` (OTLP gRPC) | `12201` (GELF UDP) |
| Verificação | *Traces* aparecem após requisições HTTP | *Logs* aparecem em Search |

---

# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* Using OpenTelemetry. Disponível em: [https://quarkus.io/guides/opentelemetry](https://quarkus.io/guides/opentelemetry)

* Centralized Log Management. Disponível em: [https://quarkus.io/guides/centralized-log-management](https://quarkus.io/guides/centralized-log-management)

* Jaeger. Disponível em: [https://www.jaegertracing.io](https://www.jaegertracing.io)

* Graylog. Disponível em: [https://www.graylog.org](https://www.graylog.org)

<center>
    <a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a>
    <br/>
    <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
