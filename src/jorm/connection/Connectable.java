package jorm.connection;

import jorm.query.Query;

public interface Connectable {
    public void OpenConnection();
    public void CloseConnection();

    public <T> Query<T> CreateQuery(Class userClass);
}
