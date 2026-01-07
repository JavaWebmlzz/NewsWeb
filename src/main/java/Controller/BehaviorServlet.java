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

    // 接收 POST 请求：用户浏览结束或心跳包
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 允许跨域 (解决 Shopping 网站调用问题)
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String visitorId = req.getParameter("visitorId");
        String categoryIdStr = req.getParameter("categoryId");
        String durationStr = req.getParameter("duration"); // 停留秒数

        if (visitorId == null || categoryIdStr == null) return;

        int categoryId = Integer.parseInt(categoryIdStr);
        int duration = Integer.parseInt(durationStr);

        // 算法逻辑：停留时间越长，加分越多
        // < 3秒: 不加分 (误触)
        // 3-10秒: +1分
        // 10-30秒: +3分
        // > 30秒: +5分
        int scoreToAdd = 0;
        if (duration > 30) scoreToAdd = 5;
        else if (duration > 10) scoreToAdd = 3;
        else if (duration > 3) scoreToAdd = 1;

        if (scoreToAdd > 0) {
            updateScore(visitorId, categoryId, scoreToAdd);
        }

        resp.getWriter().write("ok");
    }

    private void updateScore(String vid, int catId, int score) {
        // "存在即更新，不存在即插入" (Upsert)
        String sql = "INSERT INTO user_preference (visitor_id, category_id, score) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE score = score + ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vid);
            pstmt.setInt(2, catId);
            pstmt.setInt(3, score); // 初始值
            pstmt.setInt(4, score); // 累加值
            pstmt.executeUpdate();
            System.out.println("📈 [行为记录] 用户 " + vid + " 分类 " + catId + " 积分 +" + score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
