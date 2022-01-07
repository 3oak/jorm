package jorm.query;

import jorm.utils.Tuple;
import jorm.utils.TupleComparator;

import java.util.*;

@SuppressWarnings("unused")
public class QueryCommand {
    private final PriorityQueue<Tuple<QueryType, String>> commandQueue;

    public QueryCommand() {
        commandQueue = new PriorityQueue<>(new TupleComparator());
    }

    public void AddCommand(Tuple<QueryType, String> command) {
        commandQueue.add(command);
    }

    public PriorityQueue<Tuple<QueryType, String>> GetCommandQueue() {
        return commandQueue;
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

