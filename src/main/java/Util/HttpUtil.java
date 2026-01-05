package Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpUtil {

    // 发送 GET 请求并返回 JSON 字符串
    public static String get(String urlStr) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000); // 3秒超时，防止队友服务器卡死
            conn.setReadTimeout(3000);

            int responseCode = conn.getResponseCode();
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
            } else {
                System.err.println("❌ 请求失败: " + urlStr + " Code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("❌ 网络连接异常: " + e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null; // 请求失败返回空
    }
}