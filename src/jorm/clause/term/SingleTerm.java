package jorm.clause.term;

@SuppressWarnings("unused")
public class SingleTerm<T> implements Term {
    private final T genericData;

    public SingleTerm(T genericData) {
        this.genericData = genericData;
    }

    @Override
    public String ToQueryString() {
        return String.format("'%s'", genericData.toString());
    }
}
