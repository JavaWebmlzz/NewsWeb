package Controller;

import Util.DBUtil;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/api/ad-recommend")
public class AdRecommendServlet extends HttpServlet {

    // 队友的新接口地址
    private static final String TEAMMATE_API = "http://10.100.164.13:8080/api/ads/randomByPrefix";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String visitorId = req.getParameter("visitorId");

        // ==========================================
        // 1. 算法层：决定推荐哪个分类 (1, 2, 3, 4)
        // ==========================================
        int targetCatId = 0;
        String strategy = "random_cold_start";

        int favoriteCat = getUserFavoriteCategory(visitorId);
        if (favoriteCat > 0) {
            targetCatId = favoriteCat;
            strategy = "personalized_history";
        } else {
            targetCatId = new Random().nextInt(4) + 1; // 随机 1-4
        }

        System.out.println("🤖 [推荐算法] 策略=" + strategy + " | 命中分类ID=" + targetCatId);

        // ==========================================
        // 2. 网络层：调用队友 API 获取广告数据
        // ==========================================
        // 构造 URL: .../randomByPrefix?prefix=2&limit=1
        String remoteApiUrl = TEAMMATE_API + "?prefix=" + targetCatId + "&limit=1";
        String jsonResponse = HttpUtil.get(remoteApiUrl);
        System.out.println("🔍 [调试] 队友API返回原始数据: " + jsonResponse);

        // 默认兜底数据 (万一队友接口挂了)
        String finalUrl = "http://10.100.164.13:8080/uploads/ads/2_1.mp4"; // 随便写个存在的兜底
        String finalTitle = "精彩视频推荐";

        if (jsonResponse != null && jsonResponse.contains("videoFullUrl")) {
            // ==========================================
            // 3. 解析层：提取 JSON 数据
            // ==========================================
            // 队友返回的是个数组: [{"id":9, "videoFullUrl":"...", ...}]
            // 我们用正则提取，避免引入 Jackson/Gson 库导致依赖问题

            // 提取 videoFullUrl
            String url = extractJsonValue(jsonResponse, "videoFullUrl");
            if (url != null) finalUrl = url;

            // 提取 title
            String title = extractJsonValue(jsonResponse, "title");
            if (title != null) finalTitle = title;

            System.out.println("✅ [接口调用] 成功获取队友广告: " + finalTitle + " | " + finalUrl);
        } else {
            System.err.println("❌ [接口调用] 队友API无响应或格式错误: " + remoteApiUrl);
        }

        // ==========================================
        // 4. 返回层：构建前端需要的 JSON
        // ==========================================
        // 你的前端需要: { data: { url: "...", title: "...", type: "video" } }
        String myJson = String.format(
                "{\"code\": 200, \"message\": \"success\", \"data\": {\"url\": \"%s\", \"linkUrl\": \"#\", \"title\": \"%s\", \"type\": \"video\"}}",
                finalUrl, finalTitle
        );
        resp.getWriter().write(myJson);
    }

    /**
     * 简单的正则 JSON 提取器 (不依赖第三方库)
     * 针对: "key": "value" 或 "key":"value"
     */
    private String extractJsonValue(String json, String key) {
        try {
            // 匹配 "key"\s*:\s*"([^"]+)"
            Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
            Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // --- 数据库方法 (保持不变) ---
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
}