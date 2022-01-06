package jorm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

import jorm.annotation.Table;
import jorm.annotation.Column;

public class Mapper<T> {
    private final Class<T> genericClass;
    private final String tableName;
    private final HashMap<Field, String> fieldColumnMapper;

    public Mapper(Class<T> genericClass)
            throws RuntimeException {
        if (!AnnotationValidationUtils.isTableAnnotationPresent(genericClass)) {
            throw new RuntimeException(String.format("%s does not have @Table Annotation", genericClass.getName()));
        }

        this.genericClass = genericClass;
        this.tableName = genericClass.getDeclaredAnnotation(Table.class).tableName().isEmpty() ? genericClass.getName() : genericClass.getDeclaredAnnotation(Table.class).tableName();
        this.fieldColumnMapper = GenerateFieldColumnMapper();
    }

    private HashMap<Field, String> GenerateFieldColumnMapper() {
        HashMap<Field, String> mapper = new HashMap<>();

        for (Field field : genericClass.getDeclaredFields()) {
            String columnName = null;

            if (field.isAnnotationPresent(Column.class))
                columnName = field.getAnnotation(Column.class).columnName();

            mapper.put(field, columnName == null ? null : columnName.isEmpty() ? field.getName() : columnName);
        }

        return mapper;
    }

    public T Map(ResultSet resultSet)
            throws
            InstantiationException,
            IllegalAccessException,
            SQLException,
            NoSuchFieldException,
            NoSuchMethodException,
            InvocationTargetException {
        T data = genericClass.getDeclaredConstructor().newInstance();

        for (Map.Entry<Field, String> item : fieldColumnMapper.entrySet()) {
            if (item.getValue() == null)
                continue;

            Object fieldData = resultSet.getObject(item.getValue());
            Field field = data.getClass().getDeclaredField(item.getKey().getName());
            field.setAccessible(true);
            field.set(data, fieldData);
        }

        return data;
    }

    public String getTableName() {
        return tableName;
    }
}
