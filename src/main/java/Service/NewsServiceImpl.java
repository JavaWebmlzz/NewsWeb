package Service;


import Dao.NewsDAO;
import Dao.NewsDAOImpl;
import Model.News;
import java.util.List;
import java.util.Collections;

public class NewsServiceImpl implements NewsService {
    private final NewsDAO newsDAO = new NewsDAOImpl();

    @Override
    public List<News> getLatestNews(int page, int pageSize) {
        try {
            // 计算 offset: 第一页从0开始，第二页从10开始...
            int offset = (page - 1) * pageSize;
            return newsDAO.selectByPage(offset, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            // 发生异常时返回空列表，防止页面报错崩溃
            return Collections.emptyList();
        }
    }
}