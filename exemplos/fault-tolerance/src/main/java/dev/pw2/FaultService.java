/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package dev.pw2;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

@Path("/fault")
public class FaultService {

    /* Coloca no console as mensagens de erro */
    private static final Logger LOGGER = Logger.getLogger(FaultService.class.getName());

    private static final String FALL_BACK_MESSAGE = "FallbackMethod: ";

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @Retry(maxRetries = 3, delay = 2000)
    @Fallback(fallbackMethod = "recover")
    @Timeout(7000)
    public String getName(@PathParam("name") String name) {

        // Use esse trecho para simular um timeout
        //
        // try {
        // this.sleep();
        // } catch (InterruptedException e) {
        // LOGGER.info("Timeout");
        // }

        if (name.equalsIgnoreCase("error")) {
            ResponseBuilderImpl builder = new ResponseBuilderImpl();
            builder.status(Response.Status.INTERNAL_SERVER_ERROR);
            builder.entity("The requested was an error");
            Response response = builder.build();
            throw new WebApplicationException(response);
        }

        return name;
    }

    /**
     * Método usado para se recuperar de uma falha
     *
     * @param name O valor da url
     * @return uma mensagem de erro juntamente com o parâmetro de entrada
     */
    public String recover(String name) {
        return FALL_BACK_MESSAGE + name;
    }

    /**
     * Para testar o Bulkhead instale a ferramenta k6:
     *
     * https://k6.io/docs/
     *
     * Logo, na raiz desse projeto execute o comando:
     *
     * k6 run k6.js
     *
     * @param name
     * @return
     */
    @GET
    @Path("/bulkhead/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @Bulkhead(2)
    public String bulkhead(@PathParam("name") String name) {
        LOGGER.info(name);
        return name;
    }

    /**
     * Interrompe a thread por 10 segundos
     *
     * @throws InterruptedException
     */
    private void sleep() throws InterruptedException {
        Thread.sleep(10000);
    }

}
