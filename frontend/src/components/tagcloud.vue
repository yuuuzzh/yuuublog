<!-- 标签云 -->
<template>
    <div class="tagCloudBox">
        <h1 class="tag-title">标签云</h1>
        <div class="tag-list">
            <a v-for="(item,index) in tagList" :key="'t'+index" class="tag-item"
                :href="'#/Share?tagId='+item.id" :style="tagStyle(item.articleCount)">
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
        // 文章越多，标签越大越亮
        tagStyle(count) {
            var c = count || 1;
            var size = c >= 10 ? 16 : (c >= 5 ? 15 : 13);
            return {
                fontSize: size + 'px',
                opacity: c >= 10 ? 1 : (c >= 5 ? .9 : .78)
            };
        }
    },
    created() {
        this.getList()
    }
}
</script>

<style>
.tagCloudBox {
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
    padding: 4px 10px;
    border-radius: 999px;
    background: var(--surface-2, rgba(79, 140, 255, .08));
    color: var(--text-weak);
    text-decoration: none;
    transition: all .25s var(--ease, ease);
}

.tag-item:hover {
    color: #fff;
    background: linear-gradient(120deg, var(--brand), var(--brand-2));
    transform: translateY(-2px);
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
