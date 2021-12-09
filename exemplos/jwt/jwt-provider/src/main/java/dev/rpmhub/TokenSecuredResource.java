package dev.rpmhub;

import java.util.Arrays;
import java.util.HashSet;

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

@Path("/secure")
public class TokenSecuredResource {

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
        return client.sum(a, b);
    }

    @GET
    @Path("/jwt")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String generate(@Context SecurityContext ctx) {
        return Jwt.issuer("http://localhost:8080")
                .upn("rodrigo@rpmhub.dev")
                .groups(new HashSet<>(Arrays.asList("User")))
                .claim(Claims.birthdate.name(), "2001-07-13")
                .sign();
    }

}