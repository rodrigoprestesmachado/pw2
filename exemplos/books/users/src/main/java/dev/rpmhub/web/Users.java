/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.rpmhub.web;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.microprofile.jwt.Claims;

import io.smallrye.jwt.build.Jwt;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Represents a web service for managing users.
 */
@Path("/users")
public class Users {

    /**
     * The issuer URL for the JWT token.
     */
    private static final String ISSUER = "http://localhost:8080";

    /**
     * Generates a JSON Web Token (JWT) for the given full name.
     *
     * @param fullName the full name of the user
     * @return the generated JWT as a string
     */
    @POST
    @Path("/getJwt")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String generate(final String json) {

        JsonObject  jsonObject = new JsonObject(json);
        String email = jsonObject.getString("email");
        String fullName = jsonObject.getString("fullName");

        return Jwt.issuer(ISSUER)
                .upn(email)
                .groups(new HashSet<>(Arrays.asList("User", "Admin")))
                .claim(Claims.full_name, fullName)
                .sign();
    }
}
