/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package edu.ifrs;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/payment")
public class Payment {

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
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public Invoice confirmPayment(
            @FormParam("cardNumber") String cardNumber,
            @FormParam("value") String value) {

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

}
