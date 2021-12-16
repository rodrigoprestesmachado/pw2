# Trace e Log

Esse tutorial mostra os principais passos para você adicionar uma ferramenta de *trace* chamada Jaeger e também uma ferramenta para consolidar logs chamada Graylog

## Jaeger

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

Vamos aos passos de configuração do Jaeger em um projeto Quarkus: Primeiro, instale a extensão `quarkus-smallrye-opentracing` no seu projeto. Depois, configure o seu `application.properties` com as configurações do Jaeger:

```sh
quarkus.jaeger.service-name=myservice
quarkus.jaeger.sampler-type=const
quarkus.jaeger.sampler-param=1
quarkus.log.console.format=%d{HH:mm:ss} %-5p traceId=%X{traceId}, parentId=%X{parentId}, spanId=%X{spanId}, sampled=%X{sampled} [%c{2.}] (%t) %s%e%n
```

🚨 No exemplo acima, "*myservice*" será o nome do ser serviço no Jaeger.

## GrayLog

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

Agora, vamos aos passos para configurar o GrayLog no Quarkus: Primeiro,  instale a extensão `quarkus-logging-gelf` no seu projeto. Depois, configure o `application.properties` para permitir que as mensagens do seu projeto possam ser encaminhadas para o GrayLog.

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