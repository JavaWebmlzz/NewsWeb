package Service;

import Model.News;
import java.util.List;

public interface NewsService {
    List<News> getLatestNews(int page, int pageSize);
    News getNewsDetail(Integer id);
}
