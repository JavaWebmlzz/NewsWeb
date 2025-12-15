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
        <!-- 使用相对路径返回首页 -->
        <a class="navbar-brand" href="./">⬅️ 返回首页</a>
    </div>
</nav>

<div class="container bg-white p-5 rounded shadow-sm">
    <div class="row">
        <!-- 左侧：新闻正文 -->
        <div class="col-lg-8">
            <h1 class="mb-3">${news.title}</h1>
            <div class="text-muted mb-4 pb-3 border-bottom">
                <span class="me-3">📅 发布于: ${news.publishTime}</span>
                <span>👀 阅读: ${news.viewCount}</span>
                <!-- 调试显示：直接把分类ID印出来，看看是不是空的 -->
                <span class="badge bg-secondary ms-2">Debug: CatID=${news.categoryId}</span>
            </div>

            <div class="news-content fs-5" style="line-height: 1.8;">
                <c:out value="${news.content}" escapeXml="false" />
            </div>
        </div>

        <!-- 右侧：广告位 -->
        <div class="col-lg-4">
            <div class="card mb-4">
                <div class="card-header">猜你喜欢 (广告)</div>
                <div class="card-body">
                    <!-- 关键点：data-category-id 必须取到值 -->
                    <div id="ad-container"
                         class="bg-light text-center py-4"
                         data-category-id="${news.categoryId}"
                         data-visitor-id="${visitorId}">

                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        <p class="mt-2 text-muted">正在加载智能广告...</p>
                        <small class="d-block text-muted">
                            (Category: ${news.categoryId} | User: ${visitorId})
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        var adContainer = document.getElementById('ad-container');
        if (!adContainer) return;

        // 1. 获取参数
        var categoryId = adContainer.dataset.categoryId;
        var visitorId = adContainer.dataset.visitorId;

        // 🛡️ 保险措施：如果 dataset 没取到，尝试从 Debug 徽章取
        if (!categoryId) {
            console.warn("⚠️ dataset 取值失败，尝试解析 Debug 徽章...");
            var debugBadge = document.querySelector('.badge.bg-secondary');
            if (debugBadge) {
                var match = debugBadge.textContent.match(/CatID=(\d+)/);
                if (match) categoryId = match[1];
            }
        }

        console.log("🔍 前端参数 check: ", categoryId, visitorId);

        if (!categoryId) {
            adContainer.innerHTML = '<div class="alert alert-danger">Error: Category ID Missing</div>';
            return;
        }

        // 2. 构造 URL (关键修改：这里改成用 + 号拼接，不要用 ` 和
        // 这样 JSP 就不会报错了
        var apiUrl = "api/mock-ad?categoryId=" + categoryId + "&visitorId=" + visitorId + "&_t=" + new Date().getTime();

        // 3. 发送请求
        fetch(apiUrl)
            .then(function(response) { return response.json(); })
            .then(function(res) {
                console.log("✅ API Raw Response:", res);

                if (res.code === 200 && res.data) {
                    var img = res.data.imageUrl || "";
                    var link = res.data.linkUrl || "#";
                    var title = res.data.title || "Ad Recommendation";
                    var shortId = visitorId ? visitorId.substring(0, 6) : 'N/A';

                    // HTML 拼接也改成普通的字符串拼接，防止出错
                    var html = '<a href="' + link + '" target="_blank">' +
                        '<img src="' + img + '" class="img-fluid rounded shadow-sm" style="width:100%">' +
                        '</a>' +
                        '<div class="mt-2 fw-bold text-dark">' + title + '</div>' +
                        '<div class="text-muted small">Ad ID: ' + shortId + '...</div>';

                    adContainer.innerHTML = html;
                } else {
                    adContainer.innerHTML = 'No Ad Found';
                }
            })
            .catch(function(error) {
                console.error("❌ Fetch Error:", error);
                adContainer.innerHTML = '<div class="text-danger">Load Failed: ' + error.message + '</div>';
            });
    });
</script>
</body>
</html>