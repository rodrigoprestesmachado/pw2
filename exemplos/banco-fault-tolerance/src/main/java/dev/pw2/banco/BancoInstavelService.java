package dev.pw2.banco;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Simula um core bancario instavel. Os metodos abaixo NAO usam
 * tolerância a falhas: o estudante adiciona as anotacoes no resource.
 */
@ApplicationScoped
public class BancoInstavelService {

    private static final Logger LOGGER = Logger.getLogger(BancoInstavelService.class);

    private final AtomicInteger tentativasSaldo = new AtomicInteger(0);
    private final AtomicInteger chamadasCircuito = new AtomicInteger(0);
    private final AtomicInteger tentativasTransferencia = new AtomicInteger(0);

    /**
     * Falha nas 3 primeiras invocacoes de cada ciclo de 4.
     * Com @Retry(maxRetries = 3), uma unica requisicao HTTP consegue sucesso.
     */
    public BigDecimal consultarSaldo(String conta) {
        int n = tentativasSaldo.incrementAndGet();
        LOGGER.info("consultarSaldo conta=" + conta + " tentativa=" + n);
        if (n % 4 != 0) {
            throw new RuntimeException("Core bancario indisponivel");
        }
        return new BigDecimal("1500.00");
    }

    /** Sempre falha: serve para exercitar @Fallback. */
    public BigDecimal consultarSaldoSempreFalha(String conta) {
        LOGGER.info("consultarSaldoSempreFalha conta=" + conta);
        throw new RuntimeException("Core bancario fora do ar");
    }

    /** Demora 5 segundos: serve para exercitar @Timeout. */
    public BigDecimal consultarSaldoLento(String conta) throws InterruptedException {
        LOGGER.info("consultarSaldoLento inicio conta=" + conta);
        Thread.sleep(5000);
        LOGGER.info("consultarSaldoLento fim conta=" + conta);
        return new BigDecimal("1500.00");
    }

    /** Segura a thread por 3 segundos: serve para exercitar @Bulkhead. */
    public String processarTransferenciaLenta(String id) throws InterruptedException {
        LOGGER.info("processarTransferenciaLenta inicio id=" + id);
        Thread.sleep(3000);
        LOGGER.info("processarTransferenciaLenta fim id=" + id);
        return "transferencia-" + id + "-ok";
    }

    /**
     * Alterna 2 sucessos e 2 falhas. Com janela pequena, o disjuntor abre.
     */
    public BigDecimal consultarSaldoIntermitente(String conta) {
        long n = chamadasCircuito.getAndIncrement();
        LOGGER.info("consultarSaldoIntermitente conta=" + conta + " chamada=" + n);
        if (n % 4 > 1) {
            throw new RuntimeException("Falha intermitente no core");
        }
        return new BigDecimal("2200.00");
    }

    /**
     * Sempre falha. No exercicio combinado, @Retry esgota as tentativas e
     * @Fallback devolve o agendamento.
     */
    public String transferir(String origem, String destino, String valor) {
        int n = tentativasTransferencia.incrementAndGet();
        LOGGER.info("transferir " + origem + "->" + destino + " valor=" + valor + " tentativa=" + n);
        throw new RuntimeException("Transferencia rejeitada pelo core");
    }
}
