package jorm.query.builder;

import jorm.query.QueryType;
import jorm.utils.Tuple;

import java.util.PriorityQueue;

//INSERT INTO table_name (column1, column2, column3, ...)
//        VALUES (value1, value2, value3, ...);
public class InsertBuilder implements QueryBuilder{
    @Override
    public String Build(PriorityQueue<Tuple<QueryType, String>> commandQueue) {
        return null;
    }
}

