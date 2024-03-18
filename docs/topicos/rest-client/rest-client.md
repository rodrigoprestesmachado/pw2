---
layout: default
title: Rest Client
nav_order: 5
---

# Rest Client 🌐

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/rest-client/slides/index.html#/"
        title="Rest Client" width="90%" height="500" style="border:none;">
    </iframe>
</center>

# Resumo 📝

O [MicroProfile Rest Client](https://github.com/eclipse/microprofile-rest-client)
fornece uma maneira para invocarmos serviços RESTful sobre HTTP. O MicroProfile
Rest Client tenta usar APIs [Jakarta RESTful Web Services 2.1](https://jakarta.ee/specifications/restful-ws/2.1/) para manter compatibilidades e melhorar as questões de reuso.

Para criar um projeto Quarkus com suporte ao Rest Cliente utilize o seguinte
comando:

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:2.5.1.Final:create \
    -DprojectGroupId=org.acme \
    -DprojectArtifactId=service \
    -DclassName="dev.rpmhub.Client" \
    -Dpath="/client" \
    -Dextensions="resteasy,resteasy-jackson,rest-client,rest-client-jackson"
cd client
```

Note que as extensões `rest-client` e `rest-client-jackson` (JSON *binding*)
foram adicionadas ao projeto.

## Implementação 🛠️

Imagine que temos que nos comunicar um um RESTful Web Service de um carrinho de
compras (checkout) em um serviço de pagamento (payment):

<center>
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/rest-client/store.puml" alt="Carrinho de compras" width="35%" height="35%"/> <br/>
    Figura 1 - Diagrama de sequência de um carrinho de compras.
</center>

Para criarmos um Rest Client que se comunique com o serviço de pagamento temos
que primeiro declarar uma interface, assim, observe o trecho de código abaixo:

```java
@RegisterRestClient(baseUri = "https://localhost:8444/")
@AccessToken
public interface IPayment {

    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Invoice confirmPayment(
        @FormParam("cardNumber") String cardNumber,
        @FormParam("value") String value);
  }
}
```

Na declaração da interface, temos que registrar nosso Rest cliente no CDI
(*Contexts and Dependency Injection*) por meio da anotação
`@RegisterRestClient`. Logo, podemos indicar a URL base do serviço que iremos
nos comunicar usando o atributo `baseUri`.

Alternativamente, também podemos definir a URL base do serviço por meio de uma
propriedade do arquivo *application.properties*. Nesse caso, temos que utilizar
o seguinte formato:

    {nome da classe com o pacote}/mp-rest/url={url base}

Depois disso, note que a declaração dos métodos da interface do cliente é
bastante parecido com o método do serviço que desejamos nos comunicar.

Finalmente, para utilizarmos o nosso Rest Cliente utilizamos a injeção de
dependência em um bean, por exemplo:

```java
@Inject
@RestClient
IPayment paymentService;
```

## Exemplo de Código 💡

Um exemplo de código bastante simples (serviço e Rest client) pode ser acessado
por intermédio do Github:

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
# Rest cliente
code pw2/exemplos/rest-client/client
# serviço
code pw2/exemplos/rest-client/service
```

## Exercício Prático 🏋️

O exercício trata-se de uma rede social para troca de livros, onde
os usuários podem listar os livros que estão dispostos a emprestar e também
solicitar empréstimos de outros usuários. O sistema é composto por dois serviços
separados: um serviço que agrega informações sobre os livros que os usuários
disponibilizam para empréstimo e outro serviço que gerencia os empréstimos. O
objetivo é configurar a comunicação entre esses dois serviços usando
MicroProfile Rest Client.

### Serviço 1: Catálogo de Livros

O objetivo é criar um serviço que gerencie o catálogo de livros que os usuários
disponibilizam para empréstimo. O serviço deve ter operações para:

- Adicionar um livro ao catálogo

- Consultar o catálogo de livros disponíveis

- Marcar um livro como emprestado e devolvido

### Serviço 2: Serviço de Gerenciamento de Empréstimos

O objetivo é criar um serviço que gerencie os empréstimos de livros entre os
usuários. O serviço deve ter operações para:

- Registrar um novo empréstimo

- Listar os livros que podem ser emprestados

Quando um usuário solicita um empréstimo, o serviço de gerenciamento de
empréstimos deve verificar se o livro está disponível no catálogo de livros e
marcá-lo como emprestado. Neste sentido, um Rest Client deve ser utilizado para
comunicar com o serviço de catálogo de livros.

A Figura 2 apresenta um diagrama de sequência que ilustra a comunicação entre
os serviços.

<center>
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/rest-client/books.puml" alt="Biblio" width="35%" height="35%"/> <br/>
    Figura 2 - Diagrama de sequência do exercícios.
</center>


### Testes e Integração

- Teste individualmente cada serviço para garantir que as operações de
  empréstimo e consulta de livros estão funcionando corretamente.

- Após testar individualmente, integre os serviços e verifique se a comunicação
  entre eles está funcionando adequadamente para realizar operações de
  empréstimo e consulta de livros.

## Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* SmallReye Metrics. Disponível em: [https://quarkus.io/guides/rest-client](https://quarkus.io/guides/rest-client)

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>