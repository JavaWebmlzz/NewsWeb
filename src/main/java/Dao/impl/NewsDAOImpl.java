package Dao.impl;


import Dao.NewsDAO;
import Model.News;
import Util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewsDAOImpl implements NewsDAO {

    @Override
    public int insert(News news) throws Exception {
        String sql = "INSERT INTO news (title, content, summary, category_id, publish_time) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, news.getTitle());
            pstmt.setString(2, news.getContent());
            pstmt.setString(3, news.getSummary());
            pstmt.setInt(4, news.getCategoryId());
            return pstmt.executeUpdate();
        }
    }

    @Override
    public News selectById(Integer id) throws Exception {
        String sql = "SELECT * FROM news WHERE id = ? AND is_deleted = 0";
        News news = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    news = mapRowToNews(rs);
                }
            }
        }
        return news;
    }

    @Override
    public List<News> selectByPage(int offset, int limit) throws Exception {
        String sql = "SELECT * FROM news WHERE is_deleted = 0 ORDER BY publish_time DESC LIMIT ?, ?";
        List<News> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, offset);
            pstmt.setInt(2, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToNews(rs));
                }
            }
        }
        return list;
    }

    @Override
    public int count() throws Exception {
        String sql = "SELECT count(*) FROM news WHERE is_deleted = 0";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // 辅助方法：将 ResultSet 映射为对象
    private News mapRowToNews(ResultSet rs) throws SQLException {
        News news = new News();
        news.setId(rs.getInt("id"));
        news.setTitle(rs.getString("title"));
        news.setContent(rs.getString("content"));
        news.setSummary(rs.getString("summary"));
        news.setCoverImage(rs.getString("cover_image"));
        news.setCategoryId(rs.getInt("category_id"));
        news.setViewCount(rs.getInt("view_count"));

        Timestamp ts = rs.getTimestamp("publish_time");
        if (ts != null) {
            news.setPublishTime(ts.toLocalDateTime());
        }
        return news;
    }
}
