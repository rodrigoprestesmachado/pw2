/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 *
*/
package rpmhub.pw2.dev.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Frameworks and Drivers layer of Clean Architecture
 */
public class ServiceException extends WebApplicationException {

    /**
     * Service Exception constructor.
     *
     * @param message : The message of the exception
     * @param status  : The HTTP error code
     */
    public ServiceException(final String message, final Status status) {
        super(init(message, status));
    }

    /**
     * A static method to init the message.
     *
     * @param message : An error message
     * @param status  : A HTTP error code
     *
     * @return A Response object
     */
    private static Response init(final String message, final Status status) {
        List<Map<String,String>> violations = new ArrayList<>();
        violations.add(Map.of("message",message));

        return Response
            .status(status)
            .entity(Map.of("violations", violations))
            .build();
    }

}