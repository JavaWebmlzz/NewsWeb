package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 这是一个 "模拟" 的 Servlet，用来代替你组员的广告系统 API。
 * 真正上线时，前端会直接请求你组员的 URL，而不是这个。
 */
@WebServlet("/api/mock-ad")
public class MockAdServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置响应类型为 JSON
        resp.setContentType("application/json;charset=UTF-8");
        // 允许跨域 (CORS) - 方便后续调试
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // 获取参数，比如根据新闻分类推荐广告
        String category = req.getParameter("category");

        PrintWriter out = resp.getWriter();

        // 模拟简单的个性化推荐逻辑
        String jsonResponse;
        if ("tech".equals(category)) {
            // 数码类广告
            jsonResponse = """
                {
                    "code": 200,
                    "data": {
                        "title": "最新款智能手机 - 限时特惠",
                        "imageUrl": "https://cn.bing.com/th?id=OHR.RedSquirrel_ZH-CN8306063467_1920x1080.jpg&rf=LaDigue_1920x1080.jpg",
                        "linkUrl": "https://jd.com"
                    }
                }
            """;
        } else {
            // 通用广告
            jsonResponse = """
                {
                    "code": 200,
                    "data": {
                        "title": "美味咖啡 - 开启活力一天",
                        "imageUrl": "https://cn.bing.com/th?id=OHR.HidingFox_ZH-CN8799636283_1920x1080.jpg&rf=LaDigue_1920x1080.jpg",
                        "linkUrl": "https://starbucks.com"
                    }
                }
            """;
        }

        out.print(jsonResponse);
        out.flush();
    }
}
