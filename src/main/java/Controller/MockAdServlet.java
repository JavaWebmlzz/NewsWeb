package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/mock-ad")
public class MockAdServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 强制 JSON 格式
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String categoryIdStr = req.getParameter("categoryId");
        String visitorId = req.getParameter("visitorId");

        // 默认广告 (Starbucks)
        String imageUrl = "https://images.unsplash.com/photo-1497935586351-b67a49e012bf?w=600&auto=format&fit=crop&q=60";
        String linkUrl = "https://www.starbucks.com/";
        String adTitle = "Coffee Time";

        System.out.println("[MockAd] Request: cat=" + categoryIdStr + ", user=" + visitorId);

        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            String cat = categoryIdStr.trim();
            if ("2".equals(cat)) {
                // Tech (Apple)
                imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=60";
                linkUrl = "https://www.apple.com/";
                adTitle = "iPhone 16 Pro";
            } else if ("3".equals(cat)) {
                // Sports (Nike)
                imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=60";
                linkUrl = "https://www.nike.com/";
                adTitle = "Just Do It";
            }
        }

        // 手动拼接 JSON，避免 String.format 可能带来的格式问题
        // 注意：这里没有中文，绝对安全
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"code\": 200,");
        json.append("\"message\": \"success\",");
        json.append("\"data\": {");
        json.append("\"imageUrl\": \"").append(imageUrl).append("\",");
        json.append("\"linkUrl\": \"").append(linkUrl).append("\",");
        json.append("\"title\": \"").append(adTitle).append("\"");
        json.append("}");
        json.append("}");

        System.out.println("[MockAd] Response: " + json.toString());
        resp.getWriter().write(json.toString());
    }
}