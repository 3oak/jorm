package jorm.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jorm.connection.configuration.Configuration;
import jorm.query.Queryable;

@SuppressWarnings("unused")
public abstract class Connectable {
    protected Connection connection;
    
    public void OpenConnection(String connectionURL)
            throws SQLException {
        connection = DriverManager.getConnection(connectionURL);
    }

    public void OpenConnection(String connectionURL, String username, String password)
            throws SQLException {
        connection = DriverManager.getConnection(connectionURL, username, password);
    }

    public abstract void OpenConnection(Configuration configuration) throws SQLException;

    public abstract <T> Queryable<T> CreateQuery(Class<T> userClass)
            throws RuntimeException;

    public Connection GetConnection() {
        return connection;
    }

    public void CloseConnection()
            throws SQLException {
        if (connection == null || connection.isClosed())
            return;

        connection.close();
    }
}
