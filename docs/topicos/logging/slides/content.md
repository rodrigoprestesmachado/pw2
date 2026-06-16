<!-- .slide: data-background-opacity="0.3" data-background-image="https://pw2.rpmhub.dev/topicos/logging/slides/img/title.jpg" data-transition="convex" -->
# Trace e Log
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Por que observar?
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Uma requisição pode passar por **vários serviços**: API, pedidos, pagamento...
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Quando algo falha ou fica lento, surgem duas perguntas:
<!-- .element: style="margin-bottom:30px; font-size: 28px; color:white" -->

  * **Onde** está o gargalo? → *Trace* (Jaeger)
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

  * **O que** aconteceu? → *Log* (Graylog)
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Trace vs Log
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* **Trace** — caminho completo da requisição e tempo de cada etapa
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* **Span** — cada etapa dentro de um trace
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* **Log** — mensagem textual: erro, aviso, evento de negócio
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* **traceId** — liga o log ao trace no Jaeger
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Jaeger
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Ferramenta de **trace distribuído** (Uber, OpenTelemetry)
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Rastreia requisições entre serviços
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Visualiza fluxo, tempos e gargalos
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* UI: `http://localhost:16686`
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Jaeger — pontos de atenção
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* **Overhead** — coletar spans consome recursos
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* **Armazenamento** — traces acumulam ao longo do tempo
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* **Curva de aprendizado** — interpretar traces exige prática
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Em produção: usar **sampling** (amostragem)
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Jaeger no Quarkus
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Extensão: `quarkus-opentelemetry`
<!-- .element: style="margin-bottom:20px; font-size: 25px; color:white" -->

```bash
./mvnw quarkus:add-extension -Dextensions='opentelemetry'
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```properties
quarkus.otel.service.name=myservice
quarkus.otel.exporter.otlp.traces.endpoint=http://localhost:4317
```
<!-- .element: style="margin-bottom:30px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* Porta OTLP: **4317** | UI: **16686**
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Graylog
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Centraliza **logs** de várias fontes
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Interface web para busca, alertas e dashboards
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Recebe logs no formato **GELF** (*Graylog Extended Log Format*)
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* UI: `http://localhost:9000` (admin / admin)
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Graylog — pontos de atenção
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Configuração inicial mais complexa (Elasticsearch + MongoDB)
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Alto consumo de memória
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

* Filtros, pipelines e alertas exigem prática
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Graylog no Quarkus
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Extensão: `quarkus-logging-gelf`
<!-- .element: style="margin-bottom:20px; font-size: 25px; color:white" -->

```bash
./mvnw quarkus:add-extension -Dextensions='logging-gelf'
```
<!-- .element: style="margin-bottom:30px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```properties
quarkus.log.handler.gelf.enabled=true
quarkus.log.handler.gelf.host=localhost
quarkus.log.handler.gelf.port=12201
```
<!-- .element: style="margin-bottom:30px; font-size: 16px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* Criar **input** GELF UDP na porta **12201**
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Correlacionar logs e traces
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

```properties
quarkus.log.console.format=%d{HH:mm:ss} %-5p traceId=%X{traceId}, spanId=%X{spanId} [%c{2.}] %s%e%n
```
<!-- .element: style="margin-bottom:30px; font-size: 15px; font-family: arial; color:black; background-color: #F2FAF3;" -->

* Fluxo de depuração:
<!-- .element: style="margin-bottom:20px; font-size: 28px; color:white" -->

  1. Buscar erro no **Graylog**
  <!-- .element: style="margin-bottom:20px; font-size: 23px; color:white" -->

  2. Copiar o **traceId**
  <!-- .element: style="margin-bottom:20px; font-size: 23px; color:white" -->

  3. Colar no **Jaeger** → ver qual span demorou
  <!-- .element: style="margin-bottom:20px; font-size: 23px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Checklist rápido
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* **Jaeger** — extensão `opentelemetry`, porta **4317**, UI **:16686**
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* **Graylog** — extensão `logging-gelf`, porta **12201**, UI **:9000**
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* Tutorial completo: documento **Trace e Log** no site
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#D6B2FF" data-transition="convex" -->
## Teste seus conhecimentos
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:back; font-family: Marker Felt;" -->

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/logging/slides/questions.html"
        title="Trace e Logging"
        width="90%" height="500"
        style="border:none;">
    </iframe>
</center>
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
# Referências 📚
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Using OpenTelemetry. Disponível em: [https://quarkus.io/guides/opentelemetry](https://quarkus.io/guides/opentelemetry)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

* Centralized Log Management. Disponível em: [https://quarkus.io/guides/centralized-log-management](https://quarkus.io/guides/centralized-log-management)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

* Jaeger. Disponível em: [https://www.jaegertracing.io](https://www.jaegertracing.io)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

* Graylog. Disponível em: [https://www.graylog.org](https://www.graylog.org)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
<!-- .element: style="margin-top:100px; font-size: 15px; font-family: Bradley Hand" -->
