<!-- .slide: data-background-opacity="0.3" data-background-image="https://res.cloudinary.com/dotcom-prod/images/c_fill,f_auto,g_faces:center,q_auto,w_1920/v1/wt-cms-assets/2020/08/emayhqxbsu48vsdeqfl0/wtheadlessmicroservices1920x1440.jpg"
data-transition="convex"
-->
# Health
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Health
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* As verificações **de** Health checks são usadas para verificar o estado de um serviço.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Esse tipo de recurso é propício para ambientes de infraestrutura em nuvem onde processos automatizados mantêm o estado de nós de computação (kubernetes por exemplo).
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* As verificações de integridade são usadas para determinar se um nó de computação precisa ser descartado/encerado e/ou eventualmente substituído por outra instância.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Embora possa ser usado, o Health _checks_ não se destina como uma solução de monitoramento de serviços para operadores humanos.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Configuração
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Para implementar no Quarkus uma solução de health check instale a extensão smallrye-health.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

```sh
./mvnw quarkus:add-extension -Dextensions="smallrye-health"
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-health</artifactId>
</dependency>
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Implementação
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* A extensão _smallrye-health_ expõe diretamente três endpoints em REST:
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

  * **/q/health/live** -indica se o serviço está rodando (vivo).
  <!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

  * **/q/health/ready** - indica se o serviço está vivo e também pronto para atender às solicitações.
  <!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

  * **/q/health** - acumula todos os procedimentos de verificação de integridade do serviço.
  <!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Implementação
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Para responder no endpoint /q/health/live devemos implementar a interface HealthCheck e decorar a classe com a anotação @Liveness, por exemplo:
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

```java
@Liveness
public class MyLivenessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("alive");
    }
}
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Implementação
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Para responder no endpoint /q/health/ready devemos implementar a interface HealthCheck e decorar a classe com a anotação @Readiness, por exemplo:
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

```java
@Readiness
public class MyReadinessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder response = HealthCheckResponse.named("Database connection health check");

        response.up().withData("key", "value");

        // Para responder que o serviço está down
        // response.down();

        return response.build();
    }
}
```
<!-- .element: style="margin-bottom:50px; font-size: 18px; font-family: arial; color:black; background-color: #F2FAF3;" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Health Ui
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Existe também uma interface para verificar a situação do serviço que está disponível nos modos de desenvolvimento e teste do Quarkus: /q/health-ui
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Para habilitar no modo de produção utilize a propriedade quarkus.smallrye-health.ui.enable com o valor true.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

<img src="img/health-ui.png" width="70%" height="70%"/><br/>


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Referências
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* SMALLRYE HEALTH disponível em: [https://quarkus.io/guides/smallrye-health](https://quarkus.io/guides/smallrye-health)
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>