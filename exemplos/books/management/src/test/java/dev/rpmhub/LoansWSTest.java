package dev.rpmhub;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import dev.rpmhub.client.IBookCatalog;
import dev.rpmhub.model.Book;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@QuarkusTest
class LoansWSTest {

    @InjectMock
    @RestClient
    IBookCatalog catalog;

    @Test
    void testListBooksIsPublicAndDelegatesToCatalog() {
        when(catalog.listBooks()).thenReturn(
            List.of(new Book(1L, "Dom Casmurro", "Machado de Assis", false)));

        given()
            .when().get("/loans/books")
            .then()
                .statusCode(200)
                .body("title", hasItem("Dom Casmurro"));
    }

    @Test
    void testRegisterLoanWithoutAuthenticationIsRejected() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"bookId\":1,\"borrower\":\"Ana\"}")
        .when()
            .post("/loans")
        .then()
            .statusCode(401);
    }

    @Test
    @TestSecurity(user = "ana", roles = "User")
    void testRegisterLoanSuccess() {
        when(catalog.getBook(1L)).thenReturn(new Book(1L, "Dom Casmurro", "Machado de Assis", false));
        when(catalog.markAsLoaned(1L)).thenReturn(new Book(1L, "Dom Casmurro", "Machado de Assis", true));

        given()
            .contentType(ContentType.JSON)
            .body("{\"bookId\":1,\"borrower\":\"Ana\"}")
        .when()
            .post("/loans")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("bookId", is(1))
            .body("borrower", is("Ana"));
    }

    @Test
    @TestSecurity(user = "ana", roles = "User")
    void testRegisterLoanConflictWhenBookAlreadyLoaned() {
        when(catalog.getBook(2L)).thenReturn(new Book(2L, "O Cortico", "Aluisio Azevedo", true));

        given()
            .contentType(ContentType.JSON)
            .body("{\"bookId\":2,\"borrower\":\"Pedro\"}")
        .when()
            .post("/loans")
        .then()
            .statusCode(409);
    }

    @Test
    @TestSecurity(user = "ana", roles = "User")
    void testRegisterLoanNotFoundWhenBookDoesNotExist() {
        when(catalog.getBook(999L)).thenThrow(
            new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build()));

        given()
            .contentType(ContentType.JSON)
            .body("{\"bookId\":999,\"borrower\":\"Carlos\"}")
        .when()
            .post("/loans")
        .then()
            .statusCode(404);
    }
}
