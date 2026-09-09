---
layout: default
title: Rubrica de Avaliação
parent: Estudo por Projeto
nav_order: 2
---

# Rubrica de Avaliação do Projeto

Esta rubrica descreve os critérios utilizados para avaliar o trabalho
final, totalizando **10 pontos**. A nota final é a soma da pontuação
obtida em cada critério.
{: .fs-3 }

| Critério | Peso | Insuficiente (0) | Regular | Bom | Excelente (nota máxima) |
|---|---|---|---|---|---|
| **Arquitetura de micro serviços e comunicação** | 3,0 | Não há divisão em micro serviços, ou a divisão não segue o diagrama proposto e as responsabilidades estão misturadas. | Os micro serviços existem, mas a separação de responsabilidades é confusa e/ou a comunicação via REST Client apresenta falhas relevantes. | Os micro serviços têm responsabilidades bem definidas e se comunicam via REST Client, com pequenas inconsistências em relação ao diagrama proposto. | A arquitetura implementada corresponde fielmente ao diagrama de componentes proposto, com responsabilidades bem separadas e comunicação via REST Client funcionando corretamente entre todos os micro serviços. |
| **Funcionalidades implementadas** | 4,0 | A maioria dos endpoints/funcionalidades planejados não foi implementada ou não funciona. | Parte dos endpoints/funcionalidades planejados foi implementada, com falhas relevantes de funcionamento ou tratamento de erros. | A maior parte dos endpoints/funcionalidades planejados foi implementada e funciona corretamente, com pequenas lacunas no tratamento de erros ou regras de negócio. | Todos os endpoints/funcionalidades planejados na proposta foram implementados, funcionam corretamente (incluindo regras de negócio) e possuem tratamento de erros adequado. |
| **Requisitos não funcionais** | 3,0 | Nenhum requisito não funcional (JWT, tolerância a falhas, logging, métricas, health checks) foi implementado. | Poucos requisitos não funcionais foram implementados, ou estão implementados de forma incompleta/pouco funcional. | A maioria dos requisitos não funcionais planejados foi implementada e funciona corretamente, com pequenas lacunas. | Todos os requisitos não funcionais planejados (ex.: JWT, tolerância a falhas, observabilidade/logging, métricas, health checks) foram implementados de forma completa, robusta e bem integrada aos micro serviços. |
{: .fs-3 }

A soma da pontuação obtida em cada critério compõe a nota final do
trabalho, em uma escala de **0 a 10**.
{: .fs-3 }

<center>
<a href="https://rpmhub.dev" target="_blank"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
