package jorm.clause;

public abstract class Clause {
    protected String propertyName;
    protected ComparisonOperator comparisonOperator;

    protected static String ComparisonOperatorToString(ComparisonOperator operator) {
        switch (operator) {
            case GREATER:
                return ">";
            case GREATER_EQUAL:
                return ">=";
            case LESS:
                return "<";
            case LESS_EQUAL:
                return "<=";
            case NOT_EQUAL:
                return "!=";
            case EQUAL:
                return "=";
            case IN:
                return "IN";
            default:
                break;
        }
        return null;
    }

    public String ToQueryStringClause() {
        return null;
    }
}