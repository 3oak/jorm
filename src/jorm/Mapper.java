package jorm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import jorm.annotation.Table;
import jorm.annotation.Column;

public class Mapper<T> {
    private final Class<T> genericClass;
    private final String tableName;
    private final HashMap<Field, String> fieldColumnMapper;

    private Executor executor = null;
    private final LinkedList<String> queries = new LinkedList<>();

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

    private String InsertSelf(T dataObject)
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
        queries.add(InsertSelf(dataObject));

        // TODO: Use Executor here!
    }
    
    public String getTableName() {
        return tableName;
    }
}
