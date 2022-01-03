package jorm.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import jorm.connection.configuration.Configuration;
import jorm.connection.configuration.MySQLConfiguration;
import jorm.query.SqlQuery;

public class MySQLConnection implements Connectable {
    protected Connection connection;

    @Override
    public void OpenConnection(String connectionURL)
            throws SQLException {
        connection = DriverManager.getConnection(connectionURL);
    }

    @Override
    public void OpenConnection(String connectionURL, String username, String password)
            throws SQLException {
        connection = DriverManager.getConnection(connectionURL, username, password);
    }

    @Override
    public void OpenConnection(Configuration configuration)
            throws SQLException {
        MySQLConfiguration _configuration = (MySQLConfiguration) configuration;

        String connectionURL = _configuration.getConnectionURL();
        String username = _configuration.username;
        String password = _configuration.password;

        connection = DriverManager.getConnection(connectionURL, username, password);
    }

    @Override
    public void CloseConnection() throws SQLException {
        if (connection == null || connection.isClosed())
            return;

        connection.close();
    }

    @Override
    public <T> SqlQuery<T> CreateQuery(Class userClass) throws MessagingException {
        return new SqlQuery(userClass, connection);
    }
}
