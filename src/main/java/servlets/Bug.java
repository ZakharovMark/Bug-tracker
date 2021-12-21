package servlets;

import content.makers.BugPageContentMaker;
import model.db.DB;
import util.TemplatesPlaceholder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet("/bug")
public class Bug extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = (String) req.getSession().getAttribute("username");
        String bug_id = req.getParameter("bug_id");
        String projuuid = req.getParameter("uuid");
        resp.addHeader("Cache-Control", "no-cache, no-store");
        if (bug_id == null || bug_id.equals("-1")) {
            resp.getWriter().println(new BugPageContentMaker().createBugPage(userName, projuuid, req));
        } else {
            resp.getWriter().println(new BugPageContentMaker().viewBug(userName, Integer.parseInt(bug_id), projuuid, req));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("post_action") != null) {
            if (req.getParameter("post_action").equals("bug")) {
                if (req.getParameter("button").equals("post")) {
                    int bugId = DB.getDB().getBugsDB().addBug(req.getParameter("uuid"),
                            (String) req.getSession().getAttribute("username"),
                            TemplatesPlaceholder.getInstance().disableKeys(req.getParameter("description")));
                    resp.sendRedirect(req.getContextPath() + "/bug" + "?uuid=" +
                            req.getParameter("uuid") + "&" + "bug_id=" + bugId);
                }
                if (req.getParameter("button").equals("edit") &&
                        req.getSession().getAttribute("username").equals(DB.getDB().
                                getBugsDB().getBug(Integer.parseInt(req.getParameter("bug_id"))).get("uploaded_by"))) {
                    DB.getDB().getBugsDB().setBugAttributes(new HashMap() {
                        {
                            put("id", Integer.parseInt(req.getParameter("bug_id")));
                            put("bug_report",
                                    TemplatesPlaceholder.getInstance().disableKeys(req.getParameter("description")));
                        }
                    });
                    doGet(req, resp);
                }
                if (req.getParameter("button").equals("delete") &&
                        req.getSession().getAttribute("username").equals(DB.getDB().
                                getBugsDB().getBug(Integer.parseInt(req.getParameter("bug_id"))).get("uploaded_by"))) {
                    DB.getDB().getBugsDB().deleteBug(Integer.parseInt(req.getParameter("bug_id")));
                    resp.sendRedirect(req.getContextPath() +
                            "/project" + "?name_field=" + DB.getDB().getProjectDB().getNameByUUID(req.getParameter("uuid")) +
                            "&" + "uuid=" + req.getParameter("uuid") + "&go=Go");
                }
            }
        }
        if (req.getParameter("button")!=null && req.getParameter("button").equals("send")) {
            DB
                    .getDB()
                    .getAnswersDb()
                    .addAnswer(req.getParameter("bug_id"),
                            String.valueOf(req.getSession().getAttribute("username")),
                            req.getParameter("bug_or_ficha"),
                            TemplatesPlaceholder.getInstance().disableKeys(req.getParameter("answer")));
            resp.sendRedirect(req.getContextPath() + "/bug" + "?uuid=" +
                    req.getParameter("uuid") + "&" + "bug_id=" + req.getParameter("bug_id"));
        }
        if (String.valueOf(req.getSession().getAttribute("username"))
                .equals(String.valueOf(DB
                        .getDB()
                        .getAnswersDb()
                        .getAnswerAttributes(req.getParameter("id"))
                        .get("uploaded_by")))
                && !(Boolean) DB
                .getDB()
                .getAnswersDb()
                .getAnswerAttributes(req.getParameter("id"))
                .get("is_solution")) {
            switch (req.getParameter("answer_button")) {
                case "edit_solution":
                    if(req.getParameter("bug_or_ficha").equals("bug") ||
                            req.getParameter("bug_or_ficha").equals("ficha")) {
                        DB.getDB().getAnswersDb().updateAnswer(Integer.parseInt(req.getParameter("id")),
                                TemplatesPlaceholder.getInstance().disableKeys(req.getParameter("description")),
                                req.getParameter("bug_or_ficha"));
                    }
                    resp.sendRedirect(req.getContextPath() + "/bug" + "?uuid=" +
                            DB.getDB().getBugsDB().getBug(Integer.parseInt(req.getParameter("bug_id"))).get("uuid")
                            + "&" + "bug_id=" + req.getParameter("bug_id"));
                    break;
                case "delete_solution":
                    DB.getDB().getAnswersDb().deleteAnswer(req.getParameter("id"));
                    resp.sendRedirect(req.getContextPath() + "/bug" + "?uuid=" +
                            DB.getDB().getBugsDB().getBug(Integer.parseInt(req.getParameter("bug_id"))).get("uuid")
                            + "&" + "bug_id=" + req.getParameter("bug_id"));
                    break;
            }
        }
        if (req.getParameter("answer_button")!=null && req.getParameter("answer_button").equals("change")) {
            DB.getDB().getAnswersDb().changeSolutionStatus(req.getParameter("id"));
            resp.sendRedirect(req.getContextPath() + "/bug" + "?uuid=" +
                    DB.getDB().getBugsDB().getBug(Integer.parseInt(req.getParameter("bug_id"))).get("uuid")
                    + "&" + "bug_id=" + req.getParameter("bug_id"));
        }
    }
}
