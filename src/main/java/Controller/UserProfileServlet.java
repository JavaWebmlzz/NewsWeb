package Controller;

import Util.HttpUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 询问队友：这个用户属于哪个分类？(1教育, 2科技, 3体育, 4娱乐)
 */
@WebServlet("/api/mock-external-profile")
public class UserProfileServlet extends HttpServlet {

    // 队友服务器地址 (用于 Ping 存活检测)
    private static final String TEAMMATE_URL = "http://10.100.164.13:8080/admin.html";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json;charset=UTF-8");

        String visitorId = req.getParameter("visitorId");

        // 1. 检测队友服务器是否活着
        String status = HttpUtil.get(TEAMMATE_URL);

        // 2. 模拟队友返回的分类 ID
        // 规则：根据 visitorId 的哈希值，均匀分配到 1-4
        // 1=教育, 2=科技, 3=体育, 4=娱乐
        String targetCat = "2"; // 默认科技

        if (visitorId != null) {
            // 简单的取模算法，让不同用户看到不同分类
            int hash = Math.abs(visitorId.hashCode());
            int catId = (hash % 4) + 1; // 结果为 1, 2, 3, 4
            targetCat = String.valueOf(catId);
        }

        // 3. 返回 JSON
        // 这里的 shopping_cat 就是队友返回给我们的分类ID
        String json = String.format(
                "{\"code\": 200, \"data\": {\"shopping_cat\": \"%s\", \"source\": \"%s\"}}",
                targetCat,
                (status != null ? "connected" : "mock_fallback")
        );

        System.out.println("🔗 [画像查询] User=" + visitorId + " -> 归类为=" + targetCat);
        resp.getWriter().write(json);
    }
}