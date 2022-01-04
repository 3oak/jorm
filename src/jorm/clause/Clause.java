package jorm.clause;

public class Clause<TLeft, TRight> {
    private TLeft leftGenericType;
    private TRight rightGenericType;
    private ComparisonOperation comparisonOperation;

    public Clause(TLeft leftGenericType, TRight rightGenericType, ComparisonOperation comparisonOperation){
        this.leftGenericType = leftGenericType;
        this.rightGenericType = rightGenericType;
        this.comparisonOperation = comparisonOperation;
    }
    public static String ComparisonOperationToString(ComparisonOperation operation){
        switch (operation){
            case GT:
                return ">";
            case GEQ:
                return ">=";
            case LT:
                return "<";
            case LEQ:
                return "<=";
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