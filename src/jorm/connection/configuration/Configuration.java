package jorm.connection.configuration;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Configuration {
    public String username = "root";
    public String password = "root";

    public Configuration SetUsername(String username) {
        this.username = username;
        return this;
    }

    public Configuration SetPassword(String password) {
        this.password = password;
        return this;
    }

    public abstract String GetConnectionURL();
}
