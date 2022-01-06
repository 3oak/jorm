package jorm.query;

import jorm.clause.Clause;
import jorm.exception.InvalidSchemaException;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public interface Queryable<T> {
    Queryable<T> SelectAll();

    Queryable<T> Where(String queryString);

    Queryable<T> Where(Clause clauses);

    Queryable<T> And(Clause clauses);

    Queryable<T> Or(Clause clauses);

    Queryable<T> Filter(Predicate<T> predicate);

    Queryable<T> Insert(T data)
            throws Exception;

    Queryable<T> Delete(T data)
            throws Exception;

    Queryable<T> InsertOrUpdate(T data);

    Queryable<T> Update(T data);

    Queryable<T> Preload(Class<T> hasRelationshipWith) throws InvalidSchemaException;

    Queryable<T> Pick(String[] fields);

    List<T> ToList();
}

