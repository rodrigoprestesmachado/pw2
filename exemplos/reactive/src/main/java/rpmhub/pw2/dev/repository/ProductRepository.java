/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a Creative Commons Attribution 4.0 International
 * License. You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package rpmhub.pw2.dev.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import rpmhub.pw2.dev.entities.ProductEntity;

public class ProductRepository implements PanacheRepository<ProductEntity> {

    public Uni<ProductEntity> saveProduct(ProductEntity product) {
        return Panache.<ProductEntity>withTransaction(product::persist);
    }

}
