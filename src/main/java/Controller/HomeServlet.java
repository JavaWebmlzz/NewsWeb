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

        // 1. 获取分类参数 (URL类似于 /?categoryId=2)
        String catStr = req.getParameter("categoryId");
        Integer categoryId = null;
        if (catStr != null && catStr.matches("\\d+")) {
            categoryId = Integer.parseInt(catStr);
        }

        // 2. 调用 Service (传入分类ID)
        List<News> newsList = newsService.getLatestNews(1, 10, categoryId);

        // 3. 存入数据
        req.setAttribute("newsList", newsList);
        // 把当前选中的分类ID也传回去，方便前端高亮显示导航栏
        req.setAttribute("currentCategory", categoryId);

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}