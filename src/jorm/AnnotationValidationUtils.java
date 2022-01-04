package jorm;

import jorm.annotation.Table;

public class AnnotationValidationUtils {
    public static <T> boolean IsTableAnnotationPresent(Class<T> genericClass) {
        return genericClass.isAnnotationPresent(Table.class);
    }
}
