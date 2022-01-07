package jorm;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

import jorm.annotation.*;
import jorm.query.QueryCommand;
import jorm.query.QueryType;
import jorm.utils.AnnotationValidationUtils;
import jorm.utils.Tuple;

@SuppressWarnings("unused")
public class Mapper<T> {
    private final Consumer<QueryCommand> onAddRelationQuery;
    private final Class<T> genericClass;
    private final String tableName;
    private final HashMap<Field, String> fieldColumnDictionary;
    private final HashMap<Field, Mapper<?>> fieldOneToOneDictionary;
    private final HashMap<Field, Mapper<?>> fieldOneToManyDictionary;

    private final LinkedList<String> queries = new LinkedList<>();

    public Mapper(Class<T> genericClass, Consumer<QueryCommand> onAddRelationQuery)
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
        this.fieldOneToOneDictionary = GetFieldOneToOneDictionary();
        this.fieldOneToManyDictionary = GetFieldOneToManyDictionary();
        this.onAddRelationQuery = onAddRelationQuery;
    }

    public static String getColumnName(Field field) {
        return !field.isAnnotationPresent(Column.class) ?
                null : !field.getAnnotation(Column.class).name().isBlank() ?
                field.getAnnotation(Column.class).name() : field.getName();
    }

    private HashMap<Field, String> GetFieldColumnDictionary() {
        HashMap<Field, String> fieldColumnDictionary = new HashMap<>();

        for (Field field : genericClass.getDeclaredFields()) {
            String columnName =
                    !field.isAnnotationPresent(Column.class) ?
                            null : !field.getAnnotation(Column.class).name().isBlank() ?
                            field.getAnnotation(Column.class).name() : field.getName();
            if(AnnotationValidationUtils.IsAnnotationPresent(field, PrimaryKey.class) && columnName == null)
                throw new RuntimeException(String.format("PrimaryKey need define annotation Column in %s", genericClass));
            if(AnnotationValidationUtils.IsAnnotationPresent(field, ForeignKey.class) && columnName == null)
                throw new RuntimeException(String.format("ForeignKey need define annotation Column in %s", genericClass));
            if(AnnotationValidationUtils.IsAnnotationPresent(field, OneToOne.class) && !AnnotationValidationUtils.IsAnnotationPresent(genericClass, PrimaryKey.class))
                throw new RuntimeException(String.format("Relationship OneToOne need define field PrimaryKey in %s", genericClass));
            if(AnnotationValidationUtils.IsAnnotationPresent(field, OneToMany.class) && !AnnotationValidationUtils.IsAnnotationPresent(genericClass, PrimaryKey.class))
                throw new RuntimeException(String.format("Relationship OneToOne need define field PrimaryKey in %s", genericClass));
            fieldColumnDictionary.put(field, columnName);
        }

        return fieldColumnDictionary;
    }
    private HashMap<Field, Mapper<?>> GetFieldOneToOneDictionary() throws RuntimeException {
        HashMap<Field, Mapper<?>> fieldOneToOne = new HashMap<>();

        for (Field field : genericClass.getDeclaredFields()) {
            if(!field.isAnnotationPresent(OneToOne.class))
                continue;
            // Validate datatype of this field (throws Exception if it is primitive type)
            if(AnnotationValidationUtils.IsPrimitiveOrString(field.getType()))
                throw new RuntimeException(String.format("Incorrectly typed data found for annotation element %s (Found data of type %s)", field.getName() ,field.getType().getName()));
            // Validate foreign key table name of field matching with this genericClass table name
            if(!AnnotationValidationUtils.IsForeignKeyMatching(genericClass, field.getType()))
                throw new RuntimeException(String.format("ForeignKey not matching or not exist between two relationship %s and %s", genericClass, field.getType()));
            fieldOneToOne.put(field, new Mapper<>(field.getType(), this::RaiseEventAddRelationshipQuery));
        }

        return fieldOneToOne;
    }
    private HashMap<Field, Mapper<?>> GetFieldOneToManyDictionary() {
        HashMap<Field, Mapper<?>> fieldOneToMany = new HashMap<>();

        for (Field field : genericClass.getDeclaredFields()) {
            if(!field.isAnnotationPresent(OneToMany.class))
                continue;
            // Validate datatype of this field (throws Exception if it is primitive type)
            if(AnnotationValidationUtils.IsPrimitiveOrString(field.getType()))
                throw new AnnotationTypeMismatchException(null, field.getType().getName());
            // Validate foreign key table name of field matching with this genericClass table name
            if(!AnnotationValidationUtils.IsForeignKeyMatching(genericClass, field.getType()))
                throw new RuntimeException(String.format("ForeignKey not matching between two relationship %s and %s", genericClass.getName(), field.getType().getName()));
            fieldOneToMany.put(field, new Mapper<>(field.getType(), this::RaiseEventAddRelationshipQuery));
        }

        return fieldOneToMany;
    }

    public T ToDataObject(ResultSet resultSet)
            throws
            InstantiationException,
            IllegalAccessException,
            SQLException,
            NoSuchFieldException,
            NoSuchMethodException,
            InvocationTargetException {
        T data = genericClass.getDeclaredConstructor().newInstance();

        for (Map.Entry<Field, String> item : fieldColumnDictionary.entrySet()) {
            if (item.getValue() == null) continue;

            Object fieldData = resultSet.getObject(item.getValue());
            Field field = data.getClass().getDeclaredField(item.getKey().getName());
            field.setAccessible(true);
            field.set(data, fieldData);
        }
        return data;
    }

    public <T> QueryCommand DataObjectToQueryCommand(T dataObject) throws IllegalAccessException {
        if(dataObject == null)
            throw new NullPointerException();
        StringBuilder setValueQuery = new StringBuilder();
        boolean isAddPrefix = true;
        // Create update query for primitive datatype column (without relationship)
        for (Map.Entry<Field, String> item : fieldColumnDictionary.entrySet()) {
            if (item.getValue() == null) continue;
            var valueOfField = GetDataObjectOfField(item.getKey(), dataObject);
            if(valueOfField == null)
                continue;
            var column = item.getValue();
            setValueQuery.append(String.format("%s%s = %s", isAddPrefix ? "" : ", " , item.getValue(), valueOfField));
            isAddPrefix = false;
        }
        for (Map.Entry<Field, Mapper<?>> item : fieldOneToOneDictionary.entrySet()) {
            var valueOfField = GetDataObjectOfField(item.getKey(), dataObject);
            if(valueOfField == null)
                continue;
            var relationshipMapper = item.getValue();
            var relationshipQueryCommand = relationshipMapper.DataObjectToQueryCommand(valueOfField);
            var relationshipConditionQuery =
                    String.format("where %s = %s",
                    relationshipMapper.GetColumnNameForeignKeyOfType(genericClass),
                    GetValuePrimaryKey(dataObject));
            if(relationshipQueryCommand == null)
                continue;
            relationshipQueryCommand.AddCommand(Tuple.CreateTuple(QueryType.WHERE, relationshipConditionQuery));
            RaiseEventAddRelationshipQuery(relationshipQueryCommand);
        }
        for (Map.Entry<Field, Mapper<?>> item : fieldOneToManyDictionary.entrySet()) {
            var valueOfField = GetDataObjectOfField(item.getKey(), dataObject);
            if(valueOfField == null)
                continue;
            var relationshipMapper = item.getValue();
            var relationshipQueryCommand = relationshipMapper.DataObjectToQueryCommand(valueOfField);
            var relationshipConditionQuery =
                    String.format("where %s = %s",
                    relationshipMapper.GetColumnNameForeignKeyOfType(genericClass),
                    GetValuePrimaryKey(dataObject));
            if(relationshipQueryCommand == null)
                continue;
            relationshipQueryCommand.AddCommand(Tuple.CreateTuple(QueryType.WHERE, relationshipConditionQuery));
            RaiseEventAddRelationshipQuery(relationshipQueryCommand);
        }
        if(setValueQuery.toString().isBlank())
            return  null;
        QueryCommand queryCommand = new QueryCommand();
        queryCommand.AddCommand(Tuple.CreateTuple(QueryType.UPDATE, String.format("Update %s set %s", tableName, setValueQuery)));
        return queryCommand;
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
    
//    public void Delete(T dataObject)
//            throws IllegalAccessException {
//        queries.clear();
//        queries.add(DeleteFromDatabase(dataObject));
//
//
//    }
//
//    private String DeleteFromDatabase(T dataObject)
//            throws IllegalAccessException {
//
//        int from, to;
//        String
//                keywordTable = "table",
//                keywordCondition = "conditions";
//
//        /* ---------------------------------------------------------------- */
//        // Query String
//        StringBuilder queryStringBuilder = new StringBuilder(
//                "DELETE FROM" + keywordTable + " WHERE " +  keywordCondition
//        );
//
//        /* ---------------------------------------------------------------- */
//        // TABLE
//        from = queryStringBuilder.indexOf(keywordTable);
//        to = from + keywordTable.length();
//        queryStringBuilder.replace(from, to, tableName);
//
//        /* ---------------------------------------------------------------- */
//
//        // CONDITION
//
//        return queryStringBuilder.toString();
//    }

    private <T> String GetValuePrimaryKey(T dataObject) throws IllegalAccessException {
        for (Map.Entry<Field, String> item : fieldColumnDictionary.entrySet()) {
            if (item.getValue() == null)
                continue;
            if (!item.getKey().isAnnotationPresent(PrimaryKey.class))
                continue;
            Field field = item.getKey();
            field.setAccessible(true);
            return field.get(dataObject).toString();
        }
        return null;
    }
    private <T> Object GetDataObjectOfField(Field field, T dataObject) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(dataObject);
    }
    private <T, V> void SetDataObjectOfField(Field field, T dataObject, V Value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(dataObject, Value);
    }

    private void RaiseEventAddRelationshipQuery(QueryCommand queryCommand){
        if(onAddRelationQuery != null)
            onAddRelationQuery.accept(queryCommand);
    }

    public String GetTableName() {
        return tableName;
    }
    /***
     * Get the column name of primary key in this generic class
     * @return column name
     */
    public String GetColumnNamePrimaryKey() {
        for (var item : fieldColumnDictionary.entrySet()) {
            if (item.getValue() == null)
                continue;
            if (item.getKey().isAnnotationPresent(PrimaryKey.class)) return item.getKey().getName();
        }
        return null;
    }

    /***
     * Get the column name of foreign key in this generic class that reference to other table (type)
     * @param type generic class
     * @return
     */
    public String GetColumnNameForeignKeyOfType(Class<?> type){
        if(!AnnotationValidationUtils.IsTableAnnotationPresent(type))
            return null;
        var referenceTableName = type.getDeclaredAnnotation(Table.class).name().isBlank() ? type.getName() : type.getDeclaredAnnotation(Table.class).name();
        for (var item : fieldColumnDictionary.entrySet()) {
            if (item.getValue() == null)
                continue;
            if (!item.getKey().isAnnotationPresent(ForeignKey.class))
                continue;
            if(item.getKey().getDeclaredAnnotation(ForeignKey.class).tableName().equals(referenceTableName))
                return item.getValue();
        }
        return null;
    }
}
