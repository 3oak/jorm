package jorm.annotation;

import jorm.Mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {
    public String mappedBy() default "";
    public String name() default "";
}
