/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a Creative Commons Attribution 4.0 International
 * License. You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package rpmhub.pw2.dev.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import rpmhub.pw2.dev.entities.ProductEntity;
import rpmhub.pw2.dev.model.Product;
import rpmhub.pw2.dev.repository.ProductRepository;
import rpmhub.pw2.dev.usecase.ProductUC;
import rpmhub.pw2.dev.util.Converter;

@ApplicationScoped
public class ProductController {

    // Clean architecture - Use case layer
    ProductUC productUC = new ProductUC();

    // Repository layer
    ProductRepository productRep = new ProductRepository();

    // Convert from Product to ProductEntity with reflection
    Converter<Product, ProductEntity> converter = new Converter<>();

    @WithSession
    public Uni<ProductEntity> createProduct(String sku, String name,
            String description) {
        Product p = productUC.create(sku, name, description);
        Uni<ProductEntity> uni = null;
        try {
            ProductEntity entity = (ProductEntity)
                converter.convert(p, ProductEntity.class);
            uni = productRep.persist(entity);
        } catch (NoSuchMethodException | SecurityException |
                    InstantiationException | IllegalAccessException |
                    IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return uni;
    }

}
