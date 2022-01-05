package jorm.query;

import jorm.Mapper;
import jorm.clause.Clause;
import jorm.executor.Executor;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
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
                T data = mapper.ToDataObject(resultSet);
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
    public MySQLQuery<T> Where(Clause clauses) {
        return null;
    }

    @Override
    public MySQLQuery<T> And(Clause clauses) {
        return null;
    }

    @Override
    public MySQLQuery<T> Or(Clause clauses) {
        return null;
    }

    @Override
    public MySQLQuery<T> Filter(Predicate<T> predicate) {
        return this;
    }

    @Override
    public MySQLQuery<T> Insert(T dataObject) {
        // TODO:
        // - Inject data values into its own table
        // - Consider relationship mapping annotation: 1-1; 1-n, n-1
        // - Add data to corresponding tables with appropriate constraints
        // -- Insert function that creates Query
        // -- Query will be used by Executor to execute the query

        var message = mapper.Insert(dataObject);
        System.out.println(message);

        return this;
    }

    @Override
    public MySQLQuery<T> InsertOrUpdate(T data) {


        return null;
    }

    @Override
    public MySQLQuery<T> Update(T data) {
        return null;
    }

    @Override
    public MySQLQuery<T> Execute() {
        return this;
    }

    @Override
    public List<T> ToList() {
        return dataList;
    }
}
