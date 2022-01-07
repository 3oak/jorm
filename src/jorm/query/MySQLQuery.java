package jorm.query;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import jorm.Mapper;
import jorm.annotation.ForeignKey;
import jorm.clause.Clause;
import jorm.exception.InvalidSchemaException;
import jorm.utils.Tuple;

public class MySQLQuery<T> implements Queryable<T> {
    private static Connection connection;

    private final Class<T> genericClass;
    private final Mapper<T> mapper;
    private final ArrayList<T> dataList;

    private final List<QueryCommand> commandList;
    private final List<Tuple<String, QueryCommand>> waitingPreloads;

    public MySQLQuery(Class<T> genericClass, Connection connection)
            throws RuntimeException {
        if (MySQLQuery.connection == null)
            MySQLQuery.connection = connection;

        this.mapper = new Mapper<>(genericClass, this::OnAddRelationshipQuery);
        this.genericClass = genericClass;
        this.dataList = new ArrayList<>();
        this.commandList = new ArrayList<>();
        this.waitingPreloads = new ArrayList<>();

        // Default command
        commandList.add(new QueryCommand());
    }

    @Override
    public MySQLQuery<T> SelectAll() {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.SELECT,
                        mapper.GetTableName() // QueryCommand will add selected fields. Ex: SELECT <fields> FROM <table name> WHERE ...
                        // If no FIELDS is added, then SELECT *
                )
        );

        return this;
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

        return null;
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
    public MySQLQuery<T> Filter(Predicate<T> predicate) {
        return this;
    }

    @Override
    public MySQLQuery<T> Insert(T dataObject)
            throws IllegalAccessException {
        // TODO:
        // - Consider relationship mapping annotation: 1-1; 1-n, n-1
        // - Add data to corresponding tables with appropriate constraints
        mapper.Insert(dataObject);

        return this;
    }

    @Override
    public MySQLQuery<T> Delete(T data) {
        commandList.get(0).AddCommand(
                Tuple.CreateTuple(
                        QueryType.DELETE,
                        mapper.GetTableName()
                )
        );

        return this;
    }

    @Override
    public MySQLQuery<T> InsertOrUpdate(T data) {
        return null;
    }

    @Override
    public MySQLQuery<T> Update(T data) throws IllegalAccessException {
        QueryCommand queryCommand = mapper.DataObjectToQueryCommand(data);
        if(queryCommand == null)
            return this;
        //  TODO: Add to query command list
        // TODO: Update data using mapper to map data to hash map that key : column & value : value

        //command.AddCommand(Tuple.CreateTuple(QueryType.UPDATE, updateQuery));
        System.out.println(queryCommand.ExecuteCommands());
        return this;
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

    @Override
    public List<T> ToList() {
        return dataList;
    }

    private void OnAddRelationshipQuery(QueryCommand queryCommand){
        // TODO: Add to list QueryCommand
        System.out.println(queryCommand.ExecuteCommands());
    }
}
