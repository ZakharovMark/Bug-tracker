package servlets;

import content.makers.Theme;
import util.SessionCheck;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String rec = ((HttpServletRequest)servletRequest).getRequestURI();
        if(!(rec.equals("/Kurs_war/") || rec.equals("/Kurs_war/login") || rec.equals("/Kurs_war/register") || rec.startsWith("/Kurs_war/resources"))){
            boolean success = SessionCheck.success((HttpServletRequest)servletRequest);
            if(!success){
                ((HttpServletResponse)servletResponse).sendRedirect(((HttpServletRequest)servletRequest).getContextPath() + "/login");
                return;
            }
            HttpSession session = ((HttpServletRequest)servletRequest).getSession();
            session.setAttribute("theme", setTheme(((HttpServletRequest)servletRequest)));
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String setTheme(HttpServletRequest rec){
        HttpSession session = rec.getSession();
        String theme = null;
        String tapTheme = null;
        if(session != null){
            theme = (String) session.getAttribute("theme");
            if (theme == null) {
                return "sweet_home";
            }
            tapTheme = rec.getParameter("theme");
            if(tapTheme != null){
                theme = Theme.getInstance().changeTheme(theme);
            }
        }
        return theme;
    }
}
