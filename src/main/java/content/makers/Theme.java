package content.makers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Theme {
    private static Theme instance;

    public static Theme getInstance() {
        if (instance == null) {
            instance = new Theme();
        }
        return instance;
    }

    private Theme() {

    }

    public synchronized String getThemeName(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (session == null) {
            return "sweet_home";
        }
        String theme = (String) session.getAttribute("theme");
        if (theme == null) {
            return "sweet_home";
        }
        return theme;
    }

    public synchronized String changeTheme(String theme) {
        return theme.equals("sweet_home") ? "forest" : "sweet_home";
    }
}
