package jorm;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

import jorm.annotation.*;
import jorm.exception.InvalidSchemaException;
import jorm.query.QueryCommand;
import jorm.query.QueryType;
import jorm.utils.AnnotationValidationUtils;
import jorm.utils.Triplet;
import jorm.utils.Tuple;

@SuppressWarnings("unused")
public class Mapper<T> {
    private final Class<T> genericClass;

    private final String tableName;

    private final HashMap<Field, String> fieldColumnDictionary;
    private final HashMap<Field, Mapper<?>> fieldOneToOneDictionary;
    private final HashMap<Field, Mapper<?>> fieldOneToManyDictionary;

    private final Consumer<Triplet<String, String, String>> onAddRelationQuery;

    public Mapper(Class<T> genericClass, Consumer<Triplet<String, String, String>> onAddRelationQuery)
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

    public String GetTableName() {
        return tableName;
    }

    public Class<T> GetGenericClass() {
        return genericClass;
    }

    public static String GetColumnName(Field field) {
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

            if (AnnotationValidationUtils.IsAnnotationPresent(field, PrimaryKey.class) && columnName == null)
                throw new RuntimeException(
                        String.format("PrimaryKey need define annotation Column in %s", genericClass)
                );

            if (AnnotationValidationUtils.IsAnnotationPresent(field, ForeignKey.class) && columnName == null)
                throw new RuntimeException(
                        String.format("ForeignKey need define annotation Column in %s", genericClass)
                );

            if (AnnotationValidationUtils.IsAnnotationPresent(field, OneToOne.class)
                    && !AnnotationValidationUtils.IsAnnotationPresent(genericClass, PrimaryKey.class))
                throw new RuntimeException(
                        String.format("Relationship OneToOne need define field PrimaryKey in %s", genericClass)
                );

            if (AnnotationValidationUtils.IsAnnotationPresent(field, OneToMany.class)
                    && !AnnotationValidationUtils.IsAnnotationPresent(genericClass, PrimaryKey.class))
                throw new RuntimeException(
                        String.format("Relationship OneToOne need define field PrimaryKey in %s", genericClass)
                );

            fieldColumnDictionary.put(field, columnName);
        }

        return fieldColumnDictionary;
    }

    private HashMap<Field, Mapper<?>> GetFieldOneToOneDictionary()
            throws RuntimeException {
        HashMap<Field, Mapper<?>> fieldOneToOne = new HashMap<>();

        for (Field field : genericClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(OneToOne.class))
                continue;

            // Validate datatype of this field (throws Exception if it is primitive type)
            if (AnnotationValidationUtils.IsPrimitiveOrString(field.getType()))
                throw new RuntimeException(
                        String.format(
                                "Incorrectly typed data found for annotation element %s (Found data of type %s)",
                                field.getName(), field.getType().getName()
                        )
                );

            // Validate foreign key table name of field matching with this genericClass table name
            if (!AnnotationValidationUtils.IsForeignKeyMatching(genericClass, field.getType()))
                throw new RuntimeException(
                        String.format("ForeignKey not matching or not exist between two relationship %s and %s",
                                genericClass, field.getType()
                        )
                );

            fieldOneToOne.put(field, new Mapper<>(field.getType(), this::RaiseEventAddRelationshipQuery));
        }

        return fieldOneToOne;
    }

    private HashMap<Field, Mapper<?>> GetFieldOneToManyDictionary() {
        HashMap<Field, Mapper<?>> fieldOneToMany = new HashMap<>();

        for (Field field : genericClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(OneToMany.class))
                continue;

            // Validate datatype of this field (throws Exception if it is primitive type)
            if(AnnotationValidationUtils.IsPrimitiveOrString(field.getType()))
                throw new RuntimeException(String.format("Incorrectly typed data found for annotation element %s (Found data of type %s)", field.getName() ,field.getType().getName()));
            if (!List.class.isAssignableFrom(field.getType()))
                throw new RuntimeException("Invalid define datatype in relationship OneToMany");
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Class<?> actualType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            if(AnnotationValidationUtils.IsPrimitiveOrString(actualType))
                throw new RuntimeException(String.format("Incorrectly typed data found for annotation element %s (Found data of type %s)", field.getName() ,field.getType().getName()));
            // Validate foreign key table name of field matching with this genericClass table name
            if(!AnnotationValidationUtils.IsForeignKeyMatching(genericClass, actualType))
                throw new RuntimeException(String.format("ForeignKey not matching between two relationship %s and %s", genericClass.getName(), field.getType().getName()));
            fieldOneToMany.put(field, new Mapper<>(actualType, this::RaiseEventAddRelationshipQuery));
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

    public <T> Tuple<String, String> DataObjectToUpdateQuery(T dataObject) throws IllegalAccessException {
        if(dataObject == null)
            throw new NullPointerException();

        StringBuilder setValueQuery = new StringBuilder();
        boolean isAddPrefix = true;

        // Create update query for primitive datatype column (without relationship)
        for (Map.Entry<Field, String> item : fieldColumnDictionary.entrySet()) {
            if (item.getValue() == null)
                continue;

            var valueOfField = GetDataObjectOfField(item.getKey(), dataObject);

            if (valueOfField == null)
                continue;

            var column = item.getValue();
            setValueQuery.append(
                    String.format(
                            "%s%s = '%s'", isAddPrefix ? "" : ", ",
                            item.getValue(), valueOfField
                    )
            );

            isAddPrefix = false;
        }

        for (Map.Entry<Field, Mapper<?>> item : fieldOneToOneDictionary.entrySet()) {
            var valueOfField = GetDataObjectOfField(item.getKey(), dataObject);

            if (valueOfField == null)
                continue;

            var relationshipMapper = item.getValue();
            var relationshipUpdateQuery =
                    relationshipMapper.DataObjectToUpdateQuery(valueOfField);
            var relationshipConditionQuery =
                    String.format("%s = %s",
                            relationshipMapper.GetColumnNameForeignKeyOfType(genericClass),
                            GetValuePrimaryKey(dataObject));
            if (relationshipUpdateQuery == null)
                continue;
            RaiseEventAddRelationshipQuery(
                    Triplet.CreateTriplet(
                            relationshipUpdateQuery.GetHead(),
                            relationshipUpdateQuery.GetTail(),
                            relationshipConditionQuery));
        }

        for (Map.Entry<Field, Mapper<?>> item : fieldOneToManyDictionary.entrySet()) {
            var listValueOfField = GetDataObjectsOfField(item.getKey(), dataObject);
            if(listValueOfField == null)
                continue;
            for (var element : listValueOfField) {
                var relationshipMapper = item.getValue();
                var relationshipUpdateQuery = relationshipMapper.DataObjectToUpdateQuery(element);
                var relationshipConditionQuery =
                        String.format("%s = %s AND %s = %s",
                                relationshipMapper.GetColumnNameForeignKeyOfType(genericClass),
                                GetValuePrimaryKey(dataObject),
                                relationshipMapper.GetColumnNamePrimaryKey(),
                                relationshipMapper.GetValuePrimaryKey(element));
                if(relationshipUpdateQuery == null)
                    continue;
                RaiseEventAddRelationshipQuery(
                        Triplet.CreateTriplet(
                                relationshipUpdateQuery.GetHead(),
                                relationshipUpdateQuery.GetTail(),
                                relationshipConditionQuery));
            }
        }

        if (setValueQuery.toString().isBlank())
            return null;
        return setValueQuery.toString().isBlank() ? null : Tuple.CreateTuple(tableName, setValueQuery.toString());
    }

    public Tuple<String, String> GetPairColumnValue(T dataObject)
            throws IllegalAccessException {
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

            Class<?> fieldType = field.getType();

            var temporal =
                    !field.isAnnotationPresent(Temporal.class) ?
                            null : field.getAnnotation(Temporal.class).value();

            if (fieldType.isAssignableFrom(java.util.Date.class) && temporal == null) {
                throw new RuntimeException("Field has not been assigned annotation @Temporal");
            } else if (!fieldType.isAssignableFrom(java.util.Date.class) && temporal != null) {
                throw new RuntimeException("Annotation @Temporal could only be assigned to java.util.Date class");
            } else if (fieldType.isAssignableFrom(java.util.Date.class) && temporal != null) {
                switch (temporal) {
                    case DATE:
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        valueStringBuilder
                                .append("'")
                                .append(dateFormat.format(((java.util.Date) field.get(dataObject))))
                                .append("'");
                        break;
                    case TIME:
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        valueStringBuilder
                                .append("'")
                                .append(timeFormat.format(((java.util.Date) field.get(dataObject))))
                                .append("'");
                        break;
                    case TIMESTAMP:
                        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        valueStringBuilder
                                .append("'")
                                .append(datetimeFormat.format(((java.util.Date) field.get(dataObject))))
                                .append("'");
                        break;
                    default:
                        break;
                }
            } else {
                valueStringBuilder
                        .append("'")
                        .append(field.get(dataObject).toString())
                        .append("'");
            }

            index++;
        }

        return Tuple.CreateTuple(
                columnStringBuilder.toString(),
                valueStringBuilder.toString()
        );
    }

    public HashMap<Object, Mapper<?>> GetOneToOneRelationshipInstances(T dataObject)
            throws IllegalAccessException {
        var relationshipInstances = new HashMap<Object, Mapper<?>>();

        for (var field : fieldOneToOneDictionary.entrySet()) {
            var relationshipInstance =
                    GetDataObjectOfField(field.getKey(), dataObject);

            if (relationshipInstance == null)
                continue;

            var correspondingMapper = field.getValue();

            relationshipInstances.put(relationshipInstance, correspondingMapper);
        }

        return relationshipInstances;
    }

    public HashMap<List<T>, Mapper<?>> GetOneToManyRelationshipInstances(T dataObject)
            throws IllegalAccessException {
        var relationshipInstances = new HashMap<List<T>, Mapper<?>>();

        for (var field : fieldOneToManyDictionary.entrySet()) {
            var listRelationshipInstance =
                    GetDataObjectsOfField(field.getKey(), dataObject);

            if (listRelationshipInstance == null)
                continue;

            var correspondingMapper = field.getValue();

            relationshipInstances.put(listRelationshipInstance, correspondingMapper);
        }

        return relationshipInstances;
    }

    private <N> String GetValuePrimaryKey(N dataObject)
            throws IllegalAccessException {
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

    public <N> Object GetDataObjectOfField(Field field, N dataObject)
            throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(dataObject);
    }
    public <T> List<T> GetDataObjectsOfField(Field field, T dataObject) throws IllegalAccessException {
        field.setAccessible(true);
        return (List<T>) field.get(dataObject);
    }
    private <T, V> void SetDataObjectOfField(Field field, T dataObject, V Value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(dataObject, Value);
    }

    public String GetColumnNamePrimaryKey() {
        for (var item : fieldColumnDictionary.entrySet()) {
            if (item.getValue() == null)
                continue;
            if (item.getKey().isAnnotationPresent(PrimaryKey.class))
                return item.getKey().getName();
        }

        return null;
    }

    public String GetColumnNameForeignKeyOfType(Class<?> type) {
        if (!AnnotationValidationUtils.IsTableAnnotationPresent(type))
            return null;

        var referenceTableName =
                type.getDeclaredAnnotation(Table.class).name().isBlank() ?
                        type.getName() : type.getDeclaredAnnotation(Table.class).name();

        for (var item : fieldColumnDictionary.entrySet()) {
            if (item.getValue() == null)
                continue;
            if (!item.getKey().isAnnotationPresent(ForeignKey.class))
                continue;
            if (item.getKey().getDeclaredAnnotation(ForeignKey.class).tableName().equals(referenceTableName))
                return item.getValue();
        }

        return null;
    }

    private void RaiseEventAddRelationshipQuery(Triplet<String, String, String> updateQuery) {
        if (onAddRelationQuery != null)
            onAddRelationQuery.accept(updateQuery);
    }

    public Field GetPrimaryKey() throws InvalidSchemaException {
        Field field = null;
        for (Field f : genericClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(PrimaryKey.class)) {
                field = f;
            }
        }
        if (field == null) {
            throw new InvalidSchemaException("PrimaryKey", genericClass.getName());
        }
        return field;
    }

    public <N> Field GetForeignKey(Class<N> hasRelationshipWith) throws InvalidSchemaException {
        String tableName = GetTableName();

        Field field = null;
        for (Field f : hasRelationshipWith.getDeclaredFields()) {
            if (f.isAnnotationPresent(ForeignKey.class) && f.getAnnotation(ForeignKey.class).tableName().equals(tableName)) {
                field = f;
            }
        }

        if (field == null) {
            throw new InvalidSchemaException("ForeignKey", hasRelationshipWith.getName());
        }

        return field;
    }

    public <N> Tuple<Field, String> GetFieldWithRelationship(Class<N> hasRelationshipWith) throws InvalidSchemaException {
        Field field = null;
        String relationship = null;
        for (Field f : genericClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(OneToOne.class) && f.getType().isAssignableFrom(hasRelationshipWith)) {
                field = f;
                relationship = OneToOne.class.getName();

            } else if (f.isAnnotationPresent(OneToMany.class) && List.class.isAssignableFrom(f.getType())) {
                ParameterizedType genericType = (ParameterizedType) f.getGenericType();
                Class<?> actualTypeArgument = (Class<?>) genericType.getActualTypeArguments()[0];
                if (hasRelationshipWith.equals(actualTypeArgument)) {
                    field = f;
                    relationship = OneToMany.class.getName();
                }
            }
        }

        if (field == null) {
            throw new InvalidSchemaException("Relationship annotation", hasRelationshipWith.getName());
        }

        return Tuple.CreateTuple(field, relationship);
    }
}

