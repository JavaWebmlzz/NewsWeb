<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>500 - 系统繁忙</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #f8f9fa; height: 100vh; display: flex; align-items: center; justify-content: center; }
        .error-card { text-align: center; max-width: 600px; padding: 40px; }
        .error-code { font-size: 6rem; font-weight: bold; color: #ffc107; }
        .tech-info { font-size: 0.8rem; color: #adb5bd; margin-top: 20px; display: none; }
    </style>
</head>
<body>
<div class="card error-card shadow-sm border-0">
    <div class="error-code">500</div>
    <h3 class="mb-3">服务器正在“思考人生” 🤔</h3>
    <p class="text-muted">系统遇到了一点小问题，攻城狮正在紧急修复中...</p>

    <div class="d-grid gap-2 col-6 mx-auto mt-4">
        <a href="${pageContext.request.contextPath}/" class="btn btn-outline-dark">刷新重试</a>
    </div>

    <!-- 只有在开发模式下才建议查看堆栈信息 -->
    <!--
        <div class="tech-info text-start border p-3 rounded bg-light">
            <%= exception != null ? exception.getMessage() : "Unknown Error" %>
        </div>
        -->
</div>
</body>
</html>