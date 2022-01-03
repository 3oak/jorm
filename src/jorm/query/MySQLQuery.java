package jorm.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import jorm.Mapper;
import jorm.clause.Clause;

public class MySQLQuery<T> implements Queriable<T> {
    private static Connection connection;

    private T genericData;
    private Mapper<T> mapper;
    private ArrayList<T> listData;
    private String queryString;

    public MySQLQuery(T genericData, Connection connection) throws RuntimeException {
        if(this.connection == null)
            this.connection = connection;
        this.genericData = genericData;
        this.mapper = new Mapper(genericData);
        this.listData = new ArrayList<T>();
    }

    @Override
    public MySQLQuery<T> SelectAll() {
        ResultSet resultSet = null;
        try{
            if(resultSet == null)
                throw new RuntimeException(String.format("%s: Database load fail", genericData.getClass()));
            while (resultSet.next()){
                T data = mapper.Map(resultSet);
                listData.add(data);
            }
        }catch (SQLException | NoSuchFieldException | InstantiationException | IllegalAccessException e){
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Queriable<T> Where(Clause clauses) {
        return null;
    }

    @Override
    public Queriable<T> And(Clause clauses) {
        return null;
    }

    @Override
    public Queriable<T> Or(Clause clauses) {
        return null;
    }

    public Queriable<T> Select(String column) {
        return null;
    }

    @Override
    public MySQLQuery<T> Filter(Predicate<T> predicate) {
        return this;
    }

    @Override
    public Queriable<T> InsertOrUpdate(T data) {
        return null;
    }

    /**
     * Update data to database
     * @param data data to update
     */
    @Override
    public Queriable<T> Update(T data) {
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
    public Queriable<T> Execute() {
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
