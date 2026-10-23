<!-- 登录 / 注册 / 修改密码 / 第三方绑定 -->
<template>
    <div class="loginPage">
        <!-- 背景图占满全屏 -->
        <div class="bgLayer">
            <el-carousel height="100%" indicator-position="none" :interval="5000" arrow="never">
                <el-carousel-item>
                    <img src="../../static/img/login-bac0.png" />
                </el-carousel-item>
                <el-carousel-item>
                    <img src="../../static/img/login-bac1.png" />
                </el-carousel-item>
                <el-carousel-item>
                    <img src="../../static/img/login-bac2.png" />
                </el-carousel-item>
            </el-carousel>
        </div>

        <!-- 表单卡浮在背景之上 -->
        <div class="loginWrap">
            <div class="loginCard">
                <a class="cardTop" href="/">← 一蓑烟雨任平生</a>

                <!-- 登录 -->
                <div v-if="login == 1">
                    <h1 class="formTitle">欢迎回来</h1>
                    <p class="formSub">生命的意义在于不顺从</p>

                    <div v-show="loginErr" class="formError">{{ loginTitle }}</div>

                    <div class="field">
                        <label>用户名</label>
                        <el-input class="lgInput" type="text" placeholder="请输入用户名" v-model="username">
                        </el-input>
                    </div>
                    <div class="field">
                        <label>密码</label>
                        <el-input class="lgInput" :type="pwdVisible ? 'text' : 'password'" placeholder="请输入密码"
                            v-model="password" @keyup.enter.native="loginEnterFun">
                            <i slot="suffix" class="fa" :class="pwdVisible ? 'fa-eye' : 'fa-eye-slash'"
                                @click="pwdVisible = !pwdVisible"></i>
                        </el-input>
                    </div>
                    <div class="field">
                        <label>验证码</label>
                        <div class="captchaRow">
                            <el-input class="lgInput" type="text" placeholder="请输入计算结果" v-model="captcha"
                                @keyup.enter.native="loginEnterFun"></el-input>
                            <button class="mathCaptcha" type="button" @click="refreshCaptcha" title="点击刷新">
                                <span>{{ mathQuestion }}</span>
                                <i class="fa fa-refresh"></i>
                            </button>
                        </div>
                    </div>

                    <div class="formRow">
                        <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                        <a href="/Login?login=2" class="linkBtn">忘记密码？</a>
                    </div>

                    <button class="primaryBtn" @click="gotoHome">登录</button>

                    <div class="divider"><span>或使用第三方账号</span></div>
                    <div class="socialBtns">
                        <a class="socialBtn github"
                            href="https://yuuuzzh.us.kg/login/oauth/authorize?client_id%3Dc62e602f840881ab59b2%26state%3Dgithub">
                            <i class="fa fa-fw fa-github"></i>GitHub
                        </a>
                        <a class="socialBtn qq" href="javascript:void(0)" @click="showAlert">
                            <i class="fa fa-fw fa-qq"></i>QQ
                        </a>
                        <a class="socialBtn weibo" href="javascript:void(0)" @click="showAlert">
                            <i class="fa fa-fw fa-weibo"></i>微博
                        </a>
                    </div>

                    <p class="switchLine">还没有账号？<a href="/Login?login=0">立即注册</a></p>
                </div>

                <!-- 注册 -->
                <div v-if="login == 0">
                    <h1 class="formTitle">创建账号</h1>
                    <p class="formSub">注册后即可评论、收藏与订阅</p>

                    <div v-show="registerErr" class="formError">{{ registerTitle }}</div>

                    <div class="field">
                        <label>用户名</label>
                        <el-input class="lgInput" type="text" placeholder="请输入用户名" v-model="nusername"></el-input>
                        <div v-show="nusernameErr" class="fieldError">用户名错误</div>
                    </div>
                    <div class="field">
                        <label>昵称</label>
                        <el-input class="lgInput" type="text" placeholder="请输入昵称" v-model="nnickName"></el-input>
                    </div>
                    <div class="field">
                        <label>邮箱</label>
                        <el-input class="lgInput" type="email" placeholder="you@example.com" v-model="nemail">
                        </el-input>
                        <div v-show="nemailErr" class="fieldError">邮箱不能为空</div>
                    </div>
                    <div class="field">
                        <label>邮箱验证码</label>
                        <div class="captchaRow">
                            <el-input class="lgInput" type="text" placeholder="请输入验证码" v-model="emailCaptcha">
                            </el-input>
                            <el-button class="codeBtn" type="info" icon="el-icon-message" @click="sendVerificationCode"
                                :disabled="isButtonDisabled">
                                {{ isButtonDisabled ? timeLeft + 's' : '发送验证码' }}
                            </el-button>
                        </div>
                    </div>
                    <div class="field">
                        <label>密码</label>
                        <el-input class="lgInput" type="password" placeholder="6-12 位英文、数字、下划线"
                            v-model="npassword"></el-input>
                        <div v-show="npasswordErr" class="fieldError">密码错误</div>
                    </div>
                    <div class="field">
                        <label>确认密码</label>
                        <el-input class="lgInput" type="password" placeholder="请再次输入密码" v-model="npassword2"
                            @keyup.enter.native="registerEnterFun"></el-input>
                        <div v-show="npassword2Err" class="fieldError">重复密码有误</div>
                    </div>

                    <button class="primaryBtn" @click="newRegister" v-loading.fullscreen.lock="fullscreenLoading"
                        element-loading-text="提交中">注册</button>

                    <p class="switchLine">已有账号？<a href="/Login?login=1">去登录</a></p>
                </div>

                <!-- 修改密码 -->
                <div v-if="login == 2">
                    <h1 class="formTitle">重置密码</h1>
                    <p class="formSub">通过邮箱验证码重置</p>

                    <div v-show="changePasswordErr" class="formError">{{ changePasswordTitle }}</div>

                    <div class="field">
                        <label>邮箱</label>
                        <el-input class="lgInput" type="text" placeholder="请输入注册邮箱" v-model="emailPassword">
                        </el-input>
                        <div v-show="uemailErr" class="fieldError">邮箱不能为空</div>
                    </div>
                    <div class="field">
                        <label>新密码</label>
                        <el-input class="lgInput" type="password" placeholder="请输入新密码" v-model="newPassword">
                        </el-input>
                    </div>
                    <div class="field">
                        <label>确认新密码</label>
                        <el-input class="lgInput" type="password" placeholder="请再次输入新密码"
                            v-model="confirmNewPassword"></el-input>
                    </div>
                    <div class="field">
                        <label>邮箱验证码</label>
                        <div class="captchaRow">
                            <el-input class="lgInput" type="text" placeholder="请输入验证码" v-model="emailCaptchaUpdate">
                            </el-input>
                            <el-button class="codeBtn" type="info" icon="el-icon-message"
                                @click="sendCodeForUpdatePassword" :disabled="isButtonDisabled2">
                                {{ isButtonDisabled2 ? timeLeft1 + 's' : '发送验证码' }}
                            </el-button>
                        </div>
                    </div>

                    <button class="primaryBtn" @click="changePassword">确定</button>

                    <p class="switchLine">想起来了？<a href="/Login?login=1">去登录</a></p>
                </div>

                <!-- 第三方账号绑定 -->
                <div v-if="login == 9">
                    <h1 class="formTitle">请完成绑定</h1>
                    <p class="formSub">绑定邮箱后即可完成第三方登录</p>

                    <div v-show="changePasswordErr" class="formError">{{ changePasswordTitle }}</div>

                    <div class="field">
                        <label>邮箱</label>
                        <el-input class="lgInput" type="text" placeholder="请输入邮箱" v-model="emailPassword">
                        </el-input>
                        <div v-show="uemailErr" class="fieldError">邮箱不能为空</div>
                    </div>
                    <div class="field">
                        <label>邮箱验证码</label>
                        <div class="captchaRow">
                            <el-input class="lgInput" type="text" placeholder="请输入验证码" v-model="emailCaptchaUpdate">
                            </el-input>
                            <el-button class="codeBtn" type="info" icon="el-icon-message"
                                @click="sendCodeForUpdatePassword" :disabled="isButtonDisabled2">
                                {{ isButtonDisabled2 ? timeLeft1 + 's' : '发送验证码' }}
                            </el-button>
                        </div>
                    </div>

                    <button class="primaryBtn" @click="oauthWithGithub">确定</button>
                </div>
            </div>
        </div>
    </div>
</template>





<script>
import { userLogin, userRegister, sendVerificationCode, sendCodeForUpdatePassword, changePassword } from '../api/user.js'
import { setToken } from '../utils/auth.js'
import  encryptPassword  from '../utils/encryptPassword'
import { getMathCaptcha } from '@/api/captcha.js';
import request from "@/utils/request.js"
export default {
    name: 'Login',
    data() { //选项 / 数据
        return {
            username: '',//用户名
            email: '',//邮箱
            password: '',//密码
            nusername: '',//新用户注册名
            nemail: '',//新用户注册邮箱
            npassword: '',//新用户注册密码
            npassword2: '',//新用户注册重复密码
            login: 0,//是否已经登录
            loginErr: false,//登录错误
            loginTitle: '用户名或密码错误',
            nusernameErr: false,//新用户注册用户名错误
            nemailErr: false,//新用户注册邮箱错误
            uemailErr: false,//修改密码邮箱错误
            npasswordErr: false,//新用户注册密码错误
            npassword2Err: false,//新用户注册重复密码错误
            registerErr: false,//已注册错误
            registerTitle: '该邮箱已注册',
            step: 1,//注册进度
            fullscreenLoading: false,//全屏loading
            urlstate: 0,//重新注册
            captcha: '',
            captchaKey: '',
            pwdVisible: false,//密码是否明文显示
            rememberMe: false,//记住我（记住用户名）
            mathQuestion: '',
            captchaUrlUpdate: '',
            nnickName: '',
            emailCaptcha: '',
            loginForm: '',
            isButtonDisabled: false,
            isButtonDisabled2: false,
            timeLeft: 60,
            timeLeft1: 60,
            emailPassword: '',
            newPassword: '',
            confirmNewPassword: '',
            emailCaptchaUpdate: '',
            updateError: '',
            changePasswordErr: false,
            changePasswordTitle: '',
            err2005: false // 添加err2005属性，并设置初始值为false
        }
    },
    mounted() {
        // 页面加载时刷新验证码
        this.refreshCaptcha();
        // 记住我：回显上次记住的用户名
        var remembered = localStorage.getItem('rememberUser');
        if (remembered) {
            this.username = remembered;
            this.rememberMe = true;
        }
    },
    methods: { //事件处理器
        routeChange: function () {
            var that = this;
            that.login = that.$route.query.login == undefined ? 1 : parseInt(that.$route.query.login);//获取传参的login
            that.urlstate = that.$route.query.urlstate == undefined ? 0 : that.$route.query.urlstate;//获取传参的usrlstate状态码
            // console.log(that.login,that.urlstate);
        },

        changePassword: function () {
            if (this.newPassword !== this.confirmNewPassword) {
                this.changePasswordErr = true;
                this.changePasswordTitle = '两次密码不匹配';
            } else if (!this.emailCaptchaUpdate) {
                this.$message.error('验证码不能为空');
            } else {
                request.post('/user/updatePassword', {
                    emailPassword: this.emailPassword,
                    confirmNewPassword: this.confirmNewPassword,
                    emailCaptchaUpdate: this.emailCaptchaUpdate
                })
                    .then((response) => {
                        console.log(response)
                        this.$alert('修改成功', '', {
                            confirmButtonText: '确定'
                        })
                        this.isButtonDisabled2 = false;
                        this.emailPassword = '';
                        this.newPassword = '';
                        this.confirmNewPassword = '';
                        this.emailCaptchaUpdate = '';
                        this.goLogin()
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            }
        },
       
        showAlert() {
            alert("暂未接入，请使用Github登录")
        },
        sendCodeForUpdatePassword() {
            if (!this.emailPassword) {
                this.uemailErr = true;
                setTimeout(() => {
                    this.uemailErr = false;
                }, 2000); // 2秒后隐藏错误信息
                return;
            }
            if (!this.isButtonDisabled2) {
                this.isButtonDisabled2 = true;
                let timer = setInterval(() => {
                    this.timeLeft1--;
                    if (this.timeLeft1 === 0) {
                        clearInterval(timer);
                        this.isButtonDisabled2 = false;
                        this.timeLeft1 = 60;
                    }
                }, 1000);
                sendCodeForUpdatePassword(this.emailPassword).then(response => {
                    this.$message.success('验证码已发送，请注意查收。');
                }).catch(error => {
                    this.$message.error('获取验证码失败：' + error.message);
                    this.emailCaptchaUpdate = '';
                    // 重置发送验证码按钮
                    clearInterval(timer);
                    this.isButtonDisabled2 = false;
                    this.timeLeft1 = 60;

                });
            }
        },
        
        oauthWithGithub() {
            const urlParams = new URLSearchParams(window.location.search);
            const code = urlParams.get('code');
            const state = urlParams.get('state');
            if (!this.emailCaptchaUpdate) {
                this.$message.error('验证码不能为空');
            } else {
                request.post('/user/oauth/github?code='+code, {
                    emailPassword: this.emailPassword,
                    confirmNewPassword: this.confirmNewPassword,
                    emailCaptchaUpdate: this.emailCaptchaUpdate,
                })
                    .then((response) => {
                        console.log(response)
                        this.$alert('绑定成功', '', {
                            confirmButtonText: '确定'
                        })
                        this.isButtonDisabled2 = false;
                        this.emailPassword = '';
                        this.emailCaptchaUpdate = '';
                        setToken(response.token)
                        localStorage.setItem("userInfo", JSON.stringify(response.userInfo))
                        this.$router.push({ path: '/' });
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            }
        },
        sendVerificationCode() {
            if (!this.nemail) {
                this.nemailErr = true;
                this.registerTitle = '邮箱不能为空';
                setTimeout(() => {
                    this.nemailErr = false;
                }, 2000); // 2秒后隐藏错误信息
                return;
            }
            if (!this.isButtonDisabled) {
                this.isButtonDisabled = true;
                let timer = setInterval(() => {
                    this.timeLeft--;
                    if (this.timeLeft === 0) {
                        clearInterval(timer);
                        this.isButtonDisabled = false;
                        this.timeLeft = 60;
                    }
                }, 1000);
                // 在这里调用你的接口
                sendVerificationCode(this.nemail)
                    .then((response) => {

                        this.$message.success('验证码已发送到您的邮箱，请检查并填写验证码。');
                        console.log(this.$message)
                    })
                    .catch((error) => {
                        if (!error.includes("non-existent account")) {
                            // 清空验证码输入框
                            this.emailCaptcha = '';
                            // 重置发送验证码按钮
                            clearInterval(timer);
                            this.isButtonDisabled = false;
                            this.timeLeft = 60;
                        }
                    });
            }
        },
        refreshCaptcha() {//登录算术验证码
            const promise = getMathCaptcha();
            promise.then((response) => {
                // 验证码令牌：提交登录时一起带上，保证只校验本次获取的验证码
                this.captchaKey = response.captchaKey || '';
                this.mathQuestion = response.question || '';
            }).catch(error => {
                console.error(error); // 打印错误信息
                this.$message.error('获取验证码失败：' + error.message);
            });
        },

        loginEnterFun: function (e) {
            var keyCode = window.event ? e.keyCode : e.which;
            // console.log('回车登录',keyCode,e);
            if (keyCode == 13) {
                this.gotoHome();
            }
        },
        gotoHome: function () {//用户登录
            userLogin(this.username, encryptPassword.encrypt(this.password), this.captcha, this.captchaKey).then((response) => {
                // 登录成功记录token和用户信息，登录失败给对应提示
                setToken(response.token)
                // 记住我：仅记住用户名，不保存密码
                if (this.rememberMe) {
                    localStorage.setItem('rememberUser', this.username);
                } else {
                    localStorage.removeItem('rememberUser');
                }
                // 存储用户信息
                localStorage.setItem("userInfo", JSON.stringify(response.userInfo))
                // 登录成功后跳回登录前的页面；logUrl 是登录页自身或不存在时回首页（避免原地重复跳转不生效）
                var logUrl = localStorage.getItem('logUrl');
                localStorage.removeItem('logUrl');
                if (logUrl && logUrl.indexOf('/Login') !== 0) {
                    this.$router.push(logUrl);
                } else {
                    this.$router.push({ path: '/' });
                }
            })
        },
        registerEnterFun: function (e) {
            var keyCode = window.event ? e.keyCode : e.which;
            // console.log('回车注册',keyCode,e);
            if (keyCode == 13) {
                this.newRegister();
            }
        },
        newRegister: function () {//注册提交
            var that = this;
            var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
            var preg = /^(\w){6,12}$/;
            if (that.nusername) {
                that.nusernameErr = false;
            } else {
                that.nusernameErr = true;
            }
            if (reg.test(that.nemail)) {
                that.nemailErr = false;
            } else {
                that.nemailErr = true;
            }
            if (that.npassword && preg.test(that.npassword)) {
                that.npasswordErr = false;
                if (that.npassword == that.npassword2) {
                    that.npassword2Err = false;
                } else {
                    that.npassword2Err = true;
                }
            } else {
                that.npasswordErr = true;
            }
            if (!that.nusernameErr && !that.nemailErr && !that.npasswordErr) {
                that.fullscreenLoading = true;
                userRegister(that.nusername, that.nnickName, that.nemail, that.npassword, that.emailCaptcha).then((response) => {
                    this.$alert('注册成功', '', {
                        confirmButtonText: '确定'
                    })
                    //注册成功后调整到登录
                    that.goLogin()
                }).catch((error) => {
                    that.fullscreenLoading = false;
                })
            }
        },
        goLogin: function () {//去登陆
            this.$router.push({ path: '/Login?login=1' });
        },
        goRegister: function () {//去注册
            this.$router.push({ path: '/Login?login=0' });
        }

    },
    components: { //定义组件

    },
    watch: {
        // 如果路由有变化，会再次执行该方法
        '$route': 'routeChange'
    },
    created() { //生命周期函数
        var that = this;
        that.routeChange();
    }

}
</script>

<style>
/* ================= 背景图占满全屏 + 表单卡浮层 ================= */
.loginPage {
    position: relative;
    min-height: 100vh;
    overflow-x: hidden;
}

/* ---------- 全屏背景 ---------- */
.bgLayer {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
}

.bgLayer .el-carousel {
    height: 100%;
}

.bgLayer .el-carousel__container {
    height: 100% !important;
}

.bgLayer img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

/* ---------- 居中表单卡 ---------- */
.loginWrap {
    position: relative;
    z-index: 5;
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
}

.loginCard {
    position: relative;
    width: 420px;
    max-width: 92vw;
    padding: 34px 34px 30px;
    border-radius: 20px;
    /* 不使用毛玻璃（无模糊）：卡片为半透明实色，卡内文字清晰，背景图在卡片外可见 */
    background: rgba(255, 255, 255, .70);
    border: 1px solid rgba(255, 255, 255, .6);
    box-shadow: 0 26px 70px rgba(10, 18, 40, .30);
    animation: cardIn .55s cubic-bezier(.22, .8, .36, 1);
}

@keyframes cardIn {
    from {
        opacity: 0;
        transform: translateY(24px) scale(.98);
    }

    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

.cardTop {
    display: inline-block;
    margin-bottom: 18px;
    font-size: 13px;
    font-weight: 600;
    color: #4F8CFF;
    text-decoration: none;
}

.cardTop:hover {
    text-decoration: underline;
}

.formTitle {
    font-size: 25px;
    font-weight: 700;
    color: var(--text);
    margin: 0 0 6px;
}

.formSub {
    font-size: 13px;
    color: var(--text-weak);
    margin: 0 0 22px;
}

/* 字段 */
.field {
    margin-bottom: 15px;
}

.field label {
    display: block;
    font-size: 13px;
    font-weight: 500;
    color: var(--text-weak);
    margin-bottom: 7px;
}

.lgInput input {
    height: 44px;
    font-size: 14px;
    border-radius: 10px;
    border: 1px solid var(--border);
    background: rgba(91, 143, 249, .04);
    color: var(--text);
    transition: border-color .25s ease, box-shadow .25s ease;
}

.lgInput input:focus {
    border-color: #4F8CFF;
    box-shadow: 0 0 0 3px rgba(79, 140, 255, .14);
}

.lgInput .el-input__suffix i {
    cursor: pointer;
    line-height: 44px;
    color: var(--text-weak);
    padding-right: 4px;
}

.lgInput .el-input__suffix i:hover {
    color: #4F8CFF;
}

.captchaRow {
    display: flex;
    gap: 10px;
    align-items: center;
}

.captchaRow .el-input {
    flex: 1;
}

.mathCaptcha {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    height: 44px;
    min-width: 128px;
    padding: 0 14px;
    font-size: 14px;
    font-weight: 600;
    letter-spacing: 1px;
    color: #3d4759;
    background: linear-gradient(120deg, rgba(79, 140, 255, .12), rgba(123, 108, 255, .12));
    border: 1px solid rgba(79, 140, 255, .35);
    border-radius: 10px;
    cursor: pointer;
    white-space: nowrap;
    transition: transform .25s ease, border-color .25s ease, box-shadow .25s ease;
}

.mathCaptcha:hover {
    transform: translateY(-1px);
    border-color: #4F8CFF;
    box-shadow: 0 6px 16px rgba(79, 140, 255, .22);
}

.mathCaptcha .fa-refresh {
    font-size: 12px;
    color: #4F8CFF;
    transition: transform .45s ease;
}

.mathCaptcha:hover .fa-refresh {
    transform: rotate(180deg);
}

.codeBtn {
    height: 44px;
    border-radius: 10px;
    white-space: nowrap;
}

/* 错误提示 */
.formError {
    margin-bottom: 15px;
    padding: 10px 14px;
    font-size: 13px;
    color: #d93026;
    background: rgba(217, 48, 38, .08);
    border: 1px solid rgba(217, 48, 38, .28);
    border-radius: 10px;
}

.fieldError {
    margin-top: 6px;
    font-size: 12px;
    color: #d93026;
}

.formRow {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 4px 0 16px;
}

.linkBtn {
    font-size: 13px;
    color: #4F8CFF;
    text-decoration: none;
}

.linkBtn:hover {
    text-decoration: underline;
}

.primaryBtn {
    width: 100%;
    height: 46px;
    border: none;
    border-radius: 10px;
    font-size: 15px;
    font-weight: 600;
    color: #fff;
    letter-spacing: 1px;
    cursor: pointer;
    background: linear-gradient(120deg, #4F8CFF, #7B6CFF);
    box-shadow: 0 8px 22px rgba(79, 140, 255, .28);
    transition: transform .25s ease, box-shadow .25s ease, filter .25s ease;
}

.primaryBtn:hover {
    transform: translateY(-2px);
    filter: brightness(1.06);
    box-shadow: 0 12px 28px rgba(79, 140, 255, .36);
}

.primaryBtn:active {
    transform: translateY(0);
}

.divider {
    position: relative;
    text-align: center;
    margin: 20px 0 14px;
    height: 1px;
    background: #e6e9f0;
}

.divider span {
    position: relative;
    top: -11px;
    padding: 0 12px;
    font-size: 12px;
    color: var(--text-weak);
    background: transparent;
}

.socialBtns {
    display: flex;
    gap: 10px;
}

.socialBtn {
    flex: 1;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    height: 42px;
    font-size: 13px;
    color: var(--text);
    text-decoration: none;
    border: 1px solid var(--border);
    border-radius: 10px;
    background: rgba(91, 143, 249, .04);
    transition: transform .25s ease, border-color .25s ease, color .25s ease;
}

.socialBtn:hover {
    transform: translateY(-2px);
    border-color: #4F8CFF;
    color: #4F8CFF;
}

.socialBtn.github:hover {
    color: #24292e;
    border-color: #24292e;
}

.socialBtn.qq:hover {
    color: #12b7f5;
    border-color: #12b7f5;
}

.socialBtn.weibo:hover {
    color: #e6162d;
    border-color: #e6162d;
}

.switchLine {
    margin: 20px 0 0;
    font-size: 13px;
    color: var(--text-weak);
    text-align: center;
}

.switchLine a {
    color: #4F8CFF;
    font-weight: 600;
    text-decoration: none;
}

.switchLine a:hover {
    text-decoration: underline;
}

/* ---------- 移动端 ---------- */
@media screen and (max-width: 600px) {
    .loginWrap {
        padding: 24px 14px;
        align-items: flex-start;
    }

    .loginCard {
        padding: 26px 22px 24px;
        border-radius: 16px;
    }

    .formTitle {
        font-size: 22px;
    }
}
</style>
