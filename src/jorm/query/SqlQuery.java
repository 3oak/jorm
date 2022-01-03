package jorm.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import jorm.Mapper;

public class SqlQuery<T> implements Query<T> {
    private static Connection connection;

    private T genericData;
    private Mapper<T> mapper;
    private ArrayList<T> listData;

    public SqlQuery(T genericData, Connection connection) throws MessagingException {
        if(this.connection == null)
            this.connection = connection;
        this.genericData = genericData;
        this.mapper = new Mapper(genericData);
        this.listData = new ArrayList<T>();
    }


    @Override
    public SqlQuery<T> SelectAll() {
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
    public SqlQuery<T> Filter(Predicate<T> predicate) {
        return this;
    }

    /**
     * Save persistent data to database
     * @param data data to save
     */
    @Override
    public void Persist(T data) {

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
