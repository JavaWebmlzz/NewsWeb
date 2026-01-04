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

        // 1. 获取基础参数
        var visitorId = adContainer.dataset.visitorId;
        var currentCategory = adContainer.dataset.categoryId;

        // 🛡️ 容错：如果 visitorId 为空，生成一个临时的
        if (!visitorId) visitorId = "temp_" + Math.random().toString(36).substr(2, 9);

        console.log("🚀 [详情页] 开始加载广告流程...");

        // ==========================================
        // 第一步：询问【外部广告平台】
        // ==========================================
        var externalProfileApi = "api/mock-external-profile?visitorId=" + visitorId;

        // 使用 fetch 链式调用
        fetch(externalProfileApi)
            .then(function(res) {
                if (res.ok) return res.json();
                return { code: 500 }; // 失败时返回空对象
            })
            .then(function(res) {
                // 成功获取到外部画像
                var extCat = "";
                if (res.code === 200 && res.data) {
                    extCat = res.data.shopping_cat;
                    console.log("✅ [1/2] 外部画像获取成功: " + extCat);
                }
                return extCat; // 【关键】把获取到的画像传给下一步
            })
            .catch(function(err) {
                console.warn("⚠️ [1/2] 外部画像获取异常:", err);
                return ""; // 出错也返回空字符串，保证链条不断
            })
            .then(function(externalInterest) {
                // ==========================================
                // 第二步：请求【本站推荐算法】
                // (这里接收上一步传下来的 externalInterest)
                // ==========================================

                var recommendApi = "api/ad-recommend?categoryId=" + currentCategory
                    + "&visitorId=" + visitorId
                    + "&externalCat=" + externalInterest
                    + "&_t=" + new Date().getTime();

                console.log("📡 [2/2] 请求推荐算法: " + recommendApi);

                // 发起第二次请求
                return fetch(recommendApi);
            })
            .then(function(res) { return res.json(); }) // 【修复】这里现在能收到第二次请求的结果了
            .then(function(res) {
                console.log("✅ [2/2] 广告数据返回:", res);

                if (res.code === 200 && res.data) {
                    // 渲染广告
                    var img = res.data.imageUrl;
                    var link = res.data.linkUrl;
                    var title = res.data.title;

                    adContainer.innerHTML =
                        '<a href="' + link + '" target="_blank">' +
                        '<img src="' + img + '" class="img-fluid rounded mb-2 shadow-sm" style="width:100%">' +
                        '</a>' +
                        '<div class="fw-bold text-dark">' + title + '</div>' +
                        '<div class="text-muted small">基于您的浏览画像推荐</div>';
                } else {
                    adContainer.innerHTML = '暂无合适广告';
                }
            })
            .catch(function(err) {
                console.error("❌ 广告流程错误:", err);
                adContainer.innerHTML = '<div class="text-danger small">加载失败<br>' + err.message + '</div>';
            });

        // ==========================================
        // 行为采集
        // ==========================================
        var startTime = Date.now();
        window.addEventListener('beforeunload', function() {
            var duration = Math.round((Date.now() - startTime) / 1000);
            if (navigator.sendBeacon) {
                var data = new FormData();
                data.append('visitorId', visitorId);
                data.append('categoryId', currentCategory);
                data.append('duration', duration);
                navigator.sendBeacon('api/behavior', data);
            }
        });
    });
</script>

</body>
</html>