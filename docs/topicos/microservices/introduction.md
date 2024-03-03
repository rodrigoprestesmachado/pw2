---
layout: default
title: Micro serviços
nav_order: 4
---

# Micro Serviços

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/microservices/slides/index.html#/"
    title="Microservices" width="90%" height="500" style="border:none;">
    </iframe>
</center>

## Resumo 📖

Os micro serviços são uma maneira conceber a
arquitetura interna de um sistema onde as funcionalidades são vistas como
serviços independentes. Por exemplo, em um sistema de comércio eletrônico
poderia ser dividido em: catálogo de produtos, estoque, carrinho de compras,
pagamento, entrega, entre outros. A Figura 1 ilustra um exemplo de como
poderia ser organizado um sistema baseado em micro serviços.

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/microservices/microservices.puml">
        <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/microservices/microservices.puml" alt="Microservices" width="50%" height="50%"/>
    </a>
    <br/>
    Figura 1 - Exemplo de arquitetura baseada em micro serviços.
</center>

Neste tipo de arquitetura cada micro serviço é executado de uma maneira separada
dos demais serviços, inclusive, é comum utilizarem recursos de virtualização
e/ou docker para se isolar os serviço em máquinas distintas. Esse tipo de
abordagem possui semelhanças com as técnicas de modularização que
são comuns no desenvolvimento de aplicações nativas para _desktops_.

Porém,com o advento da Web, muitas aplicações passaram a ser instaladas em
servidores o que acarretou novas complicações, entre elas, a necessidade de se
suportar diversos clientes simultaneamente. Assim, por mais que pudessem ser
estruturados de maneira modular, os sistemas escritos para Web eram instalados
como um sistema monolítico, ou seja, o sistema inteiro (com todos seus módulos)
era instalado no servidor. Como consequência, para se escalar um sistema Web
se necessitava reinstalar todo os sistema em um segundo servidor.

Voltando ao exemplo de um sistema de comércio eletrônico, imagine uma situação
onde a funcionalidade do catálogo de produtos se mostre como a mais acessada
pelos usuários e, portanto, aquela que realmente consome recursos de
processamento. Agora, imagine que o sistema foi construído de maneira
monolítica, portanto, para suportar um número crescente de usuários devemos
instalar esse sistema num outro servidor. Porém, nesse cenário teremos que
instalar toda a aplicação mesmo sabendo que apenas a funcionalidade do catálogo
de produtos que demanda por recursos.

Com o passar do tempo, novas tecnologias foram surgindo o que permite que hoje
pensemos em construir sistema na Web com uma arquitetura baseada em pequenos
serviços. Exemplos das principais tecnologias que hoje estão disponíveis e que
auxiliam a construção de serviços: Serviços Web em REST, manipulação de dados
em JSON, virtualização/docker, entre muitas outras. A seguir vamos discutir as
principais vantagens e desvantagens dessa abordagem.

### Principais Vantagens

* **Escalabilidade Independente:** Cada micro serviço pode ser escalado de forma
independente, permitindo dimensionar apenas os componentes que estão
recebendo um aumento de carga, ao invés de escalar toda a aplicação.

* **_Time to marketing:_** facilita o reuso de funcionalidades no desenvolvimento
de novas aplicações, o que pode reduzir o tempo de desenvolvimento.

* **Resiliência:** Se um serviço falhar, isso não necessariamente afetará toda a
aplicação, já que outros serviços podem continuar funcionando.

* **Desacoplamento:** Cada micro serviço pode ser desenvolvido, implantado e
escalado de forma independente, o que permite a evolução de cada serviço
de maneira autônoma.

* **Tecnologia:** Cada micro serviço pode ser desenvolvido em uma linguagem de
programação diferente, o que permite a utilização da melhor tecnologia para
cada serviço.

* **Evolução Tecnológica:** Como cada micro serviço é independente, é mais fácil
adotar novas tecnologias e atualizar componentes sem afetar o sistema como um
todo.

* **Facilidade de Deploy:** Micro serviços podem ser implantados separadamente,
facilitando o processo de implantação contínua e permitindo atualizações
frequentes.

* **Desenvolvimento Independente:** Equipes podem trabalhar de forma
independente em diferentes micro serviços, acelerando o desenvolvimento e
permitindo atualizações mais rápidas.

### Principais Desvantagens

* **Complexidade Operacional:** Gerenciar e operar um grande número de micro
serviços pode aumentar a complexidade operacional, exigindo ferramentas e
práticas específicas para monitoramento, implantação, escalonamento e
manutenção.

* **Overhead de Comunicação:** Comunicação entre micro serviços geralmente é
feita através de redes, o que pode introduzir latência e overhead de rede,
especialmente em sistemas distribuídos.

* **Testabilidade:** Testar sistemas baseados em micro serviços pode ser mais
complexo do que testar monolitos, devido à necessidade de testes de integração
entre os diversos serviços.

* **Padronização:** É necessário ter padrões claros e consistentes para o
desenvolvimento, implantação e operação dos micro serviços, o que pode exigir
um esforço adicional para garantir a conformidade em toda a organização.

* **Escalabilidade Granular:** Embora os micro serviços ofereçam escalabilidade
independente, a granularidade da escalabilidade pode ser um desafio,
especialmente se os serviços tiverem dependências complexas entre si.

* **Gerenciamento de Versões:** Com múltiplos micro serviços em execução, o
gerenciamento de versões e compatibilidade entre diferentes versões de
serviços pode se tornar um desafio, especialmente em ambientes de implantação
contínua.

* **Segurança:** A segurança em um ambiente de micro serviços pode ser mais
complexa devido à necessidade de proteger várias interfaces de comunicação e
pontos de entrada.

* **Cultura Organizacional:** A mudança para uma arquitetura baseada em micro
serviços pode exigir uma mudança na cultura organizacional, incluindo uma
abordagem mais colaborativa entre equipes e uma mentalidade orientada
a serviços.

### Arquitetura de Micro Serviços

A arquitetura de micro serviços é uma abordagem de arquitetura de software que
consiste em dividir uma aplicação em um conjunto de serviços independentes, cada
um executando um processo de negócio específico. Cada serviço é construído em
torno de uma ou mais funcionalidades de negócio e pode ser implantado,
escalado e gerenciado de forma independente. Os micro serviços se comunicam
entre si através de APIs, geralmente usando protocolos de comunicação como HTTP,
REST, gRPC, entre outros.

Um tipo de arquitetura de micro serviços que tem ganhado popularidade nos últimos
anos é a arquitetura de _cloud-native_, que é projetada para ser executada em
ambientes de nuvem e aproveitar os recursos e serviços oferecidos por plataformas
de nuvem como AWS, Azure, Google Cloud, entre outros. A arquitetura de
_cloud-native_ é caracterizada por ser altamente distribuída, resiliente,
escalável e elástica, e geralmente é baseada em contêineres e orquestradores de
contêineres.

Numa arquitetura de micro serviços, existe pelo menos dois tipos de _workflows_
bastante conhecidos, são eles: coreografia e orquestração. Na coreografia, cada
micro serviço é responsável por coordenar suas próprias ações, enquanto na
orquestração, um serviço central é responsável por coordenar as ações dos
demais serviços. A escolha entre coreografia e orquestração depende do contexto
e dos requisitos do sistema, e ambas as abordagens têm vantagens e desvantagens.

### Eclipse MicroProfile

O [Eclipse MicroProfile](https://microprofile.io) é uma iniciativa de código
aberto que visa acelerar a inovação em micro serviços baseados em Java. O
MicroProfile foi criado em 2016 por um grupo de empresas e indivíduos que
estavam interessados em promover tecnologias de micro serviços para a plataforma
Java. O objetivo do MicroProfile é fornecer um conjunto de especificações e APIs
que podem ser usadas para construir e implantar aplicativos de micro serviços
baseados em Java.

O MicroProfile é baseado em tecnologias e padrões de código aberto, incluindo
Java EE, JAX-RS, CDI, JSON-P, JSON-B, JWT, OpenAPI, entre outros. O MicroProfile
também se integra com outras tecnologias de código aberto, como Kubernetes,
Docker, Prometheus, Jaeger, entre outros. O MicroProfile é projetado para ser
leve, modular e fácil de usar, e é adequado para construir aplicativos de micro
serviços que são executados em ambientes de nuvem.

## Exercícios 📝

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/microservices/questions.html"
    title="Introdução" width="90%" height="500"
    style="border:none;background-color:white;">
    </iframe>
</center>

# Referências

* Eclipse MicroProfile White Paper 2019. Disponível em:
<https://microprofile.io/resources/#white-paper>

* Saavedra, Cesar. Hands-On Enterprise Java Microservices with Eclipse
MicroProfile: Build and optimize your microservice architecture with Java.
Packt Publishing. Edição do Kindle.
