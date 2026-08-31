# loans

Projeto-base do exercício de **MicroProfile Rest Client** (rede social de
troca de livros). Este projeto usa Quarkus, o Supersonic Subatomic Java
Framework.

Consulte a seção "Exercício de Fixação" da página
[Rest Client](https://pw2.rpmhub.dev/topicos/rest-client/rest-client.html)
para a descrição completa dos *endpoints*, tipos de dados e diagramas de
sequência que este serviço (Gerenciamento de Empréstimos) deve implementar.
Este serviço deve utilizar um Rest Client (`@RegisterRestClient`) para se
comunicar com o serviço `catalog`.

O projeto já contém, em `src/test/java/dev/rpmhub/IntegrationTest.java`, o
teste de integração que sua implementação precisa fazer passar. **Não
altere esse arquivo de teste**: implemente as classes em `src/main/java`
necessárias para que ele passe.

## Rodando em modo de desenvolvimento

```shell script
./mvnw quarkus:dev
```

Este serviço sobe na porta `8081` (veja `src/main/resources/application.properties`),
para não conflitar com o serviço `catalog`, que roda na porta `8080`.

## Executando o teste de integração

1. Em um terminal, inicie o serviço `catalog`:

   ```shell script
   cd ../catalog
   ./mvnw quarkus:dev
   ```

2. Em outro terminal, inicie este serviço (`loans`):

   ```shell script
   ./mvnw quarkus:dev
   ```

3. Com os dois serviços rodando, execute o teste de integração pela sua IDE
   ou com:

   ```shell script
   ./mvnw test -Dtest=IntegrationTest
   ```
