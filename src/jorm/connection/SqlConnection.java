package jorm.connection;

import java.sql.DriverManager;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import jorm.query.SqlQuery;

public class SqlConnection implements Connectable {
    protected SqlConnection() {

    }

    protected java.sql.Connection connection;
    protected String connectionURL = "jdbc:mysql://localhost:3306/damdatabase";

    @Override
    public void OpenConnection() {
        try{
            connection = DriverManager.getConnection(connectionURL);
        }catch (Exception ignore){}
    }

    @Override
    public void CloseConnection() {
        try{
            if(connection == null || connection.isClosed())
                return;
            connection.close();
        }catch (Exception ignore){}
    }

    @Override
    public <T> SqlQuery<T> CreateQuery(Class userClass) throws MessagingException {
        return new SqlQuery(userClass, connection);
    }
}
