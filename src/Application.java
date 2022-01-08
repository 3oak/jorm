import jorm.Mapper;
import jorm.annotation.OneToMany;
import jorm.clause.Clause;
import jorm.connection.ConnectionFactory;
import jorm.connection.MySQLConnection;
import jorm.connection.configuration.MySQLConfiguration;
import jorm.query.MySQLQuery;
import jorm.query.QueryCommand;
import jorm.query.QueryType;
import jorm.query.builder.DeleteBuilder;
import jorm.query.builder.QueryBuilder;
import jorm.utils.AnnotationValidationUtils;
import jorm.utils.Tuple;
import jorm.utils.Utils;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class Application {
    public static void main(String[] args)
            throws Exception {
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
        // MySQLQuery<Character> query = connection.CreateQuery(Character.class);

        MySQLConnection connection = ConnectionFactory.CreateConnection(MySQLConnection.class);

        MySQLConfiguration _config = new MySQLConfiguration();
        _config
                .SetUsername("root")
                .SetPassword("root")
                .SetHostName("localhost");

        _config
                .SetPort("3306")
                .SetDatabaseName("test");

        System.out.println(_config.GetConnectionURL());

        try {
            connection.OpenConnection(_config);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        MySQLQuery<Character> query = connection.CreateQuery(Character.class);

        ArrayList<Weapon> weapons = new ArrayList<>();
        weapons.add(new Weapon("5", "THAI"));
        Character character = new Character("THAI", new Skill("4", "THAI"), weapons);
        query.Insert(character);

//        List<Character> characters = query.Where("char_name = 'CHUNLI'").Select().ToList();
//        for (Character character : characters) {
//            System.out.println(character.toString());
//        }
    }
}
