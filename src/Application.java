import dam.connection.ConnectionFactory;
import dam.connection.MongoConnection;
import dam.connection.SqlConnection;
import dam.query.SqlQuery;

import java.util.List;

public class Application {
    public static void main(String args[]){
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
