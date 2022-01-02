package jorm.connection.configuration;

import java.util.LinkedHashMap;

public class MySQLConfiguration extends  Configuration {
    public String host = "localhost";
    public String port = "3306";
    public String database = "";
    public LinkedHashMap<String, String> properties = new LinkedHashMap<>();

    public MySQLConfiguration setHostName(String host) {
        this.host = host;
        return this;
    }

    public MySQLConfiguration setPort(String port) {
        this.port = port;
        return this;
    }

    public MySQLConfiguration setDatabaseName(String database) {
        this.database = database;
        return this;
    }

    public MySQLConfiguration setProperty(String key, String value) {
        this.properties.put(key, value);
        return this;
    }

    public MySQLConfiguration setProperties(LinkedHashMap<String, String> properties) {
        this.properties.putAll(properties);
        return this;
    }

    @Override
    public String getConnectionURL() {
        StringBuilder connectionURL =
                new StringBuilder(String.format(
                        "jdbc:mysql://%s:%s/%s",
                        host, port, database
                ));

        if (this.properties.size() > 0) {
            int index = 0;

            for (var entry : this.properties.entrySet()) {
                String property = "";

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
