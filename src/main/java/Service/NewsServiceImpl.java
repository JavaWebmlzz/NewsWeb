package Service;


import Dao.NewsDAO;
import Dao.NewsDAOImpl;
import Model.News;
import java.util.List;
import java.util.Collections;

public class NewsServiceImpl implements NewsService {
    private final NewsDAO newsDAO = new NewsDAOImpl();

    @Override
    public List<News> getLatestNews(int page, int pageSize, Integer categoryId) {
        try {
            int offset = (page - 1) * pageSize;
            // 调用升级后的 DAO
            return newsDAO.selectByPage(offset, pageSize, categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public News getNewsDetail(Integer id) {
        if (id == null) {
            return null;
        }
        try {
            News news = newsDAO.selectById(id);

            // 如果新闻不存在，或者逻辑删除位为 true，则视为不存在
            if (news == null || (news.getIsDeleted() != null && news.getIsDeleted())) {
                return null;
            }

            return news;
        } catch (Exception e) {
            e.printStackTrace();
            // 实际生产中应记录日志，这里暂时返回 null 让 Controller 处理
            return null;
        }
    }
}