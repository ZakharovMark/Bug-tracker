package model.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswersDB {
    private static AnswersDB instance;

    protected static AnswersDB getDB() {
        if(instance == null){
            instance = new AnswersDB();
        }
        return instance;
    }

    private AnswersDB() {
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.
                    executeUpdate("create table if not exists" +
                            " answers(id int not null AUTO_INCREMENT PRIMARY KEY," +
                            " bug_id int not null," +
                            " upload_date DATETIME not null," +
                            " uploaded_by char(8) not null," +
                            " answer TEXT not null," +
                            " is_solution BIT not null," +
                            " bug_or_ficha TEXT not null" +
                            " );");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized void addAnswer(String bugId, String uploadedBy, String bugOrFicha, String answer){
        try {
            Connection c = DB.getDB().getConnection();
            PreparedStatement statement =
                            c.prepareStatement("insert into answers (bug_id," +
                                    "upload_date, uploaded_by, " +
                                    "answer, is_solution, bug_or_ficha) values (?, ?, ?, ?, ?, ?);");
            statement.setInt(1, Integer.parseInt(bugId));
            statement.setDate(2, new Date(System.currentTimeMillis()));
            statement.setString(3, uploadedBy);
            statement.setString(4, answer);
            statement.setBoolean(5, false);
            statement.setString(6, bugOrFicha);
            statement.execute();
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized void changeSolutionStatus(String id){
        try {
            boolean status = !(boolean) DB.getDB().getDB().getAnswersDb().getAnswerAttributes(id).get("is_solution");
            int bug_id = (int) DB.getDB().getAnswersDb().getAnswerAttributes(id).get("bug_id");
            if((DB.getDB().getBugsDB().getBug(bug_id).get("solved_status").equals("open") && status) ||
                    (!status && ((Integer)
                            DB.getDB().getBugsDB().getBug(bug_id).get("bug_solution_id")).equals(Integer.valueOf(id)))) {
                Connection c = DB.getDB().getConnection();
                Statement statement = c.createStatement();
                statement.executeUpdate("update answers set is_solution=" + status +
                        " where id=" + id + ";");
                if (status) {
                    Map<String, Object> vals = new HashMap();
                    vals.put("id", bug_id);
                    vals.put("solved_status", "closed");
                    vals.put("bug_solution_id", id);
                    DB.getDB().getBugsDB().setBugAttributes(vals);
                } else {
                    Map<String, Object> vals = new HashMap();
                    vals.put("id", bug_id);
                    vals.put("solved_status", "open");
                    vals.put("bug_solution_id", -1);
                    DB.getDB().getBugsDB().setBugAttributes(vals);
                }
                DB.getDB().releaseConnection(c);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized void updateAnswer(int AnswerId, String answer, String bugOrFicha){
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.executeUpdate("update answers set answer=\'"+answer+ "\'," +
                    " bug_or_ficha=\'" + bugOrFicha +"\'" +
                    " where id="+AnswerId+";");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized void deleteAnswer(String id){
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.executeUpdate("delete from answers where id="+id+";");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized void deleteAllAnswersByBug(String bugId){
        Statement statement = null;
        try {
            Connection c = DB.getDB().getConnection();
            statement = c.createStatement();
            statement.executeUpdate("delete from answers where bug_id="+bugId+";");
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized Map<String, Object> getAnswerAttributes(String id){
        Map<String, Object> attributeMap = new HashMap();
        ResultSet rs = null;
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.execute("select * from answers where id="+id+";");
            rs = statement.getResultSet();
            while(rs.next()){
                attributeMap.put("id", id);
                attributeMap.put("bug_id", rs.getInt("bug_id"));
                attributeMap.put("upload_date", rs.getDate("upload_date"));
                attributeMap.put("uploaded_by", rs.getString("uploaded_by"));
                attributeMap.put("is_solution", rs.getBoolean("is_solution"));
                attributeMap.put("answer", rs.getString("answer"));
                attributeMap.put("bug_or_ficha", rs.getString("bug_or_ficha"));
            }
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return attributeMap;
    }

    public synchronized List<Map<String, Object>> getAllAnswersByBug(int bugId){
        List<Map<String, Object>> result = new ArrayList();
        ResultSet rs = null;
        try {
            Connection c = DB.getDB().getConnection();
            Statement statement = c.createStatement();
            statement.execute("select id from answers where bug_id=" + bugId + ";");
            rs = statement.getResultSet();
            while (rs.next()) {
                result.add(getAnswerAttributes(String.valueOf(rs.getInt("id"))));
            }
            DB.getDB().releaseConnection(c);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
}
