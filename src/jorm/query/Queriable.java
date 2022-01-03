package jorm.query;

import jorm.clause.Clause;
import java.util.List;
import java.util.function.Predicate;

public interface Queriable<T> {
    public Queriable<T> SelectAll();
    public Queriable<T> Where(Clause clauses);
    public Queriable<T> And(Clause clauses);
    public Queriable<T> Or(Clause clauses);
    public Queriable<T> Filter(Predicate<T> predicate);
    public Queriable<T> InsertOrUpdate(T data);
    public Queriable<T> Update(T data);
    public Queriable<T> Insert(T data);
    public Queriable<T> Execute();
    public List<T> ToList();
}