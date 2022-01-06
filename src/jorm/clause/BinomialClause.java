package jorm.clause;

import jorm.clause.term.Term;

public class BinomialClause extends Clause {
    private Term term;

    public BinomialClause(String propertyName, ComparisonOperator operator,Term term){
        this.propertyName = propertyName;
        this.comparisonOperator = operator;
        this.term = term;
    }
    @Override
    public String toQueryStringClause() {
        return String.format("%s %s %s", propertyName, comparisonOperatorToString(comparisonOperator), term.toQueryString());
    }
}
