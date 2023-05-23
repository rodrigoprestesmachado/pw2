/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a Creative Commons Attribution 4.0 International
 * License. You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package rpmhub.pw2.dev.model;

public class Product {

    private String sku;
    private String name;
    private String description;

    public Product(String sku, String name, String description) {
        this.sku = sku;
        this.name = name;
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
