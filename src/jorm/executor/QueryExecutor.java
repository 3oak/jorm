package jorm.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;

@SuppressWarnings("unused")
public class QueryExecutor extends Executor {
    public QueryExecutor(Connection connection) {
        super(connection);
    }

    @Override
    public ResultSet execute(Queue<String> queries)
            throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(null);
    }
}
