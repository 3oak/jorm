package jorm.query;

import jorm.query.builder.QueryBuilder;
import jorm.utils.Tuple;
import jorm.utils.TupleComparator;

import java.util.*;

@SuppressWarnings("unused")
public class QueryCommand {
    private final PriorityQueue<Tuple<QueryType, String>> commandQueue;
    private QueryBuilder queryBuilder;

    public QueryCommand() {
        this.commandQueue = new PriorityQueue<>(new TupleComparator());
    }

    public QueryCommand(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        this.commandQueue = new PriorityQueue<>(new TupleComparator());
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public void AddCommand(Tuple<QueryType, String> command) {
        commandQueue.add(command);
    }

    public PriorityQueue<Tuple<QueryType, String>> GetCommandQueue() {
        return commandQueue;
    }

    public String GetExecuteQuery() {
        return queryBuilder.Build(this.commandQueue);
    }

    public void Clear() {
        commandQueue.clear();
    }
}

