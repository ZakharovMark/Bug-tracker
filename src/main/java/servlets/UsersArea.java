package servlets;

import content.makers.Theme;
import content.makers.UserProjectsListMaker;
import model.db.DB;
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

@WebServlet("/usersarea")
public class UsersArea extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = null;
        if(session != null){
            username = (String) session.getAttribute("username");
        }
        Map<String, String> vals = new HashMap<>();
        vals.put("username", username);
        vals.put("theme", Theme.getInstance().getThemeName(req));
        vals.put("content", new UserProjectsListMaker().getContent(username));
        resp.addHeader("Cache-Control", "no-cache");
        resp.getWriter().println(TemplatesPlaceholder.getInstance().fillTemplateFromFile("pa", vals));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getParameter("register_new_project")!=null) {
            DB.getDB().getProjectDB().registerNewProject(req.getParameter("name_field"), (String) req.getSession().getAttribute("username"));
        }
        doGet(req, resp);
    }
}
