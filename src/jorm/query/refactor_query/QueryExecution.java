package jorm.query.refactor_query;

public class QueryExecution {
    private Queryable queryable;

    public QueryExecution(Queryable queryable) {
        this.queryable = queryable;
    }

    public QueryExecution And() {
        return this;
    }

    public QueryExecution Or() {
        return this;
    }

    public Queryable Run() {
        return queryable;
    }

}
