package servlets;

import content.makers.ProjectPageContentMaker;
import util.SessionCheck;
import util.TemplatesPlaceholder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/project")
public class Project extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> vals = new HashMap<>();
        HttpSession session = req.getSession();
        String username = null;
        if(session != null){
            username = (String) session.getAttribute("username");
        }
        vals.put("username", username);
        vals.put("project_name", req.getParameter("name_field"));
        vals.put("uuid", req.getParameter("uuid"));
        vals.putAll(buttonClickHandler(req));
        resp.addHeader("Cache-Control", "no-cache, no-store");
        resp.getWriter().println(new ProjectPageContentMaker().getProjectPage(vals, req));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private Map<String, String> buttonClickHandler(HttpServletRequest req){
        String button = req.getParameter("button");
        Map<String, String> result = new HashMap();
        if(button != null) {
            result.put("page", req.getParameter("page"));
            result.put("on_page", req.getParameter("on_page"));
            result.put("open_closed", req.getParameter("open_closed"));
            switch (button) {
                case "open":
                    result.put("open_closed", "open");
                    break;
                case "closed":
                    result.put("open_closed", "close");
                    break;
                case "10":
                    result.put("on_page", "10");
                    break;
                case "20":
                    result.put("on_page", "20");
                    break;
                case "30":
                    result.put("on_page", "30");
                    break;
                case "prev":
                    result.put("page", String.valueOf(Integer.parseInt(req.getParameter("page")) - 1));
                    break;
                case "next":
                    result.put("page", String.valueOf(Integer.parseInt(req.getParameter("page")) + 1));
                    break;
            }
        }
        return result;
    }
}
