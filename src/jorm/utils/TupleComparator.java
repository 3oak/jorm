package jorm.utils;

import jorm.query.QueryType;

import java.util.Comparator;
import java.util.Objects;

public class TupleComparator implements Comparator<Tuple<QueryType, String>> {

    @Override
    public int compare(Tuple<QueryType, String> o1, Tuple<QueryType, String> o2) {
        QueryLevel
                queryLevel1 = GetHeadObject(o1),
                queryLevel2 = GetHeadObject(o2);

        return Integer.compare(Objects.requireNonNull(queryLevel2).ordinal(), Objects.requireNonNull(queryLevel1).ordinal());
    }

    private QueryLevel GetHeadObject(Tuple<QueryType, String> o) {
        switch (o.GetHead()) {
            case SELECT:
            case UPDATE:
            case INSERT:
            case DELETE:
                return QueryLevel.COMMANDS;
            case WHERE:
            case VALUE:
                return QueryLevel.WHERE;
            case AND:
            case OR:
                return QueryLevel.CONDITION;
            case FIELD:
            case SET:
            case COLUMN:
                return QueryLevel.FIELD;
        }
        return null;
    }
}

enum QueryLevel {
    CONDITION,
    WHERE,
    FIELD,
    COMMANDS,
}