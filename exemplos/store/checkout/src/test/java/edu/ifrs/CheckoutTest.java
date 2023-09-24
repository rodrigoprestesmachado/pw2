package edu.ifrs;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CheckoutTest {

    private String token;

    @Test
    @Order(1)
    public void testGenerateJwtEndpoint() {
        this.token = given()
        .when().get("/checkout/jwt")
        .then()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .asString();
    }

    // @Test
    // @Order(2)
    // public void testConfirmPayment() {
    //     given()
    //         .header("Authorization", "Bearer " + this.token)
    //         .when().get("/checkout")
    //         .then()
    //             .statusCode(200)
    //             .body("id", equalTo("123455"))
    //             .body("amount", equalTo("100"));
    // }

}