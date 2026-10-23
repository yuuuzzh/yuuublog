<!-- 标签云 -->
<template>
    <div class="tagCloudBox">
        <h1 class="tag-title">标签云</h1>
        <div class="tag-list">
            <a v-for="(item,index) in tagList" :key="'t'+index" class="tag-item"
                :class="'level-' + hotLevel(item.articleCount)" :href="'/Share?tagId='+item.id"
                :style="tagStyle(item.articleCount)">
                {{ item.name }}<span class="tag-count">{{ item.articleCount }}</span>
            </a>
        </div>
        <p class="tag-empty" v-if="!tagList.length">暂无标签</p>
    </div>
</template>

<script>
import { tagListWithCount } from '../api/tag.js'

export default {
    name: 'TagCloud',
    data() {
        return {
            tagList: []
        }
    },
    methods: {
        getList() {
            var that = this;
            tagListWithCount().then((response) => {
                that.tagList = response ? response : [];
            }).catch(() => {
                that.tagList = [];
            });
        },
        // 热度分档：0 冷门 -> 3 热门（字号与配色深浅都按档位递进）
        hotLevel(count) {
            var c = count || 1;
            if (c >= 10) return 3;
            if (c >= 5) return 2;
            if (c >= 2) return 1;
            return 0;
        },
        // 文章越多，标签越大
        tagStyle(count) {
            var level = this.hotLevel(count);
            var sizes = [13, 14, 15, 16];
            return {
                fontSize: sizes[level] + 'px'
            };
        }
    },
    created() {
        this.getList()
    }
}
</script>

<style>
/* 标签云：独立橙色系热度变量（不影响全站主色） */
.tagCloudBox {
    --hot: #FF8A3D;
    --hot-2: #FF5E2E;
    background: var(--surface);
    padding: 15px;
    border-radius: 5px;
    margin-bottom: 40px;
}

.tag-title {
    padding: 0 0 12px;
    font-size: 18px;
    font-weight: bold;
    color: var(--text);
    border-bottom: 1px solid var(--border);
}

.tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    padding-top: 12px;
}

.tag-item {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    padding: 5px 12px;
    border-radius: 999px;
    /* 低饱和淡底 + 主题色文字：白底/黑底都柔和，hover 点亮 */
    color: var(--hot);
    background: color-mix(in srgb, var(--hot) 9%, transparent);
    border: 1px solid color-mix(in srgb, var(--hot) 24%, transparent);
    text-decoration: none;
    letter-spacing: .5px;
    transition: transform .25s var(--ease, ease), background .25s var(--ease, ease), color .25s var(--ease, ease), border-color .25s var(--ease, ease);
}

.tag-item:hover {
    color: #fff;
    background: linear-gradient(120deg, var(--hot), var(--hot-2));
    border-color: transparent;
    transform: translateY(-3px);
}

/* 热度分层：同色系由浅入深，热度越高越浓（非随机彩色，明暗背景都不突兀） */
.tag-item.level-0 {
    background: color-mix(in srgb, var(--hot) 8%, transparent);
    border-color: color-mix(in srgb, var(--hot) 22%, transparent);
}

.tag-item.level-1 {
    background: color-mix(in srgb, var(--hot) 18%, transparent);
    border-color: color-mix(in srgb, var(--hot) 34%, transparent);
}

.tag-item.level-2 {
    background: color-mix(in srgb, var(--hot) 38%, transparent);
    border-color: color-mix(in srgb, var(--hot) 52%, transparent);
}

.tag-item.level-3 {
    background: linear-gradient(120deg, var(--hot), var(--hot-2));
    color: #fff;
    border-color: transparent;
}

.tag-item.level-3 .tag-count {
    background: rgba(255, 255, 255, .26);
    color: #fff;
}

.tag-item .tag-count {
    background: color-mix(in srgb, var(--hot) 16%, transparent);
    border-radius: 999px;
    padding: 0 6px;
    font-size: 11px;
    line-height: 16px;
}

.tag-count {
    font-size: 11px;
    opacity: .75;
}

.tag-empty {
    padding: 12px 0 0;
    color: var(--text-weak);
    font-size: 13px;
}
</style>
