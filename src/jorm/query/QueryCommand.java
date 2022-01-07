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
            stringBuilder.append(item.GetTail()).append(" ");
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
        QueryLevel
                queryLevel1 = GetHeadObject(o1),
                queryLevel2 = GetHeadObject(o2);

        return Integer.compare(Objects.requireNonNull(queryLevel2).ordinal(), Objects.requireNonNull(queryLevel1).ordinal());
    }

    private QueryLevel GetHeadObject(Tuple<QueryType, String> o) {
        switch (o.GetHead()) {
            case SELECT:
            case UPDATE:
            case INSERT:
            case DELETE:
                return QueryLevel.COMMANDS;
            case WHERE:
                return QueryLevel.WHERE;
            case AND:
            case OR:
                return QueryLevel.CONDITION;
            case FIELDS:
                return QueryLevel.FIELD;
        }
        return null;
    }
}

enum QueryLevel {
    CONDITION,
    WHERE,
    FIELD,
    COMMANDS,
}
