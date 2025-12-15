<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- 注意: Tomcat 10/11 JSTL uri 为 jakarta.tags.core --%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>${news.title} - 新闻网</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .news-meta { color: #6c757d; font-size: 0.9rem; margin-bottom: 20px; }
        .news-content { font-size: 1.1rem; line-height: 1.8; }
        .ad-placeholder { background-color: #f8f9fa; height: 250px; display: flex; align-items: center; justify-content: center; border: 1px dashed #ced4da; }
    </style>
</head>
<body>

<!-- 导航栏 (简单复用，后期可提取为 include) -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">JavaWeb News</a>
    </div>
</nav>

<div class="container">
    <!-- 面包屑导航 -->
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">首页</a></li>
            <li class="breadcrumb-item active" aria-current="page">正文</li>
        </ol>
    </nav>

    <div class="row">
        <!-- 左侧：新闻内容 -->
        <div class="col-md-8">
            <h1 class="mb-3">${news.title}</h1>

            <div class="news-meta">
                <span class="me-3">📅 发布于: ${news.publishTime}</span>
                <span>👁️ 阅读: ${news.viewCount}</span>
            </div>

            <!-- 封面图 (如果有) -->
            <c:if test="${not empty news.coverImage}">
                <div class="mb-4">
                    <img src="${news.coverImage}" class="img-fluid rounded" alt="Cover Image">
                </div>
            </c:if>

            <hr>

            <!-- 正文内容 (允许 HTML 标签渲染) -->
            <div class="news-content mt-4">
                <c:out value="${news.content}" escapeXml="false" />
            </div>

            <div class="mt-5 mb-5 text-center">
                <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">← 返回首页</a>
            </div>
        </div>

        <!-- 右侧：侧边栏 -->
        <div class="col-md-4">
            <div class="card mb-4">
                <div class="card-header">广告位</div>
                <div class="card-body">
                    <div class="ad-placeholder">
                        <span class="text-muted">此处展示广告</span>
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-header">相关推荐</div>
                <div class="card-body">
                    <ul class="list-unstyled">
                        <li><a href="#" class="text-decoration-none">暂无推荐内容</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Footer -->
<footer class="bg-light text-center text-lg-start mt-auto py-3">
    <div class="container text-center">
        <span class="text-muted">© 2023 JavaWeb News Project</span>
    </div>
</footer>

</body>
</html>
