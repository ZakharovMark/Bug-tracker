package model.db;

import java.sql.*;
import java.util.UUID;

public class ProjectDB {
    private static ProjectDB instance;

    protected static ProjectDB getDB() {
        if(instance == null){
            instance = new ProjectDB();
        }
        return instance;
    }

    private ProjectDB(){
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.
                    executeUpdate("create table if not exists" +
                            " projects(uuid char(36) NOT NULL PRIMARY KEY," +
                            " name CHAR(8) not null," +
                            " number_of_tickets int not null);");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized boolean registerNewProject(String projectName, String creatorUserName){
        Statement statement = null;
        PreparedStatement prStatement = null;
        ResultSet rs = null;
        String uuid = UUID.randomUUID().toString();
        try {
            Connection c = DB.getDB().getConnection();
            statement = c.createStatement();
            statement.execute("select * from projects where uuid='"+uuid+"';");
            rs = statement.getResultSet();
            if (rs.next()) {
                return false;
            }
            prStatement =
                    c.prepareStatement("insert into projects set uuid='"+uuid+"', name=?, number_of_tickets=0;");
            prStatement.setString(1, projectName);
            prStatement.execute();
            DB.getDB().getUsersProjectsDB(creatorUserName).addProject(uuid, projectName);
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public synchronized int getBugsNumber(String projectUUID){
        Statement statement = null;
        ResultSet rs = null;
        int number = -1;
        try {
            Connection c = DB.getDB().getConnection();
            statement = c.createStatement();
            statement.execute("select number_of_tickets from projects where uuid='"+projectUUID+"';");
            rs = statement.getResultSet();
            rs.next();
            number = rs.getInt("number_of_tickets");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return number;
    }

    public synchronized String getNameByUUID(String projectUUID){
        Statement statement = null;
        ResultSet rs = null;
        String name = null;
        try {
            Connection c = DB.getDB().getConnection();
            statement = c.createStatement();
            statement.execute("select name from projects where uuid='"+projectUUID+"';");
            rs = statement.getResultSet();
            rs.next();
            name = rs.getString("name");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return name;
    }

    public synchronized void incrementBugsNumber(String projectUUID){
        Statement statement = null;
        try {
            Connection c = DB.getDB().getConnection();
            statement = c.createStatement();
            statement.executeUpdate("update projects set number_of_tickets = number_of_tickets + 1 where uuid='"+projectUUID+"';");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
