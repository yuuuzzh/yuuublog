<template>
    <div id="app">
        <ScrollProgress></ScrollProgress>
        <ThemeToggle></ThemeToggle>
        <CommandPalette></CommandPalette>
        <CursorFx></CursorFx>
        <MobileTabBar></MobileTabBar>
        <ReadSettings></ReadSettings>
        <transition name="page" mode="out-in">
            <keep-alive :include="['Aboutme', 'Message', 'FriendsLink', 'Reward']">
                <router-view></router-view>
            </keep-alive>
        </transition>

        <el-tooltip class="item" effect="dark" content="Hi,我是YuuuChat，快来和我聊天吧！" placement="right">
            <img id="fix-image" src="../static/img/yuuu.png" title="" alt="小yu机器人" style="position: fixed;
            width: 90px;
            height: 150px;
            top: 500px; 
            z-index: 1000;
            transform: translateX(-100%);" @mouseover="zoomIn" @mouseout="zoomOut" @click="openChat">
        </el-tooltip>
        <div class="chat-container" v-if="showChat" >
            <Chat></Chat>
        </div>
    </div>
</template>

<script>
import Chat from './components/Chat.vue'
import ThemeToggle from './components/ThemeToggle.vue'
import ScrollProgress from './components/ScrollProgress.vue'
import CommandPalette from './components/CommandPalette.vue'
import CursorFx from './components/CursorFx.vue'
import MobileTabBar from './components/MobileTabBar.vue'
import ReadSettings from './components/ReadSettings.vue'
import { getStoredTheme, setStoredTheme, applyTheme } from './utils/theme.js'
export default {
    name: 'App',
    data() {
        return {
            isImageVisible: false,
            showChat: false,
            chatData: [],
            input: '',
            messages: [],
            touchStartX: null,
            touchStartY: 0,
            touchEdge: false
        }
    },
    methods: {
        // history 模式下站内普通 <a href="/xxx"> 会整页刷新，这里接管为前端路由跳转
        onInternalLinkClick(e) {
            var target = e.target;
            var link = null;
            while (target && target !== document) {
                if (target.tagName === 'A') {
                    link = target;
                    break;
                }
                target = target.parentNode;
            }
            if (!link) return;
            var href = link.getAttribute('href') || '';
            if (href.indexOf('#') === 0) return; // 页内锚点
            if (link.target === '_blank' || link.hasAttribute('download')) return; // 新窗口/下载交给浏览器
            if (e.metaKey || e.ctrlKey || e.shiftKey || e.altKey || e.button !== 0) return;
            if (href.indexOf(location.origin) === 0) {
                href = href.substring(location.origin.length);
            }
            // 只接管站内页面路径，放过接口与静态资源
            if (href.indexOf('/') !== 0 || href.indexOf('/api/') === 0 || href.indexOf('/static/') === 0) return;
            e.preventDefault();
            this.$router.push(href);
        },
        zoomIn() {
            const image = document.getElementById('fix-image');
            image.style.transition = 'transform 1s';
            image.style.transform = 'scale(1.5) translateX(10px)';
        },
        zoomOut() {
            const image = document.getElementById('fix-image');
            image.style.transition = 'transform 1s';
            image.style.transform = 'scale(1) translateX(0)';
        },
        handleScroll() {
            if (window.scrollY > 150 && !this.isImageVisible) {
                const image = document.getElementById('fix-image');
                image.style.transition = 'transform 1s';
                image.style.transform = 'translateX(0)';
                this.isImageVisible = true;
            } else if (window.scrollY <= 150 && this.isImageVisible) {
                const image = document.getElementById('fix-image');
                image.style.transition = 'transform 1s';
                image.style.transform = 'translateX(-100%)';
                this.isImageVisible = false;
            }
        },
        openChat() {
            if (localStorage.getItem('userInfo')) { //存储用户信息
                this.showChat = true;
            } else {
                this.open1();
                this.$router.push({ path: '/login' });
            }
        },
        open1() {
            const h = this.$createElement;

            this.$notify({
                title: '客官莫慌！',
                message: h('i', { style: 'color: red' }, '请先登录，然后再使用该功能')
            });
        },
        closeChat() {
            this.showChat = false;
        },
        handleClick(event) {
            // 判断点击的位置是否在聊天框之外
            if (!event.target.classList.contains('chat-container') && event.target.tagName !== 'IMG') {
                this.closeChat();
            }
        },
        // 移动端手势返回：从屏幕左边缘右滑 >80px 触发
        onTouchStart(e) {
            const t = e.touches && e.touches[0]
            if (!t) return
            this.touchStartX = t.clientX
            this.touchStartY = t.clientY
            this.touchEdge = t.clientX <= 40
        },
        onTouchEnd(e) {
            if (this.touchStartX === null) return
            const t = (e.changedTouches && e.changedTouches[0]) || {}
            const dx = (t.clientX || 0) - this.touchStartX
            const dy = (t.clientY || 0) - this.touchStartY
            this.touchStartX = null
            // 仅左边缘起手、横向位移够大且纵向不明显的滑动才算返回手势
            if (this.touchEdge && dx > 80 && Math.abs(dy) < 50) {
                if (window.history.length > 1) {
                    this.$router.back()
                } else {
                    this.$router.push({ path: '/Home' })
                }
            }
        },
        // 快捷键：T 循环切换主题（输入态下不拦截）
        onHotkey(e) {
            const el = e.target || {};
            if (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA' || el.isContentEditable) return;
            if (e.metaKey || e.ctrlKey || e.altKey) return;
            if (e.key === 't' || e.key === 'T') {
                const order = ['auto', 'light', 'dark'];
                const next = order[(order.indexOf(getStoredTheme()) + 1) % order.length];
                setStoredTheme(next);
                const real = applyTheme(next);
                this.$message.success('主题已切换：' + (real === 'dark' ? '暗色' : '亮色'));
            }
        },
    },
    components: {
        Chat,
        ThemeToggle,
        ScrollProgress,
        CommandPalette,
        CursorFx,
        MobileTabBar,
        ReadSettings
    },
    mounted() {
        window.addEventListener('scroll', this.handleScroll);
        window.addEventListener('click', this.handleClick);
        window.addEventListener('keydown', this.onHotkey);
        window.addEventListener('touchstart', this.onTouchStart, { passive: true });
        window.addEventListener('touchend', this.onTouchEnd, { passive: true });
        window.addEventListener('click', this.onInternalLinkClick);
        // 兼容已流传的旧 hash 链接：/#/DetailArticle?aid=25 -> /DetailArticle?aid=25，
        // 否则 history 模式下 hash 会被忽略而落到首页
        if (window.location.hash && window.location.hash.indexOf('#/') === 0) {
            this.$router.replace(window.location.hash.substring(1));
        }
        this.handleScroll();
    },
    destroyed() {
        window.removeEventListener('scroll', this.handleScroll);
        window.removeEventListener('click', this.handleClick);
        window.removeEventListener('keydown', this.onHotkey);
        window.removeEventListener('touchstart', this.onTouchStart);
        window.removeEventListener('touchend', this.onTouchEnd);
        window.removeEventListener('click', this.onInternalLinkClick);
    },
}
</script>

<style lang="less">
// 原博客主提色
@tcolors: #97dffd; //按钮和标签颜色
@tcolorm: #64609E; //分类的选中的颜色
@tcolord: #48456D; //按钮hover 的颜色

// /***** 粉红色主题****/
// @tcolors:#97dffd;//按钮和标签颜色
// @tcolorm:#64609E;//分类的选中的颜色
// @tcolord:#48456D;//按钮hover 的颜色
//
// /******淡黄色主题*****/
// @tcolors:#97dffd;//按钮和标签颜色
// @tcolorm:#64609E;//分类的选中的颜色
// @tcolord:#48456D;//按钮hover 的颜色
//
// /****** 淡紫色主题 ****/
// @tcolors:#97dffd;//按钮和标签颜色
// @tcolorm:#64609E;//分类的选中的颜色
// @tcolord:#48456D;//按钮hover 的颜色
.chat-container {
    position: fixed;
    top: 100px;
    width: 25%;
    // height: 50%;
    z-index: 9998;
    border: 1px solid var(--border-strong);
    border-radius: 5px;
    background-color: var(--surface);

}

/*reset*/
html,
body,
div,
span,
applet,
object,
iframe,
h1,
h2,
h3,
h4,
h5,
h6,
p,
blockquote,
pre,
a,
abbr,
acronym,
address,
big,
cite,
code,
del,
dfn,
em,
img,
ins,
kbd,
q,
s,
samp,
small,
strike,
strong,
sub,
sup,
tt,
var,
b,
u,
i,
center,
dl,
dt,
dd,
ol,
ul,
li,
fieldset,
form,
label,
legend,
caption,
tbody,
tfoot,
thead,
tr,
th,
td,
article,
aside,
canvas,
details,
embed,
figure,
figcaption,
footer,
header,
menu,
nav,
output,
ruby,
section,
summary,
time,
mark,
audio,
video {
    margin: 0;
    padding: 0;
    border: 0;
    font: inherit;
}

body {
    font-family: Arial, "STHeiti", Helvetica, sans-serif;
    background: var(--bg);
    font-size: 14px;
    color: var(--text) !important;
    min-height: 100vh;
    width: 100%;
    overflow-x: hidden;
    -webkit-overflow-x: hidden;
    -webkit-tap-highlight-color: transparent;
}

table {
    border-collapse: collapse;
    border-spacing: 0
}

fieldset,
img {
    border: 0
}

legend {
    display: none
}

// address, caption, cite, code, dfn, em, strong, th, var{
//   font-style:normal;
//   font-weight:normal
// }
ol,
ul {
    list-style: none
}

caption,
th {
    text-align: left
}

h1,
h2,
h3,
h4,
h5,
h6 {
    font-size: 100%;
    font-weight: normal
}

q:before,
q:after {
    content: ''
}

abbr,
acronym {
    border: 0
}

a {
    text-decoration: none;
}

html {
    -webkit-text-size-adjust: none;
    width: 100%;
    height: 100%
}

/*????iphone??safari????????????*/
input[type="text"],
input[type="button"],
input[type="submit"],
input[type="search"] {
    -webkit-appearance: none;
    border-radius: 0;
}

/*************其他内容*************/
#app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    width: 100%;
    overflow-x: hidden;
    -webkit-overflow-x: hidden;
    color: #444;
}

h1,
h2 {
    font-weight: normal;
}

a {
    color: #444;
}

.container {
    max-width: 80%;
    margin: 0 auto;
    padding: 0 10px;
    // overflow-x: hidden;
    // -webkit-overflow-x: hidden;
}

//时间和标题
/* 详情页头部：日期徽章（月份大、日期小，左右排）+ 标题/信息/分类 */
/* 悬挂吊牌：钉在卡片顶边、垂直垂落，带轻微摆动（瀑布感） */
.arHead {
    position: relative;
    padding-top: 52px;
    margin-bottom: 14px;
}

.arHead .arMain {
    min-width: 0;
}

.arDate {
    position: absolute;
    top: -8px;
    left: 30px;
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 18px 18px 13px;
    border-radius: 0 0 14px 14px;
    /* 白底毛玻璃吊牌 */
    background: rgba(255, 255, 255, .55);
    -webkit-backdrop-filter: blur(14px) saturate(170%);
    backdrop-filter: blur(14px) saturate(170%);
    border: 1px solid rgba(255, 255, 255, .6);
    color: var(--text, #3d4759);
    box-shadow: 0 8px 20px rgba(20, 40, 90, .12);
    transform-origin: top center;
    animation: tagSway 3.8s ease-in-out infinite;
}

/* 顶部钉孔：牌子挂在卡片顶边的"钉子"上 */
.arDate::before {
    content: '';
    position: absolute;
    top: 6px;
    left: 50%;
    transform: translateX(-50%);
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #e8edf5;
    box-shadow: inset 0 1px 2px rgba(0, 0, 0, .28);
}

.arDate .arMonth {
    font-size: 19px;
    font-weight: 700;
    line-height: 1;
}

.arDate .arDay {
    font-size: 13px;
    opacity: .85;
    line-height: 1;
    padding-left: 8px;
    border-left: 1px solid rgba(0, 0, 0, .15);
}

@keyframes tagSway {

    0%,
    100% {
        transform: rotate(-1.4deg);
    }

    50% {
        transform: rotate(1.4deg);
    }
}

.arDate .arMonth {
    font-size: 19px;
    font-weight: 700;
    line-height: 1;
}

.arDate .arDay {
    font-size: 13px;
    opacity: .85;
    line-height: 1;
    padding-left: 8px;
    border-left: 1px solid rgba(0, 0, 0, .15);
}

.s-round-date span {
    text-align: center;
    display: block;
}

.s-round-date .day {
    font-size: 30px;
    font-weight: 700;
}

// 相同的标题副标题
.tcommonBox {
    white-space: normal;
    word-wrap: break-word;
    word-break: break-all;
    position: relative;
    background: var(--surface);
    padding: 15px;
    border-radius: 5px;
    margin-bottom: 40px;
    font-size: 15px;
    box-shadow: var(--shadow-1);
}

.tcommonBox header {
    white-space: normal;
    word-wrap: break-word;
    word-break: break-all;
}

.tcommonBox header h1 {
    margin: 10px 0;
    font-size: 25px;
    font-weight: 700;
    text-align: center;
    line-height: 30px;
}

.tcommonBox header h2 {
    margin: 10px 0;
    line-height: 24px;
    text-align: center;
    color: #555;
    font-size: 14px;
}

.tcommonBox section p {
    line-height: 24px;
    margin: 10px 0;
}

.tcommonBox header h2 .el-rate,
.rateBox {
    display: inline-block;
}

.tcommonBox header h2 .el-rate i {
    font-size: 16px;
}

.s-item .article-coontent {
    font-size: 15px;
    white-space: normal;
    word-wrap: break-word;
    word-break: break-all;
}

//标题和按钮
.s-item .article-content p {
    margin: 10px 0;
    line-height: 24px;
}

.s-item .viewdetail {
    margin: 10px 0;
    line-height: 24px;
    text-align: center;
}

.s-item .viewdetail a {
    color: #fff;
    font-size: 12px;
    padding: 5px 10px;
    border-radius: 5px;
}

.viewmore {
    text-align: center;
    width: 100%;
}

.viewmore a {
    /*background:  #97dffd;*/
    /*padding:6px 0;*/
    border-radius: 5px;
    text-align: center;
    font-size: 14px;
    color: #fff;
    height: 30px;
    line-height: 30px;
    display: block;
}

@media screen and (max-width: 500px) {

    //500px 手机尺寸
    .dlikeColBox {
        //点赞收藏手机端换行
        display: block !important;
        float: none !important;
        margin: 10px auto !important;
        text-align: center !important;
    }

    .el-message-box {
        //提示弹框手机框展示
        max-width: 80%;
    }

    .userInfoBox .el-input,
    .userInfoBox .el-textarea {
        //用户中心表框
        max-width: 280px !important;
    }

    .userInfoBox li {
        //li 更改padding  缩小距离
        padding: 15px 10px !important;
    }

    .userInfoBox li .el-radio-group .el-radio {
        //标签样式调整
        margin-left: 15px !important;
        margin-bottom: 5px;
    }

    .tFriendsBox .tf-item a {
        //好友列表自适应缩小尺寸
        padding: 0 5px 0 60px !important;

        img {
            width: 50px !important;
            height: 50px !important;
            left: 5px !important;
        }
    }
}

@media screen and (max-width: 700px) {
    .headBox {
        display: none;
    }

    .mobileBox {
        //手机端导航栏显示
        display: block;
    }

    .headImgBox {
        height: 450px !important;
        margin-bottom: -140px !important;
    }

    .h-information {
        display: none;
    }

    .headImgBox .scene {
        font-size: 50px !important;
        top: 120px;
    }


}


@media screen and (min-width: 701px) {
    .headBox {
        display: block;
    }

    .mobileBox {
        display: none;
    }
}

@media screen and (max-width:800px) {
    .container {
        max-width: 100% !important;
        margin: 0 auto;
    }



    .ui.label {
        left: 0 !important;
        padding: 6px 12px !important;
    }

    .ui.label::after {
        display: none !important;
    }

    .headBox li.el-menu-item,
    .headBox .el-submenu__title {
        padding: 0 15px;
    }

    .toTop {
        display: none !important;
    }

    //隐藏上滑图标
    .hidden {
        display: none !important;
    }

}

@media screen and (min-width:801px) {

    .headBox li.el-menu-item,
    .headBox .el-submenu__title {
        padding: 0 12px;
    }

}

@media screen and (max-width:990px) {

    // .hidden{
    //     display: none!important;
    // }
    div.rightlistBox .rs2.fixed {
        position: relative;
        top: 0;
        width: auto;
    }
}

@media screen and (min-width:991px) {

    .headBox li.el-menu-item,
    .headBox .el-submenu__title {
        padding: 0 20px;
    }
}

.tcolors {
    color: @tcolors;
}

.tcolorm {
    color: @tcolorm;
}

.tcolord {
    color: @tcolord;
}

.tcolors-bg {
    background: @tcolors;
    transition: all .3s ease-in-out;
}

.tcolord-bg {
    background: @tcolord;
}

.tcolors-bg:hover {
    background: @tcolord;
}

.paginationBox li.active {
    background: @tcolorm;
}

.paginationBox li:hover {
    color: @tcolord;
}

.el-pagination button:hover {
    color: @tcolord;
}


.fitImg {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.maxW {
    max-width: 100%;
}

.fwb {
    font-weight: bold;
}

.right {
    float: right;
}

.left {
    float: left;
}

.btnone {
    border-top: none !important;
}

.brone {
    border-right: medium none !important;
}

.clearfix:before,
.clearfix:after {
    display: table;
    content: "";
}

.clearfix:after {
    clear: both
}

.smalltitle {
    /*height:36px;*/
    /*line-height: 36px;*/
}

.logoImg {
    width: 160px;
    ;
    height: 50px;
    display: inline-block;
    padding: 5px 5px;
    vertical-align: top;
}

.logoImg img {
    width: 100%;
    height: 100%;
}

//公共标签
.ui.label {
    border-color: @tcolors;
    box-shadow: 0 2px 2px 0 rgba(0, 0, 0, .14), 0 3px 1px -2px rgba(0, 0, 0, .2), 0 1px 5px 0 rgba(0, 0, 0, .12);
    background-color: #97dffd;
    /* 取消原来挂到卡片左边缘外侧的偏移与三角形，改为干净圆角标签 */
    margin: 5px 0 15px 0;
    font-size: 14px;
    border-radius: 4px;
    padding: 6px 12px;
    display: inline-block;
    color: #fff;
}

.ui.label::after {
    display: none;
}

.ui.label a {
    color: #fff;
    line-height: 100%;
}

// 详情页：分类标签固定到卡片右上角（红框位置），移动端回落为普通标签
.detailBox .ui.label {
    position: absolute;
    top: -8px;
    right: 30px;
    margin: 0;
    padding: 18px 16px 13px;
    border-radius: 0 0 14px 14px;
    /* 白底毛玻璃吊牌 */
    background: rgba(255, 255, 255, .55);
    -webkit-backdrop-filter: blur(14px) saturate(170%);
    backdrop-filter: blur(14px) saturate(170%);
    border: 1px solid rgba(255, 255, 255, .6);
    color: var(--text, #3d4759);
    box-shadow: 0 8px 20px rgba(20, 40, 90, .12);
    transform-origin: top center;
    animation: tagSway 3.8s ease-in-out infinite;
    animation-delay: .6s;
}

.detailBox .ui.label a {
    color: var(--text, #3d4759);
}

.detailBox .ui.label::before {
    content: '';
    position: absolute;
    top: 6px;
    left: 50%;
    transform: translateX(-50%);
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #e8edf5;
    box-shadow: inset 0 1px 2px rgba(0, 0, 0, .28);
}

@media screen and (max-width: 800px) {
    .detailBox .ui.label {
        top: -8px;
        right: 16px;
    }

    .arDate {
        left: 14px;
    }
}
</style>
