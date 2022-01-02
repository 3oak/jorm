package dam.connection;

import dam.query.Query;

public interface JORMConnection {
    public void OpenConnection();
    public void CloseConnection();
    public <T> Query<T> CreateQuery(Class userClass);
}
