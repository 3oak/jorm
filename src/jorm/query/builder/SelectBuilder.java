package jorm.query.builder;

import jorm.query.QueryType;
import jorm.utils.Tuple;

import java.util.PriorityQueue;

//SELECT column1, column2, ...
//        FROM table_name
//        WHERE condition;
public class SelectBuilder implements QueryBuilder {
    @Override
    public String Build(PriorityQueue<Tuple<QueryType, String>> commandQueue) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isWhereExisted = false;
        boolean isFieldExisted = false;

        while (!commandQueue.isEmpty()) {
            Tuple<QueryType, String> command = commandQueue.poll();
            QueryType type = command.GetHead();
            switch (type) {
                case SELECT:
                    stringBuilder.append(type).append(" ").append("FROM").append(" ").append(command.GetTail());
                    break;
                case FIELD:
                    if (!isFieldExisted) {
                        stringBuilder.insert(6, " " + command.GetTail());
                        isFieldExisted = true;
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

        if (!isFieldExisted) {
            stringBuilder.insert(6, " *");
        }

        return stringBuilder.toString();
    }
}
