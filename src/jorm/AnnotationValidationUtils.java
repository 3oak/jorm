package jorm;

import jorm.annotation.Table;

@SuppressWarnings("unused")
public class AnnotationValidationUtils {
    public static <T> boolean isTableAnnotationPresent(Class<T> genericClass) {
        return genericClass.isAnnotationPresent(Table.class);
    }
}
