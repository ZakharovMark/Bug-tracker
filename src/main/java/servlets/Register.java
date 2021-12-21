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

@WebServlet("/register")
public class Register extends HttpServlet {
    private String massage = "";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> vals= new HashMap();
        vals.put("massage", massage);
        vals.put("theme", "sweet_home");
        massage = "";
        String res = TemplatesPlaceholder.getInstance().fillTemplateFromFile("register", vals);
        resp.getWriter().append(res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        boolean access =
                DB.getDB().getUsersDB().register(req.getParameter("login"), req.getParameter("password")) ==
                Status.CREATED;
        if(!access){
            massage = "The selected login is already taken";
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }
        HttpSession session = req.getSession();
        session.setAttribute("username", req.getParameter("login"));
        session.setAttribute("password", req.getParameter("password"));
        session = req.getSession();
        if(session != null){
            session.removeAttribute("theme");
        }
        resp.sendRedirect(req.getContextPath() + "/usersarea");
    }
}
