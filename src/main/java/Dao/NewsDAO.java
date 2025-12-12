package Dao;


import Model.News;
import java.util.List;

public interface NewsDAO {
    // 1. 插入新闻 (用于测试或后台)
    int insert(News news) throws Exception;

    // 2. 根据ID查询详情
    News selectById(Integer id) throws Exception;

    // 3. 分页查询列表 (offset 是起始行, limit 是条数)
    List<News> selectByPage(int offset, int limit) throws Exception;

    // 4. 查询总记录数 (用于计算分页)
    int count() throws Exception;
}