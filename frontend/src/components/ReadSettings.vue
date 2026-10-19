<!-- 移动端阅读设置：主题切换 + 正文字号调节（桌面端隐藏，桌面用 ThemeToggle） -->
<template>
    <div class="read-settings">
        <button class="rs-trigger glass" type="button" title="阅读设置" @click="open = !open">
            <i class="fa fa-cog"></i>
        </button>

        <transition name="rs">
            <div class="rs-panel glass" v-if="open">
                <div class="rs-row">
                    <span class="rs-label">主题</span>
                    <div class="rs-actions">
                        <button type="button" :class="{ 'is-active': theme === 'light' }" @click="setTheme('light')">亮
                        </button>
                        <button type="button" :class="{ 'is-active': theme === 'dark' }" @click="setTheme('dark')">暗
                        </button>
                        <button type="button" :class="{ 'is-active': theme === 'auto' }" @click="setTheme('auto')">自动
                        </button>
                    </div>
                </div>

                <div class="rs-row">
                    <span class="rs-label">字号</span>
                    <div class="rs-actions">
                        <button type="button" @click="changeFontScale(-0.1)">
                            <i class="fa fa-minus"></i>
                        </button>
                        <span class="rs-value">{{ Math.round(fontScale * 100) }}%</span>
                        <button type="button" @click="changeFontScale(0.1)">
                            <i class="fa fa-plus"></i>
                        </button>
                    </div>
                </div>
            </div>
        </transition>
    </div>
</template>

<script>
import { getStoredTheme, setStoredTheme, applyTheme } from '../utils/theme.js'

const FONT_KEY = 'blogFontScale'

export default {
    name: 'ReadSettings',
    data() {
        return {
            open: false,
            theme: getStoredTheme(),
            fontScale: 1
        }
    },
    created() {
        var saved = parseFloat(localStorage.getItem(FONT_KEY));
        this.fontScale = (saved && saved >= 0.8 && saved <= 1.6) ? saved : 1;
        this.applyFontScale();
    },
    methods: {
        setTheme(theme) {
            this.theme = theme;
            setStoredTheme(theme);
            applyTheme(theme);
        },
        changeFontScale(delta) {
            var next = Math.round((this.fontScale + delta) * 10) / 10;
            if (next < 0.8) {
                next = 0.8;
            }
            if (next > 1.6) {
                next = 1.6;
            }
            this.fontScale = next;
            localStorage.setItem(FONT_KEY, String(next));
            this.applyFontScale();
        },
        applyFontScale() {
            // 正文按 --font-scale 缩放，主题样式里用 calc 引用该变量
            document.documentElement.style.setProperty('--font-scale', String(this.fontScale));
        }
    }
}
</script>

<style scoped>
.read-settings {
    display: none;
}

.rs-trigger {
    position: fixed;
    right: 12px;
    bottom: 74px;
    width: 42px;
    height: 42px;
    border: none;
    border-radius: 50%;
    color: var(--text);
    cursor: pointer;
    z-index: 1002;
}

.rs-panel {
    position: fixed;
    right: 12px;
    bottom: 124px;
    width: 210px;
    padding: 12px 14px;
    border-radius: 12px;
    z-index: 1002;
}

.rs-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    padding: 6px 0;
}

.rs-label {
    font-size: 13px;
    color: var(--text-weak);
}

.rs-actions {
    display: flex;
    align-items: center;
    gap: 6px;
}

.rs-actions button {
    min-width: 32px;
    height: 28px;
    padding: 0 8px;
    font-size: 12px;
    color: var(--text);
    background: transparent;
    border: 1px solid var(--border);
    border-radius: 6px;
    cursor: pointer;
}

.rs-actions button.is-active {
    color: #fff;
    background: linear-gradient(120deg, var(--brand), var(--brand-2));
    border-color: transparent;
}

.rs-value {
    font-size: 12px;
    color: var(--text-weak);
    min-width: 40px;
    text-align: center;
}

.rs-enter-active,
.rs-leave-active {
    transition: opacity .2s ease, transform .2s ease;
}

.rs-enter,
.rs-leave-to {
    opacity: 0;
    transform: translateY(6px);
}

@media screen and (max-width: 768px) {
    .read-settings {
        display: block;
    }
}
</style>
