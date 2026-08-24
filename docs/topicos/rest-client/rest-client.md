---
layout: default
title: Rest Client
parent: Micro Serviços Básico
nav_order: 6
---

# Rest Client 🌐

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/rest-client/slides/index.html#/"
        title="Rest Client" width="90%" height="500" style="border:none;">
    </iframe>
</center>

Até agora vimos como **criar** um Web Service REST, ou seja, como
disponibilizar recursos que outros sistemas podem consumir. Mas em uma
arquitetura de micro serviços, é muito comum que um serviço também precise
**consumir** outro serviço: por exemplo, um serviço de carrinho de compras
que precisa se comunicar com um serviço de pagamento para confirmar uma
transação.
{: .fs-3 }

Poderíamos fazer isso "na mão", usando uma biblioteca HTTP qualquer para
montar a requisição, gerenciar cabeçalhos, serializar/desserializar JSON etc.
Isso funciona, mas gera bastante código repetitivo (*boilerplate*). O
**MicroProfile Rest Client** resolve esse problema: ele permite descrever um
serviço remoto por meio de uma simples **interface Java anotada**, da mesma
forma como fazemos ao criar um recurso REST com JAX-RS. O próprio *framework*
se encarrega de montar a requisição HTTP, enviá-la e converter a resposta em
um objeto Java.
{: .fs-3 }

O [MicroProfile Rest Client](https://github.com/eclipse/microprofile-rest-client)
reutiliza as mesmas anotações do [Jakarta RESTful Web Services](https://jakarta.ee/specifications/restful-ws/2.1/)
(`@Path`, `@GET`, `@POST` etc.) que já conhecemos da criação de recursos
REST, o que reduz a curva de aprendizado e favorece o reuso de conhecimento.
{: .fs-3 }

### Passo 1: Criando o projeto

Para criar um projeto Quarkus com suporte ao Rest Client, utilize o seguinte
comando:
{: .fs-3 }

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:2.5.1.Final:create \
    -DprojectGroupId=org.acme \
    -DprojectArtifactId=service \
    -DclassName="dev.rpmhub.Client" \
    -Dpath="/client" \
    -Dextensions="resteasy,resteasy-jackson,rest-client,rest-client-jackson"
cd client
```
{: .fs-3 }

Note que as extensões `rest-client` e `rest-client-jackson` (JSON *binding*)
foram adicionadas ao projeto: a primeira provê o suporte ao MicroProfile Rest
Client, e a segunda faz a conversão automática entre JSON e objetos Java,
assim como vimos com `resteasy-reactive-jackson` na criação de recursos REST.
{: .fs-3 }

## Implementação 🛠️

Imagine que precisamos comunicar um RESTful Web Service de um carrinho de
compras (*checkout*) com um serviço de pagamento (*payment*), como ilustrado
na Figura 1: o serviço de *checkout* é o **cliente**, e o serviço de
*payment* é o **servidor** que ele deseja consumir.
{: .fs-3 }

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/rest-client/store.puml" target="blanck">
        <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/rest-client/store.puml" alt="Carrinho de compras" width="50%" height="50%"/>
    </a>
    <br/>
    Figura 1 - Diagrama de sequência de um carrinho de compras.
</center>

### Passo 2: Declarando a interface do cliente

Para criar um Rest Client que se comunique com o serviço de pagamento,
primeiro declaramos uma **interface** descrevendo as operações desse
serviço remoto, exatamente como faríamos ao descrever um recurso REST no
lado do servidor:
{: .fs-3 }

```java
@RegisterRestClient(baseUri = "https://localhost:8444/")
public interface IPayment {

    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Invoice confirmPayment(
        @FormParam("cardNumber") String cardNumber,
        @FormParam("value") String value);
}
```
{: .fs-3 }

* `@RegisterRestClient`: registra a interface como um Rest Client no CDI
  (*Contexts and Dependency Injection*), tornando-a disponível para injeção
  de dependência. O atributo `baseUri` indica a URL base do serviço remoto.
* `@Path`, `@POST`, `@Consumes`, `@Produces`, `@FormParam`: têm exatamente o
  mesmo significado que já vimos ao criar recursos REST. A diferença é que,
  aqui, essas anotações descrevem uma requisição que **enviaremos**, e não
  uma que **receberemos**.
{: .fs-3 }

🚨 Note que a assinatura do método `confirmPayment` é bem parecida com a do
método correspondente no serviço de pagamento: é assim que o MicroProfile
Rest Client sabe montar a requisição HTTP correta a partir de uma simples
chamada de método Java.
{: .fs-3 }

Alternativamente, em vez de fixar o `baseUri` na anotação, podemos definir a
URL base do serviço por meio de uma propriedade no arquivo
`application.properties`. Isso é útil quando a URL do serviço varia entre
ambientes (desenvolvimento, teste, produção):
{: .fs-3 }

```properties
{nome da classe com o pacote}/mp-rest/url={url base}
```
{: .fs-3 }

### Passo 3: Injetando e utilizando o cliente

Com a interface declarada, para utilizarmos o Rest Client basta injetá-lo em
um *bean* usando as anotações `@Inject` e `@RestClient`, como faríamos com
qualquer outra dependência gerenciada pelo CDI:
{: .fs-3 }

```java
@Inject
@RestClient
IPayment paymentService;
```
{: .fs-3 }

A partir daí, chamar `paymentService.confirmPayment(cardNumber, value)` é o
suficiente: o *framework* monta a requisição HTTP `POST /payment`, envia os
parâmetros no formato configurado (`application/x-www-form-urlencoded`) e
converte a resposta JSON automaticamente em um objeto `Invoice`.
{: .fs-3 }

### Resumo do processo

| Passo | O que fazer |
|-------|-------------|
| 1 | Adicionar as extensões `rest-client` e `rest-client-jackson` ao projeto |
| 2 | Declarar uma interface anotada com `@RegisterRestClient`, descrevendo as operações do serviço remoto |
| 3 | Injetar a interface em um *bean* com `@Inject` e `@RestClient` |
| 4 | Chamar os métodos da interface como se fossem métodos locais |
{: .fs-3 }

## Exemplo de Código 💡

Um exemplo de código completo (serviço e Rest Client) do cenário de carrinho
de compras/pagamento pode ser acessado por intermédio do GitHub:
{: .fs-3 }

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
# Serviço de checkout
code pw2/exemplos/store/checkout
# Serviço de pagamento
code pw2/exemplos/store/payment
```
{: .fs-3 }

## Exercício de Fixação 📝

Antes de avançar para o exercício prático completo, vamos consolidar o que
foi aprendido com um exercício simples e rápido.
{: .fs-3 }

Retome o serviço `ProdutoResource` criado na página anterior sobre
[Web Services](../webservices/webservices.md), que expõe o recurso
`/produtos` (veja o método `listar`, que responde a `GET /produtos` e
retorna uma lista de `Produto` em JSON).
{: .fs-3 }

Crie um **novo projeto Quarkus** com suporte a Rest Client e implemente uma
interface `IProdutoClient` capaz de consumir esse serviço remotamente. Para
isso:
{: .fs-3 }

1. Adicione as extensões `rest-client` e `rest-client-jackson` ao novo
   projeto.
2. Declare a interface `IProdutoClient`, anotada com `@RegisterRestClient`,
   com um método que realize uma requisição `GET` para `/produtos` e
   retorne `List<Produto>`.
3. Injete a interface em um recurso REST do novo projeto (por exemplo,
   `/catalogo-remoto`), usando `@Inject` e `@RestClient`.
4. Ao acessar `/catalogo-remoto`, o serviço deve retornar a lista de
   produtos obtida do serviço original, comprovando que a comunicação entre
   os dois serviços está funcionando.
{: .fs-3 }

💡 Reveja os Passos 2 e 3 desta página para lembrar como declarar a
interface do cliente e como injetá-la, e os Passos 2 e 4 da página de
[Web Services](../webservices/webservices.md) para lembrar o formato do
recurso `/produtos` e do `record Produto`.
{: .fs-3 }

## Exercício Prático 🏋️

O exercício trata-se de uma rede social para troca de livros, onde
os usuários podem listar os livros que estão dispostos a emprestar e também
solicitar empréstimos de outros usuários. O sistema é composto por dois serviços
separados: um serviço que agrega informações sobre os livros que os usuários
disponibilizam para empréstimo e outro serviço que gerencia os empréstimos. O
objetivo é configurar a comunicação entre esses dois serviços usando
MicroProfile Rest Client.
{: .fs-3 }

### Serviço 1: Catálogo de Livros

O objetivo é criar um serviço que gerencie o catálogo de livros que os usuários
disponibilizam para empréstimo. O serviço deve ter operações para:
{: .fs-3 }

- Adicionar um livro ao catálogo

- Consultar o catálogo de livros disponíveis

- Marcar um livro como emprestado e devolvido
{: .fs-3 }

### Serviço 2: Serviço de Gerenciamento de Empréstimos

O objetivo é criar um serviço que gerencie os empréstimos de livros entre os
usuários. O serviço deve ter operações para:
{: .fs-3 }

- Registrar um novo empréstimo

- Listar os livros que podem ser emprestados
{: .fs-3 }

Quando um usuário solicita um empréstimo, o serviço de gerenciamento de
empréstimos deve verificar se o livro está disponível no catálogo de livros e
marcá-lo como emprestado. Neste sentido, um Rest Client deve ser utilizado para
comunicar com o serviço de catálogo de livros.
{: .fs-3 }

A Figura 2 apresenta um diagrama de sequência que ilustra a comunicação entre
os serviços.
{: .fs-3 }

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/rest-client/books.puml" target="blanck">
        <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/rest-client/books.puml" alt="Biblio" width="50%" height="50%"/>
    </a>
    <br/>
    Figura 2 - Diagrama de sequência do exercício.
</center>


### Testes e Integração

- Teste individualmente cada serviço para garantir que as operações de
  empréstimo e consulta de livros estão funcionando corretamente.

- Após testar individualmente, integre os serviços e verifique se a comunicação
  entre eles está funcionando adequadamente para realizar operações de
  empréstimo e consulta de livros.
{: .fs-3 }

## Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* SmallReye Metrics. Disponível em: [https://quarkus.io/guides/rest-client](https://quarkus.io/guides/rest-client)
{: .fs-3 }

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>