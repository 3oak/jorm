package jorm.utils;

import jorm.annotation.Column;
import jorm.annotation.ForeignKey;
import jorm.annotation.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class AnnotationValidationUtils {
    public static <T> boolean IsTableAnnotationPresent(Class<T> genericClass) {
        return genericClass.isAnnotationPresent(Table.class);
    }

    public static <T> boolean IsAnnotationPresent(Class<T> genericClass, Class<? extends Annotation> annotation) {
        return Arrays.stream(genericClass.getDeclaredFields()).anyMatch(item -> item.isAnnotationPresent(annotation));
    }

    public static boolean IsForeignKeyMatching(Class<?> renterClass, Class<?> holderClass) {
        if (!AnnotationValidationUtils.IsTableAnnotationPresent(renterClass))
            return false;
        var referenceTableName = renterClass.getDeclaredAnnotation(Table.class).name().isBlank() ? renterClass.getName() : renterClass.getDeclaredAnnotation(Table.class).name();
        for (var item : holderClass.getDeclaredFields()) {
            if (!IsAnnotationPresent(item, Column.class)) continue;
            if (!IsAnnotationPresent(item, ForeignKey.class)) continue;
            if (item.getDeclaredAnnotation(ForeignKey.class).tableName().equals(referenceTableName)) return true;
        }
        return false;
    }

    public static boolean IsAnnotationPresent(Field field, Class<? extends Annotation> annotation) {
        return field.isAnnotationPresent(annotation);
    }

    public static boolean IsAnnotationPresentOnce(Field field, List<Class<? extends Annotation>> annotations) {
        return annotations.stream().anyMatch(field::isAnnotationPresent);
    }

    public static boolean IsPrimitiveOrString(Class<?> validateClass) {
        return Number.class.isAssignableFrom(validateClass) || validateClass.isPrimitive() || CharSequence.class.isAssignableFrom(validateClass);
    }
}
