import Dao.NewsDAO;
import Dao.impl.NewsDAOImpl;
import Model.News;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        try {
            NewsDAO dao = new NewsDAOImpl();

            System.out.println("====== 1. 测试插入 ======");
            News newNews = new News();
            newNews.setTitle("Day2 测试新闻");
            newNews.setContent("这是测试内容...");
            newNews.setSummary("测试摘要");
            newNews.setCategoryId(1); // 假设1是存在的分类ID
            int result = dao.insert(newNews);
            System.out.println("插入结果: " + (result > 0 ? "成功" : "失败"));

            System.out.println("====== 2. 测试列表查询 ======");
            List<News> list = dao.selectByPage(0, 10);
            for (News n : list) {
                System.out.println("ID: " + n.getId() + " | 标题: " + n.getTitle());
            }

            System.out.println("====== 3. 测试总数 ======");
            System.out.println("总新闻数: " + dao.count());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}