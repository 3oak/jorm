import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import jorm.annotation.Column;
import jorm.connection.ConnectionFactory;
import jorm.connection.MongoConnection;
import jorm.connection.SqlConnection;
import jorm.query.SqlQuery;

public class Application {
    public static void main(String args[]) throws MessagingException {
        // Create SQL Connection
        SqlConnection sqlConnection = ConnectionFactory.createConnection(SqlConnection.class);
        // Create MongoDB Connection
        //MongoConnection mongoConnection = ConnectionFactory.createConnection(MongoConnection.class);

        // Create query from connection
        SqlQuery<Character> sqlQuery = sqlConnection.CreateQuery(Character.class);
        List<Character> characters = sqlQuery.ToList();
        for (Character item : characters) {
            System.out.println(item);
        }
    }
}
