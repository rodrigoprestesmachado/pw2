# catalog

Projeto-base do exercício de **MicroProfile Rest Client** (rede social de
troca de livros). Este projeto usa Quarkus, o Supersonic Subatomic Java
Framework.

Consulte a seção "Exercício de Fixação" da página
[Rest Client](https://pw2.rpmhub.dev/topicos/rest-client/rest-client.html)
para a descrição completa dos *endpoints*, tipos de dados e diagramas de
sequência que este serviço (Catálogo de Livros) deve implementar.

O projeto já contém, em `src/test/java/dev/rpmhub/IntegrationTest.java`, o
teste de integração que sua implementação precisa fazer passar. **Não
altere esse arquivo de teste**: implemente as classes em `src/main/java`
necessárias para que ele passe.

## Rodando em modo de desenvolvimento

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_** Quarkus disponibiliza uma Dev UI, disponível apenas em modo
> dev, em http://localhost:8080/q/dev/.

## Executando o teste de integração

O teste de integração depende do serviço `loans` (Gerenciamento de
Empréstimos) também estar em execução. Veja as instruções no Javadoc da
classe `IntegrationTest` e no README do projeto `loans`.
