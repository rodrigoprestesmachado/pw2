/**
 * PW2 by Rodrigo Prestes Machado
 * 
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 *
*/
package edu.ifrs.ws;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Register the Web Services
 * 
 * @author Rodrigo Prestes Machado
 */
@ApplicationPath(value = "/api")
public class WSConfiguration extends Application {
	
	public WSConfiguration() { }
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
		addRestResourceClasses(resources);
		return resources;
	}

	private void addRestResourceClasses(Set<Class<?>> resources) {
		resources.add(edu.ifrs.ws.Service.class);
	}
	
}
