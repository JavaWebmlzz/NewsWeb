package Dao;


import Model.News;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


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
    public List<News> selectByPage(int offset, int limit, Integer categoryId, String keyword) throws Exception {
        // 1. 准备参数列表和基础 SQL
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM news WHERE is_deleted = 0 ");

        // 2. 动态拼接条件：分类筛选
        if (categoryId != null && categoryId > 0) {
            sql.append("AND category_id = ? ");
            params.add(categoryId);
        }

        // 3. 动态拼接条件：关键词搜索 (模糊查询)
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND title LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }

        // 4. 拼接排序和分页
        sql.append("ORDER BY publish_time DESC LIMIT ?, ?");
        params.add(offset);
        params.add(limit);

        List<News> list = new ArrayList<>();

        // 5. 执行查询
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // 循环设置参数 (因为参数数量是不固定的)
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 复用之前的映射方法
                    list.add(mapRowToNews(rs));
                }
            }
        }
        return list;
    }


    @Override
    public int count(Integer categoryId, String keyword) throws Exception {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT count(*) FROM news WHERE is_deleted = 0 ");

        if (categoryId != null && categoryId > 0) {
            sql.append("AND category_id = ? ");
            params.add(categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND title LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
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

    @Override
    public void incrementViewCount(Integer id) throws Exception {
        String sql = "UPDATE news SET view_count = view_count + 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
