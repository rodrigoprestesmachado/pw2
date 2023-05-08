# Trace e Log

Esse tutorial mostra os principais passos para você adicionar uma ferramenta
de *trace* chamada Jaeger e também uma ferramenta para consolidar _logs_ chamada
Graylog

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/logging/slides/index.html#/"
        title="Trace e Log" width="90%" height="500" style="border:none;">
    </iframe>
</center>

## Jaeger

Inspirado no [Dapper](https://research.google/pubs/pub36356/) e [OpenZipkin](https://zipkin.io/)
O Jaeger foi desenvolvido pela Uber e é uma ferramenta de *trace* distribuído.
Ele fornece visibilidade do fluxo de trabalho de um serviço (*trace*), permitindo
que os desenvolvedores vejam o desempenho e o comportamento do serviço em tempo
real.

O Jaeger opera por meio do rastreamento de solicitações de serviços (*requests*),
registrando informações sobre cada solicitação à medida que ela passa pelos
diferentes serviços do sistema. Esses registros são coletados e analisados pelo
Jaeger, permitindo que os desenvolvedores vejam como as solicitações estão sendo
processadas e onde ocorrem possíveis gargalos ou falhas.

Com o Jaeger, os desenvolvedores podem identificar rapidamente problemas de
desempenho e depurar problemas de falhas em aplicativos distribuídos complexos,
ajudando a melhorar a eficiência e a confiabilidade do sistema como um todo.

Entre as principais funcionalidades do Jaeger estão: Rastreamento de
solicitações, visualização do fluxo de trabalho, análise de desempenho, alertas
e notificações, armazenamento em longo prazo e integração com outras ferramentas.

Entretanto, o Jaeger possui algumas desvantagens, são elas: impacto no
desempenho do sistema (_overhead_), gerenciamento de dados,
conhecimento especializado e integração com algumas ferramentas pode ser um
desafio

O Jaeger é uma aplicação que segue implementa a especificação [MicroProfile OpenTracing](https://github.com/eclipse/microprofile-opentracing/) implementada por meio do [SmallRye OpenTracing](https://github.com/smallrye/smallrye-opentracing/).

Para colocar o Jaeger para rodar utilize, por exemplo, o `docker-compose.yml` abaixo:

```yml
version: '3.9'
services:

  jaeger:
    image: jaegertracing/all-in-one:latest
    ports:
        - "5775:5775/udp"
        - "6831:6831/udp"
        - "6832:6832/udp"
        - "5778:5778"
        - "16686:16686"
        - "14268:14268"
```

Vamos aos passos de configuração do Jaeger em um projeto Quarkus: Primeiro,
instale a extensão `quarkus-smallrye-opentracing` no seu projeto. Depois,
configure o seu `application.properties` com as configurações do Jaeger:

```sh
quarkus.jaeger.service-name=myservice
quarkus.jaeger.sampler-type=const
quarkus.jaeger.sampler-param=1
quarkus.log.console.format=%d{HH:mm:ss} %-5p traceId=%X{traceId}, parentId=%X{parentId}, spanId=%X{spanId}, sampled=%X{sampled} [%c{2.}] (%t) %s%e%n
```

🚨 No exemplo acima, "*myservice*" será o nome do ser serviço no Jaeger.

## GrayLog

Graylog é uma plataforma de gerenciamento e análise de _logs_ que permite
coletar, processar e analisar registros de várias fontes, como aplicativos,
serviços, sistemas operacionais e dispositivos de rede.

O  GrayLog oferece uma interface da web para pesquisar e analisar _logs_,
bem como ferramentas de alerta para notificar as equipes quando ocorrem
eventos importantes. O Graylog oferece recursos de pesquisa avançada, permitindo
que os usuários encontrem rapidamente informações específicas em seus logs.
Além disso, o Graylog oferece recursos de análise de log, como gráficos e
métricas, que podem ajudar as equipes de operações e desenvolvimento a
identificar tendências de desempenho e problemas recorrentes.

O Graylog é uma ferramenta de código aberto que oferece uma variedade de
integrações com outras ferramentas populares, [Kafka](https://kafka.apache.org),
[Prometheus](https://prometheus.io) e outros, permitindo que os usuários
personalizem a plataforma de acordo com suas necessidades.

Para colocar o GrayLog para rodar utilize, por exemplo, o `docker-compose.yml` abaixo:

```yml
version: '3.9'
services:

  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:6.8.2
    ports:
      - "9200:9200"
    environment:
      ES_JAVA_OPTS: "-Xms512m -Xmx512m"
    networks:
      - graylog

  mongo:
    image: mongo:4.0
    networks:
      - graylog

  graylog:
    image: graylog/graylog:4.2.3-1-jre11
    ports:
      - "9000:9000"
      - "12201:12201/udp"
      - "1514:1514"
    environment:
      - TZ=America/Sao_Paulo
      - GRAYLOG_ROOT_TIMEZONE=America/Sao_Paulo
      - GRAYLOG_PASSWORD_SECRET=somepasswordsomepassword
      # Password: admin
      - GRAYLOG_ROOT_PASSWORD_SHA2=8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
      - GRAYLOG_HTTP_EXTERNAL_URI=http://localhost:9000/
      - GRAYLOG_ELASTICSEARCH_INDEX_PREFIX=graylog

    networks:
      - graylog
    depends_on:
      - elasticsearch
      - mongo

networks:
  graylog:
    driver: bridge
```

Agora, vamos aos passos para configurar o GrayLog no Quarkus: Primeiro, instale
a extensão `quarkus-logging-gelf` no seu projeto. Depois, configure o
`application.properties` para permitir que as mensagens do seu projeto possam
ser encaminhadas para o GrayLog.

```sh
quarkus.log.handler.gelf.enabled=true
quarkus.log.handler.gelf.host=localhost
quarkus.log.handler.gelf.port=12201
```

Finalmente, crie um "*input*" no GrayLog:

```sh
curl -H "Content-Type: application/json" -H "Authorization: Basic YWRtaW46YWRtaW4=" -H "X-Requested-By: curl" -X POST -v -d \
'{"title":"Application log input","configuration":{"recv_buffer_size":262144,"bind_address":"0.0.0.0","port":12201,"decompress_size_limit":8388608},"type":"org.graylog2.inputs.gelf.udp.GELFUDPInput","global":true}' \
http://localhost:9000/api/system/inputs
```

🚨 Um "*input*" também pode ser criado pelo console de administração do GrayLog (System → Input → Select GELF UDP).

## Código 💡

Um exemplo funcional do Jaeger e GrayLog pode ser obtido no projeto:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
exemplos/jwt/jwt-provider
```

# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* Using Opentracing. Disponível em: [https://quarkus.io/guides/opentracing](https://quarkus.io/guides/opentracing)

* Centralized Log Management. Disponível em: [https://quarkus.io/guides/centralized-log-management](https://quarkus.io/guides/centralized-log-management)

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>