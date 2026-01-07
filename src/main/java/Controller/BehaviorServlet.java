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

@WebServlet("/api/behavior") // <--- 确保路径是这个
public class BehaviorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String visitorId = req.getParameter("visitorId");
        String catStr = req.getParameter("categoryId");
        String type = req.getParameter("type");

        System.out.println("📥 [行为接收] User=" + visitorId + ", Cat=" + catStr + ", Type=" + type);

        if (visitorId != null && catStr != null) {
            try {
                int cid = Integer.parseInt(catStr);
                int score = "click".equals(type) ? 10 : 1;

                // 写入数据库
                String sql = "INSERT INTO user_preference (visitor_id, category_id, score) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE score = score + ?";
                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, visitorId);
                    ps.setInt(2, cid);
                    ps.setInt(3, score);
                    ps.setInt(4, score);
                    ps.executeUpdate();
                    System.out.println("💾 [DB保存] 成功！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        resp.setStatus(200);
    }
}