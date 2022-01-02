package jorm.query;

import java.util.List;
import java.util.function.Predicate;

public interface Queriable<T> {
    public Queriable<T> SelectAll();

    public Queriable<T> Filter(Predicate<T> predicate);

    public List<T> ToList();
}