package jorm;

import jorm.annotation.Table;

@SuppressWarnings("unused")
public class AnnotationValidationUtils {
    public static <T> boolean isTableAnnotationPresent(T type) {
        return type.getClass().isAnnotationPresent(Table.class);
    }
}
