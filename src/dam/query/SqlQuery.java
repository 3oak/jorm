package dam.query;

import dam.Mapper;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SqlQuery<T> implements Query<T>{
    private Mapper<T> mapper = null;
    private T genericData;

    public SqlQuery(T genericData, Connection connection){
        this.genericData = genericData;
        mapper = new Mapper(genericData);
    }
    @Override
    public SqlQuery<T> SelectAll() {
        return this;
    }

    @Override
    public SqlQuery<T> Filter(Predicate<T> predicate) {
        return this;
    }

    @Override
    public List<T> ToList() {
        ArrayList<T> listData = new ArrayList<T>();

        return listData;
    }
}
