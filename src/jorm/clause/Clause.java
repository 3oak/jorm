package jorm.clause;

public class Clause{
    protected String propertyName;
    protected ComparisonOperator comparisonOperation;
    protected static String comparisonOperatorToString(ComparisonOperator operator){
        switch (operator){
            case Greater:
                return ">";
            case GEQ:
                return ">=";
            case NE:
                return "!=";
            case E:
                return "=";
            case IN:
                return "IN";
            default:
                break;
        }
        return null;
    }
    public String toQueryStringClause() { return null; }
}