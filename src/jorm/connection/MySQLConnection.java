package jorm.connection;

import jorm.query.MySQLQuery;

@SuppressWarnings("unused")
public class MySQLConnection extends Connectable {
    @Override
    public <T> MySQLQuery<T> CreateQuery(Class<T> userClass)
            throws RuntimeException {
        return new MySQLQuery<>(userClass, connection);
    }
}
