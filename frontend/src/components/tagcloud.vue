<!-- 标签云 -->
<template>
    <div class="tagCloudBox">
        <h1 class="tag-title">标签云</h1>
        <div class="tag-list">
            <a v-for="(item,index) in tagList" :key="'t'+index" class="tag-item"
                :href="'/Share?tagId='+item.id" :style="tagStyle(item.articleCount)">
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
        // 文章越多，标签越大；按 id 确定性分配一组渐变色，避免每次渲染变色
        tagStyle(item) {
            var palettes = [
                'linear-gradient(120deg, #4F8CFF, #7B6CFF)',
                'linear-gradient(120deg, #FF7AC6, #FF9A8B)',
                'linear-gradient(120deg, #38C6D9, #4F8CFF)',
                'linear-gradient(120deg, #7B6CFF, #C86DD7)',
                'linear-gradient(120deg, #FFB86C, #FF7AC6)',
                'linear-gradient(120deg, #34D1A5, #38C6D9)'
            ];
            var idx = 0, s = String(item.id || '');
            for (var i = 0; i < s.length; i++) {
                idx += s.charCodeAt(i);
            }
            var count = item.articleCount || 1;
            var size = count >= 10 ? 16 : (count >= 5 ? 15 : 13);
            return {
                background: palettes[idx % palettes.length],
                color: '#fff',
                fontSize: size + 'px',
                boxShadow: '0 4px 14px rgba(79, 140, 255, .18)'
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
    padding: 5px 12px;
    border-radius: 999px;
    color: #fff;
    text-decoration: none;
    letter-spacing: .5px;
    transition: transform .25s var(--ease, ease), box-shadow .25s var(--ease, ease), filter .25s var(--ease, ease);
}

.tag-item:hover {
    transform: translateY(-3px) scale(1.05);
    filter: brightness(1.08);
    box-shadow: 0 8px 20px rgba(79, 140, 255, .35);
}

.tag-item .tag-count {
    background: rgba(255, 255, 255, .22);
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
