package jorm.connection;

import jorm.query.Query;

public class MongoConnection implements Connectable {
    protected MongoConnection() {

    }

    @Override
    public void OpenConnection() {

    }

    @Override
    public void CloseConnection() {

    }

    @Override
    public <T> Query<T> CreateQuery(Class userClass) {
        return null;
    }
}
