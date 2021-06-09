<!-- .slide: data-background-image="https://wallpaperaccess.com/full/383133.jpg" 
data-transition="convex"
-->
# Stateless Session Beans
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white;" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white;" -->


<!-- .slide: data-background="#311F4A" data-transition="zoom" -->
## Ciclo de vida de um Stateless
<!-- .element: style="margin-bottom:50px; color:white; font-size: 45px;" -->

![imagem](img/stateless.svg) <!-- .element height="50%" width="50%" -->

Fonte: [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#the-lifecycles-of-enterprise-beans)
<!-- .element: style="margin-bottom:10px; font-size: 10px; color:white"  -->


<!-- .slide: data-background="#311F4A" data-transition="zoom" -->
## Características
<!-- .element: style="margin-bottom:40px; font-size: 50px;" -->

* Um Stateless não mantém um estado de conversação com o cliente
<!-- .element: style="margin-bottom:40px; color:white; font-size: 25px;" -->

* Durante a invocação de um método o Stateless contém um estado específico para o cliente 
<!-- .element: style="margin-bottom:40px; color:white; font-size: 25px;" -->

* Assim, todas as instâncias de Stateless são equivalentes, permitindo que o contêiner EJB possa atribuir uma instância para qualquer cliente
<!-- .element: style="margin-bottom:40px; color:white; font-size: 25px;" -->

* Os Stateless podem oferecer melhor escalabilidade para aplicativos que requerem um grande número de clientes
<!-- .element: style="margin-bottom:40px; color:white; font-size: 25px;" -->

* Os Stateless podem ser utilizados para implementar Web Services
<!-- .element: style="margin-bottom:40px; color:white; font-size: 25px;" -->


<!-- .slide: data-background="#311F4A" data-transition="zoom" -->
## Quando utilizar?
<!-- .element: style="margin-bottom:40px; font-size: 50px;" -->

* Quando o estado do bean não possui dados para um cliente específico
<!-- .element: style="margin-bottom:40px; color:white; font-size: 25px;" -->

* Quando o bean implementar uma tarefa genérica para todos os clientes (enviar um e-mail)
<!-- .element: style="margin-bottom:40px; color:white; font-size: 25px;" -->

*  Quando for necessário implementar um Web Service
<!-- .element: style="margin-bottom:40px; color:white; font-size: 25px;" -->


<!-- .slide: data-background="#F7F7F7" data-transition="zoom" -->
## Exemplo
<!-- .element: style="margin-bottom:40px; font-size: 50px;" -->

```java
package ee.jakarta.tutorial.helloservice.ejb;

import jakarta.ejb.Stateless;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@Stateless
@WebService
public class HelloServiceBean {
    private final String message = "Hello, ";

    public void HelloServiceBean() {}

    @WebMethod
    public String sayHello(String name) {
        return message + name + ".";
    }
}
```


# Referência

[The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/)
<!-- .element: style="margin-bottom:50px; font-size: 20px;" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>