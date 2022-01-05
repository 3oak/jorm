package jorm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import jorm.annotation.Table;
import jorm.annotation.Column;
import jorm.executor.Executor;

@SuppressWarnings("unused")
public class Mapper<T> {
    private final Class<T> genericClass;

    private final String tableName;
    private final HashMap<Field, String> fieldColumnDictionary;

    private Executor executor = null;

    public Mapper(Class<T> genericClass)
            throws RuntimeException {
        if (!AnnotationValidationUtils.IsTableAnnotationPresent(genericClass)) {
            throw new RuntimeException(
                    String.format("%s does not have @Table Annotation", genericClass.getName())
            );
        }

        this.genericClass = genericClass;

        Table tableAnnotation = genericClass.getAnnotation(Table.class);
        this.tableName =
                tableAnnotation.name().isBlank() ?
                        genericClass.getName() : tableAnnotation.name();
        this.fieldColumnDictionary = GetFieldColumnDictionary();
    }

    private HashMap<Field, String> GetFieldColumnDictionary() {
        HashMap<Field, String> fieldColumnDictionary = new HashMap<>();

        for (Field field : this.genericClass.getDeclaredFields()) {
            String columnName =
                    !field.isAnnotationPresent(Column.class) ?
                            null : !field.getAnnotation(Column.class).name().isBlank() ?
                            field.getAnnotation(Column.class).name() : field.getName();

            fieldColumnDictionary.put(field, columnName);
        }

        return fieldColumnDictionary;
    }

    public T ToDataObject(ResultSet resultSet)
            throws
            InstantiationException,
            IllegalAccessException,
            SQLException,
            NoSuchFieldException,
            NoSuchMethodException,
            InvocationTargetException {
        T data = this.genericClass.getDeclaredConstructor().newInstance();

        for (Map.Entry<Field, String> item : this.fieldColumnDictionary.entrySet()) {
            if (item.getValue() == null) continue;

            Object fieldData = resultSet.getObject(item.getValue());
            Field field = data.getClass().getDeclaredField(item.getKey().getName());
            field.setAccessible(true);
            field.set(data, fieldData);
        }

        return data;
    }

    private Integer InsertSelf(T dataObject) {
        Class<?> dataClass = dataObject.getClass();

        Table tableAnnotation = dataClass.getAnnotation(Table.class);
        String tableName = tableAnnotation.name().isBlank()?
                dataClass.getName() : tableAnnotation.name();

        Field[] dataFields = dataClass.getFields();
        Table[] columns = dataClass.getAnnotationsByType(Table.class);

        System.out.println(Arrays.toString(dataFields));
        System.out.println(Arrays.toString(columns));

        return 0;
    }

    private Integer InsertRelatives(T dataObject) {
        return 0;
    }

    public String Insert(T dataObject) {
        StringBuilder stringBuilder = new StringBuilder();

        // Insert data to its own table
        InsertSelf(dataObject);

        // Insert data to tables which have relationship with this table
        InsertRelatives(dataObject);

        return "";
    }
}
