package jorm.clause;

import jorm.clause.term.Term;

public class BinomialClause extends Clause {
    private Term term;

    @Override
    public String toQueryStringClause() {
        return String.format("%s %s %s", propertyName, comparisonOperatorToString(comparisonOperation), term.toQueryString());
    }
}
