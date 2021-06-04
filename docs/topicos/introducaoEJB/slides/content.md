<!-- .slide: data-background-image="https://dailytravelpill.com/wp-content/uploads/2021/02/backpacking-in-java-attractions.jpg" 
data-transition="zoom" 
-->
# Introdução aos EJBs
<!-- .element: style="margin-bottom:100px; font-size: 50px; color:black;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:black;" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom:30px; font-size: 15px; color:black;" -->


<!-- .slide: data-background="#F0FDFF" data-transition="zoom" -->
## O que são os Enterprise Java Beans?

* Um Enterprise Java Bean (EJB) é um **componente** que roda no lado do servidor que encapsula as regras de negócio de uma aplicação
<!-- .element: style="margin-bottom:70px; font-size: 30px;" -->

* A lógica do negócio é o código que cumpre o propósito da aplicação
<!-- .element: style="margin-bottom:70px; font-size: 30px;" -->

* Por exemplo, em um aplicativo de controle de estoque, os enterprise beans podem implementar a lógica de métodos como: *checkInventoryLevel* ou *orderProduct*
<!-- .element: style="margin-bottom:70px; font-size: 30px;" -->


<!-- .slide: data-background="#F0FDFF" data-transition="zoom" -->
## O que são os Enterprise Java Beans?

* A imagem apresenta, de forma geral, as camadas de software necessárias para executar os EJBs
<!-- .element: style="margin-bottom:10px; font-size: 25px;" -->

![imagem](img/software-stack.png) <!-- .element height="50%" width="50%" -->


<!-- .slide: data-background="#F0FDFF" data-transition="zoom" -->
## Camadas do Jakarta EE

![imagem](img/jakarta.svg) <!-- .element height="60%" width="60%" -->

Fonte: [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/)
<!-- .element: style="margin-bottom:50px; font-size: 15px;" -->


<!-- .slide: data-background="#F0FDFF" data-transition="zoom" -->
## Benefícios de um Enterprise Bean

* Enterprise beans simplificam o desenvolvimento de aplicações grandes e distribuídas
<!-- .element: style="margin-bottom:70px; font-size: 30px;" -->

* Primeiro, o container EJB provê serviços como: gerenciamento de transações e autorização de segurança
<!-- .element: style="margin-bottom:70px; font-size: 30px;" -->

* Segundo, como os beans encapsulam a lógica do negócio, os clientes não irão possuir código como regras de negócio ou acesso aos bancos de dados
<!-- .element: style="margin-bottom:70px; font-size: 30px;" -->

* Terceiro, novas aplicações podem ser criadas a partir dos beans que possuem as regras de negócio existentes
<!-- .element: style="margin-bottom:70px; font-size: 30px;" -->


<!-- .slide: data-background="#FBF0FF" data-transition="zoom" -->
## Quando utilizar um EJB?

* A aplicação necessitar ser escalável
<!-- .element: style="margin-bottom:30px; font-size: 30px;" -->

  * Para acomodar um número crescente de usuários, você pode necessitar distribuir os componentes de uma aplicação em várias máquinas
  <!-- .element: style="margin-bottom:50px; font-size: 20px;" -->

* As operações devem garantir a integridade dos dados
<!-- .element: style="margin-bottom:30px; font-size: 30px;" -->

  * Enterprise beans suportam transações e controlam o acesso simultâneo a objetos compartilhados
  <!-- .element: style="margin-bottom:50px; font-size: 20px;" -->

* A aplicação terá uma variedade de clientes
<!-- .element: style="margin-bottom:30px; font-size: 30px;" -->

  * Clientes remotos podem facilmente localizar um EJB. Esses clientes podem ser finos, variados e numerosos
  <!-- .element: style="margin-bottom:50px; font-size: 20px;" -->


<!-- .slide: data-background="#FDF9EC" data-transition="zoom" -->
## Tipos de Enterprise Beans

* Session
<!-- .element: style="margin-bottom:30px; font-size: 30px;" -->
  * Executam tarefas para um cliente, opcionalmente, pode implementar um Web Service
  <!-- .element: style="margin-bottom:30px; font-size: 25px;" -->
  * Os Session podem ser: Stateless, Stateful e Singleton
  <!-- .element: style="margin-bottom:30px; font-size: 25px;" -->

* Message-driven
<!-- .element: style="margin-bottom:30px; font-size: 30px;" -->
  * Atuam como um listener para um tipo de mensagem específica, como por exemplo, mensagens JMS (Java Message Service)
<!-- .element: style="margin-bottom:50px; font-size: 25px;" -->


## Referência

[The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/)
<!-- .element: style="margin-bottom:70px; font-size: 30px;" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>