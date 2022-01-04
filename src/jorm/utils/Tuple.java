package jorm.utils;

@SuppressWarnings("unused")
public class Tuple<T1, T2> {
    public T1 item1;
    public T2 item2;

    private Tuple(T1 item1, T2 item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public static <T1, T2> Tuple<T1, T2> CreateTuple(T1 item1, T2 item2) {
        return new Tuple<>(item1, item2);
    }
}
