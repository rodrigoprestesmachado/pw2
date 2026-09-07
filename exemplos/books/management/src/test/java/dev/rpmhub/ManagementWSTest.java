package dev.rpmhub;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class ManagementWSTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/q/health/live")
          .then()
             .statusCode(200)
             .body("status",is("UP"));
    }

}