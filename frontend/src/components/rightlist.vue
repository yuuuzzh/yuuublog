<!-- 右侧固定导航栏 -->
<template>
  <div class="rightlistBox">
    <section>
      <div class="r1-head">
        <img :src="this.$store.state.themeObj.center_smailimg
          ? this.$store.state.themeObj.center_smailimg
          : 'static/img/img01.jpg'
          " alt="" />
        <h1 v-if="this.$store.state.themeObj.user_start != 0">
          <span>请别再唤我为孤独之人</span>
        </h1>
      </div>
      <div class="r1-body">
        <p>蔚</p>
        <div class="catch-me">
          <div class="">
            <el-tooltip class="item" content="Github" placement="top">
              <a :href="catchMeObj.git" target="_blank"><i class="fa fa-fw fa-github"></i></a>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="QQ" placement="top">
              <a :href="catchMeObj.qq" target="_blank"><i class="fa fa-fw fa-qq"></i></a>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="微博" placement="top">
              <a :href="catchMeObj.sina" target="_blank"><i class="fa fa-fw fa-weibo"></i></a>
            </el-tooltip>
          </div>
          <div class="">
            <el-tooltip class="item" effect="dark" content="微信" placement="top">
              <a :href="catchMeObj.wechat" target="_blank"><i class="fa fa-fw fa-wechat"></i></a>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="CSDN" placement="top">
              <a :href="catchMeObj.csdn" target="_blank"><i class="">C</i></a>
            </el-tooltip>
            <el-tooltip class="item" effect="dark" content="Gitee" placement="top">
              <a :href="catchMeObj.gitee" target="_blank"><i class="">G</i></a>
            </el-tooltip>
          </div>
        </div>
      </div>
    </section>
    <yuuu-subscribe></yuuu-subscribe>
    <yuuu-tagcloud></yuuu-tagcloud>
    <section class="rs4">
      <h2 class="ui label">热门文章</h2>
      <ul>
        <li v-for="(item, index) in browseList" :key="'browseList' + index">
          <a :href="'/DetailArticle?aid=' + item.id" target="_blank">{{
            item.title
          }}</a>
          —— {{ item.viewCount }} 次围观
        </li>
      </ul>
    </section>
    <section class="rs4">
      <h2 class="ui label">登录信息</h2>
      <br>
      <div v-if="IpInfo.ip" style="font-family: 楷体">
        登录设备:<span>{{ IpInfo.devName }}</span> &nbsp;<i class="fa fa-ellipsis-v"></i> &nbsp;
        IP:<span>{{ IpInfo.ip }}</span><br>
        位置:<span>{{ IpInfo.address }}</span> <br>
        浏览器:<span>{{ IpInfo.browser }}</span> <br>
        操作系统:<span>{{ IpInfo.os }}</span> <br>
        <p id="showTime">时间：{{ formattedTime }}</p>
      </div>
      <div v-else>
        <span>登陆后查看</span>
      </div>

    </section>
    <hr>
    <section class="rs4">
      <a href="https://beian.miit.gov.cn/">
        豫ICP备2023004704号</a>
    </section>
    <!-- 右侧上滑小图片 -->
    <div v-if="this.$store.state.themeObj.user_start != 0" :class="gotoTop ? 'toTop hidden' : 'toTop goTop hidden'"
      @click="toTopfun">
      <img :src="this.$store.state.themeObj.right_img
        ? this.$store.state.themeObj.right_img
        : 'static/img/scroll.png'
        " alt="" />
    </div>
    <div v-else :class="gotoTop ? 'toTophui hidden' : 'toTophui goTophui hidden'" @click="toTopfun">
      <img :src="this.$store.state.themeObj.right_img
        ? this.$store.state.themeObj.right_img
        : 'static/img/scroll.png'
        " alt="" />
    </div>
  </div>
</template>


<script>
import { hotArticleList } from "../api/article";
import tagcloud from "./tagcloud.vue";
import subscribeBox from "./subscribe.vue";
import request from "@/utils/request.js"
export default {
  data() {
    //选项 / 数据
    return {
      fixDo: false,
      loveme: false,
      IpInfo: "",//ip
      gotoTop: false, //返回顶部
      going: false, //是否正在执行上滑动作
      browseList: "", //热门文章 浏览量最多
      artCommentList: "", //最新评论
      formattedTime: "",
      myClock: null,
      catchMeObj: {
        //个人信息
        git: "https://github.com/YuuuZzH?tab=repositories",
        qq: "/static/img/qq.png",
        sina: "https://weibo.com",
        wechat: "/static/img/wx.png",
        csdn: "https://blog.csdn.net/m0_63378258?spm=1000.2115.3001.5343",
        job: "https://www.baidu.com",
        gitee:"https://gitee.com/yuuu-zehua",
      },
    };
  },
  methods: {
    //事件处理器
    toTopfun: function (e) {
      var that = this;
      this.gotoTop = false;
      this.going = true;
      var timer = setInterval(function () {
        //获取滚动条距离顶部高度
        var osTop =
          document.documentElement.scrollTop || document.body.scrollTop;
        var ispeed = Math.floor(-osTop / 7);
        document.documentElement.scrollTop = document.body.scrollTop =
          osTop + ispeed;
        //到达顶部，清除定时器
        if (osTop == 0) {
          that.going = false;
          clearInterval(timer);
          timer = null;
        }
      }, 15);
    },
    getTime() {
      this.myClock = setInterval(() => {
        let date = new Date();
        // 获取日期的各个部分
        let year = date.getFullYear();
        let month = (date.getMonth() + 1).toString().padStart(2, '0'); // 月份补齐两位数
        let day = date.getDate().toString().padStart(2, '0'); // 天数补齐两位数
        let hours = date.getHours().toString().padStart(2, '0'); // 小时补齐两位数
        let minutes = date.getMinutes().toString().padStart(2, '0'); // 分钟补齐两位数
        let seconds = date.getSeconds().toString().padStart(2, '0'); // 秒数补齐两位数

        // 获取星期的文本表示
        let weekdays = ["日", "一", "二", "三", "四", "五", "六"];
        let dayOfWeek = weekdays[date.getDay()];

        // 将各部分拼接成格式化的时间字符串
        this.formattedTime = `${year}-${month}-${day} 星期${dayOfWeek} ${hours}:${minutes}:${seconds}`;
      }, 1000);
    },

    getHotArticleList() {
      hotArticleList().then((response) => {
        this.browseList = response;
      });
    },
    getIpList() {
      if (localStorage.getItem("userInfo")) {
        request.post("/ip").then((response) => {
          this.IpInfo = response
        });
      }
    },
  },
  components: {
    //定义组件
    "yuuu-tagcloud": tagcloud,
    "yuuu-subscribe": subscribeBox,
  },

  created() {
    //生命周期函数
    var that = this;
    window.onscroll = function () {
      var t = document.documentElement.scrollTop || document.body.scrollTop;
      // console.log(t);
      if (!that.going) {
        if (t > 600) {
          that.gotoTop = true;
        } else {
          that.gotoTop = false;
        }
      }
      if (t > 1200) {
        that.fixDo = true;
      } else {
        that.fixDo = false;
      }
    };
    //查询浏览量最多的10篇文章数据
    this.getHotArticleList();
    this.getIpList();
    this.getTime();
  },
  destroyed() {
    clearInterval(this.myClock);
  }
};
</script>

<style lang="less">
.rightlistBox {
  position: relative;
}

.rightlistBox section {
  transition: all 0.2s linear;
  position: relative;
  background: var(--surface);
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 5px;
}

.rightlistBox section:hover {
  transform: translate(0, -2px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.rightlistBox .r1-head {
  text-align: center;
  border-radius: 4px 4px 0 0;
  text-align: center;
  position: relative;
  /*box-shadow: inset 0 -70px 100px -50px rgba(0,0,0,.5);*/
}

.rightlistBox .r1-head img {
  width: 100%;
  min-height: 100px;
}

.rightlistBox .r1-head h1 {
  position: absolute;
  bottom: 5px;
  margin: 0 0 0 -65px;
  font-size: 20px;
  letter-spacing: 0.5px;
  color: #fff;
  text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.7);
  font-weight: 700;
  width: 130px;
  left: 50%;
}

.rightlistBox .r1-head h1 span {
  opacity: 0.3;
}

.rightlistBox .r1-body p {
  font-size: 14px;
  margin: 5px 0 8px 0;
  font-weight: 700;
  text-align: center;
}

.rightlistBox .r1-body .catch-me {
  text-align: center;
}

.rightlistBox .r1-body .catch-me a {
  display: inline-block;
  margin-bottom: 7px;
  position: relative;
  text-decoration: none;
}

.rightlistBox .r1-body .catch-me a:hover i {
  color: #fff;
  background: #f4692c;
}

.rightlistBox .r1-body .catch-me a i {
  display: inline-block;
  font-size: 18px;
  width: 42px;
  height: 42px;
  line-height: 42px;
  border-radius: 42px;
  color: rgba(0, 0, 0, 0.5);
  background: rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease-in-out;
  font-style: normal;
  margin: 0 3.2px;
}

/*************do you like me*******************/
.rightlistBox .rs2 {
  /*padding:10px 0 4px 0;*/
  min-height: 100px;
}

.rightlistBox .rs2.fixed {
  position: fixed;
  top: 40px;
  width: 22%;
}

.rightlistBox .rs2 p {
  color: #df2050;
  cursor: pointer;
  font-size: 20px;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: center;
  margin-top: 10px;
  font-weight: 500;
}

.rightlistBox .rs2 div {
  color: #df2050;
  cursor: pointer;
  text-align: center;
  font-size: 40px;
  position: absolute;
  width: 100%;
  height: 100px;
  line-height: 100px;
  left: 0;
  top: 30px;
}

.rightlistBox .rs2 div i.heart {
  display: inline-block;
  text-align: center;
  width: 100px;
  height: 100px;
  margin-left: -20px;
  margin-top: -5px;
  background: url(../../static/img/heart.png) no-repeat;
  background-position: 0 0;
  cursor: pointer;
  -webkit-transition: background-position 1s steps(28);
  transition: background-position 1s steps(28);
  -webkit-transition-duration: 0s;
  transition-duration: 0s;
  vertical-align: middle;
}

.rightlistBox .rs2 div i.heart:hover {
  transform: scale(1.15);
  -webkit-transform: scale(1.15);
}

.rightlistBox .rs2 div i.heart.active {
  -webkit-transition-duration: 1s;
  transition-duration: 1s;
  background-position: -2800px 0;
}

.rightlistBox .rs2 div span {
  margin-left: -30px;
}

/**********排队来说*************/
.rightlistBox .rs3 .rs3-item {
  font-size: 13px;
  line-height: 20px;
}

.rightlistBox .rs3 .rs3-item a {
  display: block;
  padding: 5px;
  transition: all 0.3s ease-out;
  border-bottom: 1px solid #ddd;
  margin: 5px 0;
}

.rightlistBox .rs3 .rs3-item a:hover {
  background: rgba(230, 244, 250, 0.5);
  border-radius: 5px;
}

.rightlistBox .rs3 .rs3-photo {
  float: left;
}

.rightlistBox .rs3 .rs3-photo img {
  border-radius: 50%;
  width: 32px;
  height: 32px;
  object-fit: cover;
}

.rightlistBox .rs3 .rs3-inner {
  margin-left: 40px;
}

.rightlistBox .rs3 .rs3-inner .rs3-author {
  font-weight: 700;
}

.rightlistBox .rs3 .rs3-inner .rs3-text {
  font-size: 12px;
  text-align: justify;
}

.rightlistBox .rs3 .rs3-item:last-child a {
  border-bottom: none;
}

/************排队看这些***************/
.rightlistBox .rs4 li {
  padding: 8px 0;
  text-align: justify;
}

.rightlistBox .rs4 li a {
  font-weight: 600;
}

.rightlistBox .rs4 li a:hover {
  color: #64609e;
}

/*回到顶部*/
/*返回到顶部*/
.toTop {
  position: fixed;
  right: 40px;
  // top: -150px;
  top: 0;
  z-index: 99;
  width: 70px;
  height: 80%;
  // border: 1px solid #ef0505;
  transition: all 0.5s 0.3s ease-in-out;
  cursor: pointer;
}

.goTop {
  top: -950px;
}

.toTop img,
.toTophui img {
  width: 100%;
  height: 100%;
}

.toTophui {
  position: fixed;
  right: 10px;
  bottom: 80px;
  z-index: 99;
  width: 120px;
  height: 120px;
  transition: all 0.5s 0.3s ease-in-out;
  cursor: pointer;
  animation: toflow 2s ease-in-out infinite;
}

@keyframes toflow {
  0% {
    /*top:400px;*/
    transform: scale(0.95) translate(0, 10px);
  }

  50% {
    /*top:410px;*/
    transform: scale(1) translate(0, 0px);
  }

  100% {
    /*top:400px;*/
    transform: scale(0.95) translate(0, 10px);
  }
}

.goTophui {
  bottom: 120vh;
}

/* ================= 侧栏统一到新设计系统 ================= */
.rightlistBox section {
  border-radius: var(--radius);
  box-shadow: var(--shadow-1);
  padding: 18px;
  background: var(--surface);
}

.rightlistBox section:hover {
  transform: translate(0, -2px);
  box-shadow: var(--shadow-2);
}

/* 头图保持原状（矩形通栏，不做圆形裁切） */

.rightlistBox .r1-head h1 span {
  color: var(--text);
}

.rightlistBox .r1-body p {
  color: var(--text-weak);
}

/* 小标题统一为胶囊标签 */
.rightlistBox section h2.ui.label,
.rightlistBox .rs4 h2.ui.label {
  display: inline-block;
  margin: 0 0 12px;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
  color: var(--brand);
  background: rgba(79, 140, 255, .12);
}

/* 联系我：只在原 42px 圆形图标上换配色，避免与 a 层样式叠出重影 */
.rightlistBox .r1-body .catch-me a:hover i {
  color: #fff;
  background: linear-gradient(120deg, var(--brand), var(--brand-2));
  box-shadow: var(--shadow-glow);
}

/* 分隔线与卡片风格统一 */
.rightlistBox hr {
  border: none;
  border-top: 1px dashed var(--border);
  margin: 20px 0;
}

/* 列表项：hover 位移 + 品牌色 */
.rightlistBox .rs4 li a,
.rightlistBox .rs3 .rs3-item a {
  color: var(--text-weak);
  transition: color .25s var(--ease), padding-left .25s var(--ease);
}

.rightlistBox .rs4 li a:hover,
.rightlistBox .rs3 .rs3-item a:hover {
  color: var(--brand);
  padding-left: 4px;
}

.rightlistBox .rs4 li {
  border-bottom: 1px dashed var(--border);
}

.rightlistBox .rs4 li:last-child {
  border-bottom: none;
}

/* 心动按钮动效 */
.rightlistBox .rs2 div i.heart {
  transition: transform .3s var(--ease), color .3s var(--ease);
}

.rightlistBox .rs2 div i.heart:hover {
  transform: scale(1.18);
}

.rightlistBox .rs2 div i.heart.active {
  color: var(--brand);
  animation: heartPop .5s var(--ease);
}

@keyframes heartPop {
  0% { transform: scale(1); }
  40% { transform: scale(1.35); }
  70% { transform: scale(.92); }
  100% { transform: scale(1); }
}

.rightlistBox .rs2 div span {
  color: var(--text-weak);
}

/* 登录信息 */
.rightlistBox .rs4 div span,
.rightlistBox .rs4 p {
  color: var(--text-weak);
}

@media (prefers-reduced-motion: reduce) {
  .rightlistBox section,
  .rightlistBox .rs2 div i.heart,
  .rightlistBox .rs4 li a { transition: none; }

  .rightlistBox .rs2 div i.heart.active { animation: none; }
}
</style>
