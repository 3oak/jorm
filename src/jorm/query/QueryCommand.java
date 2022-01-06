package jorm.query;

import jorm.utils.Tuple;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class QueryCommand {
    public final Queue<Tuple<QueryType, String>> commandQueue;

    public QueryCommand(){
        commandQueue = new LinkedList<>();
    }
    public void addCommand(Tuple<QueryType, String> command){
        commandQueue.add(command);
    }
    public String executeCommands(){
        StringBuilder stringBuilder = new StringBuilder();
        for (var item : commandQueue) {
            stringBuilder.append(item.item2 + " ");
        }
        return stringBuilder.toString();
    }
    public void clear(){
        commandQueue.clear();
    }
}
