package jorm.query;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import jorm.Mapper;
import jorm.clause.Clause;

public class MySQLQuery<T> implements Queryable<T> {
    private static Connection connection;

    private Class<T> genericClass;
    private Mapper<T> mapper;
    private ArrayList<T> listData;
    private String queryString;

    public MySQLQuery(Class<T> genericClass, Connection connection) throws RuntimeException {
        if(MySQLQuery.connection == null)
            MySQLQuery.connection = connection;
        this.genericClass = genericClass;
        this.mapper = new Mapper<>(genericClass);
        this.listData = new ArrayList<>();
    }

    @Override
    public MySQLQuery<T> SelectAll() {
        ResultSet resultSet = null;
        try{
            if(resultSet == null)
                throw new RuntimeException(String.format("%s: Database load fail", genericClass.getName()));
            while (resultSet.next()){
                T data = mapper.Map(resultSet);
                listData.add(data);
            }
        } catch (SQLException | NoSuchFieldException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Queryable<T> Where(Clause<?, ?> clauses) {
        return null;
    }

    @Override
    public Queryable<T> And(Clause<?, ?> clauses) {
        return null;
    }

    @Override
    public Queryable<T> Or(Clause<?, ?> clauses) {
        return null;
    }

    public Queryable<T> Select(String column) {
        return null;
    }

    @Override
    public MySQLQuery<T> Filter(Predicate<T> predicate) {
        return this;
    }

    @Override
    public Queryable<T> InsertOrUpdate(T data) {
        return null;
    }

    /**
     * Update data to database
     * @param data data to update
     */
    @Override
    public Queryable<T> Update(T data) {
        return null;
    }

    /**
     * Insert persistent data to database
     * @param data data to insert
     */
    @Override
    public MySQLQuery<T> Insert(T data) {
        return this;
    }

    /**
     * Execute command
     */
    @Override
    public Queryable<T> Execute() {
        return this;
    }

    /**
     * Return list data with generic type
     * @return list data
     */
    @Override
    public List<T> ToList() {
        return listData;
    }
}
