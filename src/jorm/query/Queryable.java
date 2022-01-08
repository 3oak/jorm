package jorm.query;

import jorm.clause.Clause;
import jorm.exception.InvalidSchemaException;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface Queryable<T> {
    // Chaining methods
    Queryable<T> Where(String queryString);

    Queryable<T> Where(Clause clauses);

    Queryable<T> And(Clause clauses);

    Queryable<T> Or(Clause clauses);

    <N> Queryable<T> Preload(Class<N> hasRelationshipWith)
            throws InvalidSchemaException;

    Queryable<T> Pick(String[] fields);

    // Finalizing methods
    QueryData<T> Select() throws Exception;

    void Insert(T data)
            throws Exception;

    void Delete(T data)
            throws Exception;

    void Update(T data)
            throws IllegalAccessException, InvalidSchemaException;

}