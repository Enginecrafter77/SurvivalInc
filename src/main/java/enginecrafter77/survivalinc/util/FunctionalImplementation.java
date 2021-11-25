package enginecrafter77.survivalinc.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FunctionalImplementation annotation denotes that a method is intended to be referenced as an implementation of a
 * specific {@link FunctionalInterface}, specified using {@link #of()} parameter of this annotation.
 * 
 * Generally, the method marked with this annotation should conform to the method signature of the functional interface
 * defined by {@link #of()}.
 *
 * @author Enginecrafter77
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface FunctionalImplementation {
	/**
	 * @return The class of the functional interface the method marked with this annotation should conform to.
	 */
	public Class<?> of();
}
