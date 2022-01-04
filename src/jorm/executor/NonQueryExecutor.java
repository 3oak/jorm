package jorm.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("unused")
public class NonQueryExecutor extends Executor {
    public NonQueryExecutor(String query, Connection connection) {
        super(query, connection);
    }

    @Override
    public <T> T execute() throws SQLException {
        Statement statement = connection.createStatement();
        return (T) Integer.valueOf(statement.executeUpdate(query));
    }
}
