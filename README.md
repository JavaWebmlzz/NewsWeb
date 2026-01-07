# 📰 JavaWeb 智能新闻系统 (News Website)

## 📖 项目简介
本项目是一个基于 Java Servlet/JSP 技术栈构建的新闻门户网站。
特色在于集成了**匿名用户追踪**与**上下文感知广告系统**，能够根据用户浏览的新闻分类（如科技、体育），智能推送相关联的模拟广告。

## 🛠️ 技术栈
- **后端**: Java 17, Servlet, JSP, JDBC
- **数据库**: MySQL 8.0
- **前端**: Bootstrap 5, Fetch API (原生 JS)
- **服务器**: Tomcat 11.0.11 (Jakarta EE 10)
- **工具**: Maven, Git

## ✨ 核心功能
1.  **新闻浏览**: 支持分页、关键词搜索、分类筛选（教育/科技/体育/娱乐）。
2.  **匿名追踪**: 使用 Cookie + Filter 技术，为未登录用户生成唯一 `visitor_id`。
3.  **智能广告**: 
    - 集成 Mock 广告 API。
    - 实现 Context-Aware 投放：看“科技新闻”推“手机广告”，看“体育新闻”推“跑鞋广告”。
    - 首页侧边栏与详情页侧边栏均实现了动态加载。
4.  **友好的 UI**: 骨架屏加载效果、404/500 错误页定制。

## 🚀 快速开始

### 1. 数据库配置
请在 MySQL 中执行 `src/main/resources/sql/init.sql` (需自行导出或查看代码中的建表语句)。
修改 `src/main/resources/db.properties` 中的数据库连接信息。

### 2. 环境要求
- JDK 17+
- Tomcat 10+ (推荐 11)

### 3. 部署
使用 Maven 打包：
```bash
mvn clean package
