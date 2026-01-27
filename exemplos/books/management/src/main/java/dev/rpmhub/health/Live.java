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
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Startup;

@Liveness
@Startup
public class Live implements HealthCheck {

    /**
     * Check the health of the application.
     *
     * @return HealthCheckResponse : The health of the application
     */
     @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Management is alive!");
    }

}
