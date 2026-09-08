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

import org.eclipse.microprofile.rest.client.inject.RestClient;

import dev.rpmhub.client.IBookCatalog;
import dev.rpmhub.model.Book;
import dev.rpmhub.model.Loan;
import dev.rpmhub.model.LoanRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Represents a web service for managing book loans, backed by the
 * book catalog service (consumed via Rest Client).
 */
@Path("/loans")
public class LoansWS {

    @RestClient
    @Inject
    IBookCatalog catalog;

    private final Map<Long, Loan> loans = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    /**
     * Lists the books available for loan, delegating to the catalog
     * service.
     *
     * @return the list of books, status 200
     */
    @GET
    @Path("/books")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> listBooks() {
        return catalog.listBooks();
    }

    /**
     * Registers a new loan.
     *
     * @param request the loan request (bookId and borrower)
     * @return the created loan, status 201, 404 if the book does not
     *         exist, or 409 if the book is not available
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public Response registerLoan(LoanRequest request) {
        Book book;
        try {
            book = catalog.getBook(request.bookId());
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            throw e;
        }

        if (book.loaned()) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        catalog.markAsLoaned(request.bookId());

        Loan loan = new Loan(nextId.getAndIncrement(), request.bookId(), request.borrower());
        loans.put(loan.id(), loan);

        return Response.status(Response.Status.CREATED).entity(loan).build();
    }
}
