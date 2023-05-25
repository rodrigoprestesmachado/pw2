/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a Creative Commons Attribution 4.0 International
 * License. You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package rpmhub.pw2.dev.controllers;

import org.modelmapper.ModelMapper;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import rpmhub.pw2.dev.entities.Product;
import rpmhub.pw2.dev.persistence.jpa.ProductEntity;
import rpmhub.pw2.dev.persistence.repository.ProductRepository;
import rpmhub.pw2.dev.usecase.ProductUC;
import rpmhub.pw2.dev.usecase.ProductUCI;

/**
 * Interface Adapter layer of Clean Architecture
 */
@ApplicationScoped
public class ProductController {

    // Persistence
    @Inject
    ProductRepository pRepository;

    // Clean architecture - use case layer
    ProductUCI uCase = new ProductUC();

    // Mapper (from https://modelmapper.org)
    ModelMapper mapper = new ModelMapper();

    @WithSession
    public Uni<ProductEntity> createProduct(String sku, String name,
            String description) {
        // Create a product according to the business rules defined in the
        // use case
        Product product = uCase.create(sku, name, description);
        // Convert to entity
        ProductEntity productEntity = mapper.map(product, ProductEntity.class);
        // Persist the entity
        return pRepository.saveProduct(productEntity);
    }

}