<!-- 登录注册 -->
<template>
    <div>
        <div class="carousel-container">
            <el-carousel height="100vh" indicator-position="none">
                <el-carousel-item>
                    <img style="object-fit: cover; width: 100%; height: 100%; "
                        src="../../static/img/login-bac0.png" />
                </el-carousel-item>
                <el-carousel-item>
                    <img style="object-fit: cover; width: 100%; height: 100%;"
                        src="../../static/img/login-bac1.png" />
                </el-carousel-item>
                <el-carousel-item>
                    <img style="object-fit: cover; width: 100%; height: 100%;"
                        src="../../static/img/login-bac2.png" />
                </el-carousel-item>
            </el-carousel>
        </div>

        <div class="container">
            <h1 class="loginTitle wenzi">
                生命的意义在于不顺从
            </h1>
            <!-- 修改密码 -->
            <div v-if="login == 2" class="changePasswordBox">
                <div class="lr-title">
                    <h1>修改密码</h1>
                    <p>
                        已有账号<a href="#/Login?login=1" class="tcolors">登录</a>
                    </p>
                </div>
                <el-alert v-show="changePasswordErr" :title="changePasswordTitle" type="error" show-icon :closable="false">
                </el-alert>
                <el-input style="margin-bottom: 30px;" type="text" placeholder="邮箱" v-model="emailPassword">
                </el-input>
                <el-input style="margin-bottom: 30px;" type="password" placeholder="密码" v-model="newPassword">
                </el-input>
                <el-input style="margin-bottom: 30px;" type="password" placeholder="确认密码" v-model="confirmNewPassword">
                </el-input>
                <!-- 验证码 -->
                <el-row>
                    <el-col :span="14">
                        <el-input type="text" placeholder="验证码" v-model="emailCaptchaUpdate">
                        </el-input>
                    </el-col>
                    <el-col :span="10">
                        <el-button style="width:100%;text-align: left;margin-bottom: 30px" type="primary" plain
                            icon="el-icon-message" @click="sendCodeForUpdatePassword" :disabled="isButtonDisabled2">
                            发送验证码
                        </el-button>
                    </el-col>
                </el-row>

                <el-alert v-show="uemailErr" title="邮箱不能为空" type="error" show-icon :closable="false">
                </el-alert>
                <div class="lr-btn tcolors-bg" @click="changePassword">确定</div>
            </div>


            <!-- 绑定第三方登录信息 -->
            <div v-if="login == 9" class="changePasswordBox">
                <div class="lr-title">
                    <h1>请完成绑定</h1>
                </div>
                <el-alert v-show="changePasswordErr" :title="changePasswordTitle" type="error" show-icon :closable="false">
                </el-alert>
                <el-input style="margin-bottom: 30px;" type="text" placeholder="邮箱" v-model="emailPassword">
                </el-input>
                <!-- 验证码 -->
                <el-row>
                    <el-col :span="14">
                        <el-input type="text" placeholder="验证码" v-model="emailCaptchaUpdate">
                        </el-input>
                    </el-col>
                    <el-col :span="10">
                        <el-button style="width:100%;text-align: left;margin-bottom: 30px" type="primary" plain
                            icon="el-icon-message" @click="sendCodeForUpdatePassword" :disabled="isButtonDisabled2">
                            发送验证码
                        </el-button>
                    </el-col>
                </el-row>

                <el-alert v-show="uemailErr" title="邮箱不能为空" type="error" show-icon :closable="false">
                </el-alert>
                <div class="lr-btn tcolors-bg" @click="oauthWithGithub">确定</div>
            </div>

            <!-- 登录 -->
            <div v-if="login == 1" class="loginBox">
                <div class="lr-title">
                    <h1>登录</h1>
                    <p>
                        新用户<a href="#/Login?login=0" class="tcolors">注册</a>
                    </p>
                </div>
                <el-alert v-show="loginErr" :title="loginTitle" type="error" show-icon :closable="false">
                </el-alert>
                <el-input type="text" placeholder="用户名" v-model="username">
                </el-input>
                <el-input type="password" placeholder="密码" v-model="password">
                </el-input>

                <div class="captchaBox">
                    <el-input type="text" placeholder="验证码" @keyup.enter.native="loginEnterFun" v-model="captcha"></el-input>
                    <img class="captchaImg" :src="captchaUrl" @click="refreshCaptcha">
                </div>
                <h3><a href="#/Login?login=2">忘记密码？</a></h3>
                <div class="lr-btn tcolors-bg" @click="gotoHome">登录</div>

                <div class="otherLogin">
                    <a href="https://yuuuzzh.us.kg/login/oauth/authorize?client_id%3Dc62e602f840881ab59b2%26state%3Dgithub">
                        <i class="fa fa-fw fa-github" style="background-color: black;"></i>
                    </a>
                    <a href="javascript:void(0)" @click="showAlert">
                        <i class="fa fa-fw fa-qq"></i></a>
                    <a href="javascript:void(0)" @click="showAlert"><i class="fa fa-fw fa-weibo"></i></a>
                </div>
            </div>
            <!-- 注册 -->
            <div v-if="login == 0" class="registerBox">
                <div class="lr-title">
                    <h1>注册</h1>
                    <p>
                        已有账号<a href="#/Login?login=1" class="tcolors">登录</a>
                    </p>
                </div>
                <el-alert v-show="registerErr" :title="registerTitle" type="error" show-icon :closable="false">
                </el-alert>
                <el-input type="text" placeholder="用户名" v-model="nusername">
                </el-input>
                <el-alert v-show="nusernameErr" title="用户名错误" type="error" show-icon :closable="false">
                </el-alert>
                <el-input type="text" placeholder="昵称" v-model="nnickName">
                </el-input>
                <el-input type="email" placeholder="邮箱" v-model="nemail">
                </el-input>
                <el-row>
                    <el-col :span="14">
                        <el-input type="text" placeholder="验证码" v-model="emailCaptcha">
                        </el-input>
                    </el-col>
                    <el-col :span="10">
                        <el-button style="width:100%;text-align: left;" type="info" icon="el-icon-message"
                            @click="sendVerificationCode" :disabled="isButtonDisabled">
                            {{ isButtonDisabled ? timeLeft + 's' : '发送验证码' }}
                        </el-button>
                    </el-col>
                </el-row>

                <!-- <button @click="sendVerificationCode">发送验证码</button> -->
                <el-alert v-show="nemailErr" title="邮箱不能为空" type="error" show-icon :closable="false">
                </el-alert>
                <el-input type="password" placeholder="密码:6-12位英文、数字、下划线" v-model="npassword">
                </el-input>
                <el-alert v-show="npasswordErr" title="密码错误" type="error" show-icon :closable="false">
                </el-alert>
                <el-input type="password" placeholder="确认密码" @keyup.enter.native="registerEnterFun" v-model="npassword2">
                </el-input>
                <el-alert v-show="npassword2Err" title="重复密码有误" type="error" show-icon :closable="false">
                </el-alert>
                <div class="lr-btn tcolors-bg" @click="newRegister" v-loading.fullscreen.lock="fullscreenLoading"
                    element-loading-text="提交中">注册</div>
            </div>
        </div>

    </div>
</template>

<script>
import { userLogin, userRegister, sendVerificationCode, sendCodeForUpdatePassword, changePassword } from '../api/user.js'
import { setToken } from '../utils/auth.js'
import  encryptPassword  from '../utils/encryptPassword'
import { getCaptchaImg } from '@/api/captcha.js';
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
            captchaUrl: '',
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
        refreshCaptcha() {//这个是登录验证码
            const promise = getCaptchaImg();
            promise.then((response) => {
                // 验证码令牌：提交登录时一起带上，保证只校验本次获取的验证码
                this.captchaKey = response.captchaKey || '';
                this.captchaUrl = 'data:image/png;base64,' + response.captchaImageBase64;
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
@font-face {
    font-family: 'CustomFont';
    src: url('../assets/font/ShangShouZhiZunShuFaTi/No.39-ShangShouZhiZunShuFaTi-2.ttf');
}

.wenzi {
    font-size: 24px;
    font-family: 'CustomFont';
}

.container {
    opacity: 0.80;
}

.carousel-container {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: -1;
}

/*登录注册标题*/
.loginTitle {
    text-align: center;
    font-size: 26px;
    padding-top: 50px;
    margin-bottom: 20px;
}

.captchaImg1 {
    width: 100%;
    height: auto;
}

.loginBox,
.changePasswordBox,
.registerBox {
    background: var(--surface);
    padding: 40px;
    max-width: 320px;
    margin: 0 auto;
}

.loginBox {
    padding-bottom: 0;
}

.lr-title {
    position: relative;
    height: 32px;
    line-height: 32px;
    margin-bottom: 20px;
}

.lr-title h1 {
    font-size: 24px;
    color: #666;
    font-weight: bold;
    /*width:50%;*/
}

.lr-title p {
    font-size: 12px;
    color: #999;
    position: absolute;
    right: 0;
    top: 0;
}

.lr-btn {
    color: #fff;
    text-align: center;
    letter-spacing: 5px;
    padding: 8px;
    border-radius: 5px;
    cursor: pointer;
    margin-bottom: 30px;
}

.loginBox .el-input,
.registerBox .el-input {
    margin-bottom: 20px;
}

.loginBox .el-alert,
.registerBox .el-alert {
    top: -18px;
    background-color: #888;
}

.loginBox .el-input input,
.registerBox .el-input input {
    border-radius: 4px;
}

.loginBox h3,
.registerBox h3 {
    text-align: right;
    margin-bottom: 20px;
}

.loginBox h3 a,
.registerBox h3 a {
    font-size: 13px;
    color: #999;
}

.loginBox .otherLogin {
    max-width: 320px;
    padding: 30px 40px;
    background: #ddd;
    text-align: center;
    margin-left: -40px;
    margin-right: -40px;
    /* visibility: hidden; */
}

.loginBox .otherLogin p {
    margin-bottom: 20px;
    font-size: 16px;
}

.loginBox .otherLogin a i {
    display: inline-block;
    width: 42px;
    height: 42px;
    line-height: 42px;
    font-size: 18px;
    border-radius: 50%;
    color: #fff;
    margin: 0 10px;
}

.loginBox .otherLogin a i.fa-wechat {
    background: #7bc549;
}

.loginBox .otherLogin a i.fa-qq {
    background: #56b6e7;
}

.loginBox .otherLogin a i.fa-weibo {
    background: #ff763b;
}

/*登录成功*/
.registerSuc {
    padding: 40px;
    margin: 0 auto;
}

.registerSuc .sucIcon {
    text-align: center;
    margin-bottom: 30px;
    padding-left: 60px;
}

.registerSuc .sucContent {
    line-height: 1.5;
    font-size: 15px;
    text-align: center;
}

.registerSuc .sucContent p {
    margin-top: 10px;
    font-size: 13px;
    color: #999;
}

.registerSuc .sucContent .lastbtn {
    display: inline-block;
    font-size: 14px;
    padding: 3px 10px;
    border-radius: 5px;
    color: #fff;
    cursor: pointer;
}

.registerSuc .sucContent .el-icon-close {
    fong-size: 13px;
}
</style>
