package jorm.clause.term;

import java.util.List;

public class SetOfTerm<T> implements Term<T> {
    private List<T> listGenericData;
    public SetOfTerm(List<T> listGenericData){
        this.listGenericData = listGenericData;
    }
    @Override
    public String toQueryString() {
        String queryString = "", postfix = "";
        for (int i = 0; i < listGenericData.size(); i++){
            postfix = i != listGenericData.size() - 1 ? "," : "";
            queryString += String.format("\'%s\'", listGenericData.get(i)) + postfix;
        }
        for (var item : listGenericData) {
            queryString += String.format("\'%s\'");
        }
        return String.format("(%s)", queryString);
    }
}
