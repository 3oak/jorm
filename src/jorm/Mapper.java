package jorm;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import jorm.annotation.Column;
import jorm.annotation.Table;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class Mapper<T> {
    private Class<T> genericClass;
    private String tableName;
    private HashMap<Field, String> fieldColumnMapper;

    public Mapper(T genericData) throws MessagingException {
        if(AnnotationValidationUtils.isTableAnnotationPresent(genericData)){
            throw new RuntimeException(String.format("%s does not have @Table Annotation", genericData.getClass()));
        }
        this.genericClass = (Class<T>) genericData;
        this.tableName = genericClass.getDeclaredAnnotation(Table.class).tableName().isEmpty() ? genericClass.getName() : genericClass.getDeclaredAnnotation(Table.class).tableName();
        this.fieldColumnMapper = GenerateFieldColumnMapper();
    }
    private HashMap<Field, String> GenerateFieldColumnMapper(){
        HashMap<Field, String> mapper = new HashMap<>();
        for (Field field : genericClass.getDeclaredFields()) {
            String columnName = null;
            if(field.isAnnotationPresent(Column.class))
                columnName = field.getAnnotation(Column.class).columnName();
            mapper.put(field, columnName == null ? null : columnName.isEmpty() ? field.getName() : columnName);
        }
        return mapper;
    }
    public T Map(ResultSet resultSet) throws InstantiationException, IllegalAccessException, SQLException, NoSuchFieldException {
        T data = (T) genericClass.newInstance();
        for (Map.Entry<Field, String> item : fieldColumnMapper.entrySet()) {
            if(item.getValue() == null)
                continue;
            Object fieldData = resultSet.getObject(item.getValue());
            Field field = data.getClass().getDeclaredField(item.getKey().getName());
            field.setAccessible(true);
            field.set(data, fieldData);
        }
        return data;
    }
}
