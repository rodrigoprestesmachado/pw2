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

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/fault")
public class FaultService {

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
            Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("The requested was an error")
                    .build();
            throw new WebApplicationException(response);
        }

        return name;
    }

    /**
     * Metodo usado para se recuperar de uma falha.
     *
     * @param name O valor da url
     * @return uma mensagem de erro juntamente com o parametro de entrada
     */
    public String recover(String name) {
        return FALL_BACK_MESSAGE + name;
    }

    /**
     * Para testar o Bulkhead, use a ferramenta k6:
     *
     * https://k6.io/docs/
     *
     * Na raiz desse projeto execute:
     *
     * k6 run k6.js
     */
    @GET
    @Path("/bulkhead/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @Bulkhead(2)
    public String bulkhead(@PathParam("name") String name) {
        LOGGER.info(name);
        return name;
    }

    private void sleep() throws InterruptedException {
        Thread.sleep(10000);
    }

}
