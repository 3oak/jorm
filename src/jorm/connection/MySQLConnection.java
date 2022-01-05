package jorm.connection;

import jorm.connection.configuration.Configuration;
import jorm.connection.configuration.MySQLConfiguration;
import jorm.query.MySQLQuery;

import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class MySQLConnection extends Connectable {
    public void OpenConnection(Configuration configuration)
            throws SQLException {
        MySQLConfiguration _configuration = (MySQLConfiguration) configuration;

        String connectionURL = _configuration.GetConnectionURL();
        String username = _configuration.username;
        String password = _configuration.password;

        connection = DriverManager.getConnection(connectionURL, username, password);
    }

    @Override
    public <T> MySQLQuery<T> CreateQuery(Class<T> userClass)
            throws RuntimeException {
        return new MySQLQuery<>(userClass, connection);
    }
}
