package model.db;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UsersProjectsDB {
    private String username;
    private static UsersProjectsDB instance;

    protected static UsersProjectsDB getUsersProjectsDB(String userName) {
        return new UsersProjectsDB(userName);
    }

    private UsersProjectsDB(String userName) {
        this.username = userName;
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.
                    executeUpdate("create table if not exists" +
                            " users_projects." + userName+ "_projects(uuid CHAR(36) NOT NULL," +
                            " projectName CHAR(8) not null," +
                            " PRIMARY KEY (uuid))");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized Map<String, String> getProjects(){
        Map<String, String> result = null;
        PreparedStatement statement;
        ResultSet rs;

        try{
            Connection c = DB.getDB().getConnection();
            statement = c.prepareStatement("select count(*) as projects_count from users_projects."+username+"_projects");
            statement.execute();
            rs = statement.getResultSet();
            rs.next();
            int i = rs.getInt("projects_count");
            System.out.println(i);
            if(i == 0){
                return null;
            }
            statement = c.prepareStatement("select * from users_projects."+username+"_projects");
            result = new HashMap();
            statement.execute();
            rs = statement.getResultSet();
            while(rs.next()){
                result.put(rs.getString("uuid"), rs.getString("projectName"));
            }
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

    public synchronized void addProject(String uuid, String projectName){
        try {
            Connection c = DB.getDB().getConnection();
            PreparedStatement statement = c.prepareStatement("insert into users_projects."+username+"_projects set uuid='"+
                                    uuid+"', projectName=?;");
            statement.setString(1, projectName);
            statement.execute();
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized void addProject(String uuid){
        addProject(uuid, DB.getDB().getProjectDB().getNameByUUID(uuid));
    }
}
