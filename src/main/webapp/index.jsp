<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>新闻首页</title>
    <!-- 引入 Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .news-card { margin-bottom: 20px; transition: transform 0.2s; }
        .news-card:hover { transform: translateY(-5px); box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .news-meta { font-size: 0.85rem; color: #6c757d; }
    </style>
</head>
<body>

<!-- 导航栏 -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="#">📰 新闻网</a>
    </div>
</nav>

<div class="container">
    <div class="row">
        <div class="col-md-8">
            <h2 class="mb-4 border-bottom pb-2">最新资讯</h2>

            <!-- 使用 JSTL 遍历 newsList -->
            <c:choose>
                <c:when test="${not empty newsList}">
                    <c:forEach items="${newsList}" var="news">
                        <div class="card news-card">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <a href="${pageContext.request.contextPath}/news?action=detail&id=${news.id}" class="text-decoration-none text-primary">
                                            ${news.title}
                                    </a>
                                </h5>
                                <p class="card-text">${news.summary}</p>
                                <div class="news-meta">
                                    <span class="me-3">📅 ${news.publishTime}</span>
                                    <span>👁️ 阅读: ${news.viewCount}</span>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">暂无新闻数据...</div>
                </c:otherwise>
            </c:choose>

        </div>

        <!-- 侧边栏 (留给广告组的位置) -->
        <div class="col-md-4">
            <div class="p-3 bg-light rounded">
                <h4>今日推荐</h4>
                <p>这里将是广告位...</p>
            </div>
        </div>
    </div>
</div>

</body>
</html>