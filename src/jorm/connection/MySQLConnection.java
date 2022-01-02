package jorm.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jorm.connection.configuration.Configuration;
import jorm.connection.configuration.MySQLConfiguration;
import jorm.query.SqlQuery;

public class MySQLConnection implements Connectable {
    protected Connection connection;

    @Override
    public void OpenConnection(String connectionURL)
            throws SQLException {
        try {
            connection = DriverManager.getConnection(connectionURL);
        } catch (Exception ignore) {

        }
    }

    @Override
    public void OpenConnection(String connectionURL, String username, String password)
            throws SQLException {
        try {
            connection = DriverManager.getConnection(connectionURL, username, password);
        } catch (Exception ignore) {

        }
    }

    @Override
    public void OpenConnection(Configuration configuration)
            throws SQLException {
        try {
            MySQLConfiguration _configuration = (MySQLConfiguration) configuration;

            String connectionURL = _configuration.getConnectionURL();
            String username = _configuration.username;
            String password = _configuration.password;

            connection = DriverManager.getConnection(connectionURL, username, password);
        } catch (Exception ignore) {

        }
    }

    @Override
    public void CloseConnection() {
        try {
            if (connection == null || connection.isClosed())
                return;

            connection.close();
        } catch (Exception ignore) {

        }
    }

    @Override
    public <T> SqlQuery<T> CreateQuery(Class userClass) {
        return new SqlQuery(userClass, connection);
    }
}
