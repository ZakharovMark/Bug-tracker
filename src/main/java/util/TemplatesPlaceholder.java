package util;

import java.util.Map;

public class TemplatesPlaceholder {
    private static TemplatesPlaceholder instance;
    private String keySymbol = "$";

    private TemplatesPlaceholder() {
    }

    public synchronized static TemplatesPlaceholder getInstance(){
        if(instance == null){
            instance = new TemplatesPlaceholder();
        }
        return instance;
    }

    public String disableKeys(String checkThisString){
        String result = "";
        String lastRes = ".";
        while(!result.equals(lastRes)){
            lastRes = result;
            result=checkThisString.replace(keySymbol, "S");
        }
        return result;
    }

    public String fillTemplate(String template, Map<String, String> values){
        String result = "";
        String buff = template;
        if (values.keySet().size() == 0){
            return template;
        }
        for(String key : values.keySet()){
            while(true){
                result=buff.replace(keySymbol+"{"+key+"}", values.get(key));
                if(buff.equals(result)){
                    break;
                }
                buff = result;
            }
        }
        return result;
    }

    public String fillTemplateFromFile(String fileName, Map<String, String> values){
        return fillTemplate(Util.readHTMLToString(fileName), values);
    }
}
