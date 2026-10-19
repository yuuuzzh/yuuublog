<!-- 文章详情模块 -->
<template>
  <div class="detailBox tcommonBox">
    <span class="s-round-date">
      <span class="month" v-html="showInitDate(detailObj.createTime, 'month') + '月'"></span>
      <span class="day" v-html="showInitDate(detailObj.createTime, 'date')"></span>
    </span>
    <header>
      <h1>
        <!-- <a :href="'/DetailShare?aid='+detailObj.id" target="_blank">
                        {{detailObj.title}}
                    </a> -->
        <a target="_blank">
          {{ detailObj.title }}
        </a>
      </h1>
      <h2>
        <i class="fa fa-fw fa-user"></i>发表于 <span>{{ detailObj.createTime }}</span> •
        <i class="fa fa-fw fa-eye"></i>{{ detailObj.viewCount }} 次围观 •
        <i class="fa fa-fw fa-heart"></i>{{ detailObj.likes }} 次点赞 •
        <i class="fa fa-fw fa-clock-o"></i>约 {{ minutes }} 分钟读完（{{ words }} 字）
      </h2>
      <div class="ui label">
        <a :href="'/Share?classId=' + detailObj.categoryId">{{ detailObj.categoryName }}</a>
      </div>
    </header>
    <ArticleToc :toc="toc" />

    <div class="article-content markdown-body" ref="content" v-html="detailObj.content" @click="onContentClick"></div>

    <ImageLightbox :visible.sync="lbVisible" :list="lbList" :index.sync="lbIndex" />

    <!-- 上下篇 -->
    <div class="neighborBox" v-if="detailObj.prevId || detailObj.nextId">
      <a v-if="detailObj.prevId" class="neighbor prev" :href="'/DetailArticle?aid=' + detailObj.prevId">
        <span class="nei-label"><i class="fa fa-fw fa-angle-left"></i>上一篇</span>
        <span class="nei-title">{{ detailObj.prevTitle }}</span>
      </a>
      <span v-else class="neighbor disabled"><span class="nei-label">上一篇</span><span class="nei-title">没有了</span></span>
      <a v-if="detailObj.nextId" class="neighbor next" :href="'/DetailArticle?aid=' + detailObj.nextId">
        <span class="nei-label">下一篇<i class="fa fa-fw fa-angle-right"></i></span>
        <span class="nei-title">{{ detailObj.nextTitle }}</span>
      </a>
      <span v-else class="neighbor disabled"><span class="nei-label">下一篇</span><span class="nei-title">没有了</span></span>
    </div>

    <!-- 相关文章 -->
    <div class="relatedBox" v-if="relatedList.length">
      <h3 class="related-title"><i class="fa fa-fw fa-cubes"></i>相关文章</h3>
      <ul class="related-list">
        <li v-for="(item,index) in relatedList" :key="'r'+index">
          <a :href="'/DetailArticle?aid=' + item.id" target="_blank">{{ item.title }}</a>
          <span class="related-meta">{{ showInitDate(item.createTime,'all') }} · {{ item.viewCount }} 次围观</span>
        </li>
      </ul>
    </div>

    <div class="donate">
      <div class="donate-word">
        <span @click="pdonate = !pdonate">赞赏</span>
        <div class="btnMenuCss">
          <img style="width: 35px; height: 35px; cursor: pointer;" :src="iconSrc" @click="toggleLike" />
          <img style="width: 35px; height: 35px; cursor: pointer;" :src="iconSrcDis" @click="toggleDisLike" />
          <el-button style="margin-left: 5px;" size="small" type="primary" @click="forwardPage"
            icon="el-icon-share"></el-button>
          <el-button size="small" :type="favorited ? 'warning' : 'primary'" @click="toggleFavorite"
            :icon="favorited ? 'el-icon-star-on' : 'el-icon-star-off'">
            {{ favorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button size="small" type="primary" @click="downloadById" icon="el-icon-download"></el-button>
        </div>
      </div>
      <el-row :class="pdonate ? 'donate-body' : 'donate-body donate-body-show'" :gutter="30">
        <el-col :span="12" class="donate-item">
          <div class="donate-tip">
            <img :src="detailObj.wechat_image ? detailObj.wechat_image : 'static/img/wx_pay.png'"
              :onerror="$store.state.errorImg" />
            <span>微信扫一扫，向我赞赏</span>
          </div>
        </el-col>
        <el-col :span="12" class="donate-item">
          <div class="donate-tip">
            <img :src="detailObj.alipay_image ? detailObj.alipay_image : 'static/img/ali_pay.jpg'"
              :onerror="$store.state.errorImg" />
            <span>支付宝扫一扫，向我赞赏</span>
          </div>
        </el-col>
      </el-row>
    </div>



  </div>
</template>

<script>
import { initDate } from '../utils/server.js'
import { getArticle, updateViewCount, relatedArticles } from '../api/article.js'
import { checkFavorite, toggleFavorite } from '../api/favorite.js'
import { mavonEditor } from 'mavon-editor'
import * as FileSaver from 'file-saver';
import request from "@/utils/request.js"
import mammoth from 'mammoth'
import ArticleToc from './ArticleToc.vue'
import ImageLightbox from './ImageLightbox.vue'
import { buildToc, enhanceCodeBlocks, countWords } from '../utils/articleEnhance.js'
export default {
  data() { //选项 / 数据
    return {
      aid: '',//文章ID
      pdonate: true,//打开赞赏控制,
      favorited: false,//是否已收藏
      detailObj: {},//返回详情数据
      relatedList: [],//相关文章
      haslogin: false,//是否已经登录
      title: '',//文章标题
      userId: '',//用户id
      iconSrc: "static/img/wdz.png", // Default icon
      iconSrcDis: "static/img/wdc.png",
      isLiked: false, // User's like status
      isDisLiked: false, // User's like status
      toc: [],//正文目录
      words: 0,//正文字数
      minutes: 1,//预计阅读时长
      lbVisible: false,//图片灯箱
      lbList: [],
      lbIndex: 0,
    }
  },
  methods: { //事件处理器
    showInitDate: function (date, full) {//年月日的编辑
      // console.log(detailObj.create_time,date,full);
      return initDate(date, full);
    },
    fetchLikeStatus() {
      if (this.haslogin) {
        request.get(`/like/article/${this.aid}`)
          .then((response) => {
            // console.log("res",response)
            if (response === 1) {
              this.isLiked = true;
            }
            this.iconSrc = this.isLiked ? "static/img/ydz.png" : "static/img/wdz.png";
          })
      }
    },
    toggleLike() {
      request.post(`/like/article/${this.aid}`)
        .then((response) => {
          if (this.isLiked) {
            this.isLiked = false;
            this.iconSrc = "static/img/wdz.png";
          } else {
            this.isLiked = true;
            this.iconSrc = "static/img/ydz.png";
          }
        })
    },
    async downloadById() {
      try {
        const response = await request.get(`/article/download/${this.aid}`, {}, { responseType: 'blob' });
        const content = response.content;
            const blob = new Blob([content], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
            const link = document.createElement('a');
            link.style.display = 'none';
            link.href = URL.createObjectURL(blob);
            link.download = response.title + '.docx';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(link.href);
            this.$message.success('客官莫急，已经在飞速下载了~(推荐使用WPS)');
          
      } catch (error) {
        console.error('下载失败', error);
        this.$message.error('下载失败，请重试或联系管理员');
      }
    }
    ,
    toggleDisLike() {
      if (this.isDisLiked) {
        this.isDisLiked = false;
        this.iconSrcDis = "static/img/wdc.png";
      } else {
        this.isDisLiked = true;
        this.iconSrcDis = "static/img/ydc.png";
      }
    },
    forwardPage() {
      // Get the current page's URL
      const currentPageURL = window.location.href;

      // Create a temporary input element to copy the URL to the clipboard
      const tempInput = document.createElement("input");
      tempInput.value = currentPageURL;
      document.body.appendChild(tempInput);

      // Select the text in the input element
      tempInput.select();
      tempInput.setSelectionRange(0, 99999); // For mobile devices

      // Copy the text to the clipboard
      document.execCommand("copy");

      // Remove the temporary input element
      document.body.removeChild(tempInput);

      // Provide feedback to the user
      alert("当前文章链接已复制，快去分享给小伙伴吧！");
    },

    // 正文渲染完成后的增强：目录锚点 / 代码块工具栏 / 图片灯箱 / 字数统计
    decorate: function () {
      const root = this.$refs.content
      if (!root) return
      this.toc = buildToc(root)
      enhanceCodeBlocks(root)
      const stat = countWords(root)
      this.words = stat.words
      this.minutes = stat.minutes
      const imgs = root.querySelectorAll('img')
      Array.prototype.forEach.call(imgs, function (img) {
        if (img.getAttribute('src')) img.classList.add('zoomable')
      })
    },
    onContentClick: function (e) {
      const el = e.target
      if (!el || el.tagName !== 'IMG') return
      const root = this.$refs.content
      const src = el.getAttribute('src')
      if (!root || !src) return
      const imgs = root.querySelectorAll('img')
      const list = []
      let index = 0
      Array.prototype.forEach.call(imgs, function (img, i) {
        const s = img.getAttribute('src')
        if (!s) return
        if (img === el) index = list.length
        list.push(s)
      })
      this.lbList = list
      this.lbIndex = index
      this.lbVisible = true
    },
    getRelated: function () {
      var that = this;
      that.relatedList = [];
      relatedArticles(that.aid, 6).then((response) => {
        that.relatedList = response ? response : [];
      }).catch(() => {
        that.relatedList = [];
      });
    },
    loadFavorite: function () {
      var that = this;
      that.favorited = false;
      // 未登录时不查询（接口需要登录）
      if (!localStorage.getItem('userInfo')) {
        return;
      }
      checkFavorite(that.aid).then((response) => {
        that.favorited = !!response;
      }).catch(() => {
        that.favorited = false;
      });
    },
    toggleFavorite: function () {
      var that = this;
      if (!localStorage.getItem('userInfo')) {
        localStorage.setItem('logUrl', that.$route.fullPath);
        that.$router.push({ path: '/Login', query: { login: 1 } });
        return;
      }
      toggleFavorite(that.aid).then((response) => {
        that.favorited = !!response;
        that.$message && that.$message.success(that.favorited ? '已加入收藏' : '已取消收藏');
      }).catch(() => { });
    },
    getArticleDetail: function () {
    getArticle(this.aid).then((response) => {
      this.detailObj = response
        const markdownIt = mavonEditor.getMarkdownIt()
        // markdownIt.re
        this.detailObj.content = markdownIt.render(response.content);
        this.$nextTick(this.decorate)
      }).catch(
        (error) => {
          this.$router.push({ name: 'NotFind' });
          setTimeout(() => {
            this.$router.push({ path: '/' }); // 你的首页路径
          }, 3000);
        }
      )
    },
    routeChange: function () {
      var that = this;
      that.aid = that.$route.query.aid == undefined ? 0 : parseInt(that.$route.query.aid);//获取传参的aid
      //判断用户是否存在
      if (localStorage.getItem('userInfo')) {
        that.haslogin = true;
        that.userInfo = JSON.parse(localStorage.getItem('userInfo'));
        that.userId = that.userInfo.userId;
        // console.log(that.userInfo);
      } else {
        that.haslogin = false;
      }
      //切换文章时重置正文增强状态
      this.toc = [];
      this.words = 0;
      this.minutes = 1;
      this.lbVisible = false;
      this.lbList = [];
      this.lbIndex = 0;
      //获取详情接口 
      this.getArticleDetail();
      this.getRelated();
      this.loadFavorite();
      updateViewCount(that.aid)
    }
  },
  watch: {
    // 如果路由有变化，会再次执行该方法
    '$route': 'routeChange'
  },
  components: { //定义组件
    ArticleToc,
    ImageLightbox,
  },
  created() { //生命周期函数
    var that = this;

    this.routeChange();
    this.fetchLikeStatus();
  },

}
</script>

<style lang="less">
.btnMenuCss {
  display: flex;
  align-items: center;
  position: absolute;
  right: 25px;
  top: 0;
}

.detailBox .article-content {
  font-size: 15px;
  white-space: normal;
  word-wrap: break-word;
  word-break: break-all;
  overflow-x: hidden;
}

.detailBox .article-content p {
  margin: 10px 0;
  line-height: 24px;
  word-wrap: break-word;
  word-break: break-all;
  overflow-x: hidden;
}

.detailBox .article-content pre {
  word-wrap: break-word;
  word-break: break-all;
  overflow-x: hidden;
}

.detailBox .article-content img {
  max-width: 100% !important;
  height: auto !important;
  overflow-x: hidden;
}

.detailBox .article-content a {
  color: #df2050 !important;
}

.detailBox .article-content a:hover {
  text-decoration: underline;
  color: #f00 !important;
}

.detailBox .article-content i {
  font-style: italic;
}

.detailBox .article-content strong {
  font-weight: bold;
}

.detailBox .article-content ul {
  list-style-type: disc !important;
  list-style: disc !important;
  padding-left: 40px !important;

  li {
    list-style-type: disc !important;
    list-style: disc !important;
  }
}

.detailBox .article-content h1,
.detailBox .article-content h2,
.detailBox .article-content h3 {
  font-size: 200%;
  font-weight: bold;
}

.detailBox .article-content h4,
.detailBox .article-content h5,
.detailBox .article-content h6 {
  font-size: 150%;
  font-weight: bold;
}

.detailBox .viewdetail {
  margin: 10px 0;
  line-height: 24px;
  text-align: center;
}

/*分享图标*/
.dshareBox {
  margin-top: 40px;
  position: relative;
}

.dshareBox a {
  position: relative;
  display: inline-block;
  width: 32px;
  height: 32px;
  font-size: 18px;
  border-radius: 50%;
  line-height: 32px;
  text-align: center;
  vertical-align: middle;
  margin: 4px;
  background: var(--surface);
  transition: background 0.6s ease-out;
}

.dshareBox .ds-weibo {
  border: 1px solid #ff763b;
  color: #ff763b;
}

.dshareBox .ds-weibo:hover {
  color: #fff;
  background: #ff763b;
}

.dshareBox .ds-qq {
  color: #56b6e7;
  border: 1px solid #56b6e7;
}

.dshareBox .ds-qq:hover {
  color: #fff;
  background: #56b6e7;
}

.dshareBox .ds-wechat {
  color: #7bc549;
  border: 1px solid #7bc549;
}

.dshareBox .ds-wechat:hover {
  color: #fff;
  background: #7bc549;
}

.dshareBox .ds-wechat:hover .wechatShare {
  opacity: 1;
  visibility: visible;
}

.detailBox .bdshare-button-style0-32 a {
  float: none;
  background-image: none;
  text-indent: inherit;
}

/*点赞 收藏*/
.dlikeColBox {
  display: inline-block;
  float: right;
}

.dlikeBox,
.dcollectBox {
  display: inline-block;
  padding: 0 10px;
  height: 35px;
  color: #e26d6d;
  line-height: 35px;
  border-radius: 35px;
  border: 1px solid #e26d6d;
  cursor: pointer;
}

/*赞赏*/
.donate-word {
  margin: 40px 0;
  text-align: center;
  position: relative;
}

.donate-word span {
  display: inline-block;
  width: 80px;
  height: 34px;
  line-height: 34px;
  color: #fff;
  background: #e26d6d;
  margin: 0 auto;
  border-radius: 4px;
  cursor: pointer;
}

.donate-body {
  display: none;
}

.donate-body-show {
  display: block;
}

.donate-item {
  text-align: right;
}

.donate-item:last-child {
  text-align: left;
}

.donate-item img {
  width: 100%;
  display: block;
  height: auto;
}

.donate-item div {
  display: inline-block;
  width: 150px;
  padding: 0 6px;
  box-sizing: border-box;
  text-align: center;
}

.donate-item div span {
  display: inline-block;
  width: 100%;
  margin: 10px 0;
  text-align: center;
}

.donate-body .donate-item:first-of-type div {
  color: #44b549;
}

.donate-body .donate-item:nth-of-type(2) div {
  color: #00a0e9;
}

.bd_weixin_popup {
  position: fixed !important;
}



@font-face {
  font-family: octicons-link;
  src: url(data:font/woff;charset=utf-8;base64,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) format('woff');
}

.markdown-body {
  -ms-text-size-adjust: 100%;
  -webkit-text-size-adjust: 100%;
  line-height: 1.5;
  color: #24292e;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
  font-size: 16px;
  line-height: 1.5;
  word-wrap: break-word;
}

.markdown-body .pl-c {
  color: #6a737d;
}

.markdown-body .pl-c1,
.markdown-body .pl-s .pl-v {
  color: #005cc5;
}

.markdown-body .pl-e,
.markdown-body .pl-en {
  color: #6f42c1;
}

.markdown-body .pl-smi,
.markdown-body .pl-s .pl-s1 {
  color: #24292e;
}

.markdown-body .pl-ent {
  color: #22863a;
}

.markdown-body .pl-k {
  color: #d73a49;
}

.markdown-body .pl-s,
.markdown-body .pl-pds,
.markdown-body .pl-s .pl-pse .pl-s1,
.markdown-body .pl-sr,
.markdown-body .pl-sr .pl-cce,
.markdown-body .pl-sr .pl-sre,
.markdown-body .pl-sr .pl-sra {
  color: #032f62;
}

.markdown-body .pl-v,
.markdown-body .pl-smw {
  color: #e36209;
}

.markdown-body .pl-bu {
  color: #b31d28;
}

.markdown-body .pl-ii {
  color: #fafbfc;
  background-color: #b31d28;
}

.markdown-body .pl-c2 {
  color: #fafbfc;
  background-color: #d73a49;
}

.markdown-body .pl-c2::before {
  content: "^M";
}

.markdown-body .pl-sr .pl-cce {
  font-weight: bold;
  color: #22863a;
}

.markdown-body .pl-ml {
  color: #735c0f;
}

.markdown-body .pl-mh,
.markdown-body .pl-mh .pl-en,
.markdown-body .pl-ms {
  font-weight: bold;
  color: #005cc5;
}

.markdown-body .pl-mi {
  font-style: italic;
  color: #24292e;
}

.markdown-body .pl-mb {
  font-weight: bold;
  color: #24292e;
}

.markdown-body .pl-md {
  color: #b31d28;
  background-color: #ffeef0;
}

.markdown-body .pl-mi1 {
  color: #22863a;
  background-color: #f0fff4;
}

.markdown-body .pl-mc {
  color: #e36209;
  background-color: #ffebda;
}

.markdown-body .pl-mi2 {
  color: #f6f8fa;
  background-color: #005cc5;
}

.markdown-body .pl-mdr {
  font-weight: bold;
  color: #6f42c1;
}

.markdown-body .pl-ba {
  color: #586069;
}

.markdown-body .pl-sg {
  color: #959da5;
}

.markdown-body .pl-corl {
  text-decoration: underline;
  color: #032f62;
}

.markdown-body .octicon {
  display: inline-block;
  vertical-align: text-top;
  fill: currentColor;
}

.markdown-body a {
  background-color: transparent;
  -webkit-text-decoration-skip: objects;
}

.markdown-body a:active,
.markdown-body a:hover {
  outline-width: 0;
}

.markdown-body strong {
  font-weight: inherit;
}

.markdown-body strong {
  font-weight: bolder;
}

.markdown-body h1 {
  font-size: 2em;
  margin: 0.67em 0;
}

.markdown-body img {
  border-style: none;
}

.markdown-body svg:not(:root) {
  overflow: hidden;
}

.markdown-body code,
.markdown-body kbd,
.markdown-body pre {
  font-family: monospace, monospace;
  font-size: 1em;
}

.markdown-body hr {
  box-sizing: content-box;
  height: 0;
  overflow: visible;
}

.markdown-body input {
  font: inherit;
  margin: 0;
}

.markdown-body input {
  overflow: visible;
}

.markdown-body [type="checkbox"] {
  box-sizing: border-box;
  padding: 0;
}

.markdown-body * {
  box-sizing: border-box;
}

.markdown-body input {
  font-family: inherit;
  font-size: inherit;
  line-height: inherit;
}

.markdown-body a {
  color: #0366d6;
  text-decoration: none;
}

.markdown-body a:hover {
  text-decoration: underline;
}

.markdown-body strong {
  font-weight: 600;
}

.markdown-body hr {
  height: 0;
  margin: 15px 0;
  overflow: hidden;
  background: transparent;
  border: 0;
  border-bottom: 1px solid #dfe2e5;
}

.markdown-body hr::before {
  display: table;
  content: "";
}

.markdown-body hr::after {
  display: table;
  clear: both;
  content: "";
}

.markdown-body table {
  border-spacing: 0;
  border-collapse: collapse;
}

.markdown-body td,
.markdown-body th {
  padding: 0;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  margin-top: 0;
  margin-bottom: 0;
}

.markdown-body h1 {
  font-size: 32px;
  font-weight: 600;
}

.markdown-body h2 {
  font-size: 24px;
  font-weight: 600;
}

.markdown-body h3 {
  font-size: 20px;
  font-weight: 600;
}

.markdown-body h4 {
  font-size: 16px;
  font-weight: 600;
}

.markdown-body h5 {
  font-size: 14px;
  font-weight: 600;
}

.markdown-body h6 {
  font-size: 12px;
  font-weight: 600;
}

.markdown-body p {
  margin-top: 0;
  margin-bottom: 10px;
}

.markdown-body blockquote {
  margin: 0;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 0;
  margin-top: 0;
  margin-bottom: 0;
}

.markdown-body ol ol,
.markdown-body ul ol {
  list-style-type: lower-roman;
}

.markdown-body ul ul ol,
.markdown-body ul ol ol,
.markdown-body ol ul ol,
.markdown-body ol ol ol {
  list-style-type: lower-alpha;
}

.markdown-body dd {
  margin-left: 0;
}

.markdown-body code {
  font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
  font-size: 12px;
}

.markdown-body pre {
  margin-top: 0;
  margin-bottom: 0;
  font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
  font-size: 12px;
}

.markdown-body .octicon {
  vertical-align: text-bottom;
}

.markdown-body .pl-0 {
  padding-left: 0 !important;
}

.markdown-body .pl-1 {
  padding-left: 4px !important;
}

.markdown-body .pl-2 {
  padding-left: 8px !important;
}

.markdown-body .pl-3 {
  padding-left: 16px !important;
}

.markdown-body .pl-4 {
  padding-left: 24px !important;
}

.markdown-body .pl-5 {
  padding-left: 32px !important;
}

.markdown-body .pl-6 {
  padding-left: 40px !important;
}

.markdown-body::before {
  display: table;
  content: "";
}

.markdown-body::after {
  display: table;
  clear: both;
  content: "";
}

.markdown-body>*:first-child {
  margin-top: 0 !important;
}

.markdown-body>*:last-child {
  margin-bottom: 0 !important;
}

.markdown-body a:not([href]) {
  color: inherit;
  text-decoration: none;
}

.markdown-body .anchor {
  float: left;
  padding-right: 4px;
  margin-left: -20px;
  line-height: 1;
}

.markdown-body .anchor:focus {
  outline: none;
}

.markdown-body p,
.markdown-body blockquote,
.markdown-body ul,
.markdown-body ol,
.markdown-body dl,
.markdown-body table,
.markdown-body pre {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body hr {
  height: 0.25em;
  padding: 0;
  margin: 24px 0;
  background-color: #e1e4e8;
  border: 0;
}

.markdown-body blockquote {
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
}

.markdown-body blockquote>:first-child {
  margin-top: 0;
}

.markdown-body blockquote>:last-child {
  margin-bottom: 0;
}

.markdown-body kbd {
  display: inline-block;
  padding: 3px 5px;
  font-size: 11px;
  line-height: 10px;
  color: #444d56;
  vertical-align: middle;
  background-color: #fafbfc;
  border: solid 1px #c6cbd1;
  border-bottom-color: #959da5;
  border-radius: 3px;
  box-shadow: inset 0 -1px 0 #959da5;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-body h1 .octicon-link,
.markdown-body h2 .octicon-link,
.markdown-body h3 .octicon-link,
.markdown-body h4 .octicon-link,
.markdown-body h5 .octicon-link,
.markdown-body h6 .octicon-link {
  color: #1b1f23;
  vertical-align: middle;
  visibility: hidden;
}

.markdown-body h1:hover .anchor,
.markdown-body h2:hover .anchor,
.markdown-body h3:hover .anchor,
.markdown-body h4:hover .anchor,
.markdown-body h5:hover .anchor,
.markdown-body h6:hover .anchor {
  text-decoration: none;
}

.markdown-body h1:hover .anchor .octicon-link,
.markdown-body h2:hover .anchor .octicon-link,
.markdown-body h3:hover .anchor .octicon-link,
.markdown-body h4:hover .anchor .octicon-link,
.markdown-body h5:hover .anchor .octicon-link,
.markdown-body h6:hover .anchor .octicon-link {
  visibility: visible;
}

.markdown-body h1 {
  padding-bottom: 0.3em;
  font-size: 2em;
  border-bottom: 1px solid #eaecef;
}

.markdown-body h2 {
  padding-bottom: 0.3em;
  font-size: 1.5em;
  border-bottom: 1px solid #eaecef;
}

.markdown-body h3 {
  font-size: 1.25em;
}

.markdown-body h4 {
  font-size: 1em;
}

.markdown-body h5 {
  font-size: 0.875em;
}

.markdown-body h6 {
  font-size: 0.85em;
  color: #6a737d;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 2em;
}

.markdown-body ul ul,
.markdown-body ul ol,
.markdown-body ol ol,
.markdown-body ol ul {
  margin-top: 0;
  margin-bottom: 0;
}

.markdown-body li>p {
  margin-top: 16px;
}

.markdown-body li+li {
  margin-top: 0.25em;
}

.markdown-body dl {
  padding: 0;
}

.markdown-body dl dt {
  padding: 0;
  margin-top: 16px;
  font-size: 1em;
  font-style: italic;
  font-weight: 600;
}

.markdown-body dl dd {
  padding: 0 16px;
  margin-bottom: 16px;
}

.markdown-body table {
  display: block;
  width: 100%;
  overflow: auto;
}

.markdown-body table th {
  font-weight: 600;
}

.markdown-body table th,
.markdown-body table td {
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
}

.markdown-body table tr {
  background-color: #fff;
  border-top: 1px solid #c6cbd1;
}

.markdown-body table tr:nth-child(2n) {
  background-color: #f6f8fa;
}

.markdown-body img {
  max-width: 100%;
  box-sizing: content-box;
  background-color: #fff;
}

.markdown-body img[align=right] {
  padding-left: 20px;
}

.markdown-body img[align=left] {
  padding-right: 20px;
}

.markdown-body code {
  padding: 0;
  padding-top: 0.2em;
  padding-bottom: 0.2em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
}

.markdown-body code::before,
.markdown-body code::after {
  letter-spacing: -0.2em;
  content: "\00a0";
}

.markdown-body pre {
  word-wrap: normal;
}

.markdown-body pre>code {
  padding: 0;
  margin: 0;
  font-size: 100%;
  word-break: normal;
  white-space: pre;
  background: transparent;
  border: 0;
}

.markdown-body .highlight {
  margin-bottom: 16px;
}

.markdown-body .highlight pre {
  margin-bottom: 0;
  word-break: normal;
}

.markdown-body .highlight pre,
.markdown-body pre {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 3px;
}

.markdown-body pre code {
  display: inline;
  max-width: auto;
  padding: 0;
  margin: 0;
  overflow: visible;
  line-height: inherit;
  word-wrap: normal;
  background-color: transparent;
  border: 0;
}

.markdown-body pre code::before,
.markdown-body pre code::after {
  content: normal;
}

.markdown-body .full-commit .btn-outline:not(:disabled):hover {
  color: #005cc5;
  border-color: #005cc5;
}

.markdown-body kbd {
  display: inline-block;
  padding: 3px 5px;
  font: 11px "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
  line-height: 10px;
  color: #444d56;
  vertical-align: middle;
  background-color: #fafbfc;
  border: solid 1px #d1d5da;
  border-bottom-color: #c6cbd1;
  border-radius: 3px;
  box-shadow: inset 0 -1px 0 #c6cbd1;
}

.markdown-body :checked+.radio-label {
  position: relative;
  z-index: 1;
  border-color: #0366d6;
}

.markdown-body .task-list-item {
  list-style-type: none;
}

.markdown-body .task-list-item+.task-list-item {
  margin-top: 3px;
}

.markdown-body .task-list-item input {
  margin: 0 0.2em 0.25em -1.6em;
  vertical-align: middle;
}

.markdown-body hr {
  border-bottom-color: #eee;
}

/* ===========================================================
   正文增强：代码块工具栏 / 图片灯箱 / 暗色适配
   =========================================================== */
.article-content .code-block {
  position: relative;
  margin: 18px 0;
  border-radius: var(--radius-sm);
  overflow: hidden;
  border: 1px solid var(--border);
  background: var(--surface);
  box-shadow: var(--shadow-1);
}

.article-content .code-block pre {
  margin: 0;
  background: transparent;
  border: 0;
}

.article-content .code-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  background: var(--bg-soft);
  border-bottom: 1px solid var(--border);
}

.article-content .code-lang {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1px;
  color: var(--brand);
  margin-right: auto;
}

.article-content .code-copy,
.article-content .code-toggle {
  font-size: 11px;
  line-height: 1;
  padding: 5px 10px;
  border-radius: 999px;
  border: 1px solid var(--border-strong);
  background: var(--surface);
  color: var(--text-weak);
  cursor: pointer;
  transition: color .25s var(--ease), border-color .25s var(--ease), transform .25s var(--ease);

  &:hover {
    color: var(--brand);
    border-color: var(--brand);
    transform: translateY(-1px);
  }

  &.is-done {
    color: #22c55e;
    border-color: #22c55e;
  }
}

.article-content .code-block.is-collapsed pre {
  max-height: 340px;
  overflow: hidden;
}

.article-content .code-block.is-collapsed::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 90px;
  pointer-events: none;
  background: linear-gradient(to bottom, transparent, var(--surface));
}

.article-content img.zoomable {
  cursor: zoom-in;
  border-radius: var(--radius-sm);
  transition: transform .35s var(--ease), box-shadow .35s var(--ease);

  &:hover {
    transform: scale(1.01);
    box-shadow: var(--shadow-2);
  }
}

/* 暗色模式下 GitHub markdown 硬编码的浅色底需要覆盖 */
[data-theme='dark'] .markdown-body {
  color: var(--text);

  pre,
  .highlight pre {
    background: #111827 !important;
    color: #e6edf3 !important;
  }

  code {
    background: rgba(127, 140, 170, .16);
    color: #ff9ec7;
  }

  blockquote {
    color: var(--text-weak);
    border-left-color: var(--brand);
  }

  table tr {
    background: transparent;
    border-top-color: var(--border);
  }

  table tr:nth-child(2n) {
    background: rgba(255, 255, 255, .04);
  }

  table td,
  table th {
    border-color: var(--border);
  }

  h1,
  h2 {
    border-bottom-color: var(--border);
  }

  hr {
    background: var(--border);
  }
}

@media (prefers-reduced-motion: reduce) {
  .article-content img.zoomable {
    transition: none;
  }
}

/* ---------- 上下篇 ---------- */
.neighborBox {
  display: flex;
  gap: 14px;
  margin: 30px 0 6px;
}

.neighbor {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px 16px;
  border-radius: var(--radius-sm, 10px);
  background: var(--surface-2, rgba(79, 140, 255, .06));
  color: var(--text);
  text-decoration: none;
  transition: transform .25s var(--ease, ease), background .25s var(--ease, ease);
}

a.neighbor:hover {
  transform: translateY(-2px);
  background: linear-gradient(120deg, rgba(79, 140, 255, .12), rgba(123, 108, 255, .12));
}

.neighbor.next {
  text-align: right;
  align-items: flex-end;
}

.neighbor.disabled {
  opacity: .5;
}

.nei-label {
  font-size: 12px;
  color: var(--text-weak);
}

.nei-title {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

/* ---------- 相关文章 ---------- */
.relatedBox {
  margin: 22px 0 10px;
  padding-top: 18px;
  border-top: 1px dashed var(--border);
}

.related-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text);
  margin: 0 0 12px;
}

.related-list {
  padding: 0;
}

.related-list li {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  padding: 7px 0;
  font-size: 14px;
}

.related-list a {
  color: var(--text);
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color .25s var(--ease, ease);
}

.related-list a:hover {
  color: var(--brand);
}

.related-meta {
  flex-shrink: 0;
  font-size: 12px;
  color: var(--text-weak);
}

@media screen and (max-width: 768px) {
  .neighborBox {
    flex-direction: column;
  }

  .neighbor.next {
    text-align: left;
    align-items: flex-start;
  }

  .related-list li {
    flex-direction: column;
    gap: 2px;
  }
}
</style>
