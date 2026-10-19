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
    </div>
</template>

<script>
import { subscribe } from '../api/subscribe.js'

export default {
    name: 'SubscribeBox',
    data() {
        return {
            email: '',
            loading: false
        }
    },
    methods: {
        doSubscribe() {
            var that = this;
            var mail = (that.email || '').trim();
            if (!mail) {
                that.$message && that.$message.warning('请输入邮箱');
                return;
            }
            that.loading = true;
            subscribe(mail).then(() => {
                that.email = '';
                that.$message && that.$message.success('订阅成功，感谢关注～');
            }).catch((err) => {
                var msg = (err && err.msg) || '订阅失败，请稍后再试';
                that.$message && that.$message.error(msg);
            }).finally(() => {
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
</style>
