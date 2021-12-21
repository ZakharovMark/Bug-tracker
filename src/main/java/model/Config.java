package model;

public class Config {
    private static Config instance;
    private final String username = "root";
    private final int maxConnections = 10;

    public int getMaxConnections() {
        return maxConnections;
    }

    private final String password = "ljv4545822";
    private final String connectionUrl = "jdbc:mysql://localhost:3306/mysql";

    public static Config getConfig() {
        if(instance == null){
            instance = new Config();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }
}
