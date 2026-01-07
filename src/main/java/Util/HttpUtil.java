package Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpUtil {
        /**
         * 发送 GET 请求
         * @return 返回响应内容，如果连接失败返回 null
         */
        public static String get(String urlStr) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2000); // 2秒超时，别让队友拖死我们
                conn.setReadTimeout(2000);

                // 假装自己是浏览器，防止对方服务器拦截
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = conn.getResponseCode();
                System.out.println("📡 HttpUtil请求: " + urlStr + " | 状态码: " + responseCode);

                if (responseCode == 200) {
                    // 连接成功！读取内容（虽然我们可能不用内容，但要读完流）
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        return "OK"; // 只要通了，就返回 "OK"，不返回那一堆 HTML 了
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ 连接队友服务器失败: " + e.getMessage());
            } finally {
                if (conn != null) conn.disconnect();
            }
            return null;
        }

    /**
     * 检测远程 URL 是否有效 (返回 200 表示存在)
     * 使用 HEAD 请求，速度快，不下载文件内容
     */
    public static boolean isUrlValid(String urlStr) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD"); // 关键：只请求头信息
            conn.setConnectTimeout(1000);  // 1秒超时，快速跳过
            conn.setReadTimeout(1000);
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}