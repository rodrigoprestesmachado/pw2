/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.rpmhub;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.Claims;

import io.smallrye.jwt.build.Jwt;

@Path("/jwt")
public class GenerateJWT {

    /**
     * Para saber mais sobre o processo de assinatura e criptografia acesse:
     * https://smallrye.io/docs/smallrye-jwt/generate-jwt.html
     *
     * @return A JWT no formato de String
     */
    @GET
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String getJWT(){

        /**
         * Uma observação importante, você deve autenticar (usuário e senha)
         * os usuários antes de criar um token.
         */
        return Jwt.issuer("https://localhost:8443")
            .upn("rodrigo@rpmhub.dev")
            .groups(new HashSet<>(Arrays.asList("User", "Admin")))
            .claim(Claims.full_name, "Rodrigo Prestes Machado")
            .claim(Claims.email, "rodrigo@rpmub.dev")
            .innerSign()
            .encrypt();
    }

}
