/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.rpmhub.client;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import dev.rpmhub.model.Book;
import io.quarkus.oidc.token.propagation.AccessToken;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * REST client used to communicate with the catalog service.
 */
@RegisterRestClient(baseUri = "https://localhost:8445/catalog")
@AccessToken
public interface CatalogRC {

    /**
     * Retrieves a list of available books.
     *
     * @return a list of available books
     */
    @GET
    @Path("/listBooksAvailable")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    List<Book> listBooksAvailable();

    /**
     * Marks a book as not available based on the provided ISBN.
     *
     * @param json the ISBN of the book to mark as not available
     * @return the updated list of books
     */
    @POST
    @Path("/markNotAvailable")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    List<Book> markNotAvailable(String json);

}
