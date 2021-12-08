package dev.rpmhub;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/client")
public class Client {

    @Inject
    @RestClient
    MyRemoteService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public long client() {
        return service.sum(2, 2);
    }

}