<!-- .slide: data-background-opacity="0.2" data-background-image="img/_8ebf2988-edfc-4519-840f-3b0eb0b02ba6.jpg" data-transition="convex" -->
# Trace e Log
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Jaeger
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Jaeger
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* O Jaeger é uma ferramenta para a realização de *trace* distribuído.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

* O *trace* é o registo dos requests de ponta a ponta em um sistema distribuído.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

* Fornece visibilidade do fluxo de trabalho de um serviço, permitindo
que os desenvolvedores vejam o desempenho e o comportamento do sistema.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

* Jaeger rastreia as requisições dos serviços e registra as
informações de cada _request_ à medida que passa pelos diferentes serviços 
que compõem a aplicação.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Jaeger
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Principais funcionalidades do Jaeger:
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white" -->

  * Rastreamento das solicitações (_requests_) distribuidas
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  * Visualização do fluxo de trabalho
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  * Alertas e notificações
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  * Armazenamento
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  * Análise de desempenho
  <!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Jaeger
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Algumas desvantagens do Jaeger:
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  * Impacto no desempenho do sistema (_overhead_) e custo
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  * Conhecimento especializado
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  * A integração com algumas ferramentas pode ser um desafio
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->



<!-- .slide: data-background="#21093D" data-transition="convex" -->
## GrayLog
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## GrayLog
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* O GrayLog é uma ferramenta de gerenciamento e análise de logs que permite coletar, processar e analisar registros de várias fontes
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

* Oferece uma interface da Web que permite pesquisar e analisar os logs
 <!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

* Também oferece ferramentas de alerta para notificar equipes quando ocorrem eventos importantes
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

* Possui recursos de análise de log como gráficos e métricas
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## GrayLog
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Algumas desvantagens:
<!-- .element: style="margin-bottom:40px; font-size: 30px; color:white" -->

  * Configuração complexa
  <!-- .element: style="margin-bottom:50px; font-size: 30px; color:white" -->

  * Requer habilidades técnicas
   <!-- .element: style="margin-bottom:50px; font-size: 30px; color:white" -->

  * Alto consumo de recursos e custo de manutenção
  <!-- .element: style="margin-bottom:50px; font-size: 30px; color:white" -->



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

* Using Opentracing. Disponível em: [https://quarkus.io/guides/opentracing](https://quarkus.io/guides/opentracing)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

* Centralized Log Management. Disponível em: [https://quarkus.io/guides/centralized-log-management](https://quarkus.io/guides/centralized-log-management)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

* Jaeger. Disponível em: [https://www.jaegertracing.io](https://www.jaegertracing.io)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

* GrayLog. Disponível em: [https://www.graylog.org](https://www.graylog.org)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>
<!-- .element: style="margin-top:100px; font-size: 15px; font-family: Bradley Hand" -->