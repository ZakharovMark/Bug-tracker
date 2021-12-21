package servlets;

import content.makers.Theme;
import model.db.DB;
import util.TemplatesPlaceholder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/invite")
public class InvaitePage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DB
                .getDB()
                .getUsersProjectsDB(String.valueOf(req.getSession().getAttribute("username")))
                .addProject(req.getParameter("key"));
        resp.sendRedirect("http://localhost:9877/Kurs_war/usersarea");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> vals = new HashMap();
        vals.put("project_name", req.getParameter("project_name"));
        vals.put("theme", Theme.getInstance().getThemeName(req));
        vals.put("username", String.valueOf(req.getSession().getAttribute("username")));
        vals.put("uuid", req.getParameter("uuid"));
        vals.put("invite_ref", "http://localhost:9877/Kurs_war/invite?key="+req.getParameter("uuid"));
        resp.addHeader("Cache-Control", "no-cache, no-store");
        resp.getWriter().println(TemplatesPlaceholder.getInstance().fillTemplateFromFile("invite", vals));
    }
}
