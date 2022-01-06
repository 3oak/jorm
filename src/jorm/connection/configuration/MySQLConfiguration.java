package jorm.connection.configuration;

import java.util.LinkedHashMap;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MySQLConfiguration extends Configuration {
    public String host = "localhost";
    public String port = "3306";
    public String database = "";
    public LinkedHashMap<String, String> properties = new LinkedHashMap<>();

    public MySQLConfiguration SetHostName(String host) {
        this.host = host;
        return this;
    }

    public MySQLConfiguration SetPort(String port) {
        this.port = port;
        return this;
    }

    public MySQLConfiguration SetDatabaseName(String database) {
        this.database = database;
        return this;
    }

    public MySQLConfiguration SetProperty(String key, String value) {
        this.properties.put(key, value);
        return this;
    }

    public MySQLConfiguration SetProperties(LinkedHashMap<String, String> properties) {
        this.properties.putAll(properties);
        return this;
    }

    @Override
    public MySQLConfiguration SetUsername(String username) {
        return (MySQLConfiguration) super.SetUsername(username);
    }

    @Override
    public MySQLConfiguration SetPassword(String password) {
        return (MySQLConfiguration) super.SetPassword(password);
    }

    @Override
    public String GetConnectionURL() {
        StringBuilder connectionURL =
                new StringBuilder(String.format(
                        "jdbc:mysql://%s:%s/%s",
                        host, port, database
                ));

        if (this.properties.size() > 0) {
            int index = 0;

            for (var entry : this.properties.entrySet()) {
                String property;

                if (index == 0) {
                    property =
                            String.format(
                                    "?%s=%s",
                                    entry.getKey(), entry.getValue()
                            );
                } else {
                    property =
                            String.format(
                                    "&%s=%s",
                                    entry.getKey(), entry.getValue()
                            );
                }

                connectionURL.append(property);

                index++;
            }
        }

        return connectionURL.toString();
    }
}
