# Introdução

De maneira simplificada, os micro serviços são uma maneira conceber a arquitetura interna de um sistema onde as funcionalidades são vistas como serviços independentes. Por exemplo, em um sistema de comércio eletrônico poderia ser dividido em: catálogo de produtos, estoque, carrinho de compras, pagamento, entrega, entre outros.

Neste tipo de arquitetura cada micro serviço é executado de uma maneira separada dos demais serviços, inclusive, é comum utilizarem recursos de virtualização e/ou docker para se isolar os serviço em máquinas distintas.

Esse tipo de abordagem possui semelhanças com as técnicas de modularização que são comuns no desenvolvimento de aplicações nativas para _desktops_. Porém,com o advento da Web, muitas aplicações passaram a ser instaladas em servidores Web o que acarretou novas complicações, entre elas, a necessidade de se suportar diversos clientes simultaneamente.

Assim, por mais que pudessem ser estruturados de maneira modular, os sistemas escritos para Web eram instalados como um sistema monolítico, ou seja, o sistema inteiro era instalado no servidor. Como consequência, para se escalar um sistema Web se necessitava reinstalar todo os sistema em um segundo servidor.

Voltando ao exemplo de um sistema de comércio eletrônico, imagine uma situação onde a funcionalidade do catálogo de produtos se mostre como a mais acessada pelos usuários e, portanto, aquela que realmente consome recursos de processamento. Agora, imagine que o sistema foi construído de maneira monolítica, portanto, para suportar um número crescente de usuários devemos instalar esse sistema num outro servidor. Porém, nesse cenário teremos que instalar toda a aplicação mesmo sabendo que apenas a funcionalidade do catálogo de produtos que demanda por recursos.

Com o passar do tempo, novas tecnologias foram surgindo o que permite que hoje pensemos em construir sistema na Web com uma arquitetura baseada em pequenos serviços. Exemplos de tecnologias que hoje estão disponíveis e que auxiliam na construção de serviços: Serviços Web em REST, manipulação de dados em JSON, virtualização/docker, entre muitas outras.

## Vantagens

A abordagem de micro serviços proporciona algumas vantagens, entre elas, reduz a interdependência entre as funcionalidades do sistema, facilita o reuso de funcionalidades no desenvolvimento de novas aplicações (_time to marketing_), favorece o processo de escalar uma aplicação (ex.: Kubernetes, Docker Swarm, , etc. ).

## Principais problemas

# Referências

* Eclipse MicroProfile White Paper 2019. Disponível em: https://microprofile.io/resources/#white-paper


* Saavedra, Cesar. Hands-On Enterprise Java Microservices with Eclipse MicroProfile: Build and optimize your microservice architecture with Java . Packt Publishing. Edição do Kindle.