/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package rpmhub.dev.web;

import java.util.logging.Logger;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import rpmhub.dev.model.Invoice;
import rpmhub.dev.restclient.IPayment;

/**
 * This class represents the checkout endpoint of the store application.
 * It allows users with the "User" role to confirm their payment and generates
 * a JSON Web Token (JWT) for the user with the given UPN and groups. The token
 * is signed using the issuer and full name claims.
 */
@Path("/checkout")
public class Checkout {

    /**
     * This line initializes a logger object for the Checkout class.
     */
    Logger logger = Logger.getLogger(Checkout.class.getName());

    /**
     * Injects a REST client for the IPayment service.
     */
    @Inject
    @RestClient
    IPayment paymentService;

    /**
     * Confirms the payment for the given card number and value.
     *
     * @param cardNumber the card number
     * @param value the value
     * @return the invoice
     */
    @POST
    @Path("/buy")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public Invoice buy(@FormParam("cardNumber") String cardNumber,
                           @FormParam("value") String value){
        logger.info("Confirms the payment");

        // Call the payment service to confirm the payment
        return paymentService.pay(cardNumber, value);
    }

}
