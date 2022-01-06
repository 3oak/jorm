package jorm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinColumn {
    String name() default "";
    boolean nullable() default false;
    String referencedColumnName() default "";
}
