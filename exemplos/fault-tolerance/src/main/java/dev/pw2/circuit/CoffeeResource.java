/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package dev.pw2.circuit;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/circuit")
public class CoffeeResource {

    private Long counter = 0L;

    @Inject
    CoffeeRepositoryService coffeeRepository;

    Logger LOGGER = Logger.getLogger(CoffeeResource.class.getName());

    @GET
    @Path("/{id}/availability")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response availability(@PathParam("id") int id) {

        final Long invocationNumber = counter++;

        Coffee coffee = coffeeRepository.getCoffeeById(id);
        if (coffee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            Integer availability = coffeeRepository.getAvailability(coffee);

            LOGGER.log(Level.INFO, () -> "Sucesso: " + invocationNumber);
            return Response.ok(availability).build();
        } catch (RuntimeException e) {
            String message = String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
            LOGGER.log(Level.SEVERE, () -> "Falha:" + invocationNumber + " " + message);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(message)
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .build();
        }
    }

}
