package jorm.query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jorm.Mapper;
import jorm.annotation.OneToMany;
import jorm.annotation.OneToOne;
import jorm.clause.Clause;
import jorm.exception.InvalidSchemaException;
import jorm.query.executor.Executor;
import jorm.utils.Triplet;
import jorm.utils.Tuple;

@SuppressWarnings("unused")
public class MySQLQuery<T> implements Queryable<T> {
    private static Connection connection;

    private final Mapper<T> mapper;

    private final List<QueryCommand> commandList;
    private final List<Triplet<Class<?>, String, QueryCommand>> waitingPreloads;

    private final List<String> queryList;

    private final Executor executor;

    public MySQLQuery(Class<T> genericClass, Connection connection)
            throws RuntimeException {
        if (MySQLQuery.connection == null)
            MySQLQuery.connection = connection;

        this.mapper = new Mapper<>(genericClass, this::OnAddRelationshipQuery);

        this.commandList = new ArrayList<>();
        this.waitingPreloads = new ArrayList<>();

        // Default command
        commandList.add(new QueryCommand());

        this.queryList = new ArrayList<>();
        this.executor = new Executor(connection);
    }

    @Override
    public QueryData<T> Select() throws SQLException, NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvalidSchemaException {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.SELECT,
                        mapper.GetTableName()
                )
        );

        ResultSet resultSet = executor.ExecuteQuery(commandList.get(0).GetExecuteQuery());

        ArrayList<T> data = new ArrayList<>();

        while (resultSet.next()) {
            T object = mapper.ToDataObject(resultSet);
            data.add(object);
        }

        // Get primary key
        Field primaryKey = this.mapper.GetPrimaryKey();
        primaryKey.setAccessible(true);

        for (var d : data) {
            String primaryKeyValue = (String) primaryKey.get(d);

            for (var preload : waitingPreloads) {
                QueryCommand command = preload.GetTail();
                command.AddCommand(Tuple.CreateTuple(QueryType.WHERE, preload.GetMid() + " = " + primaryKeyValue));

                ResultSet rs = executor.ExecuteQuery(command.GetExecuteQuery());

                ArrayList<T> dt = new ArrayList<>();

                while (rs.next()) {
                    T object = mapper.ToDataObject(resultSet);
                    dt.add(object);
                }

                Tuple<Field, String> f = this.mapper.GetFieldWithRelationship(preload.GetHead());
                if (f.GetTail().equals(OneToOne.class.toString())) {
                    f.GetHead().set(d, dt.get(0));
                } else if (f.GetTail().equals(OneToMany.class.toString())) {
                    f.GetHead().set(d, dt);
                }
            }
        }

        return new QueryData<>(data);
    }

    @Override
    public MySQLQuery<T> Where(String queryString) {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.WHERE,
                        queryString
                )
        );

        return this;
    }

    @Override
    public MySQLQuery<T> Where(Clause clauses) {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.WHERE,
                        clauses.ToQueryStringClause()
                )
        );

        return this;
    }

    @Override
    public MySQLQuery<T> And(Clause clauses) {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.WHERE,
                        clauses.ToQueryStringClause()
                )
        );

        return this;
    }

    @Override
    public MySQLQuery<T> Or(Clause clauses) {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.WHERE,
                        clauses.ToQueryStringClause()
                )
        );

        return this;
    }

    @Override
    public void Insert(T dataObject)
            throws Exception {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.INSERT,
                        mapper.GetTableName()
                )
        );

        var pairColumnValue = mapper.GetPairColumnValue(dataObject);
        String columns = pairColumnValue.GetHead(),
                values = pairColumnValue.GetTail();

        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.COLUMN,
                        columns
                )
        );

        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.VALUE,
                        values
                )
        );

        var pairRelationshipInstanceMappers =
                mapper.GetOneToOneRelationshipInstances(dataObject);
        for (var pairRelationshipInstanceMapper : pairRelationshipInstanceMappers.entrySet()) {
            var onetooneMapper = pairRelationshipInstanceMapper.getValue();


        }

        // var queryUpdate = mapper.DataObjectToQueryCommand();
        // commandList.add(queryUpdate);
    }

    @Override
    public void Delete(T data) {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.DELETE,
                        mapper.GetTableName()
                )
        );

        commandList.get(0).GetExecuteQuery();
    }

    @Override
    public void Update(T data)
          throws IllegalAccessException {
        var query = mapper.DataObjectToUpdateQuery(data);
        if(query == null)
            return;
        //  TODO: Add to query command list
        var tableName = query.GetHead();
        var setQuery = query.GetTail();
        commandList.get(0).AddCommand(Tuple.CreateTuple(QueryType.UPDATE, tableName));
        commandList.get(0).AddCommand(Tuple.CreateTuple(QueryType.SET, setQuery));
        System.out.println(commandList.get(0).GetExecuteQuery());
    }

    @Override
    public MySQLQuery<T> Pick(String[] fields) {
        String picker = String.join(", ", fields);
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.FIELD,
                        picker
                )
        );

        return this;
    }

    public <N> MySQLQuery<T> Preload(Class<N> hasRelationshipWith) throws InvalidSchemaException {
        QueryCommand command = new QueryCommand();

        // Setup SELECT * (without FIELDS)
        command.AddCommand(Tuple.CreateTuple(
                QueryType.SELECT,
                hasRelationshipWith.getName()
        ));

        // Get field
        Field field = this.mapper.GetForeignKey(hasRelationshipWith);

        // Get column name
        String foreignKeyName = Mapper.GetColumnName(field);

        // After the main SELECT, get the <primary key>. Then add a WHERE with clause foreignKeyName = <primaryKey>
        // Preloads will then be a list, do the same to all elements => queries
        waitingPreloads.add(Triplet.CreateTriplet(hasRelationshipWith, foreignKeyName, command));

        return this;
    }


    private void OnAddRelationshipQuery(Triplet<String, String, String> query) {
        if (query == null)
            return;
        //  TODO: Add to query command list
        var tableName = query.GetHead();
        var setQuery = query.GetMid();
        var whereQuery = query.GetTail();
        QueryCommand command = new QueryCommand();
        command.AddCommand(Tuple.CreateTuple(QueryType.UPDATE, tableName));
        command.AddCommand(Tuple.CreateTuple(QueryType.SET, setQuery));
        command.AddCommand(Tuple.CreateTuple(QueryType.WHERE, whereQuery));
        commandList.add(command);
        System.out.println(command.GetExecuteQuery());
    }

    private void GetAllQueries() {
        for (var query : queryList) {
            System.out.println(query);
        }
    }
}
