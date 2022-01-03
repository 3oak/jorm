import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import jorm.annotation.Column;
import jorm.connection.ConnectionFactory;
import jorm.connection.MySQLConnection;
import jorm.connection.configuration.MySQLConfiguration;
import jorm.query.SqlQuery;

public class Application {
    public static void main(String[] args) {
        // ** Create MySQL Connection
        // MySQLConnection connection = ConnectionFactory.createConnection(MySQLConnection.class);

        // ** Connect to MySQL Database
        /* try {
            // --- Solution 1:
            connection.OpenConnection(
                    "jdbc:mysql://localhost:3306/sakila"
            );

            // --- Solution 2:
            connection.OpenConnection(
                    "jdbc:mysql://localhost:3306/sakila",
                    "root", "root"
            );

            // --- Solution 3:
            MySQLConfiguration _config = new MySQLConfiguration();
            _config
                    .setHostName("localhost")
                    .setPort("3306")
                    .setDatabaseName("sakila")
                    .setProperty("profileSQL", "true");

            connection.OpenConnection(_config);

        } catch (Exception ignore) {

        } */

        // ** Create query from connection
        /* SqlQuery<Character> sqlQuery = connection.CreateQuery(Character.class);
        List<Character> characters = sqlQuery.ToList();
        for (Character item : characters) {
            System.out.println(item);
        } */

        MySQLConfiguration _config = new MySQLConfiguration();
        _config
                .setUsername("root")
                .setPassword("root")
                .setHostName("localhost");

        _config
                .setPort("3306")
                .setDatabaseName("sakila")
                .setProperty("key1", "value1")
                .setProperty("key2", "value2");

        LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        properties.put("key3", "value3");
        properties.put("key4", "value4");
        properties.put("key5", "value5");

        _config.setProperties(properties);

        System.out.println(_config.getConnectionURL());
    }
}
