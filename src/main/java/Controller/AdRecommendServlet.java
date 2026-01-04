package Controller;

import Util.DBUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import java.io.IOException;


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

        // 1. 设置响应头 (JSON + 跨域)
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // 2. 获取参数
        String visitorId = req.getParameter("visitorId");

        // 参数A: 当前正在看的新闻分类 (Context)
        String currentCatStr = req.getParameter("categoryId");
        int currentCatId = (currentCatStr != null && !currentCatStr.isEmpty()) ? Integer.parseInt(currentCatStr) : 0;

        // 参数B: 【新增】从广告平台(购物网站)同步过来的外部画像 (External Profile)
        String externalCatStr = req.getParameter("externalCat");
        int externalCatId = 0;
        try {
            if (externalCatStr != null && !externalCatStr.isEmpty() && !"none".equals(externalCatStr)) {
                externalCatId = Integer.parseInt(externalCatStr);
            }
        } catch (NumberFormatException e) {
            // 忽略转换错误
        }

        System.out.println("🤖 [AdAlgo] User=" + visitorId + ", ContextCat=" + currentCatId + ", ExternalCat=" + externalCatId);

        // 3. 获取所有候选广告 (从数据库 ad_pool)
        List<AdItem> ads = getAllAdsFromPool();

        // 4. 核心推荐算法 (加权计算)
        AdItem bestAd = null;
        double maxScore = -999; // 初始最低分

        for (AdItem ad : ads) {
            // --- 基础分 (0-5分随机) ---
            // 作用：让广告保持一定的随机性，不会永远只显示同一个
            double score = Math.random() * 5;

            // --- 维度A: 上下文加权 (Context) ---
            // 作用：正在看什么，就推什么
            if (ad.categoryId == currentCatId) {
                score += 30.0;
                System.out.println("   -> [" + ad.title + "] 命中上下文 (+30)");
            }

            // --- 维度B: 外部跨域画像加权 (External/Shopping) ---
            // 作用：模拟从购物网站过来的数据，权重最高，体现"精准追踪"
            if (ad.categoryId == externalCatId) {
                score += 50.0;
                System.out.println("   -> [" + ad.title + "] 命中购物画像 (+50) !!!");
            }

            // --- 维度C: 历史行为加权 (Internal History) ---
            // 作用：基于在新闻网站内部的停留时间积累
            int userInterestScore = getUserScoreForCategory(visitorId, ad.categoryId);
            if (userInterestScore > 0) {
                score += userInterestScore * 1.5; // 系数 1.5
                System.out.println("   -> [" + ad.title + "] 命中历史兴趣 (+" + (userInterestScore*1.5) + ")");
            }

            // 记录最终得分
            ad.finalScore = score;

            // 擂台赛：谁分高谁留下
            if (score > maxScore) {
                maxScore = score;
                bestAd = ad;
            }
        }

        // 5. 返回 JSON 结果
        if (bestAd != null) {
            // 在标题里显示分数，方便演示时查看效果
            String debugTitle = bestAd.title + " (Score: " + String.format("%.1f", bestAd.finalScore) + ")";

            // 手动拼接 JSON (避免引入额外库)
            String json = String.format(
                    "{\"code\": 200, \"message\": \"success\", \"data\": {\"imageUrl\": \"%s\", \"linkUrl\": \"%s\", \"title\": \"%s\"}}",
                    bestAd.imageUrl, bestAd.linkUrl, debugTitle
            );
            resp.getWriter().write(json);
        } else {
            // 兜底：如果没有广告
            resp.getWriter().write("{\"code\": 404, \"message\": \"No Ad Found\"}");
        }
    }

    // ==========================================
    // 下面是辅助方法 (DB操作)
    // ==========================================

    /**
     * 从数据库 ad_pool 表加载所有广告
     */
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
        // 如果数据库没数据，加几个硬编码的防止空指针（仅供测试）
        if (list.isEmpty()) {
            list.add(new AdItem("Default Ad", "...", "...", 0));
        }
        return list;
    }

    /**
     * 获取用户对某分类的兴趣分 (从 user_profile 表解析 JSON)
     */
    private int getUserScoreForCategory(String vid, int catId) {
        if (vid == null) return 0;

        // MySQL 5.7+ 支持 JSON_EXTRACT
        String sql = "SELECT JSON_EXTRACT(interest_json, CONCAT('$.\"', ?, '\"')) FROM user_profile WHERE visitor_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(catId));
            pstmt.setString(2, vid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // JSON_EXTRACT 可能返回 NULL，需要判空
                    String val = rs.getString(1);
                    if (val != null) {
                        return Integer.parseInt(val);
                    }
                }
            }
        } catch (Exception e) {
            // 忽略 JSON 解析错误或无记录情况
        }
        return 0; // 默认0分
    }
}