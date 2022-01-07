package jorm.query;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import jorm.Mapper;
import jorm.annotation.ForeignKey;
import jorm.clause.Clause;
import jorm.exception.InvalidSchemaException;
import jorm.utils.Tuple;

public class MySQLQuery<T> implements Queryable<T> {
    private static Connection connection;

    private final Class<T> genericClass;
    private final Mapper<T> mapper;

    private final List<QueryCommand> commandList;
    private final List<Tuple<String, QueryCommand>> waitingPreloads;

    private final List<String> queryList;

    public MySQLQuery(Class<T> genericClass, Connection connection)
            throws RuntimeException {
        if (MySQLQuery.connection == null)
            MySQLQuery.connection = connection;

        this.mapper = new Mapper<>(genericClass, this::OnAddRelationshipQuery);
        this.genericClass = genericClass;
        this.commandList = new ArrayList<>();
        this.waitingPreloads = new ArrayList<>();

        // Default command
        commandList.add(new QueryCommand());

        this.queryList = new ArrayList<>();
    }

    @Override
    public QueryData<T> Select() {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.SELECT,
                        mapper.GetTableName()
                )
        );

        queryList.add(commandList.get(0).GetExecuteQuery());

        // TODO: Execute the main query and get primary key
        String primaryKey = "";

        for (var preload : waitingPreloads) {
            QueryCommand command = preload.GetTail();
            command.AddCommand(Tuple.CreateTuple(QueryType.WHERE, preload.GetHead() + " = " + primaryKey));
            commandList.add(command);
            queryList.add(command.GetExecuteQuery());
        }
        // Run all preload command
        GetAllQueries();

        return new QueryData<>(new ArrayList<>());
//        ResultSet resultSet = null;
//
//        // TODO: Get ResultSet
//
//        try {
//            if (resultSet == null)
//                throw new RuntimeException(String.format("%s: Database load fail", genericClass.getName()));
//            while (resultSet.next()) {
//                T data = mapper.Map(resultSet);
//                dataList.add(data);
//            }
//        } catch (SQLException
//                | NoSuchFieldException
//                | InstantiationException
//                | IllegalAccessException
//                | InvocationTargetException
//                | NoSuchMethodException e) {
//            e.printStackTrace();
//        }
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
            throws IllegalAccessException {
        // TODO:
        // - Consider relationship mapping annotation: 1-1; 1-n, n-1
        // - Add data to corresponding tables with appropriate constraints
        mapper.Insert(dataObject);

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
    public void Update(T data) throws IllegalAccessException {
        QueryCommand queryCommand = mapper.DataObjectToQueryCommand(data);
        if (queryCommand == null)
            return;
        //  TODO: Add to query command list
        // TODO: Update data using mapper to map data to hash map that key : column & value : value

        //command.AddCommand(Tuple.CreateTuple(QueryType.UPDATE, updateQuery));
        System.out.println(queryCommand.GetExecuteQuery());
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

        // Get table name (OR set the one on mapper to static)
        String tableName = this.mapper.GetTableName();

        // Get field
        Field field = null;
        for (Field f : hasRelationshipWith.getDeclaredFields()) {
            if (f.isAnnotationPresent(ForeignKey.class) && f.getAnnotation(ForeignKey.class).tableName().equals(tableName)) {
                field = f;
            }
        }

        if (field == null) {
            throw new InvalidSchemaException("ForeignKey", hasRelationshipWith.getName());
        }

        // Get column name
        String foreignKeyName = Mapper.getColumnName(field);

        // After the main SELECT, get the <primary key>. Then add a WHERE with clause foreignKeyName = <primaryKey>
        // Preloads will then be a list, do the same to all elements => queries
        waitingPreloads.add(Tuple.CreateTuple(foreignKeyName, command));

        return this;
    }

    private void OnAddRelationshipQuery(QueryCommand queryCommand) {
        // TODO: Add to list QueryCommand
        System.out.println(queryCommand.GetExecuteQuery());
    }

    private void GetAllQueries() {
        for (var query: queryList) {
            System.out.println(query);
        }
    }
}
