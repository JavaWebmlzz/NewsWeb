package Controller;

import Util.DBUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import java.io.IOException;

/**
 * 核心广告推荐算法接口
 * 策略：本地加权算法
 * 1. 接收前端传来的 externalCat (这是从队友服务器查到的画像)
 * 2. 结合当前上下文 (categoryId)
 * 3. 在本地数据库 (ad_pool) 中寻找匹配度最高的广告
 */
@WebServlet("/api/ad-recommend")
public class AdRecommendServlet extends HttpServlet {

    // 内部类：简单的广告对象结构
    static class AdItem {
        String title, imageUrl, linkUrl;
        int categoryId;
        double finalScore = 0; // 计算后的得分

        public AdItem(String t, String i, String l, int c) {
            this.title = t;
            this.imageUrl = i;
            this.linkUrl = l;
            this.categoryId = c;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. 强制设置编码 (防止乱码)
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // 2. 获取参数
        String visitorId = req.getParameter("visitorId");

        // 参数A: 当前正在看的新闻分类 (Context)
        String currentCatStr = req.getParameter("categoryId");
        int currentCatId = (currentCatStr != null && !currentCatStr.isEmpty()) ? Integer.parseInt(currentCatStr) : 0;

        // 参数B: 【关键】前端从队友服务器获取到的画像 (External Profile)
        // 比如：队友返回 "2"，代表用户喜欢科技
        String externalCatStr = req.getParameter("externalCat");
        int externalCatId = 0;
        try {
            if (externalCatStr != null && !externalCatStr.isEmpty() && !"none".equals(externalCatStr)) {
                externalCatId = Integer.parseInt(externalCatStr);
            }
        } catch (NumberFormatException e) {
            // 忽略转换错误
        }

        System.out.println("🤖 [AdAlgo] 计算推荐 | User=" + visitorId + " | 上下文=" + currentCatId + " | 队友画像=" + externalCatId);

        // 3. 获取本地数据库所有广告
        List<AdItem> ads = getAllAdsFromPool();

        // 4. 核心推荐算法 (加权计算)
        AdItem bestAd = null;
        double maxScore = -999;

        for (AdItem ad : ads) {
            // --- 基础分 (0-5分随机) ---
            double score = Math.random() * 5;

            // --- 维度A: 上下文加权 ---
            if (ad.categoryId == currentCatId) {
                score += 30.0;
            }

            // --- 维度B: 队友画像加权 (权重最高) ---
            // 如果本地广告的分类 == 队友告诉我们的兴趣分类
            if (ad.categoryId == externalCatId) {
                score += 50.0;
                System.out.println("   -> [" + ad.title + "] 命中队友画像 (+50) !!!");
            }

            // --- 维度C: 本地历史行为加权 ---
            int userInterestScore = getUserScoreForCategory(visitorId, ad.categoryId);
            if (userInterestScore > 0) {
                score += userInterestScore * 1.5;
            }

            ad.finalScore = score;

            // 擂台赛
            if (score > maxScore) {
                maxScore = score;
                bestAd = ad;
            }
        }

        // 5. 返回 JSON 结果
        if (bestAd != null) {
            String debugTitle = bestAd.title;
            // 如果是根据队友画像推荐的，在标题后加个标记 (方便演示)
            if (bestAd.categoryId == externalCatId && externalCatId > 0) {
                debugTitle += " (跨域推荐)";
            }

            String json = String.format(
                    "{\"code\": 200, \"message\": \"success\", \"data\": {\"imageUrl\": \"%s\", \"linkUrl\": \"%s\", \"title\": \"%s\"}}",
                    bestAd.imageUrl, bestAd.linkUrl, debugTitle
            );
            resp.getWriter().write(json);
        } else {
            // 兜底：如果没有广告，返回默认
            String defaultJson = "{\"code\": 200, \"data\": {\"imageUrl\": \"https://placehold.co/600x400/EEE/31343C?text=News+Ad\", \"linkUrl\": \"#\", \"title\": \"赞助广告\"}}";
            resp.getWriter().write(defaultJson);
        }
    }

    // ==========================================
    // 数据库辅助方法
    // ==========================================

    private List<AdItem> getAllAdsFromPool() {
        List<AdItem> list = new ArrayList<>();
        String sql = "SELECT * FROM ad_pool";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new AdItem(
                        rs.getString("title"),
                        rs.getString("image_url"),
                        rs.getString("link_url"),
                        rs.getInt("category_id")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 防止数据库为空导致报错
        if (list.isEmpty()) {
            list.add(new AdItem("Default Ad", "https://placehold.co/600x400", "#", 0));
        }
        return list;
    }

    private int getUserScoreForCategory(String vid, int catId) {
        if (vid == null) return 0;
        String sql = "SELECT JSON_EXTRACT(interest_json, CONCAT('$.\"', ?, '\"')) FROM user_profile WHERE visitor_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(catId));
            pstmt.setString(2, vid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String val = rs.getString(1);
                    if (val != null) return Integer.parseInt(val);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}