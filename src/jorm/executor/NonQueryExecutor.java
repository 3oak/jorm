package jorm.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("unused")
public class NonQueryExecutor extends Executor {
    public NonQueryExecutor(Connection connection) {
        super(connection);
    }

    @Override
    public <T> T execute(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return (T) Integer.valueOf(statement.executeUpdate(query));
    }
}
