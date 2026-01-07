package Service;


import Dao.NewsDAO;
import Dao.NewsDAOImpl;
import Model.News;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NewsServiceImpl implements NewsService {
    private final NewsDAO newsDAO = new NewsDAOImpl();

    @Override
    public Map<String, Object> getNewsPage(int page, int pageSize, Integer categoryId, String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 计算数据库偏移量
            int offset = (page - 1) * pageSize;

            // 2. 查询数据列表
            List<News> list = newsDAO.selectByPage(offset, pageSize, categoryId, keyword);

            // 3. 查询总记录数
            int totalCount = newsDAO.count(categoryId, keyword);

            // 4. 计算总页数 (向上取整)
            int totalPage = (int) Math.ceil((double) totalCount / pageSize);

            // 5. 打包结果
            result.put("list", list);           // 新闻数据
            result.put("totalCount", totalCount); // 总条数
            result.put("totalPage", totalPage);   // 总页数
            result.put("currentPage", page);      // 当前页

        } catch (Exception e) {
            e.printStackTrace();
            result.put("list", Collections.emptyList());
            result.put("totalPage", 0);
            result.put("currentPage", 1);
        }
        return result;
    }

    @Override
    public News getNewsDetail(Integer id) {
        try {
            // 1. 先把阅读量 +1 (这里是关键修改)
            // 这样后面 selectById 查出来的就是最新的阅读量
            newsDAO.incrementViewCount(id);

            // 2. 再查数据返回
            return newsDAO.selectById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}