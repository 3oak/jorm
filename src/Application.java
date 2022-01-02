import java.util.List;

import jorm.connection.ConnectionFactory;
import jorm.connection.MongoConnection;
import jorm.connection.SqlConnection;
import jorm.query.SqlQuery;

public class Application {
    public static void main(String args[]) {
        // Create SQL Connection
        SqlConnection sqlConnection = ConnectionFactory.createConnection(SqlConnection.class);
        // Create MongoDB Connection
        MongoConnection mongoConnection = ConnectionFactory.createConnection(MongoConnection.class);

        // Create query from connection
        SqlQuery<Character> sqlQuery = sqlConnection.CreateQuery(Character.class);
        List<Character> characters = sqlQuery.ToList();
        for (var item : characters) {
            System.out.println(item);
        }
    }
}
