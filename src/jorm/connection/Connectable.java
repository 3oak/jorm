package jorm.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jorm.connection.configuration.Configuration;
import jorm.query.Queryable;

@SuppressWarnings("unused")
public abstract class Connectable {
    protected Connection connection;
    protected String connectionURL;
    protected String username;
    protected String password;

    public Connectable(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public Connectable(String connectionURL, String username, String password) {
        this.connectionURL = connectionURL;
        this.username = username;
        this.password = password;
    }

    public Connectable(Configuration configuration) {
    }

    protected void OpenConnection() throws SQLException {
        connection = DriverManager.getConnection(connectionURL, username, password);
    }

    protected void CloseConnection() throws SQLException {
        if (connection == null || connection.isClosed())
            return;

        connection.close();
    }

    protected boolean ValidateSchema() {
        // TODO: Implement this
        return true;
    }

    /* *** TEMPLATE METHOD *** */
    public void SetUpConnection() throws SQLException {
        OpenConnection();
        if (!ValidateSchema()) {
            CloseConnection();
            // TODO: Add new custom exception
            throw new RuntimeException("Database validation failed.");
        }
    }

    abstract public <T> Queryable<T> CreateQuery(Class<T> userClass) throws RuntimeException;

    public Connection getConnection() {
        return connection;
    }
}
