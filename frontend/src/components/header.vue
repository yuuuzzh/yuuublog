<!-- 头部公用 -->
<template>
	<div class="">
		<div class="headBack">
			<el-row class="container">
				<el-col :span="24">
					<!-- pc端导航 -->
					<div class="headBox">
						<el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal"
							:router="true">
							<el-menu-item index="/Home"><i class="fa fa-wa fa-home"></i> 首页</el-menu-item>
							<el-submenu index="/Share">
								<template slot="title"><i class="fa fa-wa fa-archive"></i> 分类</template>
								<el-menu-item v-for="(item, index) in classListObj" :key="'class1' + index"
									:index="'/Share?classId=' + item.id">{{ item.name }}</el-menu-item>
							</el-submenu>
							<el-menu-item index="/Reward"><i class="fa fa-wa fa-cny"></i> 赞赏</el-menu-item>
							<el-menu-item index="/Friendslink"><i class="fa fa-wa fa-users"></i>友链</el-menu-item>
							<el-menu-item index="/WordBoard"><i class="fa fa-wa fa-users"></i>留言板</el-menu-item>
						<el-menu-item index="/Archives"><i class="fa fa-wa fa-calendar"></i>归档</el-menu-item>
						<el-menu-item index="/About"><i class="fa fa-wa fa-id-card"></i>关于我</el-menu-item>
							
							
							<div class="userInfo">
								<div v-show="!haslogin" class="nologin">
									<a href="javascript:void(0);" @click="logoinFun(1)">登录&nbsp;</a>|<a
										href="javascript:void(0);" @click="logoinFun(0)">&nbsp;注册&nbsp;</a>|
										<a href="https://www.yuuu.online:8094">进入后台</a>
								</div>
								
								<div v-show="haslogin" class="haslogin">
									<i class="fa fa-fw fa-user-circle userImg"></i>
									<ul class="haslogin-info">
										<li>
											<a href="/UserInfo">个人中心</a>
										</li>
										<li>
											<a href="https://www.yuuu.online:8094">进入后台</a>
										</li>
										<li>
											<a href="javascript:void(0);" @click="userlogout">退出登录</a>
										</li>
										
									</ul>
								</div>
							</div>
						</el-menu>
					</div>
				</el-col>
			</el-row>
		</div>
		<div class="headImgBox">
			<!-- 视差背景层 -->
			<div class="hero-bg" :style="heroBgStyle"></div>
			<!-- 渐变与暗角，保证文字可读 -->
			<div class="hero-veil"></div>

			<div class="scene">
				<div><span id="luke"></span></div>
			</div>
			<div class="h-information">

				<img :src="userInfoObj.avatar ? userInfoObj.avatar : 'static/img/tou.png'" alt="">

				<h2 class="h-description">

					<span id="signature"></span>

				</h2>
			</div>

			<!-- 底部波浪 -->
			<svg class="hero-wave" viewBox="0 0 1440 120" preserveAspectRatio="none" aria-hidden="true">
				<path d="M0,64 C240,110 480,10 720,48 C960,86 1200,120 1440,72 L1440,120 L0,120 Z"></path>
			</svg>
		</div>
	</div>
</template>
<script>
import { logout } from '../api/user'
import { removeToken } from '../utils/auth'
import { getCategoryList } from '../api/category'
import { getUserInfo } from '../api/user.js'//获取用户信息，保存用户信息
import {
	Typeit,
	TypeitLine
} from '../utils/plug.js'
export default {
	data() { //选项 / 数据
		return {
			userInfo: '', //用户信息
			haslogin: false, //是否已登录
			classListObj: '', //分类
			activeIndex: '/', //当前选择的路由模块
			state: '', //icon点击状态
			pMenu: true, //手机端菜单打开
			// path:'',//当前打开页面的路径
			input: '', //input输入内容
			userInfoObj: '',//用户的信息
			headBg: 'url(static/img/headbg05.jpg)', //头部背景图
			headTou: '', //头像
			projectList: '', //项目列表
			heroShift: 0, //首屏视差位移（px）
			heroTicking: false
		}
	},
	methods: { //事件处理器
		handleOpen(key, keyPath) { //分组菜单打开
			// console.log(key, keyPath);
		},
		handleClose(key, keyPath) { //分组菜单关闭
			// console.log(key, keyPath);
		},
		searchChangeFun(e) { //input change 事件
			// console.log(e)
			if (this.input == '') {
				this.$store.state.keywords = '';
				this.$router.push({ path: '/' });
			}
		},
		getCategoryList() {
			getCategoryList().then((response) => {
				this.classListObj = response
			})
		},
		logoinFun: function (msg) { //用户登录和注册跳转
			// console.log(msg);
			localStorage.setItem('logUrl', this.$route.fullPath);
			// console.log(666,this.$router.currentRoute.fullPath);
			if (msg == 0) {
				this.$router.push({
					path: '/Login?login=0'
				});
			} else {
				this.$router.push({
					path: '/Login?login=1'
				});
			}
		},
		// 用户退出登录
		userlogout: function () {
			var that = this;
			this.$confirm('是否确认退出?', '退出提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				// console.log(that.$route.path);

				logout().then((response) => {
					removeToken()
					localStorage.removeItem('userInfo');
					that.haslogin = false;
					window.location.reload();
					that.$message({
						type: 'success',
						message: '退出成功!'
					});
					if (that.$route.path == '/UserInfo') {
						that.$router.push({
							path: '/'
						});
					}
				})
			}).catch(() => {
				//
			});

		},
			// 会话失效被 request.js 清理本地登录态时，导航栏立即切换为「登录」
		syncLoginState: function () {
			this.haslogin = !!localStorage.getItem('userInfo');
		},
		routeChange: function () {
			var that = this;
			that.pMenu = true
			this.activeIndex = this.$route.path == '/' ? '/Home' : this.$route.path;
			if (localStorage.getItem('userInfo')) { //存储用户信息
				that.haslogin = true;
				that.userInfo = JSON.parse(localStorage.getItem('userInfo'));
				// console.log(that.userInfo);
			} else {
				that.haslogin = false;
			}
			//获取分类
			this.getCategoryList()
			if (localStorage.getItem('userInfo')) {
				that.haslogin = true;
				that.userInfo = JSON.parse(localStorage.getItem('userInfo'));
				that.userId = that.userInfo.id;
				getUserInfo(that.userId).then((response) => {
					that.userInfoObj = response;
					that.userInfoObj.head_start = 0;
				})
			} else {
				that.haslogin = false;
			}

			if ((this.$route.name == "Share" || this.$route.name == "Home") && this.$store.state.keywords) {
				this.input = this.$store.state.keywords;
			} else {
				this.input = '';
				this.$store.state.keywords = '';
			}
		},
		// 首屏视差：rAF 节流，避免滚动时频繁重排
		onScroll() {
			if (this.heroTicking) return;
			var that = this;
			this.heroTicking = true;
			window.requestAnimationFrame(function () {
				var y = window.pageYOffset || document.documentElement.scrollTop || 0;
				that.heroShift = y > 500 ? 500 : y;
				that.heroTicking = false;
			});
		}
	},
	components: { //定义组件
	},
	computed: {
		signatureText() {
			var themeObj = this.$store.state.themeObj;
			return (themeObj && themeObj.autograph) ? themeObj.autograph : '且将新火试新茶，诗酒趁年华';
		},
		heroBgStyle() {
			var bg = this.$store.state.themeObj.top_image
				? 'url(' + this.$store.state.themeObj.top_image + ')'
				: 'url(static/img/headbg05.jpg)';
			return {
				backgroundImage: bg,
				transform: 'translate3d(0,' + (this.heroShift * 0.32).toFixed(1) + 'px,0) scale(1.08)'
			};
		}
	},
	watch: {
		// 如果路由有变化，会再次执行该方法
		'$route': 'routeChange',
	},
	created() { //生命周期函数
		//判断当前页面是否被隐藏
		var that = this;
		var hiddenProperty = 'hidden' in document ? 'hidden' :
			'webkitHidden' in document ? 'webkitHidden' :
				'mozHidden' in document ? 'mozHidden' :
					null;
		var visibilityChangeEvent = hiddenProperty.replace(/hidden/i, 'visibilitychange');
		var onVisibilityChange = function () {
			if (!document[hiddenProperty]) { //被隐藏
				if (that.$route.path != '/DetailShare') {
					if (localStorage.getItem('userInfo')) {
						that.haslogin = true;
					} else {
						that.haslogin = false;
					}
				}
			}
		}
		document.addEventListener(visibilityChangeEvent, onVisibilityChange);
		window.addEventListener('user-auth-changed', this.syncLoginState);
		// console.log();
		this.routeChange();


	},
	mounted() { //页面元素加载完成
		var that = this;
		var timer = setTimeout(function () {
			Typeit(that.$store.state.themeObj.user_start, "#luke"); //打字机效果
			TypeitLine(that.signatureText, "#signature", 700); //签名打字机效果
			clearTimeout(timer);
		}, 500);
		this.onScroll();
		window.addEventListener('scroll', this.onScroll, { passive: true });
	},
	destroyed() {
		window.removeEventListener('scroll', this.onScroll);
		window.removeEventListener('user-auth-changed', this.syncLoginState);
	}
}
</script>

<style>
/*********头部导航栏********/
/*头部导航栏盒子*/

.headBack {
	width: 100%;
	background: rgba(40, 42, 44, 0.6);
	/*margin-bottom:30px;*/
	box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .12), 0 0 6px 0 rgba(0, 0, 0, .04);
	position: fixed;
	left: 0;
	top: 0;
	right: 0;
	z-index: 100;
}

.headBox li.is-active {
	/*background: #48456C;*/
	background: rgba(73, 69, 107, 0.7);
}

.el-menu--horizontal>.el-submenu.is-active .el-submenu__title {
	border-bottom: none !important;
}

.headBox .el-menu {
	background: transparent;
	border-bottom: none !important;
}

.headBox .el-menu-demo li.el-menu-item,
.headBox .el-menu--horizontal .el-submenu .el-submenu__title {
	height: 38px;
	line-height: 38px;
	border-bottom: none !important;

}

.headBox .el-submenu li.el-menu-item {
	height: 38px;
	line-height: 38px;
}

.headBox li .fa-wa {
	vertical-align: baseline;
}

.headBox ul li.el-menu-item,
.headBox ul li.el-menu-item.is-active,
.headBox ul li.el-menu-item:hover,
.headBox .el-submenu div.el-submenu__title,
.headBox .el-submenu__title i.el-submenu__icon-arrow {
	color: #fff;
}

.headBox .el-menu--horizontal .el-submenu>.el-menu {
	top: 38px;
	border: none;
	padding: 0;
}

.headBox>ul li.el-menu-item:hover,
.headBox>ul li.el-submenu:hover .el-submenu__title {
	background: #48456C;
	border-bottom: none;
}

.headBox>ul .el-submenu .el-menu,
.headBox>ul .el-submenu .el-menu .el-menu-item {
	background: #48456C;
}

.headBox>ul .el-submenu .el-menu .el-menu-item {
	min-width: 0;
}

.headBox>ul .el-submenu .el-menu .el-menu-item:hover {
	background: #64609E;
}

/*pc搜索框*/

.headBox .pcsearchbox {
	padding: 0;
	max-width: 170px;
	/*min-width: 30px;*/
	height: 100%;
	line-height: 38px;
	position: absolute;
	top: 0;
	right: 0;
	cursor: pointer;
}

.headBox .pcsearchbox:hover .pcsearchinput {
	opacity: 1;
	/*transform: scaleX(1);*/
	visibility: visible;
}

.headBox .pcsearchbox i.pcsearchicon {
	color: #fff;
	padding-left: 10px;
}

.headBox .pcsearchbox .pcsearchinput {
	width: 180px;
	padding: 10px 20px 10px 20px;
	background: rgba(40, 42, 44, 0.6);
	border-radius: 0 0 2px 2px;
	position: absolute;
	right: 0;
	top: 38px;
	opacity: 0;
	visibility: hidden;
	/*transform: scaleX(0);*/
	transform-origin: right;
	transition: all 0.3s ease-out;
}

.headBox .pcsearchbox .hasSearched {
	opacity: 1;
	/*transform: scaleX(1);*/
	visibility: visible;
}

.headBox .pcsearchbox .el-input {
	width: 100%;
}

.headBox .el-input__inner {
	height: 30px;
	border: none;
	background: var(--surface);
	color: var(--text);
	/*border: 1px solid #333;*/
	border-radius: 2px;
	padding-right: 10px;
}

.headBox .userInfo {
	height: 100%;
	line-height: 38px;
	position: absolute;
	right: 30px;
	top: 0;
	color: #fff;
}

.headBox .userInfo a {
	color: #fff;
	font-size: 13px;
	transition: all 0.2s ease-out;
}

.headBox .userInfo a:hover {
	color: #48456C;
}

.headBox .nologin {
	text-align: right;
}

.headBox .haslogin {
	text-align: right;
	position: relative;
	min-width: 80px;
	cursor: pointer;
}

.headBox .haslogin:hover ul {
	visibility: visible;
	opacity: 1;
}

.headBox .haslogin ul {
	background: rgba(40, 42, 44, 0.6);
	padding: 5px 10px;
	position: absolute;
	right: 0;
	visibility: hidden;
	opacity: 0;
	transition: all 0.3s ease-out;
}

.headBox .haslogin ul li {
	border-bottom: 1px solid #48456C;
}

.headBox .haslogin ul li:last-child {
	border-bottom: 1px solid transparent;
}

/*******移动端*******/

.mobileBox {
	position: relative;
	height: 38px;
	line-height: 38px;
	color: #fff;
}

.hideMenu {
	position: relative;
	width: 100%;
	height: 100%;
	line-height: 38px;
}

.hideMenu ul.mlistmenu {
	width: 100%;
	position: absolute;
	left: 0;
	top: 100%;
	box-sizing: border-box;
	z-index: 999;
	box-shadow: 0 2px 6px 0 rgba(0, 0, 0, .12), 0 0 8px 0 rgba(0, 0, 0, .04);
	background: #48456C;
	color: #fff;
	border-right: none;
}

.hideMenu .el-submenu .el-menu {
	background: #64609E;
}

.hideMenu .el-menu-item,
.hideMenu .el-submenu__title {
	color: #fff;
}

.hideMenu>i {
	position: absolute;
	left: 10px;
	top: 12px;
	width: 30px;
	height: 30px;
	font-size: 16px;
	color: #fff;
	cursor: pointer;
}

.hideMenu .el-menu-item,
.el-submenu__title {
	height: 40px;
	line-height: 40px;
}

.mobileBox .searchBox {
	padding-left: 40px;
	width: 100%;
	box-sizing: border-box;
}

.mobileBox .searchBox .el-input__inner {
	display: block;
	border-radius: 2px;
	border: none;
	height: 25px;
}

.hideMenu ul.mlistmenu.pshow {
	display: block;
}

.hideMenu ul.mlistmenu .el-submenu__icon-arrow,
.mobileBox li.el-menu-item a {
	color: #fff;
}

.hideMenu>ul li.el-menu-item:hover,
.hideMenu>ul li.el-menu-item.is-active {
	background: #48576a;
}



/*头部背景图*/

.headImgBox {
	height: 650px;
	position: relative;
	width: 100%;
	background-size: cover;
	background-position: center 50%;
	background-repeat: no-repeat;
	margin-bottom: 90px;
}

.h-information {
	text-align: center;
	width: 70%;
	margin: auto;
	position: relative;
	top: 480px;
	padding: 40px 0;
	font-size: 16px;
	opacity: 0.98;
	background: rgba(230, 244, 249, 0.8);
	border-radius: 5px;
	z-index: 1;
	animation: b 1s ease-out;
	-webkit-animation: b 1s ease-out;
}

@-webkit-keyframes b {
	0% {
		-webkit-transform: translateY(90px);
		transform: translateY(90px);
	}

	80% {
		-webkit-transform: translateY(5px);
		transform: translateY(5px)
	}

	90% {
		-webkit-transform: translateY(-5px);
		transform: translateY(-5px)
	}

	to {
		-webkit-transform: translateY(0);
		transform: translateY(0)
	}
}

@keyframes b {
	0% {
		-webkit-transform: translateY(90px);
		transform: translateY(90px);
	}

	80% {
		-webkit-transform: translateY(5px);
		transform: translateY(5px)
	}

	90% {
		-webkit-transform: translateY(-5px);
		transform: translateY(-5px)
	}

	to {
		-webkit-transform: translateY(0);
		transform: translateY(0)
	}
}

.h-information img {
	width: 100px;
	height: 100px;
	border-radius: 100%;
	transition: all .4s ease-in-out;
	-webkit-transition: all .4s ease-in-out;
	object-fit: cover;
}

.h-information img:hover {
	transform: rotate(360deg);
	-webkit-transform: rotate(360deg);
}

.h-information h2 {
	margin-top: 20px;
	font-size: 18px;
	font-weight: 700;
	/*font-family: 'Sigmar One';*/
}

.h-information h2 a {
	background: linear-gradient(to right, #DF2050, #48456D);
	-webkit-background-clip: text;
	color: transparent;
}

.headImgBox .scene {
	width: 100%;
	/*height:300px;*/
	text-align: center;
	font-size: 100px;
	font-weight: 200;
	color: #fff;
	position: absolute;
	left: 0;
	top: 160px;
	font-family: 'Sigmar One', Arial;
	text-shadow: 0 2px 2px #47456d;

}

.headImgBox .scene span {
	transform: matrix(1, 0, 0, 1, 0, 0);
	-webkit-transform: matrix(1, 0, 0, 1, 0, 0);
	text-shadow: 1px 1px 0 #ff3f1a, -1px -1px 0 #00a7e0;
}

.saying:after {
	content: "|";
	font-family: Arial, sans-serif;
	font-size: 1em;
	/*line-height: 0;*/
	display: inline-block;
	vertical-align: baseline;
	opacity: 1;
	text-shadow: 1px 1px 0 #ff3f1a, -1px -1px 0 #00a7e0;
	animation: caret 500ms infinite;
}

@keyframes caret {

	0%,
	100% {
		opacity: 1;
	}

	50% {
		opacity: 0;
	}
}

/* ================= 首屏 Hero 升级 ================= */
.headImgBox {
	overflow: hidden;
	margin-bottom: 60px;
}

/* 视差背景层 */
/* top 向上扩 180px：视差位移（上限 500*0.32=160px）向下推背景时，顶部不会露出底色 */
.hero-bg {
	position: absolute;
	left: 0;
	top: -180px;
	right: 0;
	bottom: 0;
	background-size: cover;
	background-position: center 50%;
	background-repeat: no-repeat;
	will-change: transform;
	transform-origin: center center;
}

/* 渐变 + 暗角，确保文字在任何头图下都可读 */
.hero-veil {
	position: absolute;
	inset: 0;
	background:
		radial-gradient(120% 80% at 50% 0%, rgba(12, 18, 40, 0) 0%, rgba(12, 18, 40, .45) 70%, rgba(12, 18, 40, .68) 100%),
		linear-gradient(180deg, rgba(12, 18, 40, .28) 0%, rgba(12, 18, 40, 0) 30%, rgba(12, 18, 40, .35) 100%);
	pointer-events: none;
}

/* 场景文字与信息卡需要在遮罩之上；信息卡取 4，盖住底部波浪（波浪为 3） */
.scene,
.h-information {
	z-index: 4;
}

/* 信息卡：通栏玻璃底条，固定 300px 高，贴住首屏底部；内容偏上（中心约在条高 1/3 处） */
.h-information {
	position: absolute;
	left: 0;
	right: 0;
	top: auto;
	bottom: 0;
	width: 100%;
	margin: 0;
	height: 300px;
	display: -webkit-flex;
	display: flex;
	-webkit-flex-direction: column;
	flex-direction: column;
	-webkit-align-items: center;
	align-items: center;
	-webkit-justify-content: flex-start;
	justify-content: flex-start;
	padding: 28px 0 0;
	border-radius: 0;
	background: linear-gradient(180deg, rgba(255, 255, 255, 0) 0%, rgba(255, 255, 255, .06) 100%);
	-webkit-backdrop-filter: blur(4px);
	backdrop-filter: blur(4px);
	/* 顶部用遮罩把底色与磨砂一起淡出，避免出现明显的分界线 */
	-webkit-mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 1) 55%);
	mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 1) 55%);
	animation: b 1s ease-out;
	-webkit-animation: b 1s ease-out;
}

/* 头像：覆盖在文字上方（z 更高），边缘羽化 + 轻微半透明，与文字交汇处自然融合；保留最初版 hover 旋转 */
.h-information img {
	position: relative;
	z-index: 4;
	opacity: .6;
	-webkit-mask-image: radial-gradient(circle, #000 58%, transparent 74%);
	mask-image: radial-gradient(circle, #000 58%, transparent 74%);
	transition: all .4s ease-in-out;
	-webkit-transition: all .4s ease-in-out;
}

.h-information img:hover {
	opacity: .8;
	transform: rotate(360deg);
	-webkit-transform: rotate(360deg);
}

/* 签名：回到头像正下方（不再与头像重叠），16px；字体风格与首屏打字机一致（白字+红青错位描边） */
.h-information .h-description {
	position: static;
	margin: 20px 0 0;
	text-align: center;
	font-size: 16px !important;
	font-weight: 700;
	line-height: 1.5;
	letter-spacing: 4px;
	color: #fff;
	text-shadow: 1px 1px 0 #ff3f1a, -1px -1px 0 #00a7e0;
}

/* 底部波浪 */
.hero-wave {
	position: absolute;
	left: 0;
	right: 0;
	bottom: -1px;
	width: 100%;
	height: 90px;
	z-index: 3;
	pointer-events: none;
}

.hero-wave path {
	fill: var(--bg, #f5f7fb);
}

@media (max-width: 768px) {
	.headImgBox {
		height: 420px;
		/* 覆盖 App.vue 老规则的 -140px：内容不再与首屏重叠（每个页面都会渲染首屏） */
		margin-bottom: 0 !important;
	}

	.h-information {
		bottom: 0;
		width: 100%;
		height: 260px;
		padding: 0;
		-webkit-justify-content: center;
		justify-content: center;
	}

	/* 移动端头像恢复清晰不透明（羽化半透明在玻璃条上显得像被遮挡），并缩小让内容整体上移 */
	.h-information img {
		width: 80px;
		height: 80px;
		opacity: 1;
		-webkit-mask-image: none;
		mask-image: none;
	}

	.h-information .h-description {
		margin-top: 14px;
		font-size: 14px !important;
		letter-spacing: 2px;
	}

	.hero-wave {
		height: 56px;
	}
}

/* ================= 导航统一到新设计系统 ================= */
.headBox>ul>li.el-menu-item,
.headBox>ul>.el-submenu>.el-submenu__title {
	border-radius: 999px !important;
	margin: 0 2px;
	transition: background .25s var(--ease), transform .25s var(--ease), box-shadow .25s var(--ease);
}

.headBox>ul>li.el-menu-item:hover,
.headBox>ul>.el-submenu:hover>.el-submenu__title {
	background: rgba(255, 255, 255, .18) !important;
	transform: translateY(-1px);
}

/* 当前页：渐变胶囊 + 光晕 */
.headBox>ul>li.el-menu-item.is-active {
	background: linear-gradient(120deg, var(--brand), var(--brand-2)) !important;
	color: #fff !important;
	box-shadow: var(--shadow-glow);
}

/* 分类下拉：改用卡片风格 */
.headBox>ul .el-submenu .el-menu {
	background: var(--surface) !important;
	border-radius: 12px;
	overflow: hidden;
	box-shadow: var(--shadow-2);
	padding: 4px 0;
}

.headBox>ul .el-submenu .el-menu .el-menu-item {
	background: transparent !important;
	color: var(--text) !important;
}

.headBox>ul .el-submenu .el-menu .el-menu-item:hover {
	background: var(--bg-soft) !important;
	color: var(--brand) !important;
}

/* 搜索框 */
.headBox .pcsearchbox .el-input__inner,
.headBox .el-input__inner {
	border-radius: 999px;
	box-shadow: var(--shadow-1);
}

@media (prefers-reduced-motion: reduce) {
	.hero-bg {
		transform: none !important;
	}

	.headBox>ul>li.el-menu-item {
		transition: none;
	}
}
</style>
