package jorm.connection.configuration;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Configuration {
    public String username = "root";
    public String password = "root";

    public Configuration setUsername(String username) {
        this.username = username;
        return this;
    }

    public Configuration setPassword(String password) {
        this.password = password;
        return this;
    }

    public abstract String getConnectionURL();
}
