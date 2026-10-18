<!-- 搜索结果页 -->
<template>
    <div>
        <sg-nav></sg-nav>
        <div class="container">
            <el-row :gutter="30">
                <el-col :sm="24" :md="16" style="transition:all .5s ease-out;margin-bottom:30px;">
                    <div class="searchBox tcommonBox">
                        <div class="search-bar">
                            <i class="fa fa-fw fa-search"></i>
                            <input v-model="keyword" placeholder="输入关键词搜索文章" @keyup.enter="doSearch" />
                            <el-button size="small" type="primary" @click="doSearch">搜索</el-button>
                        </div>

                        <p class="search-summary" v-if="searched">
                            共找到 <b>{{ total }}</b> 篇与「{{ lastKeyword }}」相关的文章
                        </p>

                        <ul class="result-list" v-if="list.length">
                            <li v-for="(item,index) in list" :key="'s'+index" class="result-item">
                                <h2 class="result-title">
                                    <a :href="'#/DetailArticle?aid='+item.id" target="_blank"
                                        v-html="highlight(item.title)"></a>
                                </h2>
                                <p class="result-excerpt" v-html="highlight(item.excerpt)"></p>
                                <div class="result-meta">
                                    <span><i class="fa fa-fw fa-folder-open"></i>{{ item.categoryName || '未分类' }}</span>
                                    <span><i class="fa fa-fw fa-clock-o"></i>{{ showInitDate(item.createTime,'all') }}</span>
                                    <span><i class="fa fa-fw fa-eye"></i>{{ item.viewCount }} 次围观</span>
                                </div>
                            </li>
                        </ul>

                        <p class="search-empty" v-if="searched && !list.length">
                            没有找到相关内容，换个关键词试试～
                        </p>

                        <el-col class="viewmore">
                            <a v-show="hasMore" class="more-btn" href="javascript:void(0);" @click="loadMore">
                                <span v-if="!loading">加载更多</span>
                                <span v-else><i class="fa fa-spinner fa-spin"></i> 加载中</span>
                            </a>
                        </el-col>
                    </div>
                </el-col>
                <el-col :sm="24" :md="8">
                    <yuuu-rightlist></yuuu-rightlist>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
import header from '../components/header.vue'
import rightlist from '../components/rightlist.vue'
import { searchArticles } from '../api/article.js'
import { initDate } from '../utils/server.js'

export default {
    name: 'Search',
    data() {
        return {
            keyword: '',
            lastKeyword: '',
            list: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            loading: false,
            searched: false
        }
    },
    computed: {
        hasMore() {
            return this.searched && this.total > this.list.length;
        }
    },
    methods: {
        showInitDate(oldDate, full) {
            return initDate(oldDate, full)
        },
        // 先做 HTML 转义，再把关键词包成 <em>，避免 XSS
        highlight(text) {
            if (!text) {
                return '';
            }
            var escaped = String(text)
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#39;');
            var kw = this.lastKeyword;
            if (!kw) {
                return escaped;
            }
            var reg = new RegExp(kw.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'gi');
            return escaped.replace(reg, function (m) {
                return '<em>' + m + '</em>';
            });
        },
        doSearch() {
            var kw = (this.keyword || '').trim();
            if (!kw) {
                return;
            }
            this.lastKeyword = kw;
            this.pageNum = 1;
            this.list = [];
            this.searched = true;
            this.$router.push({ path: '/Search', query: { keyword: kw } });
            this.load();
        },
        load() {
            var that = this;
            that.loading = true;
            searchArticles({
                keyword: that.lastKeyword,
                pageNum: that.pageNum,
                pageSize: that.pageSize
            }).then((response) => {
                that.list = that.list.concat(response.rows || []);
                that.total = response.total || 0;
                if (that.total > that.list.length) {
                    that.pageNum++;
                }
            }).catch(() => {
                that.total = 0;
            }).finally(() => {
                that.loading = false;
            });
        },
        loadMore() {
            if (this.loading || !this.hasMore) {
                return;
            }
            this.load();
        },
        routeChange() {
            var kw = this.$route.query.keyword ? String(this.$route.query.keyword).trim() : '';
            this.keyword = kw;
            if (kw && kw !== this.lastKeyword) {
                this.lastKeyword = kw;
                this.pageNum = 1;
                this.list = [];
                this.searched = true;
                this.load();
            }
        }
    },
    components: {
        'sg-nav': header,
        'yuuu-rightlist': rightlist,
    },
    watch: {
        '$route': 'routeChange'
    },
    created() {
        this.routeChange()
    }
}
</script>

<style>
.searchBox {
    background: var(--surface);
    border-radius: var(--radius, 5px);
    padding: 0 0 20px;
    margin-bottom: 40px;
    overflow: hidden;
}

.search-bar {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 16px 20px;
    border-bottom: 1px solid var(--border);
}

.search-bar i {
    color: var(--text-weak);
}

.search-bar input {
    flex: 1;
    border: 1px solid var(--border);
    border-radius: 999px;
    padding: 8px 14px;
    font-size: 14px;
    color: var(--text);
    background: transparent;
    outline: none;
}

.search-bar input:focus {
    border-color: var(--brand);
}

.search-summary {
    padding: 14px 20px 0;
    margin: 0;
    font-size: 13px;
    color: var(--text-weak);
}

.result-list {
    padding: 10px 20px 0;
}

.result-item {
    padding: 14px 0;
    border-bottom: 1px dashed var(--border);
}

.result-title {
    font-size: 17px;
    font-weight: 700;
    margin: 0 0 6px;
}

.result-title a {
    color: var(--text);
    text-decoration: none;
    transition: color .25s var(--ease, ease);
}

.result-title a:hover {
    color: var(--brand);
}

.result-excerpt {
    font-size: 13px;
    line-height: 1.7;
    color: var(--text-weak);
    margin: 0 0 8px;
}

.result-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 14px;
    font-size: 12px;
    color: var(--text-weak);
}

.result-title em,
.result-excerpt em {
    font-style: normal;
    color: var(--brand);
    background: rgba(79, 140, 255, .12);
    border-radius: 3px;
    padding: 0 2px;
}

.search-empty {
    padding: 24px 20px;
    color: var(--text-weak);
    font-size: 14px;
}

@media screen and (max-width: 768px) {
    .search-bar {
        padding: 12px 16px;
    }

    .result-list {
        padding: 8px 16px 0;
    }
}
</style>
