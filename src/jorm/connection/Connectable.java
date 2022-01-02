package jorm.connection;

import java.sql.SQLException;

import jorm.connection.configuration.Configuration;
import jorm.query.Queriable;

public interface Connectable {
    void OpenConnection(String connectionURL)
            throws SQLException;

    void OpenConnection(String connectionURL, String username, String password)
            throws SQLException;

    void OpenConnection(Configuration configuration)
            throws SQLException;

    void CloseConnection();

    <T> Queriable<T> CreateQuery(Class userClass);
}
