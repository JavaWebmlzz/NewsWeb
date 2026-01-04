package Service;

import Model.News;
import java.util.Map;

public interface NewsService {
    News getNewsDetail(Integer id);
    Map<String, Object> getNewsPage(int page, int pageSize, Integer categoryId, String keyword);
}
