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

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CoffeeRepositoryService {

    private AtomicLong counter = new AtomicLong(0);

    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    public Integer getAvailability(Coffee coffee) {
        maybeFail();
        return new Random().nextInt(30);
    }

    private void maybeFail() {
        final Long invocationNumber = counter.getAndIncrement();
        // Alterna 2 sucessos e 2 falhas
        if (invocationNumber % 4 > 1) {
            throw new RuntimeException("Service failed.");
        }
    }

    public Coffee getCoffeeById(int id) {
        Coffee coffee = new Coffee();
        coffee.setId(id);
        return coffee;
    }
}
