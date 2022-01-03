package jorm.annotation;

public @interface JoinColumn {
    public String name() default "";
    public boolean nullable() default false;
}
