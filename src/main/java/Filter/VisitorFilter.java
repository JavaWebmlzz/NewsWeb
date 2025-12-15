package Filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

// 拦截所有请求，确保每个用户都有 visitor_id
@WebFilter("/*")
public class VisitorFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 1. 检查是否已有 visitor_id Cookie
        String visitorId = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("visitor_id".equals(cookie.getName())) {
                    visitorId = cookie.getValue();
                    break;
                }
            }
        }

        // 2. 如果没有，生成一个新的 UUID 并写入 Cookie
        if (visitorId == null) {
            visitorId = UUID.randomUUID().toString();
            Cookie newCookie = new Cookie("visitor_id", visitorId);
            newCookie.setMaxAge(60 * 60 * 24 * 30); // 有效期30天
            newCookie.setPath("/"); // 全站有效
            resp.addCookie(newCookie);
            System.out.println("🍪 新访客，生成 ID: " + visitorId);
        } else {
            // System.out.println("✅ 老访客 ID: " + visitorId); // 调试用，嫌吵可注释
        }

        // 3. 将 visitorId 放入 Request 域，方便后续 Servlet/JSP 使用
        req.setAttribute("visitorId", visitorId);

        chain.doFilter(request, response);
    }
}