/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a Creative Commons Attribution 4.0 International
 * License. You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package rpmhub.pw2.dev.usecase;

import rpmhub.pw2.dev.entities.Product;
/**
 * Application Business Rules layer of Clean Architecture
 */
public class ProductUC implements ProductUCI {

    /**
     * Stores a business rule: a product must have a sku.
     */
    @Override
    public Product create(String sku, String name, String description) {
       if (sku == null || sku.isEmpty()) {
           throw new IllegalArgumentException("sku is required");
       }
       else{
              return new Product(sku, name, description);
       }
    }
}
