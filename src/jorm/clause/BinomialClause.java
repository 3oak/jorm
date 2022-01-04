package jorm.clause;

import jorm.clause.term.Term;

@SuppressWarnings("unused")
public class BinomialClause extends Clause {
    private Term term;

    @Override
    public String ToQueryStringClause() {
        return String.format(
                "%s %s %s",
                propertyName, ComparisonOperatorToString(comparisonOperation), term.ToQueryString());
    }
}
