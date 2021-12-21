package content.makers;

import model.db.DB;
import util.TemplatesPlaceholder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BugPageContentMaker {
    private Map<String, String> contentSlots;

    public BugPageContentMaker() {
        contentSlots = new HashMap();
        contentSlots.put("username", "");
        contentSlots.put("bug_number", "");
        contentSlots.put("bug_status", "");
        contentSlots.put("bug_id", "-1");
        contentSlots.put("report_date", "");
        contentSlots.put("reported_by", "");
        contentSlots.put("description", "");
        contentSlots.put("answers", "");
        contentSlots.put("bug_post_hidden", "hidden");
        contentSlots.put("bug_edit_hidden", "hidden");
        contentSlots.put("bug_delete_hidden", "hidden");
        contentSlots.put("hidden_answers_hud", "hidden");
        contentSlots.put("theme", "sweet_home");
    }

    public String createBugPage(String userName, String projectUUID, HttpServletRequest req) {
        Map<String, String> vals = new HashMap();
        vals.put("username", userName);
        vals.put("bug_number", String.valueOf(DB.getDB().getProjectDB().getBugsNumber(projectUUID)));
        vals.put("bug_status", "open");
        vals.put("report_date", "right now");
        vals.put("reported_by", userName);
        vals.put("bug_post_hidden", "");
        vals.put("uuid", projectUUID);
        vals.put("theme", Theme.getInstance().getThemeName(req));
        vals.put("project_name", DB.getDB().getProjectDB().getNameByUUID(projectUUID));
        return fillPage(vals);
    }

    public String viewBug(String userName, int bug_id, String projectUUID, HttpServletRequest req) {
        Map<String, String> vals = new HashMap();
        Map<String, Object> bug = DB.getDB().getBugsDB().getBug(bug_id);
        vals.put("username", userName);
        vals.put("bug_number", String.valueOf((Integer) bug.get("ticket_number")));
        String status = (String) bug.get("solved_status");
        String solutionId = String.valueOf(bug.get("bug_solution_id"));
        if (!solutionId.equals("0")) {
            status += DB.getDB().getAnswersDb().getAnswerAttributes(solutionId).get("bug_or_ficha")!=null ?
                    DB.getDB().getAnswersDb().getAnswerAttributes(solutionId).get("bug_or_ficha") : "";
        }
        vals.put("bug_status", status);
        vals.put("report_date", String.valueOf((Date) bug.get("upload_date")));
        vals.put("reported_by", (String) bug.get("uploaded_by"));
        vals.put("bug_id", String.valueOf(bug_id));
        vals.put("project_name", DB.getDB().getProjectDB().getNameByUUID(projectUUID));
        vals.put("uuid", projectUUID);
        if (userName.equals(bug.get("uploaded_by")) && bug.get("solved_status").equals("open")) {
            vals.put("bug_edit_hidden", "");
            vals.put("bug_delete_hidden", "");
        }
        vals.put("hidden_answers_hud", "");
        vals.put("theme", Theme.getInstance().getThemeName(req));
        vals.put("answers", new AnswersMaker().answers(bug_id, userName));
        vals.put("description", String.valueOf(DB.getDB().getBugsDB().getBug(bug_id).get("bug_report")));
        return fillPage(vals);
    }

    private String fillPage(Map<String, String> content) {
        Map<String, String> tempContentSlots = new HashMap(contentSlots);
        tempContentSlots.putAll(content);
        return TemplatesPlaceholder.getInstance().fillTemplateFromFile("bug", tempContentSlots);
    }

    private class AnswersMaker {
        private String answers(int bugId, String username) {
            List<Map<String, Object>> answers = DB.getDB().getAnswersDb().getAllAnswersByBug(bugId);
            StringBuffer sb = new StringBuffer();
            for (Map<String, Object> answer : answers) {
                sb.append(constructAnswer(answer, username, bugId));
                sb.append("\n");
            }
            return sb.toString();
        }

        private String constructAnswer(Map<String, Object> bugAttributes, String username, int bugId) {
            Map<String, String> vals = new HashMap();
            vals.put("id", String.valueOf(bugAttributes.get("id")));
            vals.put("bug_id", String.valueOf(bugId));
            vals.put("autor", String.valueOf(bugAttributes.get("uploaded_by")));
            vals.put("date", String.valueOf(bugAttributes.get("upload_date")));
            vals.put("bug_or_ficha", String.valueOf(bugAttributes.get("bug_or_ficha")));
            if (vals.get("autor").equals(username) && !(Boolean) bugAttributes.get("is_solution")) {
                vals.put("hidden_solution_choose", "");
                vals.put("readonly", "");
            } else {
                vals.put("hidden_solution_choose", "hidden");
                vals.put("readonly", "readonly");
            }
            if ((Boolean) bugAttributes.get("is_solution")) {
                vals.put("solution_title_hidden", "");
            } else {
                vals.put("solution_title_hidden", "hidden");
            }
            if (username
                    .equals(String.valueOf(DB
                            .getDB()
                            .getBugsDB()
                            .getBug(bugId)
                            .get("uploaded_by")))) {
                vals.put("hidden_choose_as_true_solution", "");
            } else {
                vals.put("hidden_choose_as_true_solution", "hidden");
            }
            vals.put("description", String.valueOf(bugAttributes.get("answer")));
            return TemplatesPlaceholder.getInstance().fillTemplateFromFile("solution", vals);
        }
    }
}