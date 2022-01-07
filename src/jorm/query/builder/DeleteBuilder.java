package jorm.query.builder;

import jorm.query.QueryType;
import jorm.utils.Tuple;

import java.util.PriorityQueue;

//DELETE FROM table_name WHERE condition;
public class DeleteBuilder implements QueryBuilder {
    @Override
    public String Build(PriorityQueue<Tuple<QueryType, String>> commandQueue) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isWhereExisted = false;

        while (!commandQueue.isEmpty()) {
            Tuple<QueryType, String> command = commandQueue.poll();
            QueryType type = command.GetHead();
            switch (type) {
                case DELETE:
                    stringBuilder.append(type).append(" ").append("FROM").append(" ").append(command.GetTail());
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
