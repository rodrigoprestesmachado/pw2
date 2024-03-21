<!-- .slide: data-background-opacity="0.3" data-background-image="https://pw2.rpmhub.dev/topicos/metrics/slides/img/title.jpg" data-transition="convex" -->
# Metrics 📏
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Metrics 📏
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* A especificação [Metrics](https://github.com/eclipse/microprofile-metrics/) do Microprofile fornece uma maneira de construir e expor métricas do seu serviço
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

* Algumas ferramentas de monitoramento, como por exemplo o o [Prometheus](https://prometheus.io), utilizam os dados dos serviços para demonstrar um panorama geral de uma aplicação
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Metrics 📏
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Para utiilzar o MicroProfile Metrics em um aplicativo Quarkus, você precisa importar a extensão `quarkus-smallrye-metrics` ([SmallRye Metrics](https://github.com/smallrye/smallrye-metrics/))
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Com a extensão `quarkus-smallrye-metrics` adicionada no projeto, o Quarkus passa a fornecer um endpoint padrão (ex.: /q/metrics) no formato Prometheus
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Entretanto, o formato pode ser alterado para JSON trocando-se o cabeçalho da requisição HTTP *Accept* para `application/json`.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Alguns exemplos do que pode ser monitorado
<!-- .element: style="margin-bottom:100px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Tempo de CPU
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

* Memória ocupada
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

* Espaço em disco
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

* Desempenho de métodos críticos
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->

* Métricas de negócios (ex.: número de pagamentos por segundo)
<!-- .element: style="margin-bottom:50px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Endpoints 🕸️
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* base (`q/metrics/base`) - informações essenciais do servidor, como por exemplo, o número de classes carregadas por uma máquina virtual, ou o número de processos rodando na máquina virtual, etc.
<!-- .element: style="margin-bottom:70px; font-size: 27px; color:white" -->

* vendor  (`q/metrics/vendor`) - informações especificas sobre um fornecedor, como por exemplo, o tempo de CPU utilizado por cada processo, ou o uso recente de CPU por todo os sistema, etc.
<!-- .element: style="margin-bottom:70px; font-size: 27px; color:white" -->

* application (`q/metrics/application`) - informações personalizadas desenvolvidas por meio do mecanismo de extensão do MicroProfile Metrics.
<!-- .element: style="margin-bottom:70px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Anotações 🖼️
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* O MicroProfile Metrics possui um conjunto de anotações que podem serem usadas para criamos métricas espefíficas (`q/metrics/application`) para o serviço, são elas:
<!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

  * @Counted - Conta o número de invocações de um método
  <!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

  * @Timed - Monitora a duração de uma invocação
  <!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

  * @SimplyTimed - Monitora a duração das invocações sem considerar cálculos como média e distribuição, ou seja, trata-se de uma versão simplificada do @Timed.
  <!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

  * @Metered - Monitora a frequência de invocações
  <!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

  * @Gauge - Expõe o valor de retorno do método anotado como uma métrica
  <!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

  * @ConcurrenceGauge - Conta invocações paralelas
  <!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Histograma
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Um histograma armazena dados ao longo do tempo e, com isso, gera valores como mínimo, máximo, média, desvio padrão, entre outros
<!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

* Um histograma não possui uma anotação
<!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

* Exemplo:
<!-- .element: style="margin-bottom:40px; font-size: 27px; color:white" -->

```java
    @Inject
    @Metric(name = "histogram", absolute = true)
    Histogram histogram;
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```java
this.histogram.update(number);
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Exemplo
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

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
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Consultas 🔎
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Você pode obter informações de qualquer métrica consultando um endpoint específico usando o método OPTION do HTTP.
<!-- .element: style="margin-bottom:70px; font-size: 27px; color:white" -->

* Os metadados são expostos por padrão em `q/metrics/escope/metric-name`, onde o `escope` pode ser: base, vendor ou application e metric-name é o nome propriamente dito da métrica (no caso de um aplicativo, aquele definido no atributo name).
<!-- .element: style="margin-bottom:70px; font-size: 27px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Prometheus
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

O [Prometheus](https://prometheus.io) é um aplicativo de software gratuito usado para monitoramento e alerta de eventos.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

<img src="img/prometheus.png" width="60%" height="60%"/><br/>


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Referências 📚
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.
* <!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* SmallReye Metrics. Disponível em: [https://quarkus.io/guides/smallrye-metrics](https://quarkus.io/guides/smallrye-metrics)
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>