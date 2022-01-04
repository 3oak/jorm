package jorm.executor;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Executor {
    protected String query;
    protected Connection connection;

    public Executor(String query, Connection connection) {
        this.query = query;
        this.connection = connection;
    }

    public abstract <T> T execute() throws SQLException;
}
