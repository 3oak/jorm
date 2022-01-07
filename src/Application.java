import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import jorm.clause.ComparisonOperator;
import jorm.clause.Expression;
import jorm.connection.ConnectionFactory;
import jorm.connection.MySQLConnection;
import jorm.connection.configuration.MySQLConfiguration;
import jorm.query.MySQLQuery;
import jorm.query.QueryCommand;
import jorm.query.QueryType;
import jorm.query.builder.DeleteBuilder;
import jorm.query.builder.QueryBuilder;
import jorm.query.builder.SelectBuilder;
import jorm.utils.Tuple;

public class Application {
    public static void main(String[] args)
            throws IllegalAccessException {
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

        MySQLConnection connection = ConnectionFactory.createConnection(MySQLConnection.class);

        MySQLConfiguration _config = new MySQLConfiguration();
        _config
                .SetUsername("root")
                .SetPassword("root")
                .SetHostName("localhost");

        _config
                .SetPort("3306")
                .SetDatabaseName("ouchtion");

        System.out.println(_config.GetConnectionURL());

        try {
            connection.OpenConnection(_config);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        MySQLQuery<Character> query = connection.CreateQuery(Character.class);

        Character samuel = new Character("Samuel", 100);

        query.Insert(samuel);

        // Queryable queryable = new Queryable().Select().Where().Or().Run();
        Tuple<QueryType, String> tuple1 = Tuple.CreateTuple(QueryType.DELETE, "students");
        Tuple<QueryType, String> tuple2 = Tuple.CreateTuple(QueryType.WHERE, "a = b");
        Tuple<QueryType, String> tuple4 = Tuple.CreateTuple(QueryType.FIELD, "1, 2");
        Tuple<QueryType, String> tuple3 = Tuple.CreateTuple(QueryType.AND, "c = d");
        QueryCommand queryCommand = new QueryCommand();
        queryCommand.AddCommand(tuple2);
        queryCommand.AddCommand(tuple3);
        queryCommand.AddCommand(tuple4);
        queryCommand.AddCommand(tuple1);

        QueryBuilder builder = new DeleteBuilder();
        System.out.print(builder.Build(queryCommand.GetCommandQueue()));
    }
}
