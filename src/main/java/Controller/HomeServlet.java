package Controller;

import Model.News;
import Service.NewsService;
import Service.NewsServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// 拦截网站首页请求
@WebServlet("")
public class HomeServlet extends HttpServlet {

    private final NewsService newsService = new NewsServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. 调用 Service 获取数据 (第1页，取10条)
        List<News> newsList = newsService.getLatestNews(1, 10);

        // 2. 将数据存入 Request 域，以便 JSP 读取
        req.setAttribute("newsList", newsList);

        // 3. 转发到 JSP 页面渲染
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}