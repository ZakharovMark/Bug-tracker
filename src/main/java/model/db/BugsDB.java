package model.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BugsDB {
    private static BugsDB instance;

    protected static BugsDB getDB() {
        if(instance == null){
            instance = new BugsDB();
        }
        return instance;
    }

    private BugsDB() {
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.
                    executeUpdate("create table if not exists" +
                            " bugs(id int not null AUTO_INCREMENT PRIMARY KEY," +
                            " uuid CHAR(36) not null," +
                            " ticket_number int not null," +
                            " upload_date DATETIME not null," +
                            " uploaded_by char(8) not null," +
                            " bug_report TEXT not null," +
                            " bug_solution_id int," +
                            " solved_status TEXT not null" +
                            " );");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //return added bug id
    public synchronized int addBug(String projectUUID, String uploadedBy, String description) {
        int result = 0;
        try {
            Connection c = DB.getDB().getConnection();
            PreparedStatement statement =
                    c.prepareStatement("insert into bugs (uuid," +
                                    " ticket_number, upload_date, uploaded_by, " +
                                    "bug_report, solved_status) values (?, ?, ?, ?, ?, ?);", PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, projectUUID);
            statement.setInt(2, DB.getDB().getProjectDB().getBugsNumber(projectUUID));
            statement.setDate(3, new Date(System.currentTimeMillis()));
            statement.setString(4, uploadedBy);
            statement.setString(5, description);
            statement.setString(6, "open");
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            result = rs.getInt(1);
            DB.getDB().getProjectDB().incrementBugsNumber(projectUUID);
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public synchronized Map<String, Object> getBug(int bugId) {
        Map<String, Object> result = null;
        ResultSet rs = null;
        try {
            result = new HashMap<>();
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.execute("select * from bugs where id=" + bugId + ";");
            rs = statement.getResultSet();
            while (rs.next()) {
                result.put("id", bugId);
                result.put("uuid", rs.getString("uuid"));
                result.put("ticket_number", rs.getInt("ticket_number"));
                result.put("upload_date", rs.getDate("upload_date"));
                result.put("uploaded_by", rs.getString("uploaded_by"));
                result.put("bug_solution_id", rs.getInt("bug_solution_id"));
                result.put("bug_report", rs.getString("bug_report"));
                result.put("solved_status", rs.getString("solved_status"));
            }
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public synchronized void setBugAttributes(Map<String, Object> attributes) {
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            if (attributes.get("id") != null) {
                if (attributes.get("bug_report") != null) {
                    statement.executeUpdate("update bugs set bug_report='" + (String) attributes.get("bug_report") +
                            "' where id=" + String.valueOf((Integer) attributes.get("id")) + ";");
                }
                if (attributes.get("solved_status") != null) {
                    statement.executeUpdate("update bugs set solved_status='" + (String) attributes.get("solved_status") +
                            "' where id=" + String.valueOf((Integer) attributes.get("id")) + ";");
                }
                if (attributes.get("bug_solution_id") != null) {
                    statement.executeUpdate("update bugs set bug_solution_id="
                            + Integer.parseInt(String.valueOf(attributes.get("bug_solution_id"))) +
                            " where id=" + String.valueOf((Integer) attributes.get("id")) + ";");
                }
            }
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized List<Map<String, Object>> getBugsFromProject(String projectUUID){
        List<Map<String, Object>> result = new ArrayList();
        ResultSet rs = null;
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.execute("select id from bugs where uuid=\'" + projectUUID + "\';");
            rs = statement.getResultSet();
            while (rs.next()) {
                result.add(getBug(rs.getInt("id")));
            }
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public synchronized void deleteBug(int bug_id){
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.executeUpdate("delete from bugs where id=" + bug_id + ";");
            DB.getDB().getAnswersDb().deleteAllAnswersByBug(String.valueOf(bug_id));
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
