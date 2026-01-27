/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package rpmhub.dev.restclient;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.oidc.token.propagation.AccessToken;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import rpmhub.dev.model.Invoice;

/**
 * Interface for payment service.
 */
@RegisterRestClient(baseUri = "https://localhost:8445/payment")
@AccessToken
public interface IPayment {

    /**
     * Confirms payment with the given card number and value.
     *
     * @param cardNumber the card number to use for payment
     * @param value the value to pay
     * @return the invoice for the payment
     */
    @POST
    @Path("/pay")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Invoice pay(
        @FormParam("cardNumber") String cardNumber,
        @FormParam("value") String value);

    /**
     * Retrieves a list of invoices for the authenticated user.
     *
     * @return A list of Invoice objects in JSON format.
     */
    @GET
    @Path("/payment/getAllInvoices")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    List<Invoice> getInvoices();

}
