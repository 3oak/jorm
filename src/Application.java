import jorm.Mapper;
import jorm.annotation.OneToMany;
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
                .SetDatabaseName("ouchtion");

        System.out.println(_config.GetConnectionURL());

        try {
            connection.OpenConnection(_config);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        MySQLQuery<Character> query = connection.CreateQuery(Character.class);

        // Queryable queryable = new Queryable().Select().Where().Or().Run();
        //Tuple<QueryType, String> tuple1 = Tuple.CreateTuple(QueryType.DELETE, "students");
        //Tuple<QueryType, String> tuple2 = Tuple.CreateTuple(QueryType.WHERE, "a = b");
        //Tuple<QueryType, String> tuple4 = Tuple.CreateTuple(QueryType.FIELD, "1, 2");
        //Tuple<QueryType, String> tuple3 = Tuple.CreateTuple(QueryType.AND, "c = d");
        Tuple<QueryType, String> tuple5 = Tuple.CreateTuple(QueryType.INSERT, "INSERT INTO TABLE");
        Tuple<QueryType, String> tuple6 = Tuple.CreateTuple(QueryType.VALUE, "VALUE");
        Tuple<QueryType, String> tuple8 = Tuple.CreateTuple(QueryType.COLUMN, "(COLUMN)");
        Tuple<QueryType, String> tuple7 = Tuple.CreateTuple(QueryType.WHERE, "WHERE");
        QueryCommand queryCommand = new QueryCommand();
        queryCommand.AddCommand(tuple5);
        queryCommand.AddCommand(tuple6);
        queryCommand.AddCommand(tuple8);
        queryCommand.AddCommand(tuple7);

        while(!queryCommand.GetCommandQueue().isEmpty()) {
            System.out.print(queryCommand.GetCommandQueue().poll().GetTail() + " ");
        }

        QueryBuilder builder = new DeleteBuilder();
        System.out.print(builder.Build(queryCommand.GetCommandQueue()));
        var character = new Character("HPQ", 10);
        var skill = new Skill("skillId1", 5, "PQ");
        var skill2 = new Skill("skillId2", 15, "PQ");
        var skill3 = new Skill("skillId3", 25, "PQ");
        var weapon = new Weapon();
        var listSkill = new ArrayList<Skill>();
        listSkill.add(skill);
        listSkill.add(skill2);
        listSkill.add(skill3);
        character.skill = listSkill;
        character.weapon = weapon;
        query.Update(character);

        var samuel = new Character("Samuel", 100, Utils.ParseDate("2000-05-19"));

        query.Insert(samuel);
    }
}
