/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package rpmhub.dev.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents an invoice for a purchase made in a store.
 */
@Getter
@Setter
public class Invoice {

    /**
     * The card number used for payment.
     */
    private String cardNumber;

    /**
     * The value of the purchase.
     */
    private String value;

    /**
     * Whether the payment has been made or not.
     */
    private boolean payment;

}
