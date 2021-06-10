# Introdução aos EJBs

Rodrigo Prestes Machado (rodrigo.prestes at poa.ifrs.edu.br)



## O que são os Enterprise Jakarta Beans?


## O que são os Enterprise Jakarta Beans?

* Um enterprise java bean (ou enterprise bean) é um **componente** que roda no lado do servidor que encapsula as regras de negócio de uma aplicação
<!-- .element: class="fragment" -->

* A lógica do negócio é o código que cumpre o propósito da aplicação
<!-- .element: class="fragment" -->

* Por exemplo, em um aplicativo de controle de estoque, os enterprise beans podem implementar a lógica de métodos como: *checkInventoryLevel* ou *orderProduct*
<!-- .element: class="fragment" -->


## O que são os Enterprise Jakarta Beans?

* A imagem apresenta de maneira geral as camadas de *softwares* necessários para se executar os EJBs

![imagem](img/software-stack.png) <!-- .element height="50%" width="50%" -->


## Benefícios de um Enterprise Bean

* Enterprise beans simplificam o desenvolvimento de aplicações grandes e distribuídas
<!-- .element: class="fragment" -->

* Primeiro, o container EJB provê serviços como: gerenciamento de transações e autorização de segurança
<!-- .element: class="fragment" -->

* Segundo, como os beans encapsulam a lógica do negócio, os clientes não irão possuir código como regras de negócio ou acesso aos bancos de dados
<!-- .element: class="fragment" -->

* Terceiro, como os EJBs são componentes portáveis, novas aplicações podem ser criadas a partir dos beans existentes
<!-- .element: class="fragment" -->


## Quando utilizar um EJB?
  
* A aplicação necessitar ser escalável
<!-- .element: class="fragment" -->
  * Para acomodar um número crescente de usuários, você pode necessitar distribuir os componentes de uma aplicação em várias máquinas
  <!-- .element: class="fragment" -->
  
* As operações devem garantir a integridade dos dados
<!-- .element: class="fragment" -->
  * Enterprise beans suportam transações e controlam o acesso simultâneo a objetos compartilhados
  <!-- .element: class="fragment" -->

* A aplicação terá uma variedade de clientes
<!-- .element: class="fragment" -->
  * Clientes remotos podem facilmente localizar um EJB. Esses clientes podem ser finos, variados e numerosos
  <!-- .element: class="fragment" -->


## Tipos de Enterprise Beans

* Session
<!-- .element: class="fragment" -->  
  * Executam tarefas para um cliente, opcionalmente, pode implementar um Web Service
  <!-- .element: class="fragment" -->

* Message-driven
<!-- .element: class="fragment" -->
  * Atuam como um listener para um tipo de mensagem específica, como por exemplo, mensagens JMS (Jakarta Message Service)
  <!-- .element: class="fragment" -->



## Session Bean


## O que é um Session Bean?

* Um Session Bean encapsula uma regra de negócio que pode ser invocada por um cliente local, remoto ou por meio de Web Service
<!-- .element: class="fragment" -->

* Os Sessions Beans podem ser de três tipos:
<!-- .element: class="fragment" -->
  * Stateless
  <!-- .element: class="fragment" -->
  * Stateful
  <!-- .element: class="fragment" -->
  * Singleton
  <!-- .element: class="fragment" -->


## Stateless

* Um stateless session bean não mantém um estado conversacional com um cliente
<!-- .element: class="fragment" -->

* Quando um cliente executa um método de um stateless session bean, os atributos do bean irão conter valores específicos de um cliente somente enquanto durar a invocação
<!-- .element: class="fragment" -->

* Assim, todas as instancias de um stateless session bean são equivalentes
<!-- .element: class="fragment" -->

* Isto permite que o container EJB aloque qualquer instância para qualquer cliente
<!-- .element: class="fragment" -->


## Stateless

* Como podem suportar vários clientes, um stateless session bean oferecem uma melhor escalabilidade
<!-- .element: class="fragment" -->

* Normalmente uma aplicação necessita de menos Stateless beans do que Stateful beans para suportar o mesmo número de clientes
<!-- .element: class="fragment" -->

* Um stateless session bean pode implementar um Web Service, porém, um Stateful session bean não
<!-- .element: class="fragment" -->


## Stateful

* O estado de um objeto consiste dos valores das suas variáveis de instância (atributos)
<!-- .element: class="fragment" -->

* Em um Stateful session bean, as variáveis de instância representam o estado da interação entre um cliente e um bean (estado conversacional)
<!-- .element: class="fragment" -->

* Como o nome sugere, um Stateful session bean é similar a uma sessão interativa, ou seja, o bean não é compartilhado e possui apenas um cliente
<!-- .element: class="fragment" -->


## Stateful

* O estado é mantido enquanto durar a sessão entre o cliente e o bean. Se o cliente remove o bean, a sessão termina e o estado desaparece
<!-- .element: class="fragment" -->

* Esta natureza transitória do estado não é um problema. Quando a conversa entre o cliente e o bean termina, não há necessidade de manter o estado
<!-- .element: class="fragment" -->


## Singleton

* Um singleton session bean é instanciado apenas uma única vez durante o ciclo de vida de uma aplicação
<!-- .element: class="fragment" -->

* Um singleton session bean é similar a um stateless session beans, mas, difere apenas na questão de instanciação
<!-- .element: class="fragment" -->

* Assim, um Web Service também pode ser implementado como um singleton session bean
<!-- .element: class="fragment" -->


## Singleton

* Aplicações que utilizam este tipo de bean, podem especificar que o singleton deve ser instanciado antes da aplicação
<!-- .element: class="fragment" -->

* Assim, um singleton session bean pode realizar tarefas de inicialização ou desligamento de aplicações
<!-- .element: class="fragment" -->


## Quando utilizar os Enterprise Session Beans?


## Stateless

* Quando o estado do bean não possuir dados de um cliente específico
<!-- .element: class="fragment" -->

* Os métodos do bean executarem tarefas genéricas. Por exemplo, você pode utilizar um session bean para enviar um e-mail que confirme uma compra online
<!-- .element: class="fragment" -->

* Quando o bean necessitar implementar um Web Service
<!-- .element: class="fragment" -->


## Stateful

* Quando houver a necessidade de que o estado de um bean represente a interação entre um EB e um cliente específico
<!-- .element: class="fragment" -->

* O bean necessitar guardar informações sobre um cliente durante as invocações de um método
<!-- .element: class="fragment" -->

* O bean realizar a intermediação entre o cliente e outros componentes da aplicação, apresentando uma visão simplificada para o cliente (Facade)
<!-- .element: class="fragment" -->

* Quando o bean gerenciar o fluxo de trabalho com outros enterprise beans
<!-- .element: class="fragment" -->


## Singleton

* Quando for necessário compartilhar um estado em toda a aplicação
<!-- .element: class="fragment" -->

* Quando o bean necessitar ser acessado por várias threads de forma concorrente
<!-- .element: class="fragment" -->

* A aplicação necessitar que um bean execute tarefas de inicialização e desligamento
<!-- .element: class="fragment" -->

* Quando um bean necessitar implementar um Web Service
<!-- .element: class="fragment" -->



## Message-Driven Bean


## O que é um Message-Driven Bean?

* Um message-driven bean (MDB) permite que aplicações Jakarta EE processem mensagens de forma assíncrona
<!-- .element: class="fragment" -->

* Este tipo de bean normalmente atua como um JMS listerner
<!-- .element: class="fragment" -->

* As mensagens podem ser enviadas por:
<!-- .element: class="fragment" -->
  * Qualquer componente Jakarta EE (uma aplicação cliente, outro bean, etc.)
  <!-- .element: class="fragment" -->
  * Aplicações de utilizam JMS
  <!-- .element: class="fragment" -->
  
  * Sistemas que não utilizam a tecnologia Jakarta EE
  <!-- .element: class="fragment" -->


## O que é um Message-Driven Bean?

* Diferente de um session bean, um message-driven bean possui apenas a classe do bean, ou seja, não é acessado por meio de interfaces
<!-- .element: class="fragment" -->

* Em alguns aspectos, um message-driven bean assemelha-se de um Stateless:
<!-- .element: class="fragment" -->

  * Não guardam dados ou estado conversacional de um cliente
  <!-- .element: class="fragment" -->

  * Todas as instâncias de um message-driven bean são equivalentes
  <!-- .element: class="fragment" -->

  * Um message-driven bean pode processar mensagens de múltiplos clientes
  <!-- .element: class="fragment" -->


## O que é um Message-Driven Bean?

* Os atributos de um message-driven bean podem conter algum estado (conexão de banco, conexão JMS, etc.) para manipular as mensagens dos clientes
<!-- .element: class="fragment" -->

* Clientes não localizam e invocam métodos de um message-driven bean diretamente
<!-- .element: class="fragment" -->

* Por exemplo, um cliente acessa um bean enviando mensagens para o destino da mensagem (fila ou tópico)
<!-- .element: class="fragment" -->


## Principais características de um MDB

* Executam após receber uma mensagem de um cliente
<!-- .element: class="fragment" -->

* São invocados de forma assíncrona
<!-- .element: class="fragment" -->

* Geralmente são de curta duração
<!-- .element: class="fragment" -->

* Não representam os dados diretamente no banco de dados compartilhados, mas, podem acessar e atualizar esses dados
<!-- .element: class="fragment" -->

* Podem ser *transaction-aware*
<!-- .element: class="fragment" -->

* São stateless
<!-- .element: class="fragment" -->


## Funcionamento de um Message-Driven Bean

* Quando uma mensagem chega o container executa o método *onMessage* do message-driven bean
<!-- .element: class="fragment" -->

* O método onMessage normalmente converte a mensagem para um dos cinco tipos de dados definidos no JMS
<!-- .element: class="fragment" -->

* Uma mensagem pode ser executada como uma transação. Assim, todas as operações dentro do método *onMessage* são parte de uma única transação
<!-- .element: class="fragment" -->


## Quando Utilizar um MDB?

* Sessions beans permitem que você troque mensagens JMS de forma síncrona
<!-- .element: class="fragment" -->

* Assim, utilize message-driven beans quando você quiser realizar troca de mensagens na sua aplicação de forma assíncrona
<!-- .element: class="fragment" -->


## Referências

* Jendrock E.; Evans I.; Gollapudi D.; Haase K.; Chinmayee S.; The Jakarta EE 6 Tutorial: Basic Concepts. Ed. Prentice Hall, ISBN-10: 0137081855