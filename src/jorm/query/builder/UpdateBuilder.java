package jorm.query.builder;

import jorm.query.QueryType;
import jorm.utils.Tuple;

import java.util.PriorityQueue;

//UPDATE table_name
//        SET column1 = value1, column2 = value2, ...
//        WHERE condition;
public class UpdateBuilder implements QueryBuilder {
    @Override
    public String Build(PriorityQueue<Tuple<QueryType, String>> commandQueue) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isWhereExisted = false;
        boolean isSetExisted = false;

        while (!commandQueue.isEmpty()) {
            Tuple<QueryType, String> command = commandQueue.poll();
            QueryType type = command.GetHead();
            switch (type) {
                case UPDATE:
                    stringBuilder.append(type).append(" ").append(command.GetTail());
                    break;
                case SET:
                    if (!isSetExisted) {
                        stringBuilder.append(" ").append(type).append(" ").append(command.GetTail());
                        isSetExisted = true;
                    }
                    break;
                case WHERE:
                    if (!isWhereExisted) {
                        stringBuilder.append(" ").append(type).append(" ").append(command.GetTail());
                        isWhereExisted = true;
                    } else {
                        stringBuilder.append(" ").append("AND").append(" ").append(command.GetTail());
                    }
                    break;
                case OR:
                case AND:
                    stringBuilder.append(" ").append(type).append(" ").append(command.GetTail());
                    break;
            }
        }

        return stringBuilder.toString();
    }
}