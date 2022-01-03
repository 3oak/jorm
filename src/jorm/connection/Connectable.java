package jorm.connection;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import jorm.query.Query;

public interface Connectable {
    public void OpenConnection();
    public void CloseConnection();

    public <T> Query<T> CreateQuery(Class userClass) throws MessagingException;
}
