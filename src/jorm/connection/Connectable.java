package jorm.connection;

import java.sql.*;

import jorm.connection.configuration.Configuration;
import jorm.query.Queryable;

@SuppressWarnings("unused")
public abstract class Connectable {
    protected Connection connection;

    protected String connectionURL;
    protected String username;
    protected String password;

    private void OpenConnectionWithHost()
            throws SQLException {
        connection = DriverManager.getConnection(connectionURL, username, password);
    }

    private void OpenConnectionWithoutHost()
            throws SQLException {
        connection = DriverManager.getConnection(connectionURL);
    }

    public abstract <T> Queryable<T> CreateQuery(Class<T> userClass)
            throws RuntimeException;

    public Connection GetConnection() {
        return connection;
    }

    /* *** TEMPLATE METHOD *** */
    public void OpenConnection(String connectionURL)
            throws SQLException {
        this.connectionURL = connectionURL;

        OpenConnectionWithoutHost();
    }

    public void OpenConnection(String connectionURL, String username, String password)
            throws SQLException {
        this.connectionURL = connectionURL;
        this.username = username;
        this.password = password;

        OpenConnectionWithHost();
    }

    public void OpenConnection(Configuration configuration)
            throws SQLException {
        this.connectionURL = configuration.GetConnectionURL();
        this.username = configuration.username;
        this.password = configuration.password;

        OpenConnectionWithHost();
    }
    /* *********************** */

    public void CloseConnection()
            throws SQLException {
        if (connection == null || connection.isClosed())
            return;

        connection.close();
    }
}
