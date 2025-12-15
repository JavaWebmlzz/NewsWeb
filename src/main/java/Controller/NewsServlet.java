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

@WebServlet("/news")
public class NewsServlet extends HttpServlet {

    private final NewsService newsService = new NewsServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        // 如果没有 action 参数，或者 action 为空，默认行为（可设为跳回首页或报错）
        if (action == null || action.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        if (action.equals("detail")) {
            handleDetail(req, resp);
        } else {// 未知动作，返回 404 或首页
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    private void handleDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");

        if (idStr != null && idStr.matches("\\d+")) {
            int id = Integer.parseInt(idStr);
            News news = newsService.getNewsDetail(id);

            if (news != null) {
                // 👇👇👇 加入这几行调试打印 👇👇👇
                System.out.println("====== DEBUG START ======");
                System.out.println("正在访问新闻 ID: " + news.getId());
                System.out.println("新闻标题: " + news.getTitle());
                System.out.println("分类 ID (Obj): " + news.getCategoryId()); // 重点看这一行输出什么
                System.out.println("====== DEBUG END ======");
                // 👆👆👆 调试结束 👆👆👆

                req.setAttribute("news", news);
                req.getRequestDispatcher("/WEB-INF/views/news/detail.jsp").forward(req, resp);
                return;
            }
        }
        resp.sendError(404, "新闻未找到");
    }
    // 处理新闻详情请求
//
}
//private void handleDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String idStr = req.getParameter("id");
//        try {
//            Integer id = Integer.parseInt(idStr);
//            News news = newsService.getNewsDetail(id);
//
//            if (news == null) {
//                // 查无此新闻，返回 404
//                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "News not found or deleted.");
//                return;
//            }
//
//            // 将新闻对象存入 request，转发给 JSP
//            req.setAttribute("news", news);
//            req.getRequestDispatcher("/WEB-INF/views/news/detail.jsp").forward(req, resp);
//
//        } catch (NumberFormatException e) {
//            // ID 不是数字，属于非法请求
//            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid News ID.");
//        }
//    }