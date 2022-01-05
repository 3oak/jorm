package jorm.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("unused")
public class QueryExecutor extends Executor {
    public QueryExecutor(String query, Connection connection) {
        super(query, connection);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T execute()
            throws SQLException {
        Statement statement = connection.createStatement();
        return (T) statement.executeQuery(query);
    }
}
