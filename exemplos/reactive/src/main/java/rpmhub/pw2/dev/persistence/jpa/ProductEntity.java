/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a Creative Commons Attribution 4.0 International
 * License. You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package rpmhub.pw2.dev.persistence.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Interface Adapter layer of Clean Architecture
 */
@Entity
@Getter @Setter
@Table(name = "Product")
public class ProductEntity extends PanacheEntityBase  {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private String sku;
    private String name;
    private String description;

}
