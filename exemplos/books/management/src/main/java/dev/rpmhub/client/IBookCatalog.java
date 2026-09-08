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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Rest Client used to consume the book catalog service.
 *
 * <p>Declared as {@code @ApplicationScoped} (instead of the default
 * {@code @Dependent} scope) so it can be mocked in tests with
 * {@code @InjectMock}.</p>
 */
@RegisterRestClient(baseUri = "https://localhost:8445/books")
@ApplicationScoped
@AccessToken
public interface IBookCatalog {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Book> listBooks();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Book getBook(@PathParam("id") Long id);

    @PUT
    @Path("/{id}/loan")
    @Produces(MediaType.APPLICATION_JSON)
    Book markAsLoaned(@PathParam("id") Long id);

}
