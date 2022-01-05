package jorm.query.refactor_query;

public class QueryOption {
    private Queryable queryable;

    public QueryOption(Queryable queryable) {
        this.queryable = queryable;
    }

    public QueryExecution Where() {
        return new QueryExecution(queryable);
    }
}
