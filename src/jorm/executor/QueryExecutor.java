package jorm.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;

@SuppressWarnings("unused")
public class QueryExecutor extends Executor {
    public QueryExecutor(Connection connection) {
        super(connection);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T execute(Queue<String> queries)
            throws SQLException {
        Statement statement = connection.createStatement();
        return (T) statement.executeQuery(null);
    }
}
