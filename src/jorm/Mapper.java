package jorm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import jorm.annotation.Table;
import jorm.annotation.Column;

@SuppressWarnings("unused")
public class Mapper<T> {
    private final Class<T> genericClass;
    private final String tableName;
    private final HashMap<Field, String> fieldColumnDictionary;

    private final LinkedList<String> queries = new LinkedList<>();

    public Mapper(Class<T> genericClass)
            throws RuntimeException {
        if (!AnnotationValidationUtils.isTableAnnotationPresent(genericClass)) {
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

    private String InsertIntoDatabase(T dataObject)
            throws IllegalAccessException {
        int from, to;
        String
                keywordTable = "table",
                keywordColumn = "columns",
                keywordValues = "values";

        /* ---------------------------------------------------------------- */
        StringBuilder queryStringBuilder =
                new StringBuilder(
                        "INSERT INTO " + keywordTable + " (" + keywordColumn + ") " +
                                "VALUES (" + keywordValues + ")"
                );

        /* ---------------------------------------------------------------- */
        // TABLE
        from = queryStringBuilder.indexOf(keywordTable);
        to = from + keywordTable.length();
        queryStringBuilder.replace(from, to, tableName);

        /* ---------------------------------------------------------------- */
        // COLUMNS & VALUES
        StringBuilder columnStringBuilder = new StringBuilder();
        StringBuilder valueStringBuilder = new StringBuilder();

        int index = 0;
        for (var pairFieldColumn : this.fieldColumnDictionary.entrySet()) {
            if (pairFieldColumn.getValue() == null) continue;

            if (index != 0) {
                columnStringBuilder.append(", ");
                valueStringBuilder.append(", ");
            }

            Field field = pairFieldColumn.getKey();
            String column = pairFieldColumn.getValue();

            field.setAccessible(true);

            columnStringBuilder.append(column);
            valueStringBuilder.append(field.get(dataObject).toString());

            index++;
        }

        // TODO: Datetime process here!

        from = queryStringBuilder.indexOf(keywordColumn);
        to = from + keywordColumn.length();
        queryStringBuilder.replace(from, to, columnStringBuilder.toString());

        from = queryStringBuilder.indexOf(keywordValues);
        to = from + keywordValues.length();
        queryStringBuilder.replace(from, to, valueStringBuilder.toString());

        return queryStringBuilder.toString();
    }

    public void Insert(T dataObject)
            throws IllegalAccessException {
        queries.clear();
        queries.add(InsertIntoDatabase(dataObject));

        // TODO: Use Executor here!
    }

    public String GetTableName() {
        return tableName;
    }
}
