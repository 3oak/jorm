package jorm.connection;

import jorm.connection.configuration.Configuration;
import jorm.connection.configuration.MySQLConfiguration;
import jorm.query.MySQLQuery;

@SuppressWarnings("unused")
public class MySQLConnection extends Connectable {
    public MySQLConnection(String url) {
        super(url);
    }

    public MySQLConnection(String connectionURL, String username, String password) {
        super(connectionURL, username, password);
    }

    public MySQLConnection(Configuration configuration) {
        super(configuration);

        MySQLConfiguration _configuration = (MySQLConfiguration) configuration;
        this.connectionURL = _configuration.getConnectionURL();
        this.username = _configuration.username;
        this.password = _configuration.password;
    }

    @Override
    public <T> MySQLQuery<T> CreateQuery(Class<T> userClass) throws RuntimeException {
        return new MySQLQuery<>(userClass, connection);
    }
}
