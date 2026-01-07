package Controller;

import Util.HttpUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import Util.DBUtil; // 记得导入 DBUtil

@WebServlet("/api/ad-recommend")
public class AdRecommendServlet extends HttpServlet {

    // 队友的视频基础路径
    private static final String VIDEO_BASE_URL = "http://10.100.164.13:8080/uploads/ads/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String visitorId = req.getParameter("visitorId");

        // 1. 算法核心：查询数据库获取用户最喜欢的分类
        int targetCatId = 0;
        String strategy = "random_cold_start";

        int favoriteCat = getUserFavoriteCategory(visitorId);
        if (favoriteCat > 0) {
            targetCatId = favoriteCat;
            strategy = "personalized_history";
        } else {
            targetCatId = new Random().nextInt(4) + 1; // 随机 1-4
        }

        // ==========================================
        // 2. 【核心修改】智能寻找可用视频 (Probe Logic)
        // ==========================================
        String finalVideoUrl = "";

        // 既然队友命名不规律 (如 3_3.mp4)，我们循环检测 index 1 到 5
        for (int i = 1; i <= 5; i++) {
            // 构造尝试的 URL，例如 .../ads/3_1.mp4, .../ads/3_3.mp4
            String tryUrl = VIDEO_BASE_URL + targetCatId + "_" + i + ".mp4";

            // 探针检测：这个文件存在吗？
            if (HttpUtil.isUrlValid(tryUrl)) {
                finalVideoUrl = tryUrl;
                System.out.println("✅ [资源检测] 找到可用视频: " + tryUrl);
                break; // 找到了就停止
            } else {
                System.out.println("❌ [资源检测] 文件不存在: " + tryUrl);
            }
        }

        // 3. 【兜底逻辑】如果循环完都没找到 (比如分类4下面没有视频)
        // 强制使用一个我们知道一定存在的视频 (比如 2_1.mp4 科技)
        if (finalVideoUrl.isEmpty()) {
            System.out.println("⚠️ [资源告警] 分类 " + targetCatId + " 下没找到视频，使用默认兜底。");
            finalVideoUrl = VIDEO_BASE_URL + "2_1.mp4"; // 确保这个文件队友服务器上有！
            strategy = "fallback_default";
        }

        String title = getCategoryName(targetCatId) + (favoriteCat > 0 ? " (猜你喜欢)" : " (热门推荐)");

        System.out.println("🤖 [推荐算法] User=" + visitorId + " | 策略=" + strategy + " | 最终播放=" + finalVideoUrl);

        // 4. 返回 JSON
        String json = String.format(
                "{\"code\": 200, \"message\": \"success\", \"data\": {\"url\": \"%s\", \"linkUrl\": \"#\", \"title\": \"%s\", \"type\": \"video\"}}",
                finalVideoUrl, title
        );
        resp.getWriter().write(json);
    }

    /**
     * 读取数据库，找到分数最高的分类
     */
    private int getUserFavoriteCategory(String vid) {
        if (vid == null) return 0;
        String sql = "SELECT category_id FROM user_preference WHERE visitor_id = ? ORDER BY score DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("category_id");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private String getCategoryName(int id) {
        switch (id) {
            case 1: return "在线教育";
            case 2: return "前沿科技";
            case 3: return "体育运动";
            case 4: return "娱乐影视";
            default: return "精彩广告";
        }
    }
}