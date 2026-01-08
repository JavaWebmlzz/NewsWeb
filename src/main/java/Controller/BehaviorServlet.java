package Controller;

import Util.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/api/behavior")
public class BehaviorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String visitorId = req.getParameter("visitorId");
        String categoryIdStr = req.getParameter("categoryId");
        String type = req.getParameter("type");

        if (visitorId == null || categoryIdStr == null) return;

        try {
            int categoryId = Integer.parseInt(categoryIdStr);
            long scoreToAdd = 0;

            // ⭐ 演示专用：三级权重体系 ⭐
            switch (type) {
                case "click_ad":
                    // 1. 点击广告：超级加倍
                    scoreToAdd = 50L;
                    System.out.println("🖱️ [高权] 点击广告！User=" + visitorId + " Cat=" + categoryId + " (+50)");
                    break;

                case "open_news":
                    // 2. 打开/刷新新闻：中等权重
                    // 只要进来了，就说明想看，必须加分
                    scoreToAdd = 10L;
                    System.out.println("📖 [中权] 打开新闻！User=" + visitorId + " Cat=" + categoryId + " (+10)");
                    break;

                case "stay":
                    // 3. 停留观看：低权重累积
                    scoreToAdd = 2L;
                    // System.out.println("CLOCK [低权] 正在阅读... (+1)"); // 嫌吵可以注释掉
                    break;
            }

            if (scoreToAdd > 0) {
                updateScore(visitorId, categoryId, scoreToAdd);
            }
            resp.setStatus(200);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateScore(String vid, int catId, long score) {
        String sql = "INSERT INTO user_preference (visitor_id, category_id, score) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE score = score + ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vid);
            ps.setInt(2, catId);
            ps.setLong(3, score);
            ps.setLong(4, score);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}