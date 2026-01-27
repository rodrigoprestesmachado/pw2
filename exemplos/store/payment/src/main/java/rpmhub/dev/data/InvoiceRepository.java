/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package rpmhub.dev.data;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import rpmhub.dev.model.Invoice;

/**
 * Repository class for Invoice entity.
 */
@ApplicationScoped
public class InvoiceRepository implements PanacheRepository<Invoice> {

}
