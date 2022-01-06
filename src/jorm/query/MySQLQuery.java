package jorm.query;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import jorm.Mapper;
import jorm.clause.Clause;
import jorm.utils.Tuple;

public class MySQLQuery<T> implements Queryable<T> {
    private static Connection connection;

    private final Mapper<T> mapper;
    private final ArrayList<T> dataList;

    private final QueryCommand command;

    public MySQLQuery(Class<T> genericClass, Connection connection)
            throws RuntimeException {
        if (MySQLQuery.connection == null)
            MySQLQuery.connection = connection;

        this.mapper = new Mapper<>(genericClass);
        this.dataList = new ArrayList<>();
        this.command = new QueryCommand();
    }

    @Override
    public MySQLQuery<T> SelectAll() {
        command.AddCommand(
                Tuple.CreateTuple(
                        QueryType.SELECT,
                        String.format("select * from %s", mapper.GetTableName())
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
    public Queryable<T> Where(String queryString) {
        command.AddCommand(
                Tuple.CreateTuple(
                        QueryType.WHERE,
                        queryString
                )
        );

        return null;
    }

    @Override
    public Queryable<T> Where(Clause clauses) {
        command.AddCommand(
                Tuple.CreateTuple(
                        QueryType.WHERE,
                        String.format("where %s", clauses.ToQueryStringClause())
                )
        );

        return this;
    }

    @Override
    public Queryable<T> And(Clause clauses) {
        command.AddCommand(
                Tuple.CreateTuple(
                        QueryType.WHERE,
                        String.format("and %s", clauses.ToQueryStringClause())
                )
        );

        return this;
    }

    @Override
    public Queryable<T> Or(Clause clauses) {
        command.AddCommand(
                Tuple.CreateTuple(
                        QueryType.WHERE,
                        String.format("or %s", clauses.ToQueryStringClause())
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
    public Queryable<T> InsertOrUpdate(T data) {
        return null;
    }

    @Override
    public Queryable<T> Update(T data) {
        // TODO: Update data using mapper to map data to hash map that key : column & value : value

        return null;
    }

    @Override
    public Queryable<T> Execute() {
        System.out.println(command.ExecuteCommands());

        return this;
    }

    @Override
    public List<T> ToList() {
        return dataList;
    }
}
