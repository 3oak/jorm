package jorm.query;

import jorm.clause.Clause;
import jorm.exception.InvalidSchemaException;

@SuppressWarnings("unused")
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
    QueryData<T> Select();

    void Insert(T data)
            throws Exception;

    void Delete(T data)
            throws Exception;

    void Update(T data)
            throws IllegalAccessException;

}