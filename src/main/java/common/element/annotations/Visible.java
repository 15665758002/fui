package common.element.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Visible {

    /** Default value. */
    String value() default "";

    /**
     * If checking for visibility of a list of elements, setting a value
     * will only check for visibility of the first n elements of the list.
     */
    int checkAtMost() default -1;
}
