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

    private final Class<T> genericClass;
    private final Mapper<T> mapper;
    private final ArrayList<T> dataList;

    public MySQLQuery(Class<T> genericClass, Connection connection)
            throws RuntimeException {
        if (MySQLQuery.connection == null)
            MySQLQuery.connection = connection;

        this.genericClass = genericClass;
        this.mapper = new Mapper<>(genericClass);
        this.dataList = new ArrayList<>();
    }

    @Override
    public MySQLQuery<T> SelectAll() {
        ResultSet resultSet = null;

        // TODO: Get ResultSet

        try {
            if (resultSet == null)
                throw new RuntimeException(String.format("%s: Database load fail", genericClass.getName()));
            while (resultSet.next()) {
                T data = mapper.Map(resultSet);
                dataList.add(data);
            }
        } catch (SQLException
                | NoSuchFieldException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
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

    @Override
    public MySQLQuery<T> Filter(Predicate<T> predicate) {
        return this;
    }

    @Override
    public Queryable<T> Insert(T data) {
        // TODO:
        // - Consider relationship mapping annotation: 1-1; 1-n, n-1
        // - Add data to corresponding tables with appropriate constraints

        return null;
    }

    @Override
    public Queryable<T> InsertOrUpdate(T data) {


        return null;
    }

    @Override
    public Queryable<T> Update(T data) {
        return null;
    }

    @Override
    public Queryable<T> Execute() {
        return this;
    }

    @Override
    public List<T> ToList() {
        return dataList;
    }
}
