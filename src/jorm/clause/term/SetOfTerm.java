package jorm.clause.term;

import java.util.List;

@SuppressWarnings("unused")
public class SetOfTerm<T> implements Term {
    private final List<T> listGenericData;

    public SetOfTerm(List<T> listGenericData) {
        this.listGenericData = listGenericData;
    }

    @Override
    public String ToQueryString() {
        StringBuilder queryString = new StringBuilder();
        String postfix;

        for (int i = 0; i < listGenericData.size(); i++) {
            postfix = i != listGenericData.size() - 1 ? "," : "";
            queryString.append(String.format("'%s'", listGenericData.get(i))).append(postfix);
        }

        for (var item : listGenericData) {
            queryString.append("'%s'");
        }

        return String.format("(%s)", queryString);
    }
}
