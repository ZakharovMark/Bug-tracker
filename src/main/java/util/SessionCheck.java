package util;

import model.db.DB;
import model.db.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionCheck {
    public static boolean success(HttpServletRequest req){
        HttpSession session = req.getSession();
        String username = null;
        String password = null;
        if(session == null){
            return false;
        }
        username = (String) session.getAttribute("username");
        password = (String) session.getAttribute("password");
        if(username == null || password == null){
            return false;
        }
        boolean access = DB.getDB().getUsersDB().login(username, password) ==
                Status.ACCESS;
        return access;
    }
}
