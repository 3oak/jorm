package jorm.connection;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unused")
public class ConnectionFactory {
    private ConnectionFactory() {

    }

    /* *** SINGLETON *** */
    protected static Connectable connection;

    @SuppressWarnings("unchecked")
    public static <T extends Connectable> T createConnection(Class<T> connectionClass) {
        if (connection == null) {
            try {
                connection = connectionClass.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException
                    | InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (!connection.getClass().equals(connectionClass)) {
            String message =
                    String.format("Connection is already create with type %s", connection.getClass());
            throw new RuntimeException(message);
        }
        return (T) connection;
    }
}
