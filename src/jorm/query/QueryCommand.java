package jorm.query;

import jorm.query.builder.*;
import jorm.utils.Tuple;
import jorm.utils.TupleComparator;

import java.util.*;

@SuppressWarnings("unused")
public class QueryCommand {
    private final PriorityQueue<Tuple<QueryType, String>> commandQueue;
    private QueryCommandType queryCommandType;

    public QueryCommand() {
        this.commandQueue = new PriorityQueue<>(new TupleComparator());
    }

    public QueryCommand(QueryCommandType queryCommandType) {
        this.queryCommandType = queryCommandType;
        this.commandQueue = new PriorityQueue<>(new TupleComparator());
    }

    public void AddCommand(Tuple<QueryType, String> command) {
        switch (command.GetHead()) {
            case DELETE:
                this.queryCommandType = QueryCommandType.DELETE;
                break;
            case SELECT:
                this.queryCommandType = QueryCommandType.SELECT;
                break;
            case UPDATE:
                this.queryCommandType = QueryCommandType.UPDATE;
                break;
            case INSERT:
                this.queryCommandType = QueryCommandType.INSERT;
                break;
        }
        commandQueue.add(command);
    }

    public PriorityQueue<Tuple<QueryType, String>> GetCommandQueue() {
        return commandQueue;
    }

    public String GetExecuteQuery() {
        QueryBuilder builder = null;
        switch (queryCommandType) {
            case DELETE:
                builder = new DeleteBuilder();
                break;
            case SELECT:
                builder = new SelectBuilder();
                break;
            case INSERT:
                builder = new InsertBuilder();
                break;
            case UPDATE:
                builder = new UpdateBuilder();
                break;
        }
        return builder.Build(this.commandQueue);
    }

    public void Clear() {
        commandQueue.clear();
    }
}

