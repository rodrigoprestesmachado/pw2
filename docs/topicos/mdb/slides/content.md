<!-- .slide: data-background-opacity="0.2" data-background-image="https://www.10wallpaper.com/wallpaper/1920x1200/1511/Indonesia_island_java_volcano-scenery_HD_Wallpaper_1920x1200.jpg" 
data-transition="convex"
-->
# Message Driven Beans
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white;" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white;" -->



# Ciclo de Vida de um Message-Driven
<!-- .element: style="margin-bottom:40px; font-size: 50px; color:white;" -->


<!-- .slide: data-background="#222c44" data-transition="zoom" -->
## Ciclo de Vida de um Message-Driven
<!-- .element: style="margin-bottom:50px; color:white; font-size: 45px;" -->

![imagem](img/mdb.svg) 
<!-- .element height="50%" width="50%" -->

Fonte: [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#the-lifecycles-of-enterprise-beans)
<!-- .element: style="margin-bottom:10px; font-size: 10px; color:white"  -->


<!-- .slide: data-background="#222c44" data-transition="convex" -->
## Ciclo de Vida de um Message-Driven
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* O container EJB geralmente cria um pool de instâncias de _message-driven beans_ (MDB). Assim, para cada instância o container executa as seguintes operações:
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white; font-family: arial;" -->

    * Se o _message-driven bean_ utiliza injeção de dependência, o container resolve as referências antes de realizar a instanciação
    <!-- .element: style="margin-bottom:30px; font-size: 23px; color:white; font-family: fantasy;" -->

    * O container chama o método anotado com `@PostConstruct`, se existir
    <!-- .element: style="margin-bottom:20px; font-size: 23px; color:white; font-family: fantasy;" -->



# Implementação de um Message Driven ✉️
<!-- .element: style="margin-bottom:40px; font-size: 50px; color:white;" -->


<!-- .slide: data-background="#222c44" data-transition="convex" -->
## Implementação de um Message Driven
<!-- .element: style="margin-bottom:35px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Para criar um MDB, é necessário decorar a classe do bean com a anotação `@MessageDriven`
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white; font-family: arial;" -->

* A classe do bean precisa ser definida como pública
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white; font-family: arial;" -->

* A classe não pode ser abstrata nem final
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white; font-family: arial;" -->

* É necessário declarar um construtor sem argumentos
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white; font-family: arial;" -->

* A classe não pode definir nenhum método finalize
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white; font-family: arial;" -->

* Método finalize é chamado pelo coletor de lixo e é geralmente utilizado para liberação de recursos
<!-- .element: style="margin-bottom:35px; font-size: 25px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#222c44" data-transition="convex" -->
## Implementação de um Message Driven
<!-- .element: style="margin-bottom:45px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* A notação `@MessageDriven` tipicamente contém um atributo chamado `@ActivationConfigProperty` 
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

* Esse atributo especifica o nome JNDI de onde o bean irá consumir as mensagens
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->

* Para beans mais complexos, existe também um atributo chamado activationConfig que contém o elemento `@ActivationConfigProperty` que permite algumas configurações extras, por exemplo:
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white; font-family: arial;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="convex" -->
## Exemplo de Message-driven Bean
<!-- .element: style="margin-bottom:50px; font-size: 40px; font-family: Marker Felt;" -->

```java
@MessageDriven(mappedName="jms/MyQueue", activationConfig =  {
 @ActivationConfigProperty(propertyName = "acknowledgeMode",
                           propertyValue = "Auto-acknowledge"),
 @ActivationConfigProperty(propertyName = "destinationType",
                           propertyValue = "javax.jms.Queue")
})
public class SimpleMessageBean implements MessageListener {
    //code
}
```
<!-- .element: style="margin-bottom:40px; font-size: 16px;" -->


<!-- .slide: data-background="#222c44" data-transition="convex" -->
##  Parâmetros da anotação @ActivationConfigProperty
<!-- .element: style="margin-bottom:50px; font-size: 40px; color:white; font-family: Marker Felt;" -->

| acknowledgeMode        	| Permite controlar se uma mensagem foi consumida       	|
|------------------------	|-------------------------------------------------------	|
| destinationType        	| Queue ou topic                                        	|
| subscriptionDurability 	| Temporária ou durável                                 	|
| clientId               	| O identificador de um cliente com conexão durável     	|
| subscriptionName       	| O nome da assinatura de clientes duráveis             	|
| MessageSelector        	| Permite criar uma expressão para filtrar as mensagen  	|
| addressList            	| Utilizado para consumir mensagens de clientes remotos 	|


<!-- .slide: data-background="#222c44" data-transition="convex" -->
## Interfaces de um Message-driven Bean
<!-- .element: style="margin-bottom:40px; font-size: 40px; color:white; font-family: Marker Felt;" -->

* Diferente dos sessions beans os MDBs não possuem interface local ou remota
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white; font-family: arial;" -->

* Os clientes não realizam a localização nem a invocação dos métodos de um message-driven, por exemplo:
<!-- .element: style="margin-bottom:20px; font-size: 25px; color:white; font-family: arial;" -->

![imagem](img/queue.svg)
<!-- .element height="50%" width="50%" -->

Fonte: [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#receiving-messages-asynchronously-using-a-message-driven-bean)
<!-- .element: style="margin-bottom:10px; font-size: 10px; color:white"  -->


<!-- .slide: data-background="#222c44" data-transition="convex" -->
## Interfaces de um Message-driven Bean
<!-- .element: style="margin-bottom:40px; font-size: 40px; color:white; font-family: Marker Felt;" -->

* Os MDBs podem também responder a um tópico
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white; font-family: arial;" -->

![imagem](img/topic.svg)
<!-- .element height="50%" width="50%" -->

Fonte: [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#receiving-messages-asynchronously-using-a-message-driven-bean)
<!-- .element: style="margin-bottom:10px; font-size: 10px; color:white"  -->


<!-- .slide: data-background="#222c44" data-transition="convex" -->
## Interfaces de um Message-driven Bean
<!-- .element: style="margin-bottom:50px; font-size: 40px; color:white; font-family: Marker Felt;" -->

* Quando recebe uma mensagem o container invoca os métodos listeners
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white; font-family: arial;" -->

* Um método listener deve seguir as seguintes regras:
<!-- .element: style="margin-bottom:20px; font-size: 25px; color:white; font-family: arial;" -->

    * Deve ser declarado como público
    <!-- .element: style="margin-bottom:20px; font-size: 22px; color:white; font-family: fantasy;" -->

    * Não pode ser estático ou final
    <!-- .element: style="margin-bottom:20px; font-size: 22px; color:white; font-family: fantasy;" -->

    * Quando utilizamos JMS, respeitamos a interface MessageListener e implementamos o método onMessage
    <!-- .element: style="margin-bottom:20px; font-size: 22px; color:white; font-family: fantasy;" -->


<!-- .slide: data-background="#222c44" data-transition="convex" -->
## Interfaces de um Message-driven Bean
<!-- .element: style="margin-bottom:50px; font-size: 40px; color:white; font-family: Marker Felt;" -->

* Quando uma mensagem chega no bean, o método `onMessage` é invocado
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white;" -->

* O bean é responsável por analisar a mensagem recebida
<!-- .element: style="margin-bottom:40px; font-size: 25px; color:white;" -->

* O método `onMessage` deve seguir as regras:
<!-- .element: style="margin-bottom:30px; font-size: 25px; color:white;" -->

    * O tipo de retorno deve ser void
    <!-- .element: style="margin-bottom:20px; font-size: 22px; color:white; font-family: fantasy;" -->

    * Deve possuir um argumento do tipo `javax.jms.Message`
    <!-- .element: style="margin-bottom:20px; font-size: 22px; color:white; font-family: fantasy;" -->


<!-- .slide: data-background="#E8F4FE" data-transition="convex" -->
## Um exemplo de Message-Driven Bean
<!-- .element: style="margin-bottom:40px; font-size: 45px; font-family: Marker Felt;" -->

```java
@MessageDriven(mappedName = "jms/MyQueue")
public class MyMessageBean implements MessageListener {

    private static final Logger logger =
    Logger.getLogger(MyMessageBean.class.getName());

    public MyMessageBean() { }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage msg = (TextMessage) message;
            logger.log(Level.INFO, msg.getText());
        }
        catch (JMSException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }
}
```
<!-- .element: style="margin-bottom:40px; font-size: 14px;" -->



<!-- .slide: data-background="#222c44" data-transition="convex" -->
# Referência
<!-- .element: style="margin-bottom:40px; font-size: 50px; color:white; font-family: Marker Felt;" -->

[The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/)
<!-- .element: style="margin-bottom:50px; font-size: 20px;" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>