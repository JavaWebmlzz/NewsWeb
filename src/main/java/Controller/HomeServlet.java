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
import java.util.Map;

// 拦截网站首页请求
@WebServlet("")
public class HomeServlet extends HttpServlet {

    private final NewsService newsService = new NewsServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. 接收参数并处理默认值
        int page = 1;
        int pageSize = 5; // 每页显示5条，方便测试分页效果
        Integer categoryId = null;
        String keyword = req.getParameter("keyword"); // 搜索关键词

        // 处理 page 参数
        String pageStr = req.getParameter("page");
        if (pageStr != null && pageStr.matches("\\d+")) {
            page = Integer.parseInt(pageStr);
        }

        // 处理 categoryId 参数
        String catStr = req.getParameter("categoryId");
        if (catStr != null && catStr.matches("\\d+")) {
            categoryId = Integer.parseInt(catStr);
        }

        // 2. 调用 Service 获取“分页大礼包”
        Map<String, Object> data = newsService.getNewsPage(page, pageSize, categoryId, keyword);

        // 3. 将数据存入 Request 域
        req.setAttribute("newsList", data.get("list"));
        req.setAttribute("pagination", data); // 包含 currentPage, totalPage

        // 4. 回显当前的筛选条件 (让前端知道现在搜的是什么、在哪个分类)
        req.setAttribute("currentCategory", categoryId);
        req.setAttribute("currentKeyword", keyword);

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

}