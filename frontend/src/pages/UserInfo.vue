<!-- 用户中心 -->
<template>
    <div>
        <wbc-nav></wbc-nav>
        <div class="container">
            <div v-show="isEdit" class="tcommonBox">
                <header>
                    <h1>
                            编辑个人资料
                    </h1>
                </header>
                <section>
                    <ul class="userInfoBox">
                        <li class="avatarlist">
                            <span class="leftTitle">头像</span>
                            <el-upload
                              class="avatar-uploader"
                              name="img"
                              :action="uploadURL"
                              :show-file-list="false"
                              :on-success="handleAvatarSuccess"
                              :before-upload="beforeAvatarUpload">
                              <img   v-if="userInfoObj.avatar" :src="userInfoObj.avatar?userInfoObj.avatar:'static/img/tou.jpg'"  :onerror="$store.state.errorImg" class="avatar">
                              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                              <div slot="tip" class="el-upload__tip">点击上传头像，只能上传jpg/png文件，且不超过1mb</div>
                            </el-upload>
                        </li>
                        <li class="username">
                            <span class="leftTitle">昵称</span>
                            <el-input v-model="userInfoObj.nickName" placeholder="昵称"></el-input> <i  class="fa fa-wa fa-asterisk"></i>
                        </li>
                        <li>
                            <span class="leftTitle">电子邮件</span>
                            <span>{{userInfoObj.email}}</span>
                        </li>
                        <li>
                            <span class="leftTitle">性别</span>
                            <template>
                            
                              <el-radio class="radio" v-model="userInfoObj.sex" label="0">男</el-radio>
                              <el-radio class="radio" v-model="userInfoObj.sex" label="1">女</el-radio>
                            </template>
                        </li>


                    </ul>
                    <div class=" saveInfobtn">
                        <a class="tcolors-bg"  href="javascript:void(0);" @click="isEdit=!isEdit">返 回</a>
                        <a class="tcolors-bg" href="javascript:void(0);" @click="saveInfoFun">保 存</a>
                    </div>
                </section>
            </div>
            <div v-show="!isEdit" class="tcommonBox">
                <header>
                    <h1>个人中心</h1>
                    <div class="uc-actions">
                        <a class="uc-btn" href="/Favorite"><i class="fa fa-wa fa-star"></i>我的收藏</a>
                        <span class="uc-btn" @click="isEdit=!isEdit"><i class="fa fa-wa fa-edit"></i>编辑资料</span>
                        <span class="uc-btn uc-logout" @click="logout"><i class="fa fa-wa fa-sign-out"></i>退出登录</span>
                    </div>
                </header>

                <!-- 用户横幅：头像 + 基本信息 + 收藏数 -->
                <section>
                    <div class="uc-hero">
                        <img class="uc-avatar"
                            :src="userInfoObj.avatar?userInfoObj.avatar:'static/img/tou.jpg'"
                            :onerror="$store.state.errorImg" />
                        <div class="uc-info">
                            <h2>{{ userInfoObj.nickName ? userInfoObj.nickName : '未设置昵称' }}</h2>
                            <p><i class="fa fa-wa fa-envelope"></i>{{ userInfoObj.email ? userInfoObj.email : '未绑定邮箱' }}</p>
                            <span class="uc-sex">
                                <i class="fa fa-wa" :class="userInfoObj.sex == 1 ? 'fa-venus' : 'fa-mars'"></i>
                                {{ userInfoObj.sex == null ? '未知' : userInfoObj.sex == 0 ? '男' : '女' }}
                            </span>
                        </div>
                        <div class="uc-stat">
                            <b>{{ favCount }}</b>
                            <span>收藏文章</span>
                        </div>
                    </div>

                    <!-- 快捷入口 -->
                    <div class="uc-cards">
                        <a class="uc-card" href="/Favorite">
                            <i class="fa fa-wa fa-star"></i>
                            <span>我的收藏</span>
                            <em>{{ favCount }} 篇</em>
                        </a>
                        <a class="uc-card" href="/Login?login=2">
                            <i class="fa fa-wa fa-key"></i>
                            <span>修改密码</span>
                            <em>通过邮箱验证码</em>
                        </a>
                        <a class="uc-card" href="javascript:void(0);" @click="logout">
                            <i class="fa fa-wa fa-sign-out"></i>
                            <span>退出登录</span>
                            <em>清除本地登录态</em>
                        </a>
                    </div>
                </section>
            </div>
        
        </div>
    </div>
</template>

<script>
import header from '../components/header.vue'
import {getUserInfo,savaUserInfo} from '../api/user.js'//获取用户信息，保存用户信息
import { favoriteList } from '../api/favorite.js'
import { removeToken } from '../utils/auth.js'
import store from '../store'
    export default {
        name: 'UserInfo',
        data() { //选项 / 数据
            return {
                uploadURL:'',
                isEdit: false,
                userInfo:{},//本地存储的用户信
                userInfoObj:'',//用户的信息
                favCount: 0,//收藏文章数
            }
        },
        methods: { //事件处理器
            handleAvatarSuccess(res, file) {//上传头像
                if(res.code == 200){
                    this.userInfoObj.avatar = res.data;
                    this.userInfoObj.head_start = 1;
                }else{
                    this.$message.error('上传图片失败');
                }

            },
            beforeAvatarUpload(file) {//判断头像大小
                const isJPG = file.type == 'image/png'||file.type=='image/jpg'||file.type=='image/jpeg';
                const isLt2M = file.size / 1024 / 1024 < 1;
                // console.log(file);
                if (!isJPG) {
                  this.$message.error('上传头像图片只能是 JPG/JPEG/PNG 格式!');
                }
                if (!isLt2M) {
                  this.$message.error('上传头像图片大小不能超过 1MB!');
                }
                return isJPG && isLt2M;
            },

            saveInfoFun: function(){//保存编辑的用户信息
                var that = this;

                if(!that.userInfoObj.nickName){ //昵称为必填
                     that.$message.error('昵称为必填项，请填写昵称');
                     return;
                }


                savaUserInfo(that.userInfoObj).then((response)=>{//保存信息接口，返回展示页
                    that.$message.success( '保存成功！');
                    that.isEdit = false;
                    that.routeChange() ;
                })
            },
            logout: function(){//退出登录
                removeToken();
                localStorage.removeItem('userInfo');
                this.$message && this.$message.success('已退出登录');
                this.$router.push({ path: '/' });
            },
            loadFavCount: function(){//收藏数量
                var that = this;
                favoriteList({ pageNum: 1, pageSize: 1 }).then(function (response) {
                    that.favCount = (response && response.total) || 0;
                }).catch(function () {
                    that.favCount = 0;
                });
            },
            routeChange: function(){//展示页面信息
                var that = this;
                // console.log(this.$router,this.$route);
                if(localStorage.getItem('userInfo')){
                    that.haslogin = true;
                    that.userInfo = JSON.parse(localStorage.getItem('userInfo'));
                    that.userId = that.userInfo.id;
                    getUserInfo(that.userId).then((response)=>{
                        that.userInfoObj = response;
                        that.userInfoObj.head_start = 0;
                    })
                    that.loadFavCount();
                }else{
                    that.haslogin = false;
                    // 未登录不允许进入个人中心：跳转登录页，登录成功后跳回
                    localStorage.setItem('logUrl', that.$route.fullPath);
                    that.$router.replace({ path: '/Login', query: { login: 1 } });
                }

            }
        },
        components: { //定义组件
            'wbc-nav':header,
        },
        watch: {
           // 如果路由有变化，会再次执行该方法
           '$route':'routeChange'
         },
        created() { //生命周期函数
            this.routeChange();
            this.uploadURL = store.state.baseURL+'/upload'
        }
    }
</script>

<style>
.userInfoBox .avatarlist{
    position: relative;
}

.avatar-uploader {
    display: inline-block;
    vertical-align: top;
}
.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 50%;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 120px;
    height: 120px;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #20a0ff;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    line-height: 120px;
    text-align: center;
    position: absolute;
    top:0;
    left:0;
  }
  .avatar {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    display: block;
    object-fit: cover;
  }
.gotoEdit{
    font-size: 15px;
    float:right;
    cursor: pointer;
    color:#999;
}
.gotoEdit:hover {
    color:#333;
}
/*个人设置*/
.userInfoBox .leftTitle{
    display: inline-block;
    width:100px;
    padding: 10px 0;
}
.userInfoBox .rightInner{
    display: inline-block;
    max-width: calc(100% - 140px);
    vertical-align: top;
}
.userInfoBox li{
    padding:20px;
    border-bottom: 1px solid #ddd;
}
.userInfoBox li:last-child{
    border-bottom: 1px solid transparent;
}
.userInfoBox  .el-input,.userInfoBox  .el-textarea{
    max-width:300px;
    min-width: 100px;
}

.userInfoBox .el-input__inner{
    border-radius: 4px;
}
.userInfoBox  .el-textarea{
    vertical-align: top;
}
.saveInfobtn{
    margin: 20px 0;
    text-align: center;
}
.saveInfobtn a{
    color:#fff;
    padding:6px 20px;
    margin:5px 10px;
    border-radius: 5px;
    font-size: 14px;
}
.userInfoBox .fa-asterisk{
    color: #DF2050;
}

/* ================= 个人中心：横幅 + 快捷入口 ================= */
.tcommonBox header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 10px;
}

.uc-actions {
    display: flex;
    align-items: center;
    gap: 10px;
}

.uc-btn {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    padding: 7px 14px;
    font-size: 13px;
    color: #4F8CFF;
    background: rgba(79, 140, 255, .08);
    border: 1px solid rgba(79, 140, 255, .28);
    border-radius: 999px;
    cursor: pointer;
    text-decoration: none;
    transition: transform .25s ease, background .25s ease, color .25s ease;
}

.uc-btn:hover {
    transform: translateY(-1px);
    background: rgba(79, 140, 255, .14);
}

.uc-btn.uc-logout {
    color: #d93026;
    background: rgba(217, 48, 38, .07);
    border-color: rgba(217, 48, 38, .25);
}

.uc-btn.uc-logout:hover {
    background: rgba(217, 48, 38, .12);
}

/* 横幅 */
.uc-hero {
    display: flex;
    align-items: center;
    gap: 24px;
    padding: 26px 28px;
    border-radius: 14px;
    background: linear-gradient(120deg, rgba(79, 140, 255, .10), rgba(123, 108, 255, .10));
    border: 1px solid rgba(79, 140, 255, .16);
}

.uc-avatar {
    width: 96px;
    height: 96px;
    border-radius: 50%;
    object-fit: cover;
    border: 3px solid rgba(255, 255, 255, .9);
    box-shadow: 0 8px 22px rgba(20, 30, 60, .18);
}

.uc-info h2 {
    margin: 0 0 8px;
    font-size: 22px;
    font-weight: 700;
    color: #1d2433;
}

.uc-info p {
    margin: 0 0 8px;
    font-size: 13px;
    color: #64708a;
}

.uc-info p i {
    margin-right: 6px;
    color: #4F8CFF;
}

.uc-sex {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    padding: 3px 10px;
    font-size: 12px;
    color: #4F8CFF;
    background: rgba(79, 140, 255, .10);
    border-radius: 999px;
}

.uc-stat {
    margin-left: auto;
    text-align: center;
    padding: 14px 22px;
    border-radius: 14px;
    background: rgba(255, 255, 255, .7);
    border: 1px solid rgba(79, 140, 255, .16);
}

.uc-stat b {
    display: block;
    font-size: 26px;
    font-weight: 700;
    color: #4F8CFF;
}

.uc-stat span {
    font-size: 12px;
    color: #64708a;
}

/* 快捷入口 */
.uc-cards {
    display: flex;
    flex-wrap: wrap;
    gap: 14px;
    margin-top: 18px;
}

.uc-card {
    flex: 1;
    min-width: 160px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    padding: 18px 20px;
    border-radius: 14px;
    background: var(--surface, #fff);
    border: 1px solid rgba(79, 140, 255, .14);
    text-decoration: none;
    transition: transform .25s ease, box-shadow .25s ease, border-color .25s ease;
}

.uc-card:hover {
    transform: translateY(-3px);
    border-color: rgba(79, 140, 255, .4);
    box-shadow: 0 12px 28px rgba(79, 140, 255, .16);
}

.uc-card i {
    font-size: 18px;
    color: #4F8CFF;
}

.uc-card span {
    font-size: 15px;
    font-weight: 600;
    color: #1d2433;
}

.uc-card em {
    font-style: normal;
    font-size: 12px;
    color: #909399;
}

.uc-card:last-child:hover i {
    color: #d93026;
}

@media screen and (max-width: 768px) {
    .uc-hero {
        flex-direction: column;
        align-items: flex-start;
        gap: 14px;
    }

    .uc-stat {
        margin-left: 0;
    }

    .uc-cards {
        flex-direction: column;
    }

    .uc-card {
        flex-direction: row;
        align-items: center;
        gap: 12px;
    }

    .uc-card em {
        margin-left: auto;
    }
}
</style>
