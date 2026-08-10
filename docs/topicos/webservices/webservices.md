---
layout: default
title: Web Services
parent: Micro Serviços Básico
nav_order: 5
---

# Web Services 🌐

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/webservices/slides/index.html#/"
        title="Web Services" width="90%" height="500" style="border:none;">
    </iframe>
</center>

Um Web Service é uma tecnologia que permite a comunicação entre diferentes
sistemas de software pela internet. Ele disponibiliza uma interface acessível
pela web, utilizando padrões abertos e protocolos como HTTP, XML e JSON, para
que sistemas diferentes possam trocar dados e funcionalidades entre si, mesmo
quando desenvolvidos em plataformas e linguagens distintas.
{: .fs-3 }

Na prática, isso significa que um sistema pode solicitar dados ou serviços a
partir de outro sistema, enviar informações e até mesmo realizar operações
remotas, tudo isso através de chamadas HTTP.
{: .fs-3 }

Existem dois tipos principais de Web Services:
{: .fs-3 }

1. **XML Web Services**: Utiliza dois padrões principais: SOAP e WSDL.
   O SOAP (_Simple Object Access Protocol_) é um protocolo baseado em XML para
   troca de mensagens entre sistemas, enquanto o WSDL
   (_Web Services Description Language_) é uma linguagem baseada em XML para
   descrever a interface de um Web Service. Os Web Services baseados em XML são
   mais complexos e pesados, mas oferecem suporte a funcionalidades avançadas,
   como segurança e transações.

2. **REST (Representational State Transfer)**: O REST é um estilo arquitetural
   que utiliza os próprios métodos HTTP, como GET, POST, PUT e DELETE, para
   realizar operações sobre recursos. Os Web Services RESTful são geralmente
   mais simples de implementar e mais leves que os serviços SOAP. Devido à sua
   simplicidade, atualmente, existe uma grande adesão a este estilo de
   Web Service, por essa razão, o REST será o foco desta disciplina.
{: .fs-3 }

Os Web Services desempenham um papel fundamental na construção de arquiteturas
de sistemas como a de micro serviços. Micro serviços são uma abordagem
arquitetural na qual um aplicativo é construído como um conjunto de pequenos
serviços independentes, cada um executando um processo específico e
comunicando-se através de APIs leves, geralmente baseadas em serviços REST ou
protocolos de mensagens assíncronas. Cada serviço é desenvolvido, implantado e
dimensionado de forma independente, permitindo maior flexibilidade,
escalabilidade e facilidade de manutenção em comparação com arquiteturas
monolíticas.
{: .fs-3 }

---

Para saber mais sobre Web Services: consulte o o capítulo 7 do livro [Desenvolvimento de software, v.3 programação de sistemas web orientada a objetos em Java](https://biblioteca.ifrs.edu.br/pergamum_ifrs/biblioteca_s/acesso_login.php?cod_acervo_acessibilidade=5020683&acesso=aHR0cHM6Ly9pbnRlZ3JhZGEubWluaGFiaWJsaW90ZWNhLmNvbS5ici9ib29rcy85Nzg4NTgyNjAzNzEw&label=acesso%20restrito) para compreender detalhes sobre a implementação de Web
Services em Java.
{: .fs-3 }

---

## RESTful Web Services na prática com Quarkus 🛠️

No Jakarta EE, o [JAX-RS](https://jakarta.ee/specifications/restful-ws/) (hoje
chamado de Jakarta RESTful Web Services) provê a funcionalidade necessária
para a construção de Web Services baseados em REST. O Quarkus implementa essa
especificação por meio da extensão `resteasy-reactive`, que utiliza anotações
Java para transformar uma classe comum em um recurso REST, sem a necessidade
de arquivos de configuração XML.
{: .fs-3 }

Vamos construir, passo a passo, um pequeno catálogo de produtos para entender
as principais anotações. Os exemplos abaixo utilizam o pacote
`jakarta.ws.rs`, o mesmo utilizado pelo projeto
[PW2 ConversionService](https://github.com/rpmhubdev/pw2-conversion) já
mencionado nos exercícios desta página.
{: .fs-3 }

### Passo 1: Criando o projeto

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:3.8.2.Final:create \
    -DprojectGroupId=dev.rpmhub \
    -DprojectArtifactId=produtos \
    -DclassName="dev.rpmhub.ProdutoResource" \
    -Dpath="/produtos" \
    -Dextensions="resteasy-reactive,resteasy-reactive-jackson"
cd produtos
```
{: .fs-3 }

Note que a extensão `resteasy-reactive` implementa o JAX-RS no Quarkus,
enquanto `resteasy-reactive-jackson` adiciona suporte à conversão automática
entre objetos Java e JSON.
{: .fs-3 }

### Passo 2: Um recurso REST básico

Diferente do Jakarta EE tradicional, o Quarkus **não exige** uma classe que
estenda `Application` com a anotação `@ApplicationPath`: basta anotar uma
classe com `@Path` para transformá-la em um recurso REST:
{: .fs-3 }

```java
package dev.rpmhub;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/produtos")
public class ProdutoResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ola() {
        return "Bem-vindo ao catálogo de produtos!";
    }
}
```
{: .fs-3 }

* `@Path`: define a URI do recurso (o *endpoint* do serviço). Pode ser usada
  tanto na classe quanto em métodos individuais.
* `@GET`: indica que o método responde a requisições HTTP do tipo GET.
* `@Produces`: define o tipo [MIME](https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Basics_of_HTTP/MIME_types)
  que o método retorna para o cliente (nesse caso, texto simples).
{: .fs-3 }

Ao rodar `./mvnw quarkus:dev` e acessar `http://localhost:8080/produtos`, a
mensagem "Bem-vindo ao catálogo de produtos!" é exibida no navegador.
{: .fs-3 }

### Passo 3: Parâmetros na URI

Para buscar um produto específico, precisamos de um identificador na própria
URI, por exemplo, `/produtos/1`. Isso é feito com `@PathParam`:
{: .fs-3 }

```java
@GET
@Path("/{id}")
@Produces(MediaType.APPLICATION_JSON)
public Produto buscarPorId(@PathParam("id") Long id) {
    return catalogo.get(id);
}
```
{: .fs-3 }

O trecho `{id}` no `@Path` funciona como uma variável de *template*: o valor
informado na URI é injetado no parâmetro do método por meio de
`@PathParam("id")`. Para representar o produto, podemos usar um `record`
Java: ele já gera automaticamente construtor e métodos de acesso aos campos.
A conversão desse objeto para JSON na resposta é feita pela extensão
`resteasy-reactive-jackson`, adicionada no Passo 1:
{: .fs-3 }

```java
public record Produto(Long id, String nome, String categoria, double preco) {}
```
{: .fs-3 }

### Passo 4: Parâmetros de consulta (*query string*)

Quando o filtro é opcional, o mais comum é utilizar parâmetros de consulta
(*query params*), por exemplo, `/produtos?categoria=eletronicos`. Nesse caso,
utilizamos a anotação `@QueryParam`:
{: .fs-3 }

```java
@GET
@Produces(MediaType.APPLICATION_JSON)
public List<Produto> listar(@QueryParam("categoria") String categoria) {
    if (categoria == null) {
        return catalogo.values().stream().toList();
    }
    return catalogo.values().stream()
            .filter(p -> p.categoria().equalsIgnoreCase(categoria))
            .toList();
}
```
{: .fs-3 }

Note que, diferente do `@PathParam`, o parâmetro de consulta é **opcional**:
se o cliente não informar `categoria` na URL, o valor injetado será `null`.
{: .fs-3 }

### Passo 5: Recebendo dados simples no corpo da requisição

Nem toda requisição POST envia um objeto estruturado. Quando o corpo contém
apenas um valor simples (um número ou um texto, por exemplo), basta declarar
o parâmetro do método com o tipo primitivo esperado, sem nenhuma classe
auxiliar:
{: .fs-3 }

```java
@POST
@Path("/desconto")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public String aplicarDesconto(double preco) {
    double comDesconto = preco * 0.9;
    return Double.toString(comDesconto);
}
```
{: .fs-3 }

* `@POST`: indica que o método responde a requisições HTTP do tipo POST.
* `@Consumes`: define o tipo MIME que o método espera **receber** do cliente.
  Nesse caso, `TEXT_PLAIN` informa que o corpo da requisição é apenas texto
  puro, e não um objeto JSON.
{: .fs-3 }

Esse é exatamente o padrão utilizado no exercício de conversão de quilômetros
por hora para milhas por hora (veja os [Exercícios - Parte 1](#exercícios---parte-1-)
abaixo): o corpo da requisição contém somente o número a ser convertido, sem
nenhuma estrutura JSON.
{: .fs-3 }

### Passo 6: Recebendo dados estruturados em JSON

Quando o corpo da requisição representa um objeto com vários campos, o mais
comum é enviá-lo em JSON. No JAX-RS, basta declarar um parâmetro com o tipo
do objeto esperado: a conversão de JSON para objeto Java (e vice-versa) é
feita automaticamente pela extensão `resteasy-reactive-jackson`, adicionada
no Passo 1:
{: .fs-3 }

```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response criar(Produto produto) {
    catalogo.put(produto.id(), produto);
    return Response.status(Response.Status.CREATED).entity(produto).build();
}
```
{: .fs-3 }

Note que o parâmetro `produto` não possui nenhuma anotação: quando um método
recebe um objeto sem `@PathParam`, `@QueryParam` ou similares, o JAX-RS
entende que ele deve ser preenchido a partir do corpo (*body*) da requisição.
{: .fs-3 }

🚨 Uma dúvida comum é a diferença entre `@Consumes` e `@Produces`:
`@Consumes` descreve o que o servidor **aceita receber**, enquanto `@Produces`
descreve o que o servidor **envia de volta**.
{: .fs-3 }

### Passo 7: Controlando a resposta HTTP

Até aqui, os métodos retornaram diretamente um objeto (`Produto`, `List<Produto>`).
Isso funciona bem quando a resposta é sempre "200 OK". Porém, muitas vezes
precisamos informar códigos de status HTTP diferentes, por exemplo, `201` ao
criar um recurso, `404` quando ele não existe, ou `204` quando a remoção é
concluída sem conteúdo de retorno. Para esses casos, utilizamos a classe
`jakarta.ws.rs.core.Response`:
{: .fs-3 }

```java
@DELETE
@Path("/{id}")
public Response remover(@PathParam("id") Long id) {
    if (catalogo.remove(id) == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.noContent().build(); // HTTP 204
}
```
{: .fs-3 }

| Método | Código HTTP | Situação |
|--------|-------------|----------|
| `Response.status(Response.Status.CREATED)` | 201 | Recurso criado com sucesso |
| `Response.ok()` | 200 | Requisição processada com sucesso |
| `Response.noContent()` | 204 | Sucesso, mas sem conteúdo de retorno (ex.: remoção) |
| `Response.status(Response.Status.NOT_FOUND)` | 404 | Recurso não encontrado |
{: .fs-3 }

### Resumo das anotações

| Anotação | Onde é usada | Função |
|----------|--------------|--------|
| `@Path` | Classe ou método | Define a URI do recurso ou *endpoint*. Aceita variáveis de *template*, por exemplo, `/produtos/{id}` |
| `@GET` `@POST` `@PUT` `@DELETE` | Método | Associa o método a um verbo HTTP |
| `@PathParam` | Parâmetro de método | Injeta um valor vindo de uma variável do `@Path` |
| `@QueryParam` | Parâmetro de método | Injeta um valor vindo da *query string* (`?nome=valor`) |
| `@Consumes` | Método | Define o tipo MIME que o método recebe do cliente |
| `@Produces` | Método | Define o tipo MIME que o método envia ao cliente |
{: .fs-3 }

## Exercícios - Parte 1 📝

Desenvolva um Web Service em Rest utilizando o framework Quarkus que permita
realizar as seguintes conversões de unidades de medida:
{: .fs-3 }

1. **Conversão de Quilômetro por hora para Milhas por hora**:
   - Este método deve aceitar requisições do tipo POST e produzir os resultados
   em formato de texto.
   - A fórmula de conversão a ser utilizada é: 1 quilômetro por hora equivale a
   0.621371 milhas por hora.

1. **Conversão de Nós para Quilômetros por hora**:
   - Este método deve aceitar requisições do tipo GET e retornar os resultados
   em formato JSON.
   - A fórmula de conversão a ser aplicada é: 1 nó equivale a 1.852 quilômetros
   por hora.
{: .fs-3 }

💡 Reveja os passos 2 (recurso básico), 3 (`@PathParam`), 5 (corpo em texto
simples) e 7 (`Response`) da seção anterior para lembrar como declarar
métodos GET/POST e retornar JSON.
{: .fs-3 }

Certifique-se de implementar corretamente os casos de teste do exercício.
{: .fs-3 }

```java
/**
     * Test case for converting kilometers to miles.
     *
     * This test sends a POST request to the "/Conversion/km-to-miles"
     * endpoint with a body of "50" (representing 50 kilometers per hour).
     * The expected result is a response with a status code of 200 and a
     * body of "31.06855" (the equivalent value in miles per hour).
    */
    @Test
    void testConversionKmMiles() {
        given()
            .contentType(ContentType.TEXT)
            // 50 quilômetros por hora
            .body("50")
        .when()
            .post("/Conversion/km-to-miles")
        .then()
            .contentType(ContentType.TEXT)
            .statusCode(200)
            .body(is("31.06855"));
    }

    /**
     * Test case to verify the conversion from knots to kilometers per hour.
     * The expected value for 1 knot in km/h is 1.852.
    */
    @Test
    void testConversionKnotsKm() {
        given()
            .contentType(ContentType.TEXT)
        .when()
            .contentType(ContentType.JSON)
            .get("/Conversion/knots-to-km/1")
        .then()
            .statusCode(200)
            .body("value", is(1.852f));
    }
```
{: .fs-3 }

    ⚠️ Caso você tenha dificuldades para implementar o exercício, consulte o
    código fonte do projeto [PW2 ConversionService](https://github.com/rpmhubdev/pw2-conversion)
    para obter um exemplo.
{: .fs-3 }

## Exercícios - Parte 2 📝

Desenvolva um microserviço para gerenciar uma lista de tarefas. A API REST do
serviço deve oferecer as seguintes funcionalidades:
{: .fs-3 }

1) Para adicionar uma nova tarefa, envie uma requisição POST para a rota
/tarefas. A requisição deve conter um JSON com o título e a descrição da tarefa.
Em resposta, o servidor retorna a tarefa criada com um ID gerado automaticamente
no formato JSON.

2) Para listar as tarefas cadastradas, envie uma requisição GET para a rota
/tarefas. A resposta será um JSON contendo todas as tarefas armazenadas.

3) Para excluir uma tarefa, use o método DELETE e inclua o ID da tarefa na rota
/tarefas/{id}. A resposta será um JSON confirmando a exclusão da tarefa.

4) Desafio Opcional: adicione suporte a filtros, por exemplo, GET
/tarefas?concluida=true.
{: .fs-3 }

💡 Este exercício é uma boa oportunidade para reaproveitar o exemplo do
catálogo de produtos: a criação de tarefas (POST em JSON) segue o mesmo
padrão do passo 6, a listagem/filtro (GET com `@QueryParam`) segue o passo 4,
e a exclusão (DELETE) segue o passo 7.
{: .fs-3 }

## Material complementar (legado) 📼

As gravações abaixo são materiais mais antigos da disciplina, anteriores à
adoção do Quarkus como *framework* principal. Elas utilizam Jakarta EE
tradicional (implantado em um servidor de aplicação), e não o Quarkus. Ficam
disponíveis apenas como referência histórica.
{: .fs-3 }

### RESTFul Web Services

Deprecated
{: .label .label-red }

<center>
<iframe width="560" height="315" src="https://www.youtube.com/embed/PU8EhAHptlQ" title="RESTFul Web Services" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
</center>

### XML Web Services

Deprecated
{: .label .label-red }

<center>
<iframe width="560" height="315" src="https://www.youtube.com/embed/2nP7rzaIw5Y" title="XML Web Services" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
</center>

Dúvidas na configuração do XML Web Service? seguem os arquivos de configuração [server.xml](server.xml) e [pom.xml](pom.xml) utilizados no vídeo.
{: .fs-3 }

## Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#the-lifecycles-of-enterprise-beans)

* Writing JSON REST services. Disponível em: [https://quarkus.io/guides/rest-json](https://quarkus.io/guides/rest-json)

* Jakarta RESTful Web Services. Disponível em: [https://jakarta.ee/specifications/restful-ws/](https://jakarta.ee/specifications/restful-ws/)
{: .fs-3 }

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
