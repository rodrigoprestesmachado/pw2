package dev.rpmhub;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

@QuarkusTest
class UsersWSTest {
    @Test
    void testHelloEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"aluno@example.com\",\"fullName\":\"Aluno Teste\"}")
                .when().post("/users/getJwt")
                .then()
                .statusCode(200)
                .body(not(emptyOrNullString()));
    }

}