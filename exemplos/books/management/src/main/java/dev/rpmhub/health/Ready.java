/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.rpmhub.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import dev.rpmhub.client.PingRC;
import jakarta.inject.Inject;

@Readiness
public class Ready implements HealthCheck {

    /**
     * Inject the Catalog service
     */
    @Inject
    @RestClient
    PingRC service;

    /**
     * Check the health of the application.
     *
     * @return HealthCheckResponse
     */
     @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder response =
            HealthCheckResponse.named(Ready.class.getName());
        checkCatalogService(response);
        return response.build();
    }

    /**
     * Check the catalog service
     *
     * @param HealthCheckResponseBuilder response
     * @return
     */
    private HealthCheckResponseBuilder checkCatalogService(
            HealthCheckResponseBuilder response) {
        if (service.ping().equals("Pong")) {
            response.up().withData("CatalogService", "up");
        }
        return response;
    }
}
