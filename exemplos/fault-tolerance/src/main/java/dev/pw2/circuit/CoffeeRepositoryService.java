/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package dev.pw2.circuit;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;

/**
 * This class provides access to coffee data and implements fault tolerance
 * mechanisms.
 */
@ApplicationScoped
public class CoffeeRepositoryService {

    private AtomicLong counter = new AtomicLong(0);

    /**
     * Returns the availability of a coffee.
     *
     * @param coffee The coffee to check availability for.
     * @return An integer representing the availability of the coffee.
     */
    @CircuitBreaker(requestVolumeThreshold = 2)
    public Integer getAvailability(Coffee coffee) {
        maybeFail();
        // Java expression that generates a random integer between 0 (inclusive)
        // and 30 (exclusive)
        return new Random().nextInt(30);
    }

    /**
     * This method introduces artificial failures in the service. It throws a
     * RuntimeException every other invocation, alternating between 2 successful
     * and 2 failing invocations.
     */
    private void maybeFail() {
        // introduce some artificial failures
        final Long invocationNumber = counter.getAndIncrement();
        // alternate 2 successful and 2 failing invocations
        if (invocationNumber % 4 > 1) {
            throw new RuntimeException("Service failed.");
        }
    }

    /**
     * Returns a Coffee object with the specified ID.
     *
     * @param id The ID of the Coffee object to retrieve.
     * @return The Coffee object with the specified ID.
     */
    public Coffee getCoffeeById(int id) {
        Coffee coffee = new Coffee();
        coffee.setId(id);
        return coffee;
    }
}