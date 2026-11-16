package dev.pw2.banco;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BancoResourceTest {

    @Test
    @Order(1)
    void retryRecuperaSaldoAposFalhasTransitorias() {
        given()
                .when().get("/banco/saldo/123")
                .then()
                .statusCode(200)
                .body(containsString("1500.00"));
    }

    @Test
    @Order(2)
    void fallbackDevolveSaldoEmCache() {
        given()
                .when().get("/banco/saldo-cache/123")
                .then()
                .statusCode(200)
                .body(is("Saldo em cache da conta 123: 1000.00"));
    }

    @Test
    @Order(3)
    void timeoutInterrompeConsultaLenta() {
        given()
                .when().get("/banco/saldo-lento/123")
                .then()
                .statusCode(500);
    }

    @Test
    @Order(4)
    void transferenciaCombinadaUsaFallbackQuandoCoreFalha() {
        given()
                .queryParam("origem", "111")
                .queryParam("destino", "222")
                .queryParam("valor", "50")
                .when().get("/banco/transferir")
                .then()
                .statusCode(200)
                .body(containsString("Transferencia agendada"));
    }
}
