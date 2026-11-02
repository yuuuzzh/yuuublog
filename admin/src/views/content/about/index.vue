<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px" v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="称呼" prop="name">
            <el-input v-model="form.name" placeholder="展示在关于我页面的名字，如 yuuu" maxlength="64" />
          </el-form-item>
          <el-form-item label="一句话头衔" prop="title">
            <el-input v-model="form.title" placeholder="如：后端工程师 / 摄影爱好者" maxlength="128" />
          </el-form-item>
          <el-form-item label="签名" prop="summary">
            <el-input v-model="form.summary" placeholder="一句话座右铭" maxlength="255" />
          </el-form-item>
          <el-form-item label="所在城市" prop="location">
            <el-input v-model="form.location" placeholder="如：北京" maxlength="64" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="用于前台展示联系邮箱" maxlength="128" />
          </el-form-item>
          <el-form-item label="GitHub" prop="github">
            <el-input v-model="form.github" placeholder="GitHub 主页地址" maxlength="255" />
          </el-form-item>
          <el-form-item label="个人网站" prop="website">
            <el-input v-model="form.website" placeholder="个人主页地址" maxlength="255" />
          </el-form-item>
          <el-form-item label="社交矩阵">
            <div class="form-tip">留空则不显示对应图标；QQ 填号即可（前台按临时会话加好友），微信需上传二维码</div>
          </el-form-item>
          <el-form-item label="QQ号" prop="qq">
            <el-input v-model="form.qq" placeholder="QQ 号码，如 123456789" maxlength="64" />
          </el-form-item>
          <el-form-item label="微博" prop="weibo">
            <el-input v-model="form.weibo" placeholder="微博主页地址" maxlength="255" />
          </el-form-item>
          <el-form-item label="CSDN" prop="csdn">
            <el-input v-model="form.csdn" placeholder="CSDN 主页地址" maxlength="255" />
          </el-form-item>
          <el-form-item label="Gitee" prop="gitee">
            <el-input v-model="form.gitee" placeholder="Gitee 主页地址" maxlength="255" />
          </el-form-item>
          <el-form-item label="微信二维码">
            <div class="about-upload">
              <img v-if="form.wechatQr" :src="form.wechatQr" class="about-avatar" />
              <i v-else class="el-icon-picture-outline avatar-uploader-icon" />
              <el-upload class="upload-btn" :show-file-list="false" :http-request="uploadWechatQr"
                accept="image/png,image/jpg,image/jpeg">
                <el-button size="small" type="primary">选择图片</el-button>
              </el-upload>
              <el-input v-model="form.wechatQr" placeholder="也可直接填写二维码图片地址" />
            </div>
          </el-form-item>
          <el-form-item label="技能" prop="skills">
            <el-input v-model="form.skills" type="textarea" :rows="3"
              placeholder="逗号分隔，可写熟练度，如：Java:90,Spring Boot:85,Vue:75" maxlength="512" />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="头像">
            <div class="about-upload">
              <img v-if="form.avatar" :src="form.avatar" class="about-avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon" />
              <el-upload class="upload-btn" :show-file-list="false" :http-request="uploadAvatar"
                accept="image/png,image/jpg,image/jpeg">
                <el-button size="small" type="primary">选择图片</el-button>
              </el-upload>
              <el-input v-model="form.avatar" placeholder="也可直接填写图片地址" />
            </div>
          </el-form-item>
          <el-form-item label="顶部封面">
            <div class="about-upload">
              <img v-if="form.cover" :src="form.cover" class="about-cover" />
              <i v-else class="el-icon-picture-outline avatar-uploader-icon" />
              <el-upload class="upload-btn" :show-file-list="false" :http-request="uploadCover"
                accept="image/png,image/jpg,image/jpeg">
                <el-button size="small" type="primary">选择图片</el-button>
              </el-upload>
              <el-input v-model="form.cover" placeholder="也可直接填写图片地址" />
            </div>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio label="0">展示</el-radio>
              <el-radio label="1">停用</el-radio>
            </el-radio-group>
            <div class="form-tip">停用后前台「关于我」不展示内容</div>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="个人介绍" prop="content">
        <mavon-editor v-model="form.content" :toolbars="toolbars" placeholder="支持 Markdown，前台会自动渲染" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :disabled="!$hasPermission('content:about:edit')" @click="submitForm">保 存</el-button>
        <el-button @click="getInfo">重 置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getAbout, saveAbout } from "@/api/content/about";
import { uploadImg } from "@/api/content/upload";

export default {
  name: "About",
  data() {
    return {
      loading: true,
      form: {
        status: "0",
        content: ""
      },
      rules: {
        name: [{ required: true, message: "称呼不能为空", trigger: "blur" }]
      },
      toolbars: {
        bold: true, italic: true, header: true, underline: true, strikethrough: true,
        quote: true, ol: true, ul: true, link: true, code: true, table: true,
        imagelink: true, help: true, undo: true, redo: true, preview: true
      }
    };
  },
  created() {
    this.getInfo();
  },
  methods: {
    getInfo() {
      this.loading = true;
      getAbout().then(res => {
        this.form = Object.assign({ status: "0", content: "" }, res || {});
        if (!this.form.status) {
          this.form.status = "0";
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    uploadAvatar(img) {
      uploadImg(img.file).then(response => {
        this.form.avatar = response;
      }).catch(error => {
        this.$message.error(error.msg || "上传失败");
      });
    },
    uploadWechatQr(img) {
      uploadImg(img.file).then(response => {
        this.form.wechatQr = response;
        this.$modal.msgSuccess("上传成功");
      });
    },
    uploadCover(img) {
      uploadImg(img.file).then(response => {
        this.form.cover = response;
      }).catch(error => {
        this.$message.error(error.msg || "上传失败");
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return;
        }
        saveAbout(this.form).then(() => {
          this.$message.success("保存成功");
          this.getInfo();
        });
      });
    }
  }
};
</script>

<style scoped>
.about-upload {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.about-upload img {
  margin-right: 10px;
  object-fit: cover;
}

.about-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
}

.about-cover {
  width: 160px;
  height: 90px;
  border-radius: 6px;
}

.upload-btn {
  margin-right: 10px;
}

.about-upload .el-input {
  margin-top: 8px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
}
</style>
