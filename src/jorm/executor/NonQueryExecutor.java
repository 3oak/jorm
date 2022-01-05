package jorm.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("unused")
public class NonQueryExecutor extends Executor {
    public NonQueryExecutor(String query, Connection connection) {
        super(query, connection);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer execute()
            throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(query);
    }
}
