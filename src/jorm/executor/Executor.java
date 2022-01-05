package jorm.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;

@SuppressWarnings("unused")
public abstract class Executor {
    protected Connection connection;

    public Executor(Connection connection) {
        this.connection = connection;
    }

    public abstract <T> T execute(Queue<String> queries)
            throws SQLException;
}
