# yuuublog

基于 Spring Boot + Vue 的前后端分离个人博客，包含博客前台、后台管理端与后端服务三部分。

线上示例：**https://www.yuuu.online**（前台） / **https://www.yuuu.online:8094**（后台管理）

## 功能总览

### 博客前台

- **文章**：Markdown 渲染、阅读进度、悬浮目录、代码高亮与复制、图片灯箱、字数统计与阅读时长
- **发现**：标签云、按标签筛选、文章归档（按年月时间线）、相关文章推荐、上下篇导航
- **搜索**：全文搜索结果页（标题 + 正文匹配，关键词上下文高亮），⌘K 命令面板
- **互动**：评论（Emoji、点赞、回复、删除自己的评论）、留言板弹幕、点赞、收藏（稍后读）
- **订阅**：邮箱订阅，新文章发布自动邮件推送（含一键退订），评论邮件通知（博主 / 被回复者）
- **关于我**：后台可配置的个人介绍页（头像 / 头衔 / 技能栈 / Markdown 正文 / 联系方式）
- **体验**：暗色模式、移动端适配（底部 Tab、阅读设置浮标：主题切换 + 字号调节）、首屏视差与打字机动效
- **SEO**：history 路由（URL 不带 #）、sitemap.xml、RSS 订阅、robots.txt

### 后台管理

- **数据仪表盘**：统计卡片（文章 / 分类 / 标签 / 评论 / 浏览 / 点赞 / 访客）、分类分布、热门文章 TOP10、近 30 天访客趋势、访客系统分布、近一年文章发布热力图
- **内容管理**：文章（Markdown 编辑、草稿自动保存、置顶、发布状态）、分类、标签、友链、关于我
- **互动治理**：评论管理（审核通过 / 屏蔽 / 删除，敏感词过滤）、邮件订阅者管理
- **图片管理**：对象存储文件列举 / 删除 / 复制链接；上传图片自动压缩（限宽 1920、jpg 重编码）
- **日志**：登录日志、操作日志、访客日志（访问趋势 / 地区热力图 / 系统分布）
- **系统**：用户、角色、菜单权限（RBAC）

## 技术栈

| 端 | 技术 |
|---|---|
| 后端 | JDK 8、Spring Boot 2.5、MyBatis-Plus、MySQL 8、Redis 6、MongoDB（聊天记录）、Spring Security + JWT |
| 前台 / 管理端 | Vue 2、Element UI、mavon-editor、ECharts |
| 存储 | 对象存储（七牛云，图片） |

## 目录结构

```text
yuuublog/
├── backend/          # Java 多模块工程
│   ├── yuuu-framework/   # 公共模块（安全、Redis、邮件、实体与服务）
│   ├── yuuu-blog/        # 前台服务（默认端口 7777）
│   ├── yuuu-admin/       # 管理端服务（默认端口 8989）
│   └── yuuu_blog.sql     # 数据库脚本（含关于我 / 订阅 / 评论状态等增量段落）
├── frontend/         # 博客前台
├── admin/            # 博客后台管理
└── scripts/          # 本地构建部署脚本
```

## 快速开始

### 1. 数据库与中间件

1. 安装 MySQL 8、Redis 6
2. 导入 `backend/yuuu_blog.sql`
3. 修改后端配置中的数据源 / Redis / 第三方密钥：
   - `backend/yuuu-blog/src/main/resources/application.yml`
   - `backend/yuuu-admin/src/main/resources/application.yaml`

> OSS、邮箱、GitHub OAuth 等密钥请自行填写，勿把真实密钥提交到仓库。

### 2. 启动后端

```bash
cd backend
# 使用 IDE 分别启动，或 Maven 打包后运行：
# yuuu-blog  -> 端口 7777
# yuuu-admin -> 端口 8989
```

### 3. 启动前台

```bash
cd frontend
npm install
npm run dev
# 默认 http://localhost:8080 ，开发环境 API 指向 http://localhost:7777
```

### 4. 启动管理端

```bash
cd admin
npm install
npm run dev
# 默认 http://localhost:9528 ，开发环境 API 指向 http://localhost:8989
```

## 部署

- 前后台分别打成 jar（`mvn -DskipTests package`），静态端 `npm run build` 产出 dist
- Nginx：前台 dist 静态托管 + `try_files ... /index.html`（history 路由回落），`/api/`、`/admin-api/` 反向代理到两个后端
- 本地一键构建部署：`./scripts/deploy-local.sh all|backend|frontend|admin`（构建 → 校验 → rsync 直传 → 重启 → 健康检查，失败自动回滚）

## 安全相关

- 登录与订阅验证码：一次性令牌维度存储（多人并发取码互不影响），不下发验证码明文
- 订阅接口：验证码 + 同 IP 限流（60 秒 1 次、每天 10 次）
- 评论接口：登录后才能评论，同一用户 60 秒最多 3 条；敏感词过滤；后台可审核 / 屏蔽
- Redis 序列化：Fastjson 已关闭 autoType，仅放行本项目与 JDK 基础类型白名单
- 缓存读写全部降级：Redis 不可用时自动回源数据库，不影响页面访问

## License

MIT
