<!-- .slide: data-background-image="https://wallpaperaccess.com/full/118572.jpg" 
data-transition="convex"
-->
# Session Beans
<!-- .element: style="margin-bottom:300px; font-size: 60px; color:white; color:white" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white; color:white" -->



# Desenvolvendo Session Beans


<!-- .slide: data-background="#1D2F44" data-transition="fade; color:white" -->
## Desenvolvendo Session Beans
* Para desenvolver um Session Bean você necessita criar:
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->
  * **Classe do Enterprise bean:** implementa as regras de negócio de um bean
  <!-- .element: style="margin-bottom:50px; font-size: 20px;  color:white" -->

  * **Interface de negócio:** define os métodos que serão implementados por um bean
  <!-- .element: style="margin-bottom:50px; font-size: 20px;  color:white" -->
  
  * **classes Helpers :** são classes necessárias para a implementação do bean (ex.: dependências)
  <!-- .element: style="margin-bottom:50px; font-size: 20px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="fade; color:white" -->
## Interface de um EJB
<!-- .element: style="margin-bottom:70px; color:white" -->

* As interfaces de negócio de um EJB podem ser:
<!-- .element: style="margin-bottom:50px; font-size: 30px; color:white" -->

  * Métodos públicos (no-interface view)
  <!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->
  * Interface de negócio:
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->
    * Interface local
    <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white" -->
    * Interface remota
    <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="fade; color:white" -->
##  Interface local ou remota?
<!-- .element: style="margin-bottom:70px; color:white" -->

* Quando você projeta uma aplicação Jakarta EE, uma das primeiras decisões é o tipo de acesso aos enterprise beans: local, remota ou Web Services
<!-- .element: style="margin-bottom:50px; font-size: 30px; color:white" -->

* Permitir o acesso local ou remoto depende dos seguintes fatores:
<!-- .element: style="margin-bottom:30px; font-size: 30px; color:white" -->

  * **Acoplamento entre os beans:** Se o acoplamento entre os beans for forte, ou seja, um bean depende de outro, então estes beans são fortes candidatos para acesso local
  <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white" -->

  * **Tipo dos clientes:** Se um enterprise bean é acessado por várias aplicações, então, é interessante permitir acesso remoto
  <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
 # Interface local ou remota?
 <!-- .element: style="margin-bottom:50px; font-size: 30px; color:white" -->

* **Distribuição dos Componentes:** Aplicações Jakarta EE são escaláveis pois, permitem que os componentes sejam distribuídos em múltiplas máquinas. Assim, neste tipo de cenário é adequado permitir acesso remoto
<!-- .element: style="margin-bottom:80px; font-size: 25px; color:white" -->

* **Performance:** Devido a fatores como a latência da rede, chamadas remotas pode ser mais lentas do que as chamadas locais. Por outro lado, se você distribuir componentes entre servidores diferentes, você pode melhorar o desempenho geral da aplicação
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->



# Métodos Públicos


<!-- .slide: data-background="#1D2F44" data-transition="slide color:white" -->
## Métodos Públicos
<!-- .element: style="margin-bottom:50px; color:white" -->

* A interface pública de um bean é uma visão local
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Os métodos públicos da classe que implementa um enterprise bean são expostos aos clientes locais que podem acessar o bean
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Enterprise beans desenvolvidos desta forma não implementam uma interface de negócio
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="slide color:white" -->
## Métodos Públicos
<!-- .element: style="margin-bottom:40px; color:white" -->

* Se a interface do bean não é decorada com @Local ou @Remote, então, por padrão, considera-se uma interface **local**
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

* Para construir um enterprise bean que permite o acesso apenas local, você pode fazer o seguinte:
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->
  
  * Escrever uma classe que implementa um bean, mas, que não implemente uma interface de negócio.
   <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white" -->

```java
package ee.jakarta.tutorial.converter.ejb;

import java.math.BigDecimal;
import jakarta.ejb.*;

@Stateless
public class ConverterBean {
    private BigDecimal yenRate = new BigDecimal("83.0602");
    private BigDecimal euroRate = new BigDecimal("0.0092016");

    public BigDecimal dollarToYen(BigDecimal dollars) {
        BigDecimal result = dollars.multiply(yenRate);
        return result.setScale(2, BigDecimal.ROUND_UP);
    }

    public BigDecimal yenToEuro(BigDecimal yen) {
        BigDecimal result = yen.multiply(euroRate);
        return result.setScale(2, BigDecimal.ROUND_UP);
    }
}
```
 <!-- .element: style="margin-bottom:20px; font-size: 13px; color:white" -->



# Interface de Negócio Local


<!-- .slide: data-background="#1D2F44" data-transition="concave color:white" -->
## Clientes Locais
<!-- .element: style="margin-bottom:30px; color:white" -->

* Um cliente local tem as seguintes características:
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  * Deve ser executado na mesma aplicação que o enterprise bean
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

  * Pode ser um componente Web ou outro enterprise bean
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

  * A localização do enterprise bean não é transparente
  <!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="concave color:white" -->
## Interface de Negócio Local
<!-- .element: style="margin-bottom:40px; color:white" -->

* Para utilizar uma interface local devemos primeiro anotar a interface de negócio do bean com @Local. Por exemplo:
<!-- .element: style="margin-bottom:10px; font-size: 25px; color:white" -->

```java
@Local
public interface InterfaceName { ... }
```
<!-- .element: style="margin-bottom:10px; font-size: 16px; color:white" -->

* Depois, devemos especificar uma interface para a classe que implementa o bean por meio da anotação @Local. Por exemplo:
<!-- .element: style="margin-bottom:25px; font-size: 25px; color:white" -->

```java
@Local(InterfaceName.class)
public class BeanName implements InterfaceName { ... }
```
<!-- .element: style="margin-bottom:10px; font-size: 16px; color:white" -->



# Interface de Negócio Remota


<!-- .slide: data-background="#1D2F44" data-transition="zoom color:white" -->
## Clientes Remotos
<!-- .element: style="margin-bottom:40px; color:white" -->

* Um cliente remoto de um enterprise bean tem as seguintes características:
<!-- .element: style="margin-bottom:40px; font-size: 28px; color:white" -->

  * Pode ser um componente Web, um aplicativo cliente ou outro enterprise bean
  <!-- .element: style="margin-bottom:25px; font-size: 20px; color:white" -->

  * Pode ser executado em uma máquina diferente e uma JVM diferente do enterprise bean que ele acessa
   <!-- .element: style="margin-bottom:25px; font-size: 20px; color:white" -->

  * A localização do enterprise bean deve ser transparente para o cliente remoto
   <!-- .element: style="margin-bottom:25px; font-size: 20px; color:white" -->

  * O enterprise bean deve implementar uma interface de negócios, ou seja, um cliente remoto não pode acessar um bean apenas através da interface pública (no-interface view)
   <!-- .element: style="margin-bottom:25px; font-size: 20px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="zoom color:white" -->
## Interface de Negócio Remota
<!-- .element: style="margin-bottom:40px; color:white" -->

* Para criar um bean que permite o acesso remoto, você deve:
<!-- .element: style="margin-bottom:20px; font-size: 30px; color:white" -->

  * Decorar a interface de negócios do enterprise bean com a anotação @Remote anotação
  <!-- .element: style="margin-bottom:10px; font-size: 20px; color:white" -->

  ```java
  @Remote
  public interface InterfaceName {... }
  ```
  <!-- .element: style="margin-bottom:10px; font-size: 16px; color:white" -->

  * Depois, devemos especificar uma interface para a classe que implementa o bean por meio da anotação @Remote. Por exemplo:
  <!-- .element: style="margin-bottom:20px; font-size: 20px; color:white" -->

  ```java
  @Remote(InterfaceName.class)
  public class BeanName implements InterfaceName { ... }
  ```
<!-- .element: style="margin-bottom:10px; font-size: 16px; color:white" -->

  * A interface remota define os métodos de negócio e ciclo de vida que são específicos para o bean
  <!-- .element: style="margin-bottom:15px; font-size: 20px; color:white" -->



# Acessando um Enterprise Bean


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Acessando um Enterprise Bean
<!-- .element: style="margin-bottom:50px; color:white" -->

* Os clientes pode obter instâncias de um EJBs de duas maneiras:
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

  * **[Dependency injection:](https://eclipse-ee4j.github.io/jakartaee-tutorial/#jakarta-ee-contexts-and-dependency-injection)** por meio de anotações Jakarta
  <!-- .element: style="margin-bottom:40px; font-size: 22px; color:white" -->

  * **Jakarta Naming and Directory Interface ([JNDI](https://eclipse-ee4j.github.io/jakartaee-tutorial/#resources-and-jndi-naming)) lookup:** usando a sintaxe do JNDI para encontrar a instância do bean
  <!-- .element: style="margin-bottom:40px; font-size: 22px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Acessando um Enterprise Bean
<!-- .element: style="margin-bottom:50px; color:white" -->

* Injeção de Dependência é a maneira mais fácil para obter uma referência para um enterprise bean
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Entretanto, clientes que rodam fora do servidor de aplicação, como por exemplo, aplicações Jakarta SE, necessitam localizar o bean por meio de JNDI
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Por exemplo, classes POJO (*Plain Old Jakarta Objects*) devem utilizar JNDI para acessar um session bean
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Acessando Beans locais
<!-- .element: style="margin-bottom:40px; color:white" -->

* O acesso do cliente ao enterprise beans locais é feito através de injeção de dependência ou JNDI
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* Para obter uma referência para a interface local do bean quando por meio da injeção de dependência, use a anotação javax.ejb.EJB
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  ```java
  @EJB(beanName="ServiceBean”)
  private Service service;
  ```
  <!-- .element: style="margin-bottom:40px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Acessando Beans locais
<!-- .element: style="margin-bottom:70px; color:white" -->

* Para obter uma referência através de JNDI, use os métodos da interface javax.naming.InitialContext:
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  ```java
  Exemplo ExampleLocal = (Exemplo) InitialContext.lookup("java:módulo / ExampleLocal");
  ```
  <!-- .element: style="margin-bottom:40px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Acessando Beans Remotos
<!-- .element: style="margin-bottom:40px; color:white" -->

* O acesso do cliente para um bean que implementa uma interface remota é realizado através de injeção de dependência ou JNDI
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

* Para obter uma referência para um bean que implementa uma interface de negócios remota por meio de injeção de dependência, use a anotação `javax.ejb.EJB` e especifique o nome da interface remota:
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

```java
@EJB(beanName="ServiceBean")
private Service service;
```
<!-- .element: style="margin-bottom:40px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Acessando Beans Remotos
<!-- .element: style="margin-bottom:40px; color:white" -->

* Para obter uma referência para a interface remota de um bean através de JNDI, use a interface javax.naming.InitialContext como forma de pesquisa:
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

```java
ExampleRemote example = (ExampleRemote)
InitialContext.lookup("java:global/myApp/ExampleRemote");
```
<!-- .element: style="margin-bottom:40px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Sintaxe JNDI
<!-- .element: style="margin-bottom:20px; color:white" -->

* Existem três namespaces JNDI para procurar enterprises beans: java:global, java:module, java:app
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

  * **java:global** - é a maneira de encontrar beans remotos usando consultas JNDI. Endereços JNDI são da seguinte forma:
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

    * java:global[/application name]/module name/bean name[/interface name]
    <!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

  * **java:module** - é utilizado para encontrar enterprise beans dentro do mesmo módulo. Endereços JNDI que utilizam java:module tem o formato:
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

      * java:module/bean name/[interface name]
      <!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->
    
  * **java:app** - é utilizado para encontrar enterprise beans dentro da mesma aplicação:
  <!-- .element: style="margin-bottom:40px; font-size: 25px; color:white" -->

    * java:app[/module name]/enterprise bean name[/interface name]
    <!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->



# Empacotamento


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Empacotando um EJB

* Existem duas formas de fazer um deploy de enterprise beans por meio de arquivos JAR (Jakarta Archive):
<!-- .element: style="margin-bottom:50px; font-size: 22px; color:white" -->

* Você pode empacotar um ou mais JARs dentro de um EAR (Enterprise Archive). Assim, quando você fizer o deploy de um EAR no servidor de aplicação todos os beans também serão implantados
<!-- .element: style="margin-bottom:50px; font-size: 22px; color:white" -->

* Você também pode implantar um JAR EJB que não está contido em um arquivo EAR
<!-- .element: style="margin-bottom:5px; font-size: 22px; color:white" -->

![imagem](img/ear.svg) <!-- .element height="50%" width="50% color:white" -->

Fonte: [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#packaging)
<!-- .element: style="margin-bottom:10px; font-size: 10px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Empacotando um EJB num Jar

* Estrutura de um JAR Enterprise Bean Empacotando um EJB
<!-- .element: style="margin-bottom 25px; font-size: 20px; color:white" -->

![imagem](img/jar.svg) <!-- .element height="50%" width="50% color:white" -->

Fonte: [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#packaging)
<!-- .element: style="margin-bottom:50px; font-size: 10px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Empacotando um EJB num WAR

* Enterprises Beans frequentemente fornecem a lógica de negócios de uma aplicação Web
<!-- .element: style="margin-bottom:20px; font-size: 20px; color:white" -->

* Assim, o empacotamento do bean dentro do módulo Web (WAR) simplifica a organização da aplicação
<!-- .element: style="margin-bottom:20px; font-size: 20px; color:white" -->

* Enterprise beans podem ser empacotados dentro de um módulo WAR como um arquivo de classes Jakarta
<!-- .element: style="margin-bottom:20px; font-size: 20px; color:white" -->

* Também pode ser empacotados como arquivos JAR e incluídos dentro do WAR
<!-- .element: style="margin-bottom:5px; font-size: 20px; color:white" -->

![imagem](img/war.svg) <!-- .element height="50%" width="50% color:white" -->

Fonte: [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#packaging)
<!-- .element: style="margin-bottom:50px; font-size: 10px; color:white" -->


<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Empacotando um EJB num WAR

* Para incluir uma classe de um enterprise bean em um WAR, basta colocar a classe no diretório WEB-INF/classes
<!-- .element: style="margin-bottom:50px; font-size: 20px; color:white" -->

* Para incluir um arquivo JAR que contém um enterprise bean em um WAR, é necessário colocar o arquivo JAR dentro do diretório WEB-INF/lib
<!-- .element: style="margin-bottom:50px; font-size: 20px; color:white" -->

* **Nota:** Arquivos JAR que contêm classes enterprises beans empacotados dentro de um módulo WAR não são considerados arquivos JAR EJB
<!-- .element: style="margin-bottom:50px; font-size: 20px; color:white" -->

* **Nota:** Se a aplicação utilizar um EBJ deploy descriptor (ejb-jar.xml), então, este arquivo deve ser localizado no diretório WEB-INF
<!-- .element: style="margin-bottom:50px; font-size: 20px; color:white" -->




<!-- .slide: data-background="#1D2F44" data-transition="convex color:white" -->
## Referência

[The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/)
<!-- .element: style="margin-bottom:50px; font-size: 20px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>