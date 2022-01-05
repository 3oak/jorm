package jorm.query.refactor_query;

public class Queryable {
    public QueryExecution SelectAll() {
        return new QueryExecution(this);
    }

    public QueryOption Select() {
        return new QueryOption(this);
    }

    public QueryExecution Insert() {
        return new QueryExecution(this);
    }

    public QueryOption Update() {
        return new QueryOption(this);
    }

    public QueryOption Delete() {
        return new QueryOption(this);
    }
}

