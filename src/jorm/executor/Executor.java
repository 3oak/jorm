package jorm.executor;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Executor {
    protected Connection connection;

    public Executor(Connection connection) {
        this.connection = connection;
    }

    public abstract <T> T execute(String query) throws SQLException;
}