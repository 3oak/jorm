package jorm.query;

import jorm.clause.Clause;

import java.util.List;
import java.util.function.Predicate;

public interface Queryable<T> {
    Queryable<T> SelectAll();

    Queryable<T> Where(Clause<?, ?> clauses);

    Queryable<T> And(Clause<?, ?> clauses);

    Queryable<T> Or(Clause<?, ?> clauses);

    Queryable<T> Filter(Predicate<T> predicate);

    Queryable<T> InsertOrUpdate(T data);

    Queryable<T> Update(T data);

    Queryable<T> Insert(T data);

    Queryable<T> Execute();

    List<T> ToList();
}