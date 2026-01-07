package Controller;

import Util.HttpUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 最终版适配器 Servlet
 * 唯一负责路径: /api/mock-external-profile
 */
@WebServlet("/api/mock-external-profile")
public class UserProfileServlet extends HttpServlet {

    // 队友的地址
    private static final String TEAMMATE_URL = "http://10.100.164.13:8080/admin.html";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json;charset=UTF-8");

        String visitorId = req.getParameter("visitorId");

        // 尝试 Ping 队友
        System.out.println("🔗 [Check] 正在连接队友: " + TEAMMATE_URL);
        String connectionStatus = HttpUtil.get(TEAMMATE_URL);

        String shoppingCat = "1";

        if ("OK".equals(connectionStatus)) {
            // 队友在线 -> 模拟返回数据
            System.out.println("✅ 队友在线");
            if (visitorId != null && visitorId.hashCode() % 2 == 0) {
                shoppingCat = "2"; // 科技
            } else {
                shoppingCat = "3"; // 体育
            }
        } else {
            // 队友离线 -> 兜底
            System.out.println("⚠️ 队友离线，使用兜底");
            shoppingCat = "2";
        }

        String jsonResponse = String.format(
                "{\"code\": 200, \"message\": \"success\", \"data\": {\"shopping_cat\": \"%s\", \"source\": \"%s\"}}",
                shoppingCat,
                ("OK".equals(connectionStatus) ? "remote_system" : "local_fallback")
        );

        resp.getWriter().write(jsonResponse);
    }
}