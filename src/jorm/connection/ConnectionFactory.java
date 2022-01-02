package jorm.connection;

public class ConnectionFactory {
    /* *** SINGLETON *** */
    protected static Connection connection;

    public static <T extends Connection> T createConnection(Class<T> connectionClass) {
        if (connection == null) {
            if (connectionClass.equals(SqlConnection.class))
                connection = new SqlConnection();
            else if (connectionClass.equals(MongoConnection.class))
                connection = new MongoConnection();
        }
        return (T) connection;
    }
}
