/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package rpmhub.dev.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Invoice extends PanacheEntity {

    /**
     * The card number associated with the invoice.
     */
    @NotEmpty
    private String cardNumber;

    /**
     * The value of the invoice.
     */
    @NotEmpty
    private String value;

    /**
     * Whether or not the invoice has been paid.
     */
    private boolean payment;

    /**
     * Returns the card number associated with the invoice.
     * @return The card number.
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the card number associated with the invoice.
     * @param cardNumber The card number to set.
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Returns the value of the invoice.
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the invoice.
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns whether or not the invoice has been paid.
     * @return True if the invoice has been paid, false otherwise.
     */
    public boolean isPayment() {
        return payment;
    }

    /**
     * Sets whether or not the invoice has been paid.
     * @param payment True if the invoice has been paid, false otherwise.
     */
    public void setPayment(boolean payment) {
        this.payment = payment;
    }
}
