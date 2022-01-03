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
    public String toQueryStringClause() { return null; }
}