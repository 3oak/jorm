package jorm.connection;

import java.sql.SQLException;

import jorm.connection.configuration.Configuration;
import jorm.query.Queryable;

public interface Connectable {
    void OpenConnection(String connectionURL)
            throws SQLException;

    void OpenConnection(String connectionURL, String username, String password)
            throws SQLException;

    void OpenConnection(Configuration configuration)
            throws SQLException;

    void CloseConnection()
            throws SQLException;

    <T> Queryable<T> CreateQuery(Class<T> userClass) throws RuntimeException;
}
