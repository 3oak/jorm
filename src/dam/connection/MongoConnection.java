package dam.connection;

import dam.query.Query;

public class MongoConnection implements JORMConnection {
    protected MongoConnection(){}

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
