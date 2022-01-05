package jorm.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;

@SuppressWarnings("unused")
public class NonQueryExecutor extends Executor {
    public NonQueryExecutor(Connection connection) {
        super(connection);
    }

    @Override
    public Integer execute(Queue<String> queries)
            throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(null);
    }
}
