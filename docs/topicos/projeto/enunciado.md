---
layout: default
title: Enunciado do Projeto
parent: Estudo por Projeto
nav_order: 1
---

# Enunciado do Trabalho Final

Para a realização do trabalho final, você deve projetar e implementar um
**backend** de software, escrito em **Java** com **Quarkus**, dividido em
**dois ou mais micro serviços** que se comuniquem entre si.
{: .fs-3 }

Existem vários exemplos de sistemas que podem servir de inspiração para o
domínio da aplicação, como um sistema de pedidos (com micro serviços de
produtos, pedidos e pagamentos), um sistema de biblioteca (com micro
serviços de acervo, usuários e empréstimos), um sistema de reservas (com
micro serviços de hospedagens, reservas e avaliações), entre muitos outros.
Além dessas opções, você também pode propor um domínio original de sua
autoria: os estudantes têm liberdade criativa para tentar algo novo.
{: .fs-3 }

Caso você utilize como base um tutorial, vídeo ou exemplo de terceiros para
a implementação de algum dos micro serviços, o(a) estudante deve seguir a
referência até o final e, em seguida, **acrescentar pelo menos cinco
funcionalidades adicionais** às apresentadas na referência utilizada,
**citando explicitamente a fonte** (documentação, tutorial ou vídeo)
consultada.
{: .fs-3 }

## Entrega da proposta em PDF

Antes de iniciar a implementação, cada estudante deve entregar um documento
em **PDF** descrevendo a proposta do projeto, contendo pelo menos:
{: .fs-3 }

* **Conceito do sistema**: uma descrição geral do sistema, incluindo o
  domínio, o objetivo do usuário final e o contexto de uso.
* **Arquitetura de micro serviços**: um [diagrama de componentes
  UML](https://www.uml-diagrams.org/component-diagrams.html) e uma
  descrição de cada micro serviço proposto (mínimo de dois), suas
  responsabilidades e como eles se comunicam entre si (REST, REST Client,
  mensageria, etc.).
* **Modelo de dados**: as principais entidades de cada micro serviço e seus
  relacionamentos.
* **Lista de funcionalidades a serem desenvolvidas**: uma lista objetiva
  dos endpoints e funcionalidades planejadas para a versão final do
  projeto, separadas por micro serviço.
* **Requisitos não funcionais**: quais conceitos da disciplina serão
  aplicados (ex.: autenticação/autorização com JWT, tolerância a falhas,
  observabilidade/logging, métricas, health checks, etc.).
* **Referências**: lista de fontes consultadas (documentação, tutoriais,
  vídeos, frameworks, sistemas que serviram de inspiração, etc.).
{: .fs-3 }

## Requisitos gerais

* O backend deve ser implementado em **Java**, utilizando o framework
  **Quarkus**, e dividido em **no mínimo dois micro serviços**
  independentes, cada um com sua própria responsabilidade e persistência
  de dados.
* Os micro serviços devem se comunicar entre si por meio de chamadas REST
  (usando REST Client).
* É permitido o uso de bibliotecas ou exemplos de terceiros, desde que a
  origem de qualquer código ou template utilizado como base seja
  **referenciada** no trabalho.
* Caso algum micro serviço seja baseado em um tutorial, vídeo ou exemplo de
  terceiros, é obrigatório implementar **no mínimo cinco funcionalidades
  novas** naquele micro serviço, que não constem na referência original,
  além de creditar a fonte utilizada.
* Projetos originais (criados do zero, sem seguir um tutorial específico)
  têm liberdade total de escopo, desde que apresentem complexidade
  compatível com os conceitos trabalhados na disciplina (ex.: JPA/Hibernate,
  REST, REST Client, JWT, tolerância a falhas, logging, métricas, health
  checks, entre outros).
{: .fs-3 }

## Rubrica de Avaliação

Os critérios utilizados para a correção do trabalho final estão descritos
na [Rubrica de Avaliação](rubrica.md).
{: .fs-3 }

## Referências

* [Quarkus - Supersonic Subatomic Java](https://quarkus.io)
{: .fs-3 }

<center>
<a href="https://rpmhub.dev" target="_blank"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
