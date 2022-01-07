package jorm.query;

import java.util.ArrayList;
import java.util.List;

public class QueryData<T> {
    private final ArrayList<T> dataList;

    public QueryData(ArrayList<T> dataList) {
        this.dataList = dataList;
    }

    public List<T> ToList() {
        return dataList;
    }
}
