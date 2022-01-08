package jorm.clause.term;

import jorm.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class SetOfTerm<T> implements Term {
    private final List<T> listGenericData;

    public SetOfTerm(List<T> listGenericData) {
        this.listGenericData = listGenericData;
    }
    public SetOfTerm(T... listGenericData) {
        this.listGenericData = List.of(listGenericData);
    }

    @Override
    public String ToQueryString() {
        StringBuilder queryString = new StringBuilder();
        String postfix;

        for (int i = 0; i < listGenericData.size(); i++) {
            postfix = i != listGenericData.size() - 1 ? "," : "";
            queryString.append(String.format("%s", Utils.ToStringQueryValue(listGenericData.get(i)))).append(postfix);
        }
        return String.format("(%s)", queryString);
    }
}
