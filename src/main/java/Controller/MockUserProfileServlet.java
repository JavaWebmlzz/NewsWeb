package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * 模拟【广告管理平台】提供的用户信息 API
 * 真实场景下，这个接口在队友的项目里，域名可能是 http://ad-platform.com/api/profile
 */
@WebServlet("/api/mock-external-profile")
public class MockUserProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. 允许跨域 (CORS)
        // 因为真实场景下，新闻网和广告网是两个域名，必须加这个头，否则前端 fetch 会报错
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // 2. 设置响应格式为 JSON
        resp.setContentType("application/json;charset=UTF-8");

        // 3. 获取前端传来的访客 ID
        String visitorId = req.getParameter("visitorId");

        // 4. 模拟大数据分析逻辑 (根据 ID 瞎编一个兴趣)
        // 默认没兴趣
        String shoppingInterest = "none";
        String recentAction = "browse_home";

        if (visitorId != null) {
            // 为了演示效果明显：
            // 如果 UUID 包含字母 'a' 或 数字 '1'，就假装他刚买了手机 (Category 2 = 科技)
            if (visitorId.contains("a") || visitorId.contains("1")) {
                shoppingInterest = "2";
                recentAction = "buy_iphone_16";
            }
            // 如果 UUID 包含字母 'b' 或 数字 '2'，就假装他刚买了球鞋 (Category 3 = 体育)
            else if (visitorId.contains("b") || visitorId.contains("2")) {
                shoppingInterest = "3";
                recentAction = "buy_nike_shoes";
            }
        }

        // 5. 构造返回的 JSON 数据
        // 格式: { "code": 200, "data": { "shopping_cat": "2", "recent_action": "..." } }
        String jsonResponse = String.format(
                "{\"code\": 200, \"message\": \"success\", \"data\": {\"shopping_cat\": \"%s\", \"recent_action\": \"%s\"}}",
                shoppingInterest, recentAction
        );

        // 6. 打印日志方便后端观察
        System.out.println("🔗 [MockExternalAPI] 收到新闻站查询请求 | 访客: " + visitorId + " | 返回画像: " + shoppingInterest);

        // 7. 发送响应
        resp.getWriter().write(jsonResponse);

    }
}
