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
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = conn.getResponseCode();

                System.out.println("📡 HttpUtil请求: " + urlStr + " | Code: " + responseCode);

                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        return response.toString();
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ 网络异常: " + e.getMessage());
            } finally {
                if (conn != null) conn.disconnect();
            }
            return null;
        }
}