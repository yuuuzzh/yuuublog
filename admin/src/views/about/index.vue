<template>
  <div class="about-page">
    <div class="about-card">
      <h1>YuuuBlog 管理系统</h1>
      <p class="sub">基于 SpringBoot + Vue 的前后端分离个人博客</p>

      <h2>前台功能</h2>
      <ul>
        <li>用户：注册 / 登录（支持 GitHub OAuth2 第三方登录）/ 修改资料</li>
        <li>文章：浏览 / 点赞 / 评论 / 收藏（稍后读）/ 下载（docx / markdown）</li>
        <li>搜索：全文搜索结果页（标题 + 正文匹配，关键词高亮）</li>
        <li>发现：标签云、按标签筛选、文章归档、相关文章推荐、上下篇导航</li>
        <li>订阅：邮件订阅，新文章发布自动推送（含一键退订）</li>
        <li>其他：友链申请、留言板弹幕、YuuuChat 聊天机器人、网络打赏、暗色模式</li>
      </ul>

      <h2>后台功能</h2>
      <ul>
        <li>内容管理：文章（Markdown 编辑 / 草稿自动保存）、分类、标签、友链、关于我</li>
        <li>互动治理：评论管理（审核 / 屏蔽 / 删除）、邮件订阅者管理、敏感词过滤</li>
        <li>图片管理：对象存储文件列举 / 删除；上传图片自动压缩</li>
        <li>数据看板：统计卡片、分类分布、热门文章、近 30 天访客趋势、发布热力图</li>
        <li>系统：用户 / 角色 / 菜单 / 日志（登录日志、操作日志、访客日志）</li>
      </ul>

      <h2>技术栈</h2>
      <ul>
        <li>后端：Spring Boot 2.x + MyBatis-Plus + Redis + MySQL + MongoDB（聊天记录）</li>
        <li>前台：Vue 2 + Element UI + mavon-editor + ECharts</li>
        <li>后台：Vue 2 + Element UI + ECharts</li>
        <li>部署：前后台 jar + 静态 dist，Nginx 反向代理，对象存储（图片）</li>
      </ul>

      <h2>链接</h2>
      <ul>
        <li>前台：<a href="https://www.yuuu.online" target="_blank">www.yuuu.online</a></li>
        <li>项目地址：<a href="https://github.com/yuuuzzh/yuuublog" target="_blank">github.com/yuuuzzh/yuuublog</a></li>
        <li>问题反馈：864326354@qq.com</li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'About'
}
</script>

<style scoped>
.about-page {
  padding: 20px;
}

.about-card {
  max-width: 860px;
  margin: 0 auto;
  padding: 28px 34px;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 6px 18px rgba(20, 30, 60, .1);
}

.about-card h1 {
  font-size: 26px;
  margin: 0 0 6px;
  color: #1d2433;
}

.about-card .sub {
  color: #64708a;
  font-size: 14px;
  margin: 0 0 20px;
}

.about-card h2 {
  font-size: 17px;
  color: #1d2433;
  border-left: 4px solid #4F8CFF;
  padding-left: 10px;
  margin: 22px 0 10px;
}

.about-card ul {
  padding-left: 24px;
  margin: 0;
}

.about-card li {
  font-size: 14px;
  line-height: 2;
  color: #3d4759;
}

.about-card a {
  color: #4F8CFF;
  text-decoration: none;
}
</style>
