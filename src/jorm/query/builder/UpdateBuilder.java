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
        return null;
    }
}