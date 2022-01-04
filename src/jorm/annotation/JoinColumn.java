package jorm.annotation;

public @interface JoinColumn {
    String name() default "";
    boolean nullable() default false;
}
