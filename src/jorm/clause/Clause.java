package jorm.clause;

@SuppressWarnings("unused")
public class Clause {
    protected String propertyName;
    protected ComparisonOperator comparisonOperation;

    protected static String ComparisonOperatorToString(ComparisonOperator operator) {
        switch (operator) {
            case Greater:
                return ">";
            case GEqual:
                return ">=";
            case Less:
                return "<";
            case LEqual:
                return "<=";
            case NotEqual:
                return "!=";
            case Equal:
                return "=";
            case In:
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