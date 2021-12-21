package model.db;

import model.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DB {
    private static DB instance;
    private List<Connection> connectionPool;
    private List<Connection> usedConnections;

    public static DB getDB(){
        if(instance == null){
            instance = new DB();
        }
        return instance;
    }

    private DB() {
        connectionPool = new ArrayList();
        usedConnections = new ArrayList();
        for(int i = 0; i< Config.getConfig().getMaxConnections(); i++){
            connectionPool.add(createConnection());
        }
    }

    public synchronized Connection getConnection(){
        if(connectionPool.size() == 0){
            connectionPool.add(createConnection());
        }
        Connection connection = connectionPool
                .remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public synchronized boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    public synchronized int countFreeConnections(){
        return connectionPool.size();
    }

    private synchronized Connection createConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(Config.getConfig().getConnectionUrl(),
                    Config.getConfig().getUsername(),
                    Config.getConfig().getPassword());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public UsersDB getUsersDB(){
        return UsersDB.getDB();
    }

    public UsersProjectsDB getUsersProjectsDB(String userName){
        return UsersProjectsDB.getUsersProjectsDB(userName);
    }

    public ProjectDB getProjectDB(){
        return ProjectDB.getDB();
    }

    public BugsDB getBugsDB() {
        return BugsDB.getDB();
    }

    public AnswersDB getAnswersDb(){
        return AnswersDB.getDB();
    }
}
