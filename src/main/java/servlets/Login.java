package servlets;

import content.makers.Theme;
import model.db.DB;
import model.db.Status;
import util.TemplatesPlaceholder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class Login extends HttpServlet {
    private String massage = "";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = "username";
        String password = "password";
        HttpSession session = req.getSession();
        if(session != null) {
            if(session.getAttribute(username) != null){
                username = (String) session.getAttribute(username);
            }
            if(session.getAttribute(password) != null){
                password = (String) session.getAttribute(password);
            }
        }
        Map<String, String> vals = new HashMap();
        vals.put("username", username);
        vals.put("password", password);
        vals.put("theme", "sweet_home");
        vals.put("massage", massage);
        massage = "";
        String res = TemplatesPlaceholder.getInstance().fillTemplateFromFile("login", vals);
        resp.getWriter().append(res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        boolean access = DB.getDB().getUsersDB().login(req.getParameter("login"), req.getParameter("password")) ==
                Status.ACCESS;
        if(!access){
            massage = "Bad login or password";
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        HttpSession session = req.getSession();
        session.setAttribute("username", req.getParameter("login"));
        session.setAttribute("password", req.getParameter("password"));
        session.removeAttribute("theme");
        resp.sendRedirect(req.getContextPath() + "/usersarea");
    }
}
