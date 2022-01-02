package jorm.connection;

public class ConnectionFactory {
    /* *** SINGLETON *** */
    protected static Connectable connection;

    public static <T extends Connectable> T createConnection(Class<T> connectionClass) {
        if (connection == null) {
            try {
                connection = connectionClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(!connection.getClass().equals(connectionClass)){
            String message = String.format("Connection is already create with type %s", connection.getClass());
            throw new RuntimeException(message);
        }
        return (T) connection;
    }
}
