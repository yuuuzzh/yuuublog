<!-- 邮件订阅：新文章发布后通知订阅者 -->
<template>
    <div class="subscribeBox">
        <h1 class="sub-title">订阅更新</h1>
        <p class="sub-desc">留下邮箱，新文章发布时第一时间通知你。</p>
        <div class="sub-form">
            <input v-model="email" type="email" placeholder="your@email.com" @keyup.enter="doSubscribe" />
            <button type="button" :disabled="loading" @click="doSubscribe">
                {{ loading ? '提交中' : '订阅' }}
            </button>
        </div>
        <div class="sub-captcha">
            <input v-model="captcha" type="text" placeholder="验证码" @keyup.enter="doSubscribe" />
            <img v-if="captchaUrl" class="sub-captcha-img" :src="captchaUrl" @click="refreshCaptcha" title="点击刷新" />
            <span v-else class="sub-captcha-img is-loading" @click="refreshCaptcha">点击获取</span>
        </div>
    </div>
</template>

<script>
import { subscribe } from '../api/subscribe.js'
import { getCaptchaImg } from '../api/captcha.js'

export default {
    name: 'SubscribeBox',
    data() {
        return {
            email: '',
            captcha: '',
            captchaKey: '',
            captchaUrl: '',
            loading: false
        }
    },
    created() {
        this.refreshCaptcha()
    },
    methods: {
        refreshCaptcha() {
            var that = this;
            that.captchaUrl = '';
            that.captchaKey = '';
            getCaptchaImg().then((res) => {
                that.captchaKey = res.captchaKey || '';
                that.captchaUrl = 'data:image/png;base64,' + res.captchaImageBase64;
            }).catch(() => {
                that.captchaUrl = '';
            });
        },
        doSubscribe() {
            var that = this;
            var mail = (that.email || '').trim();
            if (!mail) {
                that.$message && that.$message.warning('请输入邮箱');
                return;
            }
            if (!(that.captcha || '').trim()) {
                that.$message && that.$message.warning('请输入验证码');
                return;
            }
            that.loading = true;
            subscribe(mail, that.captcha.trim(), that.captchaKey).then(() => {
                that.email = '';
                that.$message && that.$message.success('订阅成功，感谢关注～');
                that.refreshCaptcha();
            }).catch((err) => {
                var msg = (err && err.msg) || '订阅失败，请稍后再试';
                that.$message && that.$message.error(msg);
                that.refreshCaptcha();
            }).finally(() => {
                that.captcha = '';
                that.loading = false;
            });
        }
    }
}
</script>

<style>
.subscribeBox {
    background: var(--surface);
    padding: 15px;
    border-radius: 5px;
    margin-bottom: 40px;
}

.sub-title {
    padding: 0 0 12px;
    font-size: 18px;
    font-weight: bold;
    color: var(--text);
    border-bottom: 1px solid var(--border);
}

.sub-desc {
    margin: 10px 0 12px;
    font-size: 13px;
    color: var(--text-weak);
    line-height: 1.7;
}

.sub-form {
    display: flex;
    gap: 8px;
}

.sub-form input {
    flex: 1;
    min-width: 0;
    padding: 8px 12px;
    font-size: 13px;
    color: var(--text);
    background: transparent;
    border: 1px solid var(--border);
    border-radius: 6px;
    outline: none;
}

.sub-form input:focus {
    border-color: var(--brand);
}

.sub-form button {
    padding: 0 16px;
    font-size: 13px;
    color: #fff;
    background: linear-gradient(120deg, var(--brand), var(--brand-2));
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: filter .25s var(--ease, ease);
}

.sub-form button:hover:not(:disabled) {
    filter: brightness(1.08);
}

.sub-form button:disabled {
    opacity: .6;
    cursor: not-allowed;
}

.sub-captcha {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 10px;
}

.sub-captcha input {
    flex: 1;
    min-width: 0;
    padding: 8px 12px;
    font-size: 13px;
    color: var(--text);
    background: transparent;
    border: 1px solid var(--border);
    border-radius: 6px;
    outline: none;
}

.sub-captcha input:focus {
    border-color: var(--brand);
}

.sub-captcha-img {
    height: 34px;
    min-width: 88px;
    border-radius: 6px;
    border: 1px solid var(--border);
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    color: var(--text-weak);
    background: var(--surface-2, rgba(79, 140, 255, .06));
}
</style>
