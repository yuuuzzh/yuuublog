<!-- 我的收藏 -->
<template>
    <div>
        <sg-nav></sg-nav>
        <div class="container">
            <el-row :gutter="30">
                <el-col :sm="24" :md="16" style="transition:all .5s ease-out;margin-bottom:30px;">
                    <div class="favBox tcommonBox">
                        <header>
                            <h1>我的收藏<span class="fav-total">共 {{ total }} 篇</span></h1>
                        </header>
                        <section>
                            <ul class="fav-list" v-if="list.length">
                                <li v-for="(item,index) in list" :key="'f'+index">
                                    <a class="fav-title" :href="'/DetailArticle?aid=' + item.articleId" target="_blank">
                                        {{ item.title }}
                                    </a>
                                    <p class="fav-summary">{{ item.summary }}</p>
                                    <div class="fav-meta">
                                        <span><i class="fa fa-fw fa-eye"></i>{{ item.viewCount }} 次围观</span>
                                        <span><i class="fa fa-fw fa-clock-o"></i>{{ showInitDate(item.createTime,'all') }} 收藏</span>
                                        <a class="fav-remove" href="javascript:void(0);" @click="remove(item)">取消收藏</a>
                                    </div>
                                </li>
                            </ul>
                            <p class="fav-empty" v-if="!list.length && !loading">还没有收藏任何文章～</p>
                            <el-col class="viewmore">
                                <a v-show="hasMore" class="more-btn" href="javascript:void(0);" @click="loadMore">
                                    <span v-if="!loading">加载更多</span>
                                    <span v-else><i class="fa fa-spinner fa-spin"></i> 加载中</span>
                                </a>
                            </el-col>
                        </section>
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
import { favoriteList, toggleFavorite } from '../api/favorite.js'
import { initDate } from '../utils/server.js'

export default {
    name: 'Favorite',
    data() {
        return {
            list: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            loading: false
        }
    },
    computed: {
        hasMore() {
            return this.total > this.list.length;
        }
    },
    methods: {
        showInitDate(oldDate, full) {
            return initDate(oldDate, full)
        },
        load() {
            var that = this;
            that.loading = true;
            favoriteList({ pageNum: that.pageNum, pageSize: that.pageSize }).then((response) => {
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
        remove(item) {
            var that = this;
            toggleFavorite(item.articleId).then(() => {
                that.list = that.list.filter(function (i) {
                    return i.articleId !== item.articleId;
                });
                that.total = Math.max(0, that.total - 1);
                that.$message && that.$message.success('已取消收藏');
            }).catch(() => { });
        }
    },
    components: {
        'sg-nav': header,
        'yuuu-rightlist': rightlist,
    },
    created() {
        this.load()
    }
}
</script>

<style>
.favBox {
    background: var(--surface);
    border-radius: var(--radius, 5px);
    padding: 0 0 15px;
    margin-bottom: 40px;
    overflow: hidden;
}

.favBox header h1 {
    font-size: 22px;
    font-weight: bold;
    color: var(--text);
    padding: 18px 30px;
    border-bottom: 1px solid var(--border);
}

.fav-total {
    float: right;
    font-size: 13px;
    font-weight: normal;
    color: var(--text-weak);
}

.fav-list {
    padding: 10px 30px 0;
}

.fav-list li {
    padding: 14px 0;
    border-bottom: 1px dashed var(--border);
}

.fav-title {
    font-size: 16px;
    font-weight: 700;
    color: var(--text);
    text-decoration: none;
    transition: color .25s var(--ease, ease);
}

.fav-title:hover {
    color: var(--brand);
}

.fav-summary {
    margin: 6px 0 8px;
    font-size: 13px;
    line-height: 1.7;
    color: var(--text-weak);
}

.fav-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 14px;
    font-size: 12px;
    color: var(--text-weak);
}

.fav-remove {
    color: var(--brand);
    text-decoration: none;
}

.fav-empty {
    padding: 24px 30px;
    color: var(--text-weak);
    font-size: 14px;
}

@media screen and (max-width: 768px) {
    .favBox header h1 {
        padding: 14px 18px;
        font-size: 19px;
    }

    .fav-list {
        padding: 8px 18px 0;
    }
}
</style>
