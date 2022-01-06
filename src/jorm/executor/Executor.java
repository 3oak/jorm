package jorm.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;

@SuppressWarnings("unused")
public class Executor {
    protected Connection connection;

    public Executor(Connection connection) {
        this.connection = connection;
    }

    public void ExecuteNonQuery(Queue<String> queries)  {
        try (Statement statement = connection.createStatement()) {  // Auto resource management
            connection.setAutoCommit(false);
            while (!queries.isEmpty()) {
                statement.executeUpdate(queries.remove());
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public ResultSet ExecuteQuery(String query) {
        try (Statement statement = connection.createStatement()) {  // Auto resource management
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
