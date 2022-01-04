package jorm.clause.term;

public class SingleTerm<T> implements Term<T> {
    private T genericData;
    public SingleTerm(T genericData){
        this.genericData = genericData;
    }
    @Override
    public String toQueryString() {
        return genericData.toString();
    }
}
