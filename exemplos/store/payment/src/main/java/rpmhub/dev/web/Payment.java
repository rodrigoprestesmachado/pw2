/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package rpmhub.dev.web;

import java.util.List;
import java.util.logging.Logger;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import rpmhub.dev.data.InvoiceRepository;
import rpmhub.dev.model.Invoice;

@Path("/payment")
public class Payment {

    /**
     * This line initializes a logger object for the Checkout class.
     */
    Logger logger = Logger.getLogger(Payment.class.getName());

    /**
     * Injects an instance of the InvoiceRepository class to be used in the
     * service.
     */
    @Inject
    InvoiceRepository invoiceRepository;

    /**
     * Confirms a payment by creating and persisting an invoice with the
     * provided card number and value. Only users with the "User" role are
     * allowed to access this endpoint.
     *
     * @param cardNumber the card number used for the payment
     * @param value      the value of the payment
     * @return the created and persisted invoice
     */
    @POST
    @Path("/pay")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public Invoice pay(
            @FormParam("cardNumber") String cardNumber,
            @FormParam("value") String value) {

        logger.info("Confirms the payment");

        // Create a new invoice object
        Invoice invoice = new Invoice();
        invoice.setCardNumber(cardNumber);
        invoice.setValue(value);
        invoice.setPayment(true);

        // Persist the invoice object using the invoiceRepository
        invoiceRepository.persist(invoice);

        // Return the created and persisted invoice object
        return invoice;
    }

    /**
     * Retrieves a list of all invoices from the database.
     *
     * @return A list of Invoice objects.
     */
    @GET
    @Path("/getAllInvoices")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public List<Invoice> getInvoices(){
        return invoiceRepository.findAll().list();
    }

}
