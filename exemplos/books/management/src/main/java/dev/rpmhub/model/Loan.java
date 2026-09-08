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
 * Represents a loan registered by the loans management service.
 *
 * @param id the loan identifier
 * @param bookId the identifier of the loaned book
 * @param borrower who requested the loan
 */
public record Loan(Long id, Long bookId, String borrower) {}
