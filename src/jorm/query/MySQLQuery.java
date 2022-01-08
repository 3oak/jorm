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

import jorm.Mapper;
import jorm.annotation.OneToMany;
import jorm.annotation.OneToOne;
import jorm.clause.Clause;
import jorm.exception.DefaultConstructorNotFoundException;
import jorm.exception.InvalidSchemaException;
import jorm.utils.Triplet;
import jorm.utils.Tuple;
import jorm.utils.Utils;

@SuppressWarnings("unused")
public class MySQLQuery<T> implements Queryable<T> {
    private static Connection connection;

    private final Mapper<T> mapper;

    private final List<QueryCommand> commandList;
    private final List<Triplet<Class<?>, String, QueryCommand>> waitingPreloads;

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
    }

    @Override
    public QueryData<T> Select() throws SQLException, NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException, InvalidSchemaException {

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
        } catch (SQLException | DefaultConstructorNotFoundException e) {
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
                    if (dt.size() == 0) {
                        f.GetHead().set(d, null);
                    } else {
                        f.GetHead().set(d, dt.get(0));
                    }
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
                        QueryType.VALUES,
                        values
                )
        );

        var pairRelationshipInstanceMappers =
                mapper.GetOneToOneRelationshipInstances(dataObject);
        var valuePrimaryKey = mapper.GetDataObjectOfField(mapper.GetPrimaryKey(), dataObject);
        for (var pairRelationshipInstanceMapper : pairRelationshipInstanceMappers.entrySet()) {
            var instance = pairRelationshipInstanceMapper.getKey();
            var onetooneMapper = pairRelationshipInstanceMapper.getValue();
            var queryTriplet = onetooneMapper.DataObjectToUpdateQueryForInsert(instance, dataObject, mapper);
            var conditionQuery =
                    String.format("%s = %s",
                    onetooneMapper.GetColumnNamePrimaryKey(),
                    Utils.ToStringQueryValue(onetooneMapper.GetDataObjectOfField(onetooneMapper.GetPrimaryKey(), instance)));

            var queryCommand = new QueryCommand();
            queryCommand.AddCommand(
                    Tuple.CreateTuple(
                            QueryType.UPDATE,
                            queryTriplet.GetHead()
                    )
            );
            queryCommand.AddCommand(
                    Tuple.CreateTuple(
                            QueryType.SET,
                            queryTriplet.GetMid()
                    )
            );
            queryCommand.AddCommand(
                    Tuple.CreateTuple(
                            QueryType.WHERE,
                            conditionQuery
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
                            Utils.ToStringQueryValue(mapper.GetDataObjectOfField(mapper.GetPrimaryKey(), dataObject)));
            var instanceColumnNamePrimaryKey = onetoomanyMapper.GetColumnNamePrimaryKey();
            var instanceFieldPrimaryKey = onetoomanyMapper.GetPrimaryKey();
            for (var instance : listInstance) {
                var queryTriplet = onetoomanyMapper.DataObjectToUpdateQueryForInsert(instance, dataObject, mapper);
                var queryCommand = new QueryCommand();
                queryCommand.AddCommand(
                        Tuple.CreateTuple(
                                QueryType.UPDATE,
                                queryTriplet.GetHead()
                        )
                );
                queryCommand.AddCommand(
                        Tuple.CreateTuple(
                                QueryType.SET,
                                queryTriplet.GetMid()
                        )
                );
                queryCommand.AddCommand(
                        Tuple.CreateTuple(
                                QueryType.WHERE,
                                String.format("%s = %s",
                                        instanceColumnNamePrimaryKey,
                                        Utils.ToStringQueryValue(onetoomanyMapper.GetDataObjectOfField(instanceFieldPrimaryKey, instance)))
                        )
                );
                if(queryTriplet.GetTail()){
                    queryCommand.AddCommand(
                            Tuple.CreateTuple(
                                    QueryType.AND,
                                    whereQueryString
                            )
                    );
                }
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
            throws IllegalAccessException, InvalidSchemaException {
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
        for (var command : commandList){
            String exeQuery = command.GetExecuteQuery();
            queryList.add(exeQuery);
        }

        try (Statement statement = connection.createStatement()) {
            // Auto resource management
            connection.setAutoCommit(false);
            System.out.println(queryList.size());
            while (!queryList.isEmpty()) {
                var queryString = queryList.poll();
                System.out.println(queryString);
                statement.executeUpdate(queryString);
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
