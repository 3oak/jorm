public class JORMConnection {
    /* *** SINGLETON *** */
    protected JORMConnection connection;

    private JORMConnection() {

    }

    public JORMConnection getConnection() {
        if (connection == null) {
            connection = new JORMConnection();
        }

        return connection;
    }

    /* *** PUBLIC METHODS *** */
    public void OpenConnection() {

    }

    public void CloseConnection() {

    }

    public <T> void Insert (T DataObject) {

    }

    public <T> void Update (T DataObject) {

    }

    public <T> void Delete (T DataObject) {

    }
}
