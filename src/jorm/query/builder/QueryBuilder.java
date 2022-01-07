package jorm.query.builder;

import jorm.query.QueryType;
import jorm.utils.Tuple;

import java.util.PriorityQueue;

public interface QueryBuilder {
    String Build(PriorityQueue<Tuple<QueryType, String>> commandQueue);
}
