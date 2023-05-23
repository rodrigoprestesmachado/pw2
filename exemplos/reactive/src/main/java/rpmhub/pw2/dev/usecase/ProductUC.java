/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a Creative Commons Attribution 4.0 International
 * License. You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package rpmhub.pw2.dev.usecase;

import rpmhub.pw2.dev.model.Product;

public class ProductUC implements ProductI {

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
