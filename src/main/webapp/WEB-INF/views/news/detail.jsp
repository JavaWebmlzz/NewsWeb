<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>${news.title} - 新闻网</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="./">⬅️ 返回首页</a>
    </div>
</nav>

<div class="container bg-white p-5 rounded shadow-sm">
    <div class="row">
        <!-- 左侧：新闻正文 -->
        <div class="col-lg-8">
            <h1 class="mb-3">${news.title}</h1>

            <!-- 封面图 -->
            <c:if test="${not empty news.coverImage}">
                <div class="mb-4">
                    <img src="${news.coverImage}" class="img-fluid rounded shadow-sm" style="width: 100%; max-height: 500px; object-fit: cover;" alt="封面图">
                </div>
            </c:if>

            <div class="text-muted mb-4 pb-3 border-bottom">
                <span class="me-3">📅 发布于: ${news.publishTime}</span>
                <span>👀 阅读: ${news.viewCount}</span>
                <span class="badge bg-secondary ms-2">CatID=${news.categoryId}</span>
            </div>

            <div class="news-content fs-5" style="line-height: 1.8;">
                <c:out value="${news.content}" escapeXml="false" />
            </div>
        </div>

        <!-- 右侧：广告位 -->
        <div class="col-lg-4">
            <div class="card mb-4 sticky-top" style="top: 20px;">
                <div class="card-header bg-primary text-white">🔥 个性化推荐 (广告)</div>
                <div class="card-body">
                    <!-- 广告容器 -->
                    <div id="ad-container"
                         class="text-center py-4"
                         data-category-id="${news.categoryId}"
                         data-visitor-id="${visitorId}">

                        <!-- 初始加载动画 -->
                        <div class="spinner-border text-primary mb-2" role="status"></div>
                        <p class="text-muted small">正在连接广告联盟...</p>
                        <p class="text-muted small" style="font-size: 10px;">ID: ${visitorId}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 核心 JS 逻辑 -->
<script>
    document.addEventListener("DOMContentLoaded", function() {
    var adContainer = document.getElementById('ad-container');
    if (!adContainer) return;

    var visitorId = adContainer.dataset.visitorId;
    var currentCategory = adContainer.dataset.categoryId; // 当前新闻的分类

    // 1. 请求广告 (直接问推荐接口，不需要中间商了)
    var apiUrl = "api/ad-recommend?visitorId=" + visitorId + "&_t=" + Date.now();

    fetch(apiUrl)
    .then(res => res.json())
    .then(res => {
    if (res.code === 200 && res.data) {
    // 渲染视频
    adContainer.innerHTML =
    '<div class="ratio ratio-16x9 mb-2">' +
    '<video src="' + res.data.url + '" autoplay muted loop class="rounded shadow-sm" style="width:100%"></video>' +
    '</div>' +
    '<div class="fw-bold text-dark">' + res.data.title + '</div>';
}
});

    // ==========================================
    // 2. 核心：行为上报 (埋点)
    // ==========================================

    // 记录：如果用户在当前页面停留超过 5 秒，就算一次有效阅读
    // (为了演示效果，我们设置短一点，比如 3 秒就上报一次)
    setInterval(function() {
    // 只有当页面可见时才上报
    if (!document.hidden) {
    console.log("⏱️ 用户正在阅读分类 " + currentCategory + "，发送心跳包...");

    var formData = new URLSearchParams();
    formData.append('visitorId', visitorId);
    formData.append('categoryId', currentCategory);
    formData.append('type', 'view'); // 类型：浏览

        fetch('api/behavior', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: formData
        }).then(res => {
            if(res.ok) console.log("✅ 上报成功"); // <--- 必须看到这就话
            else console.error("❌ 上报失败", res.status);
        });
    }
    }, 3000); // 每3秒触发一次
});
</script>

</body>
</html>