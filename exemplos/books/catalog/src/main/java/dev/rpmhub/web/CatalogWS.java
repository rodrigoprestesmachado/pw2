/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.rpmhub.web;

import java.util.ArrayList;
import java.util.List;

import dev.rpmhub.model.Book;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Represents a web service for managing a catalog of books.
 */
@Path("/catalog")
public class CatalogWS {

    private ArrayList<Book> books = new ArrayList<>();

    public CatalogWS() {
        loadBooks();
    }

    /**
     * Retrieves a list of available books.
     *
     * @return a list of available books
     */
    @GET
    @Path("/listBooksAvailable")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public List<Book> listBooksAvailable() {
        return books;
    }

    /**
     * Marks a book as not available based on the provided ISBN.
     *
     * @param isbn the ISBN of the book to mark as not available
     * @return the updated list of books
    */
    @POST
    @Path("/markNotAvailable")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public List<Book> markNotAvailable(String json) {

        JsonObject jsonObject = new JsonObject(json);
        String isbn = jsonObject.getString("isbn");

        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                book.setAvailable(false);
                break;
            }
        }
        return books;
    }

    /**
     * Pings the service.
     *
     * @return a response
     */
    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "Pong";
    }

    /**
     * Loads books into the catalog.
     */
    private void loadBooks() {
        Book book = new Book();
        book.setTitle("The Hitchhiker's Guide to the Galaxy");
        book.setAuthor("Douglas Adams");
        book.setIsbn("0345391802");
        book.setAvailable(true);
        books.add(book);
    }
}
