package content.makers;

import model.db.DB;
import util.TemplatesPlaceholder;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectPageContentMaker {
    private Map<String, String> contentSlots;

    public ProjectPageContentMaker() {
        contentSlots = new HashMap();
        contentSlots.put("username", "");
        contentSlots.put("project_name", "");
        contentSlots.put("open_closed", "open");
        contentSlots.put("on_page", "10");
        contentSlots.put("page", "1");
        contentSlots.put("choose_open", "choosed");
        contentSlots.put("choose_closed", "unchoosed");
        contentSlots.put("choosed_ope_10", "");
        contentSlots.put("choosed_ope_20", "");
        contentSlots.put("choosed_ope_30", "");
        contentSlots.put("uuid", "");
        contentSlots.put("content", "");
        contentSlots.put("page", "1");
        contentSlots.put("theme", "sweet_home");
    }

    public String getProjectPage(Map<String, String> parameters, HttpServletRequest req){
        Map<String, String> contentSlotsBuff = new HashMap(contentSlots);
        for (String key : parameters.keySet()){
            contentSlotsBuff.put(key, parameters.get(key));
        }
        contentSlotsBuff.put("choosed_ope_"+contentSlotsBuff.get("on_page"), "choosed_ope");
        if(contentSlotsBuff.get("open_closed").equals("close")){
            contentSlotsBuff.put("choose_open", "unchoosed");
            contentSlotsBuff.put("choose_closed", "choosed");
        }
        List<Map<String, Object>> bugs = DB.getDB().getBugsDB().getBugsFromProject(parameters.get("uuid"));
        List<Map<String, Object>> current = new ArrayList();
        boolean selectedStatusIsOpen = !contentSlotsBuff.get("open_closed").startsWith("close");
        for(Map<String, Object> m : bugs){
            if(selectedStatusIsOpen){
                if(m.get("solved_status").equals("open")){
                    current.add(m);
                }
            }else{
                if(!m.get("solved_status").equals("open")){
                    current.add(m);
                }
            }
        }

        contentSlotsBuff.put("theme", Theme.getInstance().getThemeName(req));
        contentSlotsBuff.put("page",
                String.valueOf(
                        correctPage(parameters.get("page")!=null ? Integer.parseInt(parameters.get("page")) : 1
                                , current.size(), parameters)));
        contentSlotsBuff.put("content", getContent(contentSlotsBuff, current));
        return TemplatesPlaceholder.getInstance().fillTemplateFromFile("project_page", contentSlotsBuff);
    }

    private String getContent(Map<String, String> parameters, List<Map<String, Object>> bugs){
        StringBuilder result = new StringBuilder();
        int bugsNumber = bugs.size();
        int bugsOnPage = (parameters.get("on_page")!= null) ? Integer.parseInt(parameters.get("on_page")) : 10;
        int page = Integer.parseInt(parameters.get("page"));
        boolean selectedStatus = parameters.get("open_closed").equals("close");
        int count = page*(bugsOnPage)+1;
        for (int i = (page-1)*bugsOnPage; i<count && i<bugsNumber; i++){
            if(selectedStatus){
                if(!bugs.get(i).get("solved_status").equals("open")){
                    result.append(bugTicket(bugs.get(i)));
                }
            }else{
                if(bugs.get(i).get("solved_status").equals("open")){
                    result.append(bugTicket(bugs.get(i)));
                }
            }
        }
        return result.toString();
    }

    private int correctPage(int page, int bugsNumber, Map<String, String> parameters){
        int bugsOnPage = (parameters.get("on_page")!= null) ? Integer.parseInt(parameters.get("on_page")) : 10;
        int pagesNumber = (int) Math.ceil(((float)bugsNumber-1.0)/(float)bugsOnPage);
        if(pagesNumber == 0){
            pagesNumber++;
        }
        if(page<1){
            return 1;
        }
        if(page>pagesNumber){
            return pagesNumber;
        }
        return page;
    }

    private String bugTicket(Map<String, Object> bug){
        Map<String, String> vals = new HashMap();
        vals.put("bug_number", String.valueOf(bug.get("ticket_number")));
        vals.put("upload_date", String.valueOf(bug.get("upload_date")));
        vals.put("upload_by", String.valueOf(bug.get("uploaded_by")));
        vals.put("bug_id", String.valueOf(bug.get("id")));
        vals.put("uuid", String.valueOf(bug.get("uuid")));
        return TemplatesPlaceholder.getInstance().fillTemplateFromFile("project_bug_servis_box", vals);
    }
}
