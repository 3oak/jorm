package jorm.connection;

import jorm.query.Query;

public interface Connection {
    public void OpenConnection();
    public void CloseConnection();

    public <T> Query<T> CreateQuery(Class userClass);
}
