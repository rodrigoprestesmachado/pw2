/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.rpmhub.web;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import dev.rpmhub.model.Book;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Represents a web service for managing a catalog of books.
 */
@Path("/books")
public class CatalogWS {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    /**
     * Adds a new book to the catalog.
     *
     * @param book the book to add (id and loaned are ignored/overridden)
     * @return the created book, status 201
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public Response addBook(Book book) {
        Book created = new Book(nextId.getAndIncrement(), book.title(), book.author(), false);
        books.put(created.id(), created);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Retrieves the catalog of books.
     *
     * @return the list of books, status 200
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> listBooks() {
        return List.copyOf(books.values());
    }

    /**
     * Retrieves a specific book.
     *
     * @param id the book identifier
     * @return the book, status 200, or 404 if it does not exist
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(@PathParam("id") Long id) {
        Book book = books.get(id);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(book).build();
    }

    /**
     * Marks a book as loaned.
     *
     * @param id the book identifier
     * @return the updated book, status 200, 404 if it does not exist, or 409
     *         if it is already loaned
     */
    @PUT
    @Path("/{id}/loan")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public Response loanBook(@PathParam("id") Long id) {
        Book book = books.get(id);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (book.loaned()) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        Book updated = new Book(book.id(), book.title(), book.author(), true);
        books.put(updated.id(), updated);
        return Response.ok(updated).build();
    }

    /**
     * Marks a book as returned.
     *
     * @param id the book identifier
     * @return the updated book, status 200, or 404 if it does not exist
     */
    @PUT
    @Path("/{id}/return")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public Response returnBook(@PathParam("id") Long id) {
        Book book = books.get(id);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Book updated = new Book(book.id(), book.title(), book.author(), false);
        books.put(updated.id(), updated);
        return Response.ok(updated).build();
    }
}
