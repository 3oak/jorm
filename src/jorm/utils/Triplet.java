package jorm.utils;

public class Triplet<T1, T2, T3> {
    private T1 item1;
    private T2 item2;
    private T3 item3;

    private Triplet(T1 item1, T2 item2, T3 item3){
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
    }

    public static <T1, T2, T3> Triplet<T1, T2, T3> CreateTriplet(T1 item1, T2 item2, T3 item3) {
        return new Triplet<>(item1, item2, item3);
    }

    public T1 GetHead() { return item1; }
    public T2 GetMid() {
        return item2;
    }
    public T3 GetTail() {
        return item3;
    }
}