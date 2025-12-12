package Util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class DBUtil {
    private static Properties props = new Properties();

    static {
        try {
            // 加载 src/main/resources 下的配置文件
            InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            props.load(in);
            Class.forName(props.getProperty("db.driver"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("加载数据库配置失败");
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
        );
    }

    // 释放资源的通用方法
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}