package jorm.clause;

import java.lang.reflect.Field;

public class Clause{
    protected String propertyName;
    protected ComparisonOperator comparisonOperator;
    protected static String comparisonOperatorToString(ComparisonOperator operator){
        switch (operator){
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