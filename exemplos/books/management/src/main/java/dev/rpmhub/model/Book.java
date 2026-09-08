/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.rpmhub.model;

/**
 * Represents a book, as exposed by the catalog service.
 *
 * @param id the book identifier
 * @param title the book title
 * @param author the book author
 * @param loaned whether the book is currently loaned
 */
public record Book(Long id, String title, String author, boolean loaned) {}
