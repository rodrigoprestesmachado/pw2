# Introdução aos EJBs

Rodrigo Prestes Machado (rodrigo.prestes at poa.ifrs.edu.br)



## O que é um Enterprise Bean?

* Um enterprise bean é um componente que roda no lado do servidor que encapsula as regras de negócio de uma aplicação

* A lógica do negócio é o código que cumpre o propósito da aplicação

* Por exemplo, em um aplicativo de controle de estoque, os enterprise beans podem implementar a lógica de métodos como: *checkInventoryLevel* ou *orderProduct*



## Benefícios de um Enterprise Bean

* Enterprise beans simplificam o desenvolvimento de aplicações grandes e
    distribuídas

* Primeiro, o container EJB provê serviços como: gerenciamento de transações e autorização de segurança

* Segundo, como os beans encapsulam a lógica do negócio, os clientes não irão possuir código como regras de negócio ou acesso aos bancos de dados

* Terceiro, como os EJBs são componentes portáveis, novas aplicações podem ser criadas a partir dos beans existentes



## Quando utilizar um EJB?
  
* A aplicação necessitar ser escalável
  * Para acomodar um número crescente de usuários, você pode necessitar distribuir os componentes de uma aplicação em várias máquinas
  
* As operações devem garantir a integridade dos dados
  * Enterprise beans suportam transações e controlam o acesso simultâneo a objetos compartilhados

* A aplicação terá uma variedade de clientes
  * Clientes remotos podem facilmente localizar um EB. Esses clientes podem ser finos, variados e numerosos



## Tipos de Enterprise Beans

* Session
  * Executam tarefas para um cliente, opcionalmente, pode implementar um Web Service

* Message-driven
  * Atuam como um listener para um tipo de mensagem específica, como por exemplo, mensagens JMS (Java Message Service)



# Session Bean



## O que é um Session Bean?

* Um Session Bean encapsula uma regra de negócio que pode ser invocada por um cliente local, remoto ou por meio de Web Service

* Os Sessions Beans podem ser de três tipos:
  * Stateless
  * Stateful
  * Singleton



## Stateless

* Um stateless session bean não mantém um estado conversacional com um cliente

* Quando um cliente executa um método de um stateless session bean, os atributos do bean irão conter valores específicos de um cliente somente enquanto durar a invocação

* Assim, todas as instancias de um stateless session bean são equivalentes

* Isto permite que o container EJB aloque qualquer instância para qualquer cliente



## Stateless

* Como podem suportar vários clientes, um stateless session bean oferecem uma melhor escalabilidade

* Normalmente uma aplicação necessita de menos **Stateless** beans do que **Stateful** beans para suportar o mesmo número de clientes

* Um stateless session bean pode implementar um Web Service, porém, um **Stateful** session bean não




## Stateful

* O estado de um objeto consiste dos valores das suas variáveis de instância (atributos)

* Em um **Stateful** session bean, as variáveis de instância representam o estado da interação entre um cliente e um bean (estado conversacional)

* Como o nome sugere, um **Stateful** session bean é similar a uma sessão interativa, ou seja, o bean não é compartilhado e possui apenas um cliente



## Stateful

* O estado é mantido enquanto durar a sessão entre o cliente e o bean. Se o cliente remove o bean, a sessão termina e o estado desaparece

* Esta natureza transitória do estado não é um problema. Quando a conversa entre o cliente e o bean termina, não há necessidade de manter o estado


## Singleton

* Um singleton session bean é instanciado apenas uma única vez durante o ciclo de vida de uma aplicação

* Um singleton session bean é similar a um stateless session beans, mas, difere apenas na questão de instanciação

* Assim, um Web Service também pode ser implementado como um singleton session bean



## Singleton

* Aplicações que utilizam este tipo de bean, podem especificar que o singleton deve ser instanciado antes da aplicação

* Assim, um singleton session bean pode realizar tarefas de inicialização ou desligamento de aplicações



# Quando Utilizar os Session Beans?



## Stateless

* Quando o estado do bean não possuir dados de um cliente específico

* Os métodos do bean executarem tarefas genéricas. Por exemplo, você pode utilizar um session bean para enviar um e-mail que confirme uma compra online

* Quando o bean necessitar implementar um Web Service



## Stateful

* Quando houver a necessidade de que o estado de um bean represente a interação entre um EB e um cliente específico

* O bean necessitar guardar informações sobre um cliente durante as invocações de um método
  
* O bean realizar a intermediação entre o cliente e outros componentes da aplicação, apresentando uma visão simplificada para o cliente (Facade)

* Quando o bean gerenciar o fluxo de trabalho com outros enterprise beans



## Singleton

* Quando for necessário compartilhar um estado em toda a aplicação
  
* Quando o bean necessitar ser acessado por várias threads de forma concorrente
  
* A aplicação necessitar que um bean execute tarefas de inicialização e desligamento
  
* Quando um bean necessitar implementar um Web Service



# Message-Driven Bean



## O que é um Message-Driven Bean?

* Um message-driven bean permite que aplicações Java EE processem mensagens de forma assíncrona

* Este tipo de bean normalmente atua como um JMS listerner

* As mensagens podem ser enviadas por:
  * Qualquer componente Java EE (uma aplicação cliente, outro bean, etc.)
  * Aplicações de utilizam JMS
  * Sistemas que não utilizam a tecnologia Java EE



## O que é um Message-Driven Bean?

* Diferente de um session bean, um message-driven bean possui apenas a classe do bean, ou seja, não é acessado por meio de interfaces

* Em alguns aspectos, um message-driven bean assemelha-se de um **Stateless**:
  * Não guardam dados ou estado conversacional de um cliente
  * Todas as instâncias de um message-driven bean são equivalentes
  * Um message-driven bean pode processar mensagens de múltiplos clientes



## O que é um Message-Driven Bean?

* Os atributos de um message-driven bean podem conter algum estado (conexão de banco, conexão JMS, etc.) para manipular
    as mensagens dos clientes

* Clientes não localizam e invocam métodos de um message-driven bean diretamente

* Por exemplo, um cliente acessa um bean enviando mensagens para o destino da mensagem (fila ou tópico)



## Características

* Executam após receber uma mensagem de um cliente

* São invocados de forma assíncrona

* Geralmente são de curta duração

* Não representam os dados diretamente no banco de dados compartilhados, mas, podem acessar e atualizar esses dados

* Podem ser transaction-aware

* São stateless



## Message-Driven Bean

* Quando uma mensagem chega o container executa o método *onMessage* do message-driven bean

* O método onMessage normalmente converte a mensagem para um dos cinco tipos de dados definidos no JMS

* Uma mensagem pode ser executada como uma transação. Assim, todas as operações dentro do método onMessage são parte de uma única transação



## Quando Utilizar?

* Sessions beans permitem que você troque mensagens JMS de forma síncrona

* Assim, utilize message-driven beans quando você quiser realizar troca de mensagens na sua aplicação de forma assíncrona



## Referências

* Jendrock E.; Evans I.; Gollapudi D.; Haase K.; Chinmayee S.; The Java EE 6 Tutorial: Basic Concepts. Ed. Prentice Hall, ISBN-10: 0137081855