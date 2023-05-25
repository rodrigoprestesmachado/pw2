/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 *
*/
package rpmhub.pw2.dev.ws;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import rpmhub.pw2.dev.controllers.ProductController;

/**
 * Frameworks and Drivers layer of Clean Architecture
 */
@Path("/product ")
public class ProductWS {

    // Clean architecture - Interface and Adapters
    @Inject
    ProductController controller;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createProduct(@FormParam("sku") String sku,
            @FormParam("name") String name,
            @FormParam("description") String description) {
                return controller.createProduct(sku, name, description)
                .onFailure()
                    .transform(exception ->{
                        String message = exception.getMessage();
                        throw new ServiceException(message,
                            Response.Status.BAD_REQUEST);
                    })
                .onItem().ifNotNull().transform(product -> {
                    return Response.ok(product).build();
                });
    }

}
