package dev.pw2;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

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
