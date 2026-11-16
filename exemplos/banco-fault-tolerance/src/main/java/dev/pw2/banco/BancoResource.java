package dev.pw2.banco;

import java.math.BigDecimal;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * Versao resolvida dos exercicios de fixacao.
 * Na pagina da disciplina o codigo de partida vem SEM as anotacoes.
 */
@Path("/banco")
public class BancoResource {

    private static final Logger LOGGER = Logger.getLogger(BancoResource.class);

    @Inject
    BancoInstavelService core;

    /** Exercicio 1: @Retry */
    @GET
    @Path("/saldo/{conta}")
    @Produces(MediaType.TEXT_PLAIN)
    @Retry(maxRetries = 3, delay = 500)
    public String saldo(@PathParam("conta") String conta) {
        BigDecimal saldo = core.consultarSaldo(conta);
        return "Saldo da conta " + conta + ": " + saldo;
    }

    /** Exercicio 2: @Fallback */
    @GET
    @Path("/saldo-cache/{conta}")
    @Produces(MediaType.TEXT_PLAIN)
    @Retry(maxRetries = 2, delay = 200)
    @Fallback(fallbackMethod = "saldoEmCache")
    public String saldoComFallback(@PathParam("conta") String conta) {
        BigDecimal saldo = core.consultarSaldoSempreFalha(conta);
        return "Saldo da conta " + conta + ": " + saldo;
    }

    public String saldoEmCache(String conta) {
        LOGGER.warn("Fallback acionado para conta=" + conta);
        return "Saldo em cache da conta " + conta + ": 1000.00";
    }

    /** Exercicio 3: @Timeout */
    @GET
    @Path("/saldo-lento/{conta}")
    @Produces(MediaType.TEXT_PLAIN)
    @Timeout(2000)
    public String saldoLento(@PathParam("conta") String conta) throws InterruptedException {
        BigDecimal saldo = core.consultarSaldoLento(conta);
        return "Saldo da conta " + conta + ": " + saldo;
    }

    /** Exercicio 4: @Bulkhead */
    @GET
    @Path("/transferencia-lenta/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    @Bulkhead(2)
    public String transferenciaLenta(@PathParam("id") String id) throws InterruptedException {
        return core.processarTransferenciaLenta(id);
    }

    /** Exercicio 5: @CircuitBreaker */
    @GET
    @Path("/saldo-circuito/{conta}")
    @Produces(MediaType.TEXT_PLAIN)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    public String saldoCircuito(@PathParam("conta") String conta) {
        BigDecimal saldo = core.consultarSaldoIntermitente(conta);
        return "Saldo da conta " + conta + ": " + saldo;
    }

    /** Exercicio 6: combinacao de anotacoes */
    @GET
    @Path("/transferir")
    @Produces(MediaType.TEXT_PLAIN)
    @Timeout(3000)
    @Retry(maxRetries = 3, delay = 300)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "transferenciaAgendada")
    public String transferir(
            @QueryParam("origem") String origem,
            @QueryParam("destino") String destino,
            @QueryParam("valor") String valor) {
        return core.transferir(origem, destino, valor);
    }

    public String transferenciaAgendada(String origem, String destino, String valor) {
        LOGGER.warn("Fallback de transferencia acionado");
        return "Transferencia agendada para processamento posterior: "
                + origem + " -> " + destino + " valor=" + valor;
    }
}
