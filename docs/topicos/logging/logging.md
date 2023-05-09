# Trace e Log

Esse tutorial mostra os principais passos para você adicionar uma ferramenta
de *trace* chamada Jaeger e também uma ferramenta para consolidar __logs__ chamada
Graylog

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/logging/slides/index.html#/"
        title="Trace e Log" width="90%" height="500" style="border:none;">
    </iframe>
</center>

## Jaeger

Inspirado no [Dapper](https://research.google/pubs/pub36356/) e [OpenZipkin](https://zipkin.io/) o [Jaeger](https://www.jaegertracing.io) foi desenvolvido pela Uber e é uma ferramenta de *trace* distribuído. O *trace* é o registo de uma requisição de ponta a ponta em um sistema distribuído. Ele fornece visibilidade do fluxo de trabalho de um serviço (*trace*), permitindo que os desenvolvedores vejam o desempenho e o comportamento do serviço em tempo real.

O [Jaeger](https://www.jaegertracing.io) opera por meio do rastreamento dos *requests*, registrando informações sobre cada solicitação à medida que ela passa pelos diferentes
serviços do sistema. Esses registros são coletados e analisados pelo Jaeger, permitindo 
que os desenvolvedores vejam como as solicitações estão sendo processadas e onde ocorrem possíveis gargalos ou falhas.

Com o Jaeger, os desenvolvedores podem identificar rapidamente problemas de
desempenho e depurar falhas em aplicativos distribuídos complexos, ajudando a melhorar a eficiência e a confiabilidade do sistema como um todo.

Entre as principais funcionalidades do Jaeger estão: Rastreamento de
solicitações, visualização do fluxo de trabalho, análise de desempenho, alertas e notificações, armazenamento no longo prazo e integração com outras ferramentas.

Entretanto, o Jaeger possui algumas desvantagens, são elas: impacto no
desempenho do sistema (_overhead_), custo adicional, gerenciamento de dados, conhecimento especializado e integração com algumas ferramentas pode ser um desafio.

O Jaeger é uma aplicação que segue implementa a especificação [OpenTelemetry](https://quarkus.io/guides/opentelemetry).

Para executar o Jaeger utilize, por exemplo, o `docker-compose.yml` abaixo:

```yml
version: '3.9'
services:

  jaeger:
    image: jaegertracing/all-in-one:latest
    ports:
      - "16686:16686" # Jaeger UI
      - "14268:14268" # Receive legacy OpenTracing traces, optional
      - "4317:4317"   # OTLP gRPC receiver
      - "4318:4318"   # OTLP HTTP receiver, not yet used by Quarkus, optional
      - "14250:14250" # Receive from external otel-collector, optional
    environment:
      - COLLECTOR_OTLP_ENABLED=true
```

Para executar um arquivo `docker-compose.yml`, siga os seguintes passos:

1. Certifique-se de ter o [Docker](https://www.docker.com/) e o [Docker Compose](https://docs.docker.com/compose/) instalados em sua máquina.
1. Navegue até o diretório onde o arquivo `docker-compose.yml` está localizado.
   Abra um terminal ou prompt de comando no diretório em questão.
1. Execute o comando `docker-compose up -d` para iniciar todos os contêineres
   definidos no arquivo `docker-compose.yml`. Este comando irá baixar as imagens
   necessárias do Docker Hub e executar os contêineres em questão.
1. Aguarde até que todos os contêineres sejam iniciados e estejam prontos para
   uso.
1. Cado deseje interromper e remover todos os contêineres definidos no arquivo
   `docker-compose.yml`, execute o comando `docker-compose down`.

### Jaeger com Quarkus

Vamos aos passos de configuração do Jaeger em um projeto Quarkus: Primeiro,
instale a extensão `quarkus-opentelemetry` no seu projeto. Depois,
configure o seu `application.properties` com as configurações do Jaeger:

```sh
quarkus.otel.service.name=myservice
quarkus.otel.exporter.otlp.traces.endpoint=http://localhost:4317
quarkus.log.console.format=%d{HH:mm:ss} %-5p traceId=%X{traceId}, parentId=%X{parentId}, spanId=%X{spanId}, sampled=%X{sampled} [%c{2.}] (%t) %s%e%n
```

🚨 A configuração acima mostra como configurar a integração do Jaeger com o
Quarkus.

1. A primeira linha `quarkus.otel.service.name` define o nome do
  serviço que está sendo monitorado. Nesse caso, "myservice" é o nome do serviço.
1. A segunda linha `quarkus.otel.exporter.otlp.traces.endpoint` define o endpoint aonde
  informações de log serão armazenadas. Neste caso, o endpoint é `http://localhost:4317`.
1. A quarta linha `quarkus.log.console.format=%d{HH:mm:ss} %-5p
  traceId=%X{traceId}, parentId=%X{parentId}, spanId=%X{spanId},
  sampled=%X{sampled} [%c{2.}] (%t) %s%e%n` define o formato de log que será
  usado.

## GrayLog

O [Graylog](https://www.graylog.org) é uma ferramenta de gerenciamento e análise de logs que permite coletar, processar e analisar registros de várias fontes, como aplicativos, serviços, sistemas operacionais e dispositivos de rede.

O [Graylog](https://www.graylog.org) oferece uma interface da Web para pesquisar e analisar logs, bem como ferramentas de alerta para notificar as equipes quando ocorrem eventos importantes. Além disso, o Graylog oferece recursos de análise de log, como gráficos e métricas, que podem ajudar as equipes de operações e desenvolvimento a identificar tendências de desempenho e problemas recorrentes.

O [Graylog](https://www.graylog.org) é uma ferramenta de código aberto que
oferece uma variedade de integrações com outras ferramentas populares,
[Kafka](https://kafka.apache.org), [Prometheus](https://prometheus.io) e outros, permitindo que os usuários personalizem a plataforma de acordo com suas necessidades.

Para colocar o GrayLog para rodar utilize, por exemplo, o `docker-compose.yml`
abaixo:

```yml
version: '3.9'
services:

  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:6.8.2
    ports:
      - "9200:9200" # Elasticsearch HTTP
    environment:
      ES_JAVA_OPTS: "-Xms512m -Xmx512m" # Maximum memory allocation pool
    networks:
      - graylog # Use the same network defined above

  mongo:
    image: mongo:4.0 
    networks:
      - graylog # Use the same network defined above

  graylog:
    image: graylog/graylog:4.2.3-1-jre11
    ports:
      - "9000:9000" # Graylog web interface and REST API
      - "12201:12201/udp" # GELF UDP
      - "1514:1514" # GELF TCP
    environment:
      - TZ=America/Sao_Paulo
      - GRAYLOG_ROOT_TIMEZONE=America/Sao_Paulo
      - GRAYLOG_PASSWORD_SECRET=somepasswordsomepassword
      # Password: admin
      - GRAYLOG_ROOT_PASSWORD_SHA2=8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
      - GRAYLOG_HTTP_EXTERNAL_URI=http://localhost:9000/
      - GRAYLOG_ELASTICSEARCH_INDEX_PREFIX=graylog

    networks:
      - graylog # the network named graylog
    depends_on:
      - elasticsearch
      - mongo

networks:
  graylog:
    driver: bridge # Use the default bridge driver
```

Depois de executar o comando `docker-compose up -d`, você pode acessar o GrayLog.

Agora, vamos aos passos para configurar o GrayLog no Quarkus: Primeiro, instale a extensão `quarkus-logging-gelf` no seu projeto. Depois, configure o
`application.properties` para permitir que as mensagens do seu projeto possam
ser encaminhadas para o GrayLog.

```sh
quarkus.log.handler.gelf.enabled=true
quarkus.log.handler.gelf.host=localhost
quarkus.log.handler.gelf.port=12201
```

Esse código configura as propriedades de registro (_logging_) do Quarkus para
enviar _logs_ em formato GELF (Graylog Extended Log Format) para um servidor
local hospedado em `localhost` na porta `12201`.

O GELF é um formato de registro que permite a estruturação de dados adicionais
nos _logs_, como campos adicionais, tags e outras informações importantes, além das mensagens padrão de registro. Essas informações adicionais podem ser usadas para melhorar a análise de _logs_ e a depuração de problemas no sistema.

Ao configurar essas propriedades, o Quarkus enviará _logs_ formatados em GELF para o servidor hospedado em `localhost` na porta `12201`, permitindo que o servidor colete e analise os _logs_ do aplicativo Quarkus.

Finalmente, crie um "*input*" no GrayLog:

```sh
curl -H "Content-Type: application/json" -H "Authorization: Basic YWRtaW46YWRtaW4=" -H "X-Requested-By: curl" -X POST -v -d \
'{"title":"Application log input","configuration":{"recv_buffer_size":262144,"bind_address":"0.0.0.0","port":12201,"decompress_size_limit":8388608},"type":"org.graylog2.inputs.gelf.udp.GELFUDPInput","global":true}' \
http://localhost:9000/api/system/inputs
```

🚨 Um "*input*" também pode ser criado pelo console de administração do
GrayLog(System → Input → Select GELF UDP).

Na prática, um _input_ é uma fonte de dados que o Graylog pode monitorar e coletar informações. Por exemplo, se você tem um aplicativo executando em um servidor, é possível configurar um _input_ para coletar os _logs_ desse servidor.

Embora o [Graylog](https://www.graylog.org) seja uma plataforma de
gerenciamento e análise de logs robusta e altamente escalável, há algumas
desvantagens a serem consideradas:

1. Configuração complexa: A configuração inicial do Graylog pode ser complexa,
   principalmente se você tiver muitas fontes de dados diferentes ou se precisar criar filtros e alertas personalizados.
2. Requer habilidades técnicas: Para aproveitar ao máximo o Graylog, é
   necessário ter conhecimento técnico em sistemas operacionais, redes,
   bancos de dados, entre outras áreas, o que pode ser um desafio para equipes
   que não possuem essas habilidades internamente.
3. Alto consumo de recursos: O Graylog é uma plataforma de log que requer muitos recursos para executar de forma eficiente, o que pode ser um problema para organizações com limitações de recursos de hardware ou nuvem.

# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* Using OpenTelemetry. Disponível em: [https://quarkus.io/guides/opentelemetry](https://quarkus.io/guides/opentelemetry)

* Centralized Log Management. Disponível em: [https://quarkus.io/guides/centralized-log-management](https://quarkus.io/guides/centralized-log-management)

* Jaeger. Disponível em: [https://www.jaegertracing.io](https://www.jaegertracing.io)

* GrayLog. Disponível em: [https://www.graylog.org](https://www.graylog.org)

<center>
    <a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a>
    <br/>
    <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>