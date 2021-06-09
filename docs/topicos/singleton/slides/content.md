<!-- .slide: data-background-image="https://wallpaperaccess.com/full/383133.jpg" 
data-transition="convex"
-->
# Singleton Session Beans
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white;" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white;" -->



# Ciclo de vida de um Singleton


<!-- .slide: data-background="#0D255D" data-transition="fade" -->
## Ciclo de vida de um Singleton
<!-- .element: style="margin-bottom:50px; color:white; font-size: 45px;" -->

![imagem](img/stateless.svg) <!-- .element height="50%" width="50%" -->

Fonte: [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#the-lifecycles-of-enterprise-beans)
<!-- .element: style="margin-bottom:10px; font-size: 10px; color:white;" -->


<!-- .slide: data-background="#0D255D" data-transition="fade" -->
## Ciclo de vida de um Singleton
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* Diferente de um stateful sesion bean, um singleton session bean nunca é desativado (_passivate_). Assim, o ciclo de vida de um singleton possui apenas dois estágios: não criado e pronto
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* O container EJB é responsável por iniciar o ciclo de vida do bean, porém, o bean pode ser iniciado antes da aplicação se a anotação `@Startup` for utilizada
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Logo, o container executa qualquer injeção de dependência e invoca o método anotado com `@PostConstruct` (se existir)
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* No final do ciclo de vida, o container invoca o método anotado com `@PreDestroy` (se existir)
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->



# Inicialização de um Singleton


<!-- .slide: data-background="#0D255D" data-transition="fade" -->
# Inicialização de um Singleton
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* O container EJB é responsável por determinar quando um singleton será inicializado
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Porém, você pode decorar o bean com a anotação `@Startup`. Neste caso, o bean será inicializado antes da aplicação (eager initialization)
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Isto permite que o bean execute tarefas de inicialização de toda a aplicação
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

```java
@Startup
@Singleton
public class StatusBean { //code }
```
<!-- .element: style="margin-bottom:50px; font-size: 20px;" -->


<!-- .slide: data-background="#0D255D" data-transition="fade" -->
# Inicialização de um Singleton
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* As vezes múltiplos beans são utilizados para inicializar dados de uma aplicação numa ordem determinada
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Neste caso, podemos utilizar a anotação `@DependsOn` para declarar as dependências na inicialização
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* A anotação `@DependsOn` recebe como parâmetro uma ou mais strings que indicam as dependências na inicialização do bean
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->


<!-- .slide: data-background="#0D255D" data-transition="fade" -->
# Inicialização de um Singleton
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

```java
@Singleton
public class PrimaryBean { //code }

@Singleton
@DependsOn("PrimaryBean")
public class SecondaryBean { //code }

@Singleton
@DependsOn({"PrimaryBean", "SecondaryBean"})
public class TertiaryBean { //code }
```
<!-- .element: style="margin-bottom:50px; font-size: 20px;" -->



# Acesso Concorrente


<!-- .slide: data-background="#0D255D" data-transition="zoom" -->
# Acesso Concorrente
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* Singletons beans foram projetados para permitir acesso concorrente, ou seja, situações onde múltiplos clientes acessam o bean ao mesmo tempo
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* O acesso concorrente pode ser gerenciado de duas maneiras:
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

    * Concorrência orientada ao container
    <!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

    * Concorrência orientada ao bean
    <!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->


<!-- .slide: data-background="#0D255D" data-transition="zoom" -->
# Acesso Concorrente
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* A anotação `@ConcurrencyManagement` é utilizada para especificar o tipo de gerenciamento da concorrência
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* A anotação `@ConcurrencyManagement` necessita receber como parâmetro um dos dois tipos:
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

    * ConcurrencyManagementType.CONTAINER
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

    * ConcurrencyManagementType.BEAN
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Se a anotação `@ConcurrencyManagement` não for utilizada, então é a gerencia da concorrência é realizada por meio do container
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->


<!-- .slide: data-background="#0D255D" data-transition="convex" -->
# Acesso Concorrente
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

```java
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@DependsOn("YourSingletonBean")
@Singleton
public class MySingletonBean { //code }
```
<!-- .element: style="margin-bottom:50px; font-size: 18px;" -->


<!-- .slide: data-background="#0D255D" data-transition="convex" -->
# Acesso Concorrente: Container
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* Neste Caso, o container controla o acesso aos métodos de negócio
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* A anotação `@Lock` e o tipo LockType são utilizados para especificar o nível de acesso aos métodos do singleton
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* LockType é uma enumeração que possui dois valores READ e WRITE
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Anotamos um método com `@Lock(LockType.READ)` quando um método é acessado de forma concorrente por vários clientes
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Por outro lado, anotamos um método com `@Lock(LockType.WRITE)` quando um método, que modifica o estado de um bean, é acessado de forma concorrente por vários clientes
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->


<!-- .slide: data-background="#0D255D" data-transition="convex" -->
# Acesso Concorrente: Container
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* Se você anotar uma classe singleton com `@Lock`, então, todos os métodos de negócio irão utilizar a regra definida
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Entretanto, se você desejar, uma nova definição de concorrência pode ser feita no nível no método
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Se nenhuma anotação for feita no nível da classe, então o container atribui como padrão a diretiva , `@Lock(LockType.WRITE)` para todos os métodos de negócio
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->


<!-- .slide: data-background="#0D255D" data-transition="convex" -->
# Acesso Concorrente: Container
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

```java
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Singleton
public class ExampleSingletonBean {
  private String state;

  @Lock(LockType.READ)
  public String getState() {
    return state;
  }

  @Lock(LockType.WRITE)
  public void setState(String newState) {
    state = newState;
  }
}
```
<!-- .element: style="margin-bottom:50px; font-size: 18px;" -->


<!-- .slide: data-background="#0D255D" data-transition="zoom" -->
# Acesso Concorrente: Container
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* No exemplo anterior, o método getState pode ser acessado por vários clientes ao mesmo tempo
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Porém, quando o método setState é invocado, todos os métodos do bean são travados
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Isso evita que dois clientes tentem alterar de forma simultânea o estado de um bean
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->


<!-- .slide: data-background="#0D255D" data-transition="zoom" -->
# Acesso Concorrente: Container
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

```java
@Singleton
@Lock(LockType.READ)
public class SharedSingletonBean {
  private String data;
  private String status;

  public String getData() {
    return data;
  }

  public String getStatus() {
    return status;
  }

  @Lock(LockType.WRITE)
  public void setStatus(String newStatus) {
    status = newStatus;
  }
}
```


<!-- .slide: data-background="#0D255D" data-transition="zoom" -->
# Acesso Concorrente: Container
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* Se um método é anotado com o tipo WRITE, o acesso para os clientes será bloqueado até que a invocação termine ou um timeout ocorra
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Quando um timeout ocorre o container lança a exceção `ConcurrentAccessTimeoutException`
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Para configurar um timeout é necessário utilizar a anotação `@AccessTimeout`
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Esta anotação é utilizada para atribuirmos um tempo em milissegundos antes que um timeout ocorra
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->


<!-- .slide: data-background="#0D255D" data-transition="zoom" -->
# Acesso Concorrente: Container
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* Se um valor de timeout for adicionado na classe, então todos os métodos terão um valor de timeout definido (READ ou WRITE)
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Porém, um método pode sobrescrever um valor de timeout padrão deste que explicitamente seja redecorado com a anotação `@AccessTimeout`
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* A anotação @AccessTimeout possui um parâmetro obrigatório value e um parâmetro opcional timeUnit
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->


<!-- .slide: data-background="#0D255D" data-transition="zoom" -->
# Acesso Concorrente: Container
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

```java
@Singleton
@AccessTimeout(value=120000)
public class StatusSingletonBean {
  private String status;

  @Lock(LockType.WRITE)
  public void setStatus(String new Status) {
    status = newStatus;
  }

  @Lock(LockType.WRITE)
  @AccessTimeout(value=360000)
  public void doTediousOperation {
    ...
  }
}

@Singleton
@AccessTimeout(value=60, timeUnit=SECONDS)
public class StatusSingletonBean { //code }
```
<!-- .element: style="margin-bottom:50px; font-size: 18px;" -->


<!-- .slide: data-background="#0D255D" data-transition="zoom" -->
# Acesso Concorrente: Bean
<!-- .element: style="margin-bottom:50px; font-size: 45px;" -->

* Singletons que utilizam o gerenciamento da concorrência por meio do bean permitem o acesso concorrente em todos os métodos de negócio
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Desta forma, o desenvolvedor é responsável por assegurar que o estado do singleton esta sincronizado entre todos os clientes
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->

* Neste caso, é permitido que os desenvolvedores utilizem as [primitivas](https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html) de sincronização de Java para prevenir erros
<!-- .element: style="margin-bottom:50px; color:white; font-size: 25px;" -->



## Referência

[The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/)
<!-- .element: style="margin-bottom:50px; font-size: 20px;" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>