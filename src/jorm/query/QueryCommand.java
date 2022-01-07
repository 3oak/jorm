package jorm.query;

import jorm.utils.Tuple;

import java.util.*;

@SuppressWarnings("unused")
public class QueryCommand {
    public final PriorityQueue<Tuple<QueryType, String>> commandQueue;

    public QueryCommand() {
        commandQueue = new PriorityQueue<>(new TupleComparator());
    }

    public void AddCommand(Tuple<QueryType, String> command) {
        commandQueue.add(command);
    }

    public String ExecuteCommands() {
        StringBuilder stringBuilder = new StringBuilder();

        for (var item : commandQueue) {
            stringBuilder.append(item.item2).append(" ");
        }

        return stringBuilder.toString();
    }

    public void Clear() {
        commandQueue.clear();
    }
}

class TupleComparator implements Comparator<Tuple<QueryType, String>> {

    @Override
    public int compare(Tuple<QueryType, String> o1, Tuple<QueryType, String> o2) {
        QueryLevel queryLevel1 = null, queryLevel2 = null;

        switch (o1.item1) {
            case SELECT:
            case UPDATE:
            case INSERT:
            case DELETE:
                queryLevel1 = QueryLevel.COMMANDS;
                break;
            case WHERE:
                queryLevel1 = QueryLevel.WHERE;
                break;
            case AND:
            case OR:
                queryLevel1 = QueryLevel.CONDITION;
                break;
            case FIELDS:
                queryLevel1 = QueryLevel.FIELD;
                break;
        }

        switch (o2.item1) {
            case SELECT:
            case UPDATE:
            case INSERT:
            case DELETE:
                queryLevel2 = QueryLevel.COMMANDS;
                break;
            case WHERE:
                queryLevel2 = QueryLevel.WHERE;
                break;
            case AND:
            case OR:
                queryLevel2 = QueryLevel.CONDITION;
                break;
            case FIELDS:
                queryLevel2 = QueryLevel.FIELD;
                break;
        }

        if (queryLevel1.ordinal() < queryLevel2.ordinal()) {
            return 1;
        } else if (queryLevel1.ordinal() > queryLevel2.ordinal()) {
            return -1;
        } else {
            return 0;
        }
    }

}

enum QueryLevel {
    CONDITION,
    WHERE,
    FIELD,
    COMMANDS,
}
