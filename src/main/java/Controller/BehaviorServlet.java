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
            updateUserProfile(visitorId, categoryId, scoreToAdd);
        }

        resp.getWriter().write("ok");
    }

    // 更新数据库中的 JSON 画像
    private void updateUserProfile(String vid, int catId, int score) {
        // 这里使用 MySQL 8.0 的 JSON 函数进行原子更新
        // 如果记录不存在则插入，存在则更新对应 Key 的值
        String sql = "INSERT INTO user_profile (visitor_id, interest_json) " +
                "VALUES (?, JSON_OBJECT(?, ?)) " +
                "ON DUPLICATE KEY UPDATE " +
                "interest_json = JSON_SET(interest_json, " +
                "CONCAT('$.\"', ?, '\"'), " + // JSON Key 路径
                "COALESCE(JSON_EXTRACT(interest_json, CONCAT('$.\"', ?, '\"')), 0) + ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vid);
            pstmt.setString(2, String.valueOf(catId));
            pstmt.setInt(3, score);
            pstmt.setString(4, String.valueOf(catId)); // Key for JSON_SET
            pstmt.setString(5, String.valueOf(catId)); // Key for JSON_EXTRACT
            pstmt.setInt(6, score);
            pstmt.executeUpdate();
            System.out.println("User " + vid + " category " + catId + " score increased by " + score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
