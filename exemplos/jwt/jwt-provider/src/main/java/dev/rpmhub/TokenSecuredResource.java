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
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.smallrye.jwt.build.Jwt;

@Path("/secured")
public class TokenSecuredResource {

    private static final Logger LOGGER = Logger.getLogger(TokenSecuredResource.class.getName());

    @Inject
    JsonWebToken token;

    @Inject
    @RestClient
    Client client;

    @GET
    @Path("/sum/{a}/{b}")
    @RolesAllowed({ "User" })
    @Produces(MediaType.TEXT_PLAIN)
    public long sum(@Context SecurityContext ctx, @PathParam("a") long a, @PathParam("b") long b) {
        LOGGER.info("JWT-PROVIDED: sum");
        return client.sum(a, b);
    }

    @GET
    @Path("/jwt")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String generate(@Context SecurityContext ctx) {
        LOGGER.info("JWT-PROVIDED: generate");
        return Jwt.issuer("http://localhost:8080")
                .upn("rodrigo@rpmhub.dev")
                .groups(new HashSet<>(Arrays.asList("User")))
                .claim(Claims.birthdate.name(), "2001-07-13")
                .sign();
    }

}