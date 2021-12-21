package content.makers;

import model.db.DB;
import util.TemplatesPlaceholder;

import java.util.HashMap;
import java.util.Map;

public class UserProjectsListMaker {
    public String getContent(String userName) {
        StringBuilder result = new StringBuilder();
        Map<String, String> prjs = DB.getDB().getUsersProjectsDB(userName).getProjects();
        if (prjs == null) {
            return TemplatesPlaceholder.getInstance().fillTemplateFromFile("ypwbh", new HashMap());
        }
        Map<String, String> vals;
        for (String s : prjs.keySet()) {
            vals = new HashMap();
            vals.put("prjuuid", s);
            vals.put("prjname", prjs.get(s));
            result.append(TemplatesPlaceholder.getInstance()
                    .fillTemplateFromFile("pa_proj_tokens_tamplate", vals));
        }
        return result.toString();
    }
}
