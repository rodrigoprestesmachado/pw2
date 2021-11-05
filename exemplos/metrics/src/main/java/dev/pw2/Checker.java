/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.pw2;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;

@Path("/")
public class Checker {

    // Injeta um objeto da classe Histogram
    @Inject
    @Metric(name = "histogram", absolute = true)
    Histogram histogram;

    // Armazena o maior número par encontrado
    private long highestEven = 1;

    @GET
    @Path("/{number}")
    @Produces(MediaType.TEXT_PLAIN)
    @Counted(name = "counter", description = "The number of execution of the check method")
    @Timed(name = "timer", description = "A measure of how long it takes to  execute the check method", unit = MetricUnits.MICROSECONDS)
    public String check(@PathParam("number") long number) {
        // Histogram
        this.histogram.update(number);
        if (number < 1) {
            return "The number must be > 1";
        }
        if (number % 2 == 1) {
            return number + ": Odd";
        }
        if (number > highestEven) {
            this.highestEven = number;
        }
        return number + ": Even";
    }

    // Retorna o maior número par encontrado como uma métrica
    @Gauge(name = "highestEven", unit = MetricUnits.NONE, description = "Highest even so far")
    public Long highestEven() {
        return this.highestEven;
    }
}