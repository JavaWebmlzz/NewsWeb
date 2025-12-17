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
        <a class="navbar-brand" href="./">📰 新闻网</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto"> <!-- me-auto 让左边菜单靠左 -->
                <li class="nav-item">
                    <a class="nav-link ${empty currentCategory ? 'active' : ''}" href="./">全部</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentCategory == 1 ? 'active' : ''}" href="?categoryId=1">国际</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentCategory == 2 ? 'active' : ''}" href="?categoryId=2">科技</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentCategory == 3 ? 'active' : ''}" href="?categoryId=3">体育</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentCategory == 4 ? 'active' : ''}" href="?categoryId=4">娱乐</a>
                </li>
            </ul>

            <!-- 👇👇👇 新增：搜索框 👇👇👇 -->
            <form class="d-flex" action="./" method="get">
                <!-- 如果当前在某个分类下，搜索时保留分类ID -->
                <c:if test="${not empty currentCategory}">
                    <input type="hidden" name="categoryId" value="${currentCategory}">
                </c:if>

                <input class="form-control me-2" type="search" name="keyword"
                       placeholder="搜索新闻..." value="${currentKeyword}" aria-label="Search">
                <button class="btn btn-outline-light" type="submit">搜索</button>
            </form>
            <!-- 👆👆👆 新增结束 👆👆👆 -->

        </div>
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

                    <!-- 分页条 -->
                    <c:if test="${pagination.totalPage > 1}">
                        <nav aria-label="Page navigation" class="mt-4">
                            <ul class="pagination justify-content-center">

                                <!-- 定义一个基础参数串，确保翻页时不会丢掉分类和搜索词 -->
                                <c:set var="baseParams" value="&categoryId=${currentCategory}&keyword=${currentKeyword}" />

                                <!-- 上一页 -->
                                <li class="page-item ${pagination.currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${pagination.currentPage - 1}${baseParams}">上一页</a>
                                </li>

                                <!-- 页码循环 (1, 2, 3...) -->
                                <c:forEach begin="1" end="${pagination.totalPage}" var="i">
                                    <li class="page-item ${pagination.currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="?page=${i}${baseParams}">${i}</a>
                                    </li>
                                </c:forEach>

                                <!-- 下一页 -->
                                <li class="page-item ${pagination.currentPage == pagination.totalPage ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${pagination.currentPage + 1}${baseParams}">下一页</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>

                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">暂无新闻数据...</div>
                </c:otherwise>
            </c:choose>

        </div>

        <!-- 侧边栏 (留给广告组的位置) -->
        <div class="col-md-4">
            <div class="p-3 bg-light rounded shadow-sm sticky-top" style="top: 20px;">
                <h4 class="fst-italic border-bottom pb-2">今日推荐</h4>

                <!-- 广告容器 (复用昨天的逻辑) -->
                <!-- 首页没有特定分类，我们默认传 0 或空，VisitorId 通过 Filter 自动获取 -->
                <div id="home-ad-container"
                     class="text-center py-3"
                     data-category-id="${currentCategory != null ? currentCategory : 0}"
                     data-visitor-id="${visitorId}">
                    <div class="spinner-border text-secondary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
        document.addEventListener("DOMContentLoaded", function() {
        var adContainer = document.getElementById('home-ad-container');
        if (!adContainer) return;

        var catId = adContainer.dataset.categoryId;
        var visId = adContainer.dataset.visitorId;

        // API URL (首页广告)
        var apiUrl = "api/mock-ad?categoryId=" + catId + "&visitorId=" + visId + "&_t=" + new Date().getTime();

        fetch(apiUrl)
        .then(function(res){ return res.json(); })
        .then(function(res){
        if(res.code === 200 && res.data) {
        var html = '<a href="' + res.data.linkUrl + '" target="_blank">' +
        '<img src="' + res.data.imageUrl + '" class="img-fluid rounded" style="width:100%">' +
        '</a>';
        adContainer.innerHTML = html;
    } else {
        adContainer.innerHTML = '暂无推荐';
    }
    })
        .catch(function(e){ console.error(e); adContainer.innerHTML = 'Ad Error'; });
    });

</script>
</body>
</html>