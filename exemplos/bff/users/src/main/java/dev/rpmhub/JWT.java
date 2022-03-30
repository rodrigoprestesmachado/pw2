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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.Claims;

import io.smallrye.jwt.build.Jwt;

@Path("/getjwt")
public class JWT {

    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String getJWT(@FormParam("name") String name, @FormParam("email") String email){
        /**
         * Uma observação importante, você deve autenticar (usuário e senha)
         * os usuários antes de criar um token.
         */
        return Jwt.issuer("https://localhost:8443")
            .upn(email)
            .groups(new HashSet<>(Arrays.asList("User", "Admin")))
            .claim(Claims.full_name, name)
            .claim(Claims.email, email)
            .sign();
    }

}
