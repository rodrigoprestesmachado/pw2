package dev.pw2;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class FaultServiceTest {

    @Test
    public void basicTest() {
        given()
                .when().get("/fault/tolerance")
                .then()
                .statusCode(200)
                .body(is("tolerance"));
    }

}