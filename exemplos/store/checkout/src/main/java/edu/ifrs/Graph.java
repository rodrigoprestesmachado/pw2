package edu.ifrs;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@GraphQLApi
@ApplicationScoped
public class Graph {

     /**
     * This line initializes a logger object for the Checkout class.
     */
    Logger logger = Logger.getLogger(Graph.class.getName());

    /**
     * Injects a REST client for the IPayment service.
     */
    @Inject
    @RestClient
    IPayment service;

    /**
     * Retrieves all invoices.
     *
     * @return a list of all invoices
     */
    @Query("getAllInvoices")
    @Description("Get all invoices")
    public List<Invoice> getAllInvoices() {
        return service.getInvoices();
    }

}
