package Controller;
import Util.HttpUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
/**
 * 负责与【队友服务器】通信的中转站
 */
@WebServlet("/api/mock-external-profile")
public class UserProfileServlet extends HttpServlet {

    // ⚠️⚠️⚠️ 队友的 IP 和端口 (必须填对) ⚠️⚠️⚠️
    private static final String REMOTE_HOST = "http://10.100.164.12:8080";
    // ⚠️⚠️⚠️ 队友的用户画像接口路径 (必须填对) ⚠️⚠️⚠️
    private static final String API_PATH = "/api/user/profile";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json;charset=UTF-8");

        String visitorId = req.getParameter("visitorId");

        // 1. 尝试连接队友服务器
        String remoteUrl = REMOTE_HOST + API_PATH + "?visitorId=" + visitorId;
        System.out.println("🔗 正在查询队友接口: " + remoteUrl);

        // 使用我们写的 HttpUtil 发请求
        String jsonResult = HttpUtil.get(remoteUrl);

        // 2. 检查结果
        if (jsonResult != null && !jsonResult.isEmpty()) {
            System.out.println("✅ 队友服务器响应成功: " + jsonResult);
            // 直接把队友的 JSON 转发给前端
            resp.getWriter().write(jsonResult);
        } else {
            // 3. 【兜底】如果队友服务器挂了，返回本地模拟数据
            // 这样演示时绝对不会报错
            System.out.println("⚠️ 队友服务器未响应，启用本地 Mock 数据");
            // 默认返回：喜欢科技 (2)
            String fallbackJson = "{\"code\": 200, \"data\": {\"shopping_cat\": \"2\"}}";
            resp.getWriter().write(fallbackJson);
        }
    }
}
