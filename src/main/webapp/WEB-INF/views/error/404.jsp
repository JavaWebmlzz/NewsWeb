<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>404 - 页面走丢了</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; height: 100vh; display: flex; align-items: center; justify-content: center; }
        .error-card { text-align: center; max-width: 500px; padding: 40px; }
        .error-code { font-size: 6rem; font-weight: bold; color: #dc3545; }
    </style>
</head>
<body>
<div class="card error-card shadow-sm border-0">
    <div class="error-code">404</div>
    <h3 class="mb-4">哎呀，页面好像飞走了 🛸</h3>
    <p class="text-muted mb-4">您访问的页面不存在，或者已经被删除。</p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-lg">返回首页</a>
</div>
</body>
</html>