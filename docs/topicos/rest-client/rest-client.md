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
*payment* é o **servidor** que ele deseja consumir. Nesse cenário, o
*checkout* envia o número do cartão (`cardNumber`) e o valor (`value`) para
o *payment*, que valida o pagamento e retorna um `Invoice` confirmando a
validação (`valid = true`).
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

Do lado do serviço de pagamento, o método correspondente valida os dados
recebidos e retorna um `Invoice` informando se o pagamento é válido:
{: .fs-3 }

```java
public record Invoice(boolean valid) {}
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
converte a resposta JSON automaticamente em um objeto `Invoice`. Se o
pagamento for validado com sucesso, `invoice.valid()` retorna `true`,
exatamente como ilustrado na Figura 1.
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

O exercício trata-se de uma rede social para troca de livros, onde
os usuários podem listar os livros que estão dispostos a emprestar e também
solicitar empréstimos de outros usuários. O sistema é composto por dois serviços
separados: um serviço que agrega informações sobre os livros que os usuários
disponibilizam para empréstimo e outro serviço que gerencia os empréstimos. O
objetivo é configurar a comunicação entre esses dois serviços usando
MicroProfile Rest Client.
{: .fs-3 }

### Tipos de Dados

Antes de detalhar os *endpoints*, defina os seguintes tipos (podem ser
implementados como `record` Java, como vimos anteriormente, ou como uma
classe Java tradicional, com atributos, construtor e métodos de acesso):
{: .fs-3 }

```java
// Representa um livro no catálogo
public record Book(
    Long id,
    String title,
    String author,
    boolean loaned) {}
```
{: .fs-3 }

```java
// Representa um empréstimo registrado pelo serviço de gerenciamento
public record Loan(
    Long id,
    Long bookId,
    String borrower) {}
```
{: .fs-3 }

```java
// Corpo enviado para solicitar um novo empréstimo
public record LoanRequest(
    Long bookId,
    String borrower) {}
```
{: .fs-3 }

💡 O campo `borrower` representa **quem está solicitando o empréstimo** (o
"tomador" do livro), e não quem o disponibilizou no catálogo. Como o
exercício não modela um cadastro de usuários, esse campo é apenas uma
`String` livre com o nome (ou identificador) da pessoa. Por exemplo, ao solicitar o
empréstimo do livro de `id = 3` para a Ana, o corpo da requisição
`POST /loans` seria:
{: .fs-3 }

```json
{
  "bookId": 3,
  "borrower": "Ana"
}
```
{: .fs-3 }

E a resposta (`Loan` criado, status `201`) incluiria esse mesmo valor:
{: .fs-3 }

```json
{
  "id": 1,
  "bookId": 3,
  "borrower": "Ana"
}
```
{: .fs-3 }

### Serviço 1: Catálogo de Livros

O objetivo é criar um serviço que gerencie o catálogo de livros que os usuários
disponibilizam para empréstimo. O serviço deve expor os seguintes *endpoints*,
todos com o prefixo de rota `/books`:
{: .fs-3 }

| Operação | Método | URL | Corpo da requisição | Corpo da resposta |
|----------|--------|-----|----------------------|--------------------|
| Adicionar um livro ao catálogo | `POST` | `/books` | `Book` (JSON, sem `id`) | `Book` criado (JSON), status `201` |
| Consultar o catálogo de livros disponíveis | `GET` | `/books` | - | `List<Book>` (JSON), status `200` |
| Consultar um livro específico | `GET` | `/books/{id}` | - | `Book` (JSON), status `200`, ou `404` se não existir |
| Marcar um livro como emprestado | `PUT` | `/books/{id}/loan` | - | `Book` atualizado (JSON), status `200`, ou `404`/`409` se o livro não existir/já estiver emprestado |
| Marcar um livro como devolvido | `PUT` | `/books/{id}/return` | - | `Book` atualizado (JSON), status `200`, ou `404` se não existir |
{: .fs-3 }

💡 O `@PathParam("id")` é utilizado para identificar o livro na URL, e o
`Response` (visto no [Passo 7](../webservices/webservices.md)) é útil para
retornar os diferentes códigos de status HTTP descritos acima, por exemplo,
`404 Not Found` quando o `id` informado não existe no catálogo.
{: .fs-3 }

### Serviço 2: Serviço de Gerenciamento de Empréstimos

O objetivo é criar um serviço que gerencie os empréstimos de livros entre os
usuários. O serviço deve expor os seguintes *endpoints*, com o prefixo de rota
`/loans`:
{: .fs-3 }

| Operação | Método | URL | Corpo da requisição | Corpo da resposta |
|----------|--------|-----|----------------------|--------------------|
| Registrar um novo empréstimo | `POST` | `/loans` | `LoanRequest` (JSON) | `Loan` criado (JSON), status `201`, ou `409 Conflict` se o livro não estiver disponível |
| Listar os livros que podem ser emprestados | `GET` | `/loans/books` | - | `List<Book>` (JSON), status `200` |
{: .fs-3 }

Quando um usuário solicita um empréstimo (`POST /loans`), o serviço de
gerenciamento de empréstimos deve utilizar um **Rest Client** para se
comunicar com o serviço de catálogo de livros e:
{: .fs-3 }

1. Verificar se o livro (`bookId`) existe e está disponível (`GET /books/{id}`
   no serviço de catálogo);
2. Caso esteja disponível, marcá-lo como emprestado (`PUT /books/{id}/loan`
   no serviço de catálogo) e então registrar o `Loan` localmente;
3. Caso não esteja disponível (ou não exista), retornar `409 Conflict` (ou
   `404 Not Found`, respectivamente) sem registrar o empréstimo.
{: .fs-3 }

A Figura 2 apresenta um diagrama de sequência que ilustra a comunicação entre
os dois serviços durante a solicitação de um novo empréstimo (`POST /loans`).
{: .fs-3 }

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/rest-client/books-loan.puml" target="blanck">
        <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/rest-client/books-loan.puml" alt="Registrar empréstimo" width="50%" height="50%"/>
    </a>
    <br/>
    Figura 2 - Diagrama de sequência para registrar um novo empréstimo.
</center>

Da mesma forma, a operação `GET /loans/books` não deve manter uma cópia local
dos livros: ela deve delegar a consulta ao serviço de catálogo por meio do
Rest Client (`GET /books` no serviço de catálogo) e simplesmente repassar o
resultado ao cliente que fez a requisição.
{: .fs-3 }

A Figura 3 apresenta o diagrama de sequência correspondente à listagem dos
livros disponíveis para empréstimo (`GET /loans/books`).
{: .fs-3 }

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/rest-client/books-list.puml" target="blanck">
        <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/rest-client/books-list.puml" alt="Listar livros disponíveis" width="50%" height="50%"/>
    </a>
    <br/>
    Figura 3 - Diagrama de sequência para listar os livros disponíveis para empréstimo.
</center>

A interface do Rest Client no serviço de gerenciamento de empréstimos deve se
parecer com o exemplo abaixo (compare com a interface `IPayment` do exemplo
anterior): ela reúne as operações usadas nos dois diagramas acima.
{: .fs-3 }

```java
@RegisterRestClient(baseUri = "http://localhost:9080/books")
public interface IBookCatalog {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Book> listBooks();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Book getBook(@PathParam("id") Long id);

    @PUT
    @Path("/{id}/loan")
    @Produces(MediaType.APPLICATION_JSON)
    Book markAsLoaned(@PathParam("id") Long id);
}
```
{: .fs-3 }

### Projetos-Base

Para começar, faça o clone do monorepo da disciplina e abra os dois
projetos-base (um para cada serviço), já configurados com as dependências
Quarkus corretas e nas portas `9080` (catálogo) e `9081` (empréstimos):
{: .fs-3 }

```sh
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
# Serviço de catálogo de livros
code pw2/exemplos/library/catalog
# Serviço de gerenciamento de empréstimos
code pw2/exemplos/library/loans
```
{: .fs-3 }

### Testes e Integração

Cada um dos dois projetos-base já contém um teste de integração que sua
implementação precisa fazer passar (**não altere esse arquivo**): no
serviço de catálogo, em `src/test/java/dev/ifrs/CatalogTest.java`; no
serviço de empréstimos, em `src/test/java/dev/ifrs/LoansTest.java`. Ambos
os arquivos têm conteúdo idêntico (apenas o nome da classe muda), já que os
testes dependem da comunicação real entre os dois serviços. Juntos, os
métodos dessa classe cobrem **todos os *endpoints*** descritos nas tabelas
dos dois serviços: o fluxo completo ilustrado nas Figuras 2 e 3 (cadastro
de um livro, consulta dos livros disponíveis, solicitação de empréstimo e
confirmação de que o catálogo foi atualizado), além dos casos de listagem
(`GET /books`), consulta/alteração de um livro inexistente
(`404 Not Found`) e das operações de emprestar/devolver um livro
diretamente no catálogo (`PUT /books/{id}/loan` e `PUT /books/{id}/return`).
{: .fs-3 }

Como o teste depende dos **dois serviços rodando ao mesmo tempo**, siga
esta ordem:
{: .fs-3 }

1. Implemente primeiro o Serviço 1 (Catálogo de Livros) e valide-o
   isoladamente, iniciando-o com `./mvnw quarkus:dev` e testando os
   *endpoints* manualmente (por exemplo, com o cURL ou o Dev UI do Quarkus).

2. Implemente o Serviço 2 (Gerenciamento de Empréstimos), incluindo o Rest
   Client (`IBookCatalog`) que consome o catálogo.

3. Com os dois serviços em execução (cada um em seu próprio terminal),
   execute o teste de integração, pela sua IDE ou com
   `./mvnw test -Dtest=CatalogTest` (no projeto `catalog`) ou
   `./mvnw test -Dtest=LoansTest` (no projeto `loans`), a partir de
   qualquer um dos dois projetos.
{: .fs-3 }

🚨 Esse teste só passa quando os **dois serviços estão de fato
integrados**: ele depende da comunicação real via Rest Client entre o
serviço de empréstimos e o serviço de catálogo, e não de *mocks*.
{: .fs-3 }

## Teste seus conhecimentos 🧠

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/rest-client/questions.html"
        title="Rest Client" width="90%" height="500"
        style="border:none;background-color:white;">
    </iframe>
</center>

## Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* SmallReye Metrics. Disponível em: [https://quarkus.io/guides/rest-client](https://quarkus.io/guides/rest-client)
{: .fs-3 }

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>