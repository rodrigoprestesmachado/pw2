/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.rpmhub;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/first")
public class First {

    private static final Logger LOGGER = Logger.getLogger(First.class.getName());

    /* Recupera as informações do token */
    @Inject
    @Claim(standard = Claims.full_name)
    String fullName;

    /* Rest client */
    @Inject
    @RestClient
    Second backend;

    @GET
    @Path("/sum/{a}/{b}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"Admin"})
    public int sum(@PathParam("a") int a, @PathParam("b") int b){
        LOGGER.log(Level.INFO, "First (fullname): {0}", fullName);
        return backend.getSum(a, b);
    }

}
