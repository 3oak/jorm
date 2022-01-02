package jorm.connection;

import java.sql.DriverManager;

import jorm.query.SqlQuery;

public class SqlConnection implements Connection {
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
    public <T> SqlQuery<T> CreateQuery(Class userClass) {
        return new SqlQuery(userClass, connection);
    }
}
