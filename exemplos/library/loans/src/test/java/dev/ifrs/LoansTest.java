package dev.ifrs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;

import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

/**
 * Teste de integração do exercício de Rest Client (rede social de troca de
 * livros).
 *
 * Este teste NÃO é um {@code @QuarkusTest}: ele não sobe a aplicação
 * sozinho, pois precisa dos DOIS serviços rodando ao mesmo tempo, cada um em
 * seu próprio processo. Antes de executar este teste, em dois terminais
 * separados:
 *
 * <pre>
 * # Terminal 1 - Serviço de Catálogo de Livros (porta 9080)
 * cd catalog
 * ./mvnw quarkus:dev
 *
 * # Terminal 2 - Serviço de Gerenciamento de Empréstimos (porta 9081)
 * cd loans
 * ./mvnw quarkus:dev
 * </pre>
 *
 * Com os dois serviços em execução, rode este teste (nesta classe ou na
 * cópia idêntica dela, dentro do projeto "loans") a partir da sua IDE ou
 * com {@code ./mvnw test -Dtest=IntegrationTest}.
 *
 * Este mesmo arquivo existe (idêntico) nos dois projetos do exercício, já
 * que ele depende da comunicação real entre ambos. Juntos, os métodos de
 * teste desta classe cobrem todos os endpoints do exercício:
 *
 * <ul>
 * <li>Catálogo: {@code POST /books}, {@code GET /books},
 * {@code GET /books/{id}}, {@code PUT /books/{id}/loan},
 * {@code PUT /books/{id}/return}</li>
 * <li>Empréstimos: {@code POST /loans}, {@code GET /loans/books}</li>
 * </ul>
 */
class LoansTest {

    static final String CATALOG_URL = "http://localhost:9080";
    static final String LOAN_URL = "http://localhost:9081";

    /**
     * Cadastra um livro diretamente no serviço de catálogo e retorna o
     * {@code id} gerado. Método auxiliar usado pelos demais testes.
     */
    private int createBook(String title, String author) {
        return given()
                .baseUri(CATALOG_URL)
                .contentType(ContentType.JSON)
                .body("{\"title\":\"" + title + "\",\"author\":\"" + author + "\"}")
                .when()
                .post("/books")
                .then()
                .statusCode(201)
                .body("title", is(title))
                .body("author", is(author))
                .body("loaned", is(false))
                .extract().path("id");
    }

    /**
     * Fluxo completo ilustrado nas Figuras 2 e 3: cadastro de um livro no
     * catálogo, consulta dos livros disponíveis, solicitação de empréstimo
     * pelo serviço de gerenciamento e confirmação de que o catálogo foi de
     * fato atualizado. Cobre {@code POST /books}, {@code GET /loans/books}
     * e {@code POST /loans} (sucesso e conflito).
     */
    @Test
    void testFullLoanFlow() {
        // 1. Cadastra um livro diretamente no serviço de catálogo
        int bookId = createBook("Grande Sertão: Veredas", "Guimarães Rosa");

        // 2. Confirma que o livro aparece na listagem exposta pelo
        // serviço de empréstimos (que consulta o catálogo via Rest Client)
        given()
                .baseUri(LOAN_URL)
                .when()
                .get("/loans/books")
                .then()
                .statusCode(200)
                .body("id", hasItem(bookId));

        // 3. Solicita o empréstimo através do serviço de gerenciamento
        given()
                .baseUri(LOAN_URL)
                .contentType(ContentType.JSON)
                .body("{\"bookId\":" + bookId + ",\"borrower\":\"Ana\"}")
                .when()
                .post("/loans")
                .then()
                .statusCode(201)
                .body("bookId", is(bookId))
                .body("borrower", is("Ana"));

        // 4. Confirma, diretamente no catálogo, que o livro foi marcado
        // como emprestado pelo Rest Client do serviço de empréstimos
        given()
                .baseUri(CATALOG_URL)
                .when()
                .get("/books/" + bookId)
                .then()
                .statusCode(200)
                .body("loaned", is(true));

        // 5. Uma nova tentativa de empréstimo do mesmo livro deve falhar
        given()
                .baseUri(LOAN_URL)
                .contentType(ContentType.JSON)
                .body("{\"bookId\":" + bookId + ",\"borrower\":\"Pedro\"}")
                .when()
                .post("/loans")
                .then()
                .statusCode(409);
    }

    /**
     * Cobre {@code POST /loans} quando o livro informado não existe no
     * catálogo: deve retornar {@code 404 Not Found}, sem registrar o
     * empréstimo.
     */
    @Test
    void testRegisterLoanForNonExistentBook() {
        given()
                .baseUri(LOAN_URL)
                .contentType(ContentType.JSON)
                .body("{\"bookId\":999999,\"borrower\":\"Carlos\"}")
                .when()
                .post("/loans")
                .then()
                .statusCode(404);
    }

    /**
     * Cobre {@code GET /books} (listagem completa do catálogo) e
     * {@code GET /books/{id}} com um {@code id} inexistente
     * ({@code 404 Not Found}).
     */
    @Test
    void testListBooksAndGetNonExistentBook() {
        int bookId = createBook("Dom Casmurro", "Machado de Assis");

        given()
                .baseUri(CATALOG_URL)
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .body("id", hasItem(bookId));

        given()
                .baseUri(CATALOG_URL)
                .when()
                .get("/books/999999")
                .then()
                .statusCode(404);
    }

    /**
     * Cobre {@code PUT /books/{id}/loan} e {@code PUT /books/{id}/return}
     * chamados diretamente no serviço de catálogo (sem passar pelo serviço
     * de empréstimos), incluindo o caso de {@code id} inexistente
     * ({@code 404 Not Found}).
     */
    @Test
    void testMarkBookAsLoanedAndReturnedDirectlyOnCatalog() {
        int bookId = createBook("O Cortiço", "Aluísio Azevedo");

        given()
                .baseUri(CATALOG_URL)
                .when()
                .put("/books/" + bookId + "/loan")
                .then()
                .statusCode(200)
                .body("loaned", is(true));

        given()
                .baseUri(CATALOG_URL)
                .when()
                .put("/books/" + bookId + "/return")
                .then()
                .statusCode(200)
                .body("loaned", is(false));

        given()
                .baseUri(CATALOG_URL)
                .when()
                .put("/books/999999/loan")
                .then()
                .statusCode(404);
    }
}
