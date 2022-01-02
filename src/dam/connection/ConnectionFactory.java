package dam.connection;

public class ConnectionFactory {
    /* *** SINGLETON *** */
    protected static JORMConnection JORMConnection;
    public static <T extends JORMConnection> T createConnection(Class<T> connectionClass){
        if (JORMConnection == null){
            if(connectionClass.equals(SqlConnection.class))
                JORMConnection = new SqlConnection();
            else if(connectionClass.equals(MongoConnection.class))
                JORMConnection = new MongoConnection();
        }
        return (T) JORMConnection;
    }
}
