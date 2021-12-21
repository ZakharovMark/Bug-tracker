package model.db;

import java.sql.*;

public class UsersDB {
    private static UsersDB instance;

    protected static UsersDB getDB() {
        if(instance == null){
            instance = new UsersDB();
        }
        return instance;
    }

    private UsersDB() {
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.
                    executeUpdate("create table if not exists" +
                            " users(id MEDIUMINT NOT NULL AUTO_INCREMENT," +
                            " loginName CHAR(8) not null," +
                            " password CHAR(8) not null," +
                            " PRIMARY KEY (id))");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized Status login(String login, String password) {
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            Connection c = DB.getDB().getConnection();
            statement = c.prepareStatement("select * from users where loginName = ?;");
            statement.setString(1, login);
            statement.execute();
            rs = statement.getResultSet();
            if (rs == null) {
                return Status.DENIED;
            }
            rs.next();
            if (!rs.getString("password").equals(password)) {
                return Status.DENIED;
            }
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Status.DENIED;
        }
        return Status.ACCESS;
    }

    public synchronized Status register(String login, String password) {
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            Connection c = DB.getDB().getConnection();
            statement = c.prepareStatement("select * from users where loginName = ?;");
            statement.setString(1, login);
            statement.execute();
            rs = statement.getResultSet();
        if (rs.next()) {
            return Status.DENIED;
        }
        statement = c.prepareStatement("insert into users set loginName=?, password=?;");
        statement.setString(1, login);
        statement.setString(2, password);
        statement.execute();
        DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Status.DENIED;
        }
        return Status.CREATED;
    }
}

