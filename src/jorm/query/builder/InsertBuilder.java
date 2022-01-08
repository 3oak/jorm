package jorm.query.builder;

import jorm.query.QueryType;
import jorm.utils.Tuple;

import java.util.PriorityQueue;

//INSERT INTO table_name (column1, column2, column3, ...)
//        VALUES (value1, value2, value3, ...);
public class InsertBuilder implements QueryBuilder {
    @Override
    public String Build(PriorityQueue<Tuple<QueryType, String>> commandQueue) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isColumnExisted = false;
        boolean isValueExisted = false;

        while (!commandQueue.isEmpty()) {
            Tuple<QueryType, String> command = commandQueue.poll();
            QueryType type = command.GetHead();
            switch (type) {
                case INSERT:
                    stringBuilder.append(type).append(" ").append("INTO").append(" ").append(command.GetTail());
                    break;
                case COLUMN:
                    if (!isColumnExisted) {
                        stringBuilder.append(" ").append("(").append(command.GetTail()).append(")");
                        isColumnExisted = true;
                    }
                    break;
                case VALUES:
                    if (!isValueExisted) {
                        stringBuilder.append(" ").append(type).append(" ").append("(").append(command.GetTail()).append(")");
                        isValueExisted = true;
                    }
                    break;
            }
        }

        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }
}

