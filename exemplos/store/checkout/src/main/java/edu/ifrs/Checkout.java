/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package edu.ifrs;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * This class represents the checkout endpoint of the store application.
 * It allows users with the "User" role to confirm their payment and generates
 * a JSON Web Token (JWT) for the user with the given UPN and groups. The token
 * is signed using the issuer and full name claims.
 */
@Path("/checkout")
public class Checkout {

    @Inject
    @RestClient
    IPayment service;

    /**
     * This method confirms the payment of an invoice and returns the invoice
     * as a JSON object. Only users with the "User" role are allowed to access
     * this method.
     * @return the confirmed invoice as a JSON object
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public Invoice confirm(){
        return service.confirmPayment("123455","100");
    }

    /**
     * Generates a JSON Web Token (JWT) for the user with the given UPN and
     * groups. The token is signed using the issuer and full name claims.
     *
     * @return the generated JWT as a string
     */
    @GET
    @Path("/jwt")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String generate() {
        return Jwt.issuer("http://localhost:8080")
            .upn("rodrigo@rpmhub.dev")
            .groups(new HashSet<>(Arrays.asList("User", "Admin")))
            .claim(Claims.full_name, "Rodrigo Prestes Machado")
            .sign();
    }

}
