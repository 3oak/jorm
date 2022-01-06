package jorm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToOne {
    String mappedBy() default "";
    String name() default "";
}
