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
 * Body sent to request a new loan.
 *
 * @param bookId the identifier of the book to loan
 * @param borrower who is requesting the loan
 */
public record LoanRequest(Long bookId, String borrower) {}
