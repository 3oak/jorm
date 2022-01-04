package jorm.annotation;

@SuppressWarnings("unused")
public @interface JoinColumn {
    String name() default "";
    boolean nullable() default false;
}
