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
                    <div id="ad-container" class="ad-placeholder">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">正在加载...</span>
                        </div>
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
<script>
    document.addEventListener("DOMContentLoaded", function() {
        // 1. 确定当前上下文 (比如这里假设新闻分类ID是 2 代表科技)
        // 实际项目中，你可以用 ${news.categoryId} 获取真实分类
        const categoryTag = "tech"; // 这里先写死模拟

        // 2. 定义 API 地址
        // 如果是你组员的电脑，可能是 'http://192.168.x.x:8080/ad-system/api/recommend'
        // 这里用我们刚才写的 Mock 地址
        const apiUrl = "${pageContext.request.contextPath}/api/mock-ad?category=" + categoryTag;

        // 3. 发起异步请求 (AJAX)
        fetch(apiUrl)
            .then(response => response.json())
            .then(res => {
                if (res.code === 200 && res.data) {
                    const ad = res.data;
                    const adHtml = `
                        <a href="` + ad.linkUrl + `" target="_blank" title="` + ad.title + `">
                            <img src="` + ad.imageUrl + `" class="img-fluid rounded" alt="广告">
                        </a>
                        <div class="text-end"><small class="text-muted" style="font-size:10px;">广告</small></div>
                    `;
                    // 渲染到页面
                    document.getElementById("ad-container").innerHTML = adHtml;
                    document.getElementById("ad-container").classList.remove("ad-placeholder"); // 去掉边框样式
                }
            })
            .catch(error => {
                console.error('广告加载失败:', error);
                document.getElementById("ad-container").innerText = "暂无推荐";
            });
    });
</script>
</body>
</html>
