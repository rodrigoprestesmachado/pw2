/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package rpmhub.dev.web;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import rpmhub.dev.model.Invoice;
import rpmhub.dev.restclient.IPayment;

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
