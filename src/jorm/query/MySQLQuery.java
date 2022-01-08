package jorm.query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

    //private final List<String> queryList;

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

        //this.queryList = new ArrayList<>();
        this.executor = new Executor(connection);
    }

    @Override
    public QueryData<T> Select() throws SQLException, NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvalidSchemaException {
//        for (Triplet<Class<?>, String, QueryCommand> test : waitingPreloads) {
//            System.out.println(test.GetMid());
//        }

        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.SELECT,
                        mapper.GetTableName()
                )
        );

        ArrayList<T> data = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            // Auto resource management
            ResultSet resultSet = statement.executeQuery(commandList.get(0).GetExecuteQuery());
            while (resultSet.next()) {
                T object = mapper.ToDataObject(resultSet);
                data.add(object);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Get primary key
        Field primaryKey = this.mapper.GetPrimaryKey();
        primaryKey.setAccessible(true);

        for (var d : data) {
            String primaryKeyValue = (String) primaryKey.get(d);

            for (var preload : waitingPreloads) {
                QueryCommand command = preload.GetTail().clone();
                command.AddCommand(Tuple.CreateTuple(QueryType.WHERE, preload.GetMid() + " = " + "'" + primaryKeyValue + "'"));

                var dt = new ArrayList<>();

                try (Statement statement = connection.createStatement()) {
                    // Auto resource management
                    ResultSet rs = statement.executeQuery(command.GetExecuteQuery());
                    while (rs.next()) {
                        var sub_mapper = new Mapper<>(preload.GetHead(), null);
                        var object = preload.GetHead().cast(preload.GetHead().getConstructor().newInstance());
                        object = sub_mapper.ToDataObject(rs);
                        dt.add(object);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return new QueryData<>(data);
                }
//
                Tuple<Field, String> f = this.mapper.GetFieldWithRelationship(preload.GetHead());
                if (f.GetTail().equals(OneToOne.class.getName())) {
                    f.GetHead().set(d, dt.get(0));
                } else if (f.GetTail().equals(OneToMany.class.getName())) {
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
            var instance = pairRelationshipInstanceMapper.getKey();
            var onetooneMapper = pairRelationshipInstanceMapper.getValue();

            var queryTuple = onetooneMapper.DataObjectToUpdateQuery(instance);
            var queryCommand = new QueryCommand();
            queryCommand.AddCommand(
                    Tuple.CreateTuple(
                            QueryType.UPDATE,
                            queryTuple.GetHead()
                    )
            );
            queryCommand.AddCommand(
                    Tuple.CreateTuple(
                            QueryType.SET,
                            queryTuple.GetTail()
                    )
            );

            commandList.add(queryCommand);
        }

        // TODO: Add OneToMany Relationship handling feature HERE!!!
        var pairRelationshipListInstanceMappers =
                mapper.GetOneToManyRelationshipInstances(dataObject);
        for (var pairRelationshipListInstanceMapper : pairRelationshipListInstanceMappers.entrySet()){
            var listInstance = pairRelationshipListInstanceMapper.getKey();
            var onetoomanyMapper = pairRelationshipListInstanceMapper.getValue();
            var whereQueryString =
                            String.format("%s = %s",
                            onetoomanyMapper.GetColumnNameForeignKeyOfType(mapper.GetGenericClass()),
                            mapper.GetDataObjectOfField(mapper.GetPrimaryKey(), dataObject));
            var instanceColumnNamePrimaryKey =onetoomanyMapper.GetColumnNamePrimaryKey();
            var instanceFieldPrimaryKey = onetoomanyMapper.GetPrimaryKey();
            for (var instance : listInstance) {
                var queryTuple = onetoomanyMapper.DataObjectToUpdateQuery(instance);
                var queryCommand = new QueryCommand();
                queryCommand.AddCommand(
                        Tuple.CreateTuple(
                                QueryType.UPDATE,
                                queryTuple.GetHead()
                        )
                );
                queryCommand.AddCommand(
                        Tuple.CreateTuple(
                                QueryType.SET,
                                queryTuple.GetTail()
                        )
                );
                queryCommand.AddCommand(
                        Tuple.CreateTuple(
                                QueryType.WHERE,
                                whereQueryString
                        )
                );
                queryCommand.AddCommand(
                        Tuple.CreateTuple(
                                QueryType.AND,
                                String.format("%s = %s",
                                        instanceColumnNamePrimaryKey,
                                        onetoomanyMapper.GetDataObjectOfField(instanceFieldPrimaryKey, instance))
                        )
                );
                commandList.add(queryCommand);
            }
        }
        ExecuteCommandQuery();
    }

    @Override
    public void Delete(T data) {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.DELETE,
                        mapper.GetTableName()
                )
        );
        ExecuteCommandQuery();
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
        ExecuteCommandQuery();
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
        Mapper<N> mapper = new Mapper<>(hasRelationshipWith, null);
        command.AddCommand(Tuple.CreateTuple(
                QueryType.SELECT,
                mapper.GetTableName()
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
    }

    private void ExecuteCommandQuery(){
        var queryList = new LinkedList<String>();
        for (var command : commandList)
            queryList.add(command.GetExecuteQuery());

        try (Statement statement = connection.createStatement()) {
            // Auto resource management
            connection.setAutoCommit(false);
            while (!queryList.isEmpty()) {
                statement.executeUpdate(queryList.remove());
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

    public void GetAllCommand() {
        for (var command : commandList) {
            System.out.println(command.GetExecuteQuery());
        }
    }
}
