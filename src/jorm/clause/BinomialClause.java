package jorm.clause;

import jorm.clause.term.Term;

@SuppressWarnings("unused")
public class BinomialClause extends Clause {
    private final Term term;

    public BinomialClause(String propertyName, ComparisonOperator operator, Term term) {
        this.propertyName = propertyName;
        this.comparisonOperator = operator;
        this.term = term;
    }

    @Override
    public String ToQueryStringClause() {
        return String.format(
                "%s %s %s",
                propertyName, ComparisonOperatorToString(comparisonOperator), term.ToQueryString()
        );
    }
}
