package jorm.clause;

import jorm.clause.term.SetOfTerm;
import jorm.clause.term.SingleTerm;

import java.util.List;

@SuppressWarnings("unused")
public final class Expression {
    public static <T> Clause SimpleBinomialClause(String propertyName, ComparisonOperator operator, T genericData)
            throws IllegalArgumentException {
        ValidateData(genericData);
        return new BinomialClause(propertyName, operator, new SingleTerm<>(genericData));
    }

    public static <T> Clause In(String propertyName, List<T> genericData)
            throws IllegalArgumentException {
        ValidateData(genericData);
        return new BinomialClause(propertyName, ComparisonOperator.IN, new SetOfTerm<>(genericData));
    }

    private static <T> void ValidateData(T genericData)
            throws IllegalArgumentException {
        if (genericData instanceof Integer)
            return;
        if (genericData instanceof String)
            return;

        throw new IllegalArgumentException(String.format("Unsupported datatype %s", genericData.getClass()));
    }
}
