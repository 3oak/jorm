package jorm.query;

import java.util.List;
import java.util.function.Predicate;

public interface Query<T> {
    public Query<T> SelectAll();
    public Query<T> Filter(Predicate<T> predicate);

    public List<T> ToList();
}