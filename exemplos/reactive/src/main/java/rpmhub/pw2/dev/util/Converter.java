/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a Creative Commons Attribution 4.0 International
 * License. You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */
package rpmhub.pw2.dev.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Converter<T, J> {

    /**
     * Convert T to J
     *
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public J convert(T t, Class j) throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // Instanciante a java generic object
        Constructor<J> constructor = j.getConstructor();
        final J i = constructor.newInstance();

        // Get all methods from java generic object
        Method[] methods = i.getClass().getMethods();
        // Iterate over all methods
        for (Method method : methods) {
            // If method starts with "set" then invoke it
            if (method.getName().startsWith("set") && !method.getName().contains("Id")) {
                String getMethodName = getMethodName(method.getName());
                method.invoke(i, t.getClass().getDeclaredMethod(getMethodName).invoke(t));
            }
        }
        return i;
    }

    /**
     * Convert the set method to get method
     *
     * @param field
     * @return
     */
    private String getMethodName(String field) {
        return "g" + field.substring(1, field.length());
    }

}
