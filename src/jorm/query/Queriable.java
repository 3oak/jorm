package jorm.query;

import java.util.List;
import java.util.function.Predicate;

public interface Queriable<T> {
    public Queriable<T> SelectAll();
    public Queriable<T> Filter(Predicate<T> predicate);
    public void Persist(T data);
    public List<T> ToList();
}