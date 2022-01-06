package jorm.query;

import jorm.utils.Tuple;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("unused")
public class QueryCommand {
    public final Queue<Tuple<QueryType, String>> commandQueue;

    public QueryCommand() {
        commandQueue = new LinkedList<>();
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
