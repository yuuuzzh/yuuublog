<!-- 文章列表 -->
<template>
    <el-row class="sharelistBox">

        <!-- 首屏骨架屏 -->
        <template v-if="loading && articleList.length === 0">
            <el-col :span="24" v-for="n in 3" :key="'sk' + n">
                <div class="article-card skeleton-card">
                    <div class="sk-cover skeleton"></div>
                    <div class="card-body">
                        <div class="sk-line skeleton" style="width:38%"></div>
                        <div class="sk-line skeleton" style="width:72%;height:20px;margin:14px 0"></div>
                        <div class="sk-line skeleton" style="width:100%"></div>
                        <div class="sk-line skeleton" style="width:88%"></div>
                    </div>
                </div>
            </el-col>
        </template>

        <el-col :span="24" v-for="(item,index) in articleList" :key="'article'+index">
            <article class="article-card hover-lift" v-reveal="Math.min(index,5)*70">
                <a class="card-cover" :href="'#/DetailArticle?aid='+item.id" target="_blank">
                    <img v-if="item.thumbnail" :src="item.thumbnail" :alt="item.title" loading="lazy">
                    <span v-else class="cover-fallback" :style="coverStyle(item.title)">
                        <i>{{ coverInitial(item.title) }}</i>
                    </span>
                    <span class="cover-date">
                        <b v-html="showInitDate(item.createTime,'month')+'月'"></b>
                        <i v-html="showInitDate(item.createTime,'date')"></i>
                    </span>
                </a>
                <div class="card-body">
                    <a class="chip cat" :href="'#/Share?classId='+item.categoryId">{{item.categoryName}}</a>
                    <h2 class="card-title">
                        <a :href="'#/DetailArticle?aid='+item.id" target="_blank">{{item.title}}</a>
                    </h2>
                    <p class="card-summary">{{item.summary}}</p>
                    <div class="card-meta">
                        <span><i class="fa fa-fw fa-clock-o"></i>{{showInitDate(item.createTime,'all')}}</span>
                        <span><i class="fa fa-fw fa-eye"></i>{{item.viewCount}} 次围观</span>
                        <span><i class="fa fa-fw fa-heart"></i>{{ item.likes }} 次点赞</span>
                    </div>
                    <a class="read-more" :href="'#/DetailArticle?aid='+item.id" target="_blank">
                        阅读全文
                        <i class="fa fa-arrow-right"></i>
                    </a>
                </div>
            </article>
        </el-col>

        <el-col class="viewmore">
            <a v-show="hasMore" class="more-btn" href="javascript:void(0);" @click="addMoreFun">
                <span v-if="!loading">点击加载更多</span>
                <span v-else><i class="fa fa-spinner fa-spin"></i> 加载中</span>
            </a>
            <a v-show="!hasMore" class="more-btn is-end" href="javascript:void(0);">已经到底啦 ~</a>
        </el-col>
    </el-row>
</template>

<script>
import { initDate } from '../utils/server.js'
import { articleList } from '../api/article'
import { coverStyle, coverInitial } from '../utils/cover.js'
export default {
    name: 'Share',
    data() { //选项 / 数据
        return {
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                categoryId: 0,
                tagId: 0
            },
            articleList: [],
            hasMore: true,
            loading: false
        }
    },

    methods: { //事件处理器
        showInitDate: function (oldDate, full) {
            return initDate(oldDate, full)
        },
        coverStyle: function (title) {
            return coverStyle(title)
        },
        coverInitial: function (title) {
            return coverInitial(title)
        },
        getList() {
            this.loading = true
            articleList(this.queryParams).then((response) => {
                this.articleList = this.articleList.concat(response.rows)
                if (response.total <= this.articleList.length) {
                    this.hasMore = false
                } else {
                    this.hasMore = true
                    this.queryParams.pageNum++
                }
            }).catch(() => {
                this.hasMore = false
            }).finally(() => {
                this.loading = false
            })
        },
        showSearchShowList: function (initData) {//展示数据
            if (initData) {
                this.articleList = []
            }
            this.getList()
        },
        addMoreFun: function () {//查看更多
            this.showSearchShowList(false);
        },
        routeChange: function () {
            var that = this;
            this.queryParams.categoryId = (that.$route.query.classId == undefined ? 0 : parseInt(that.$route.query.classId));//获取传参的classId
            this.queryParams.tagId = (that.$route.query.tagId == undefined ? 0 : parseInt(that.$route.query.tagId));//按标签筛选
            this.showSearchShowList(true);
        }
    },
    components: { //定义组件

    },
    watch: {
        // 如果路由有变化，会再次执行该方法
        '$route': 'routeChange',
        '$store.state.keywords': 'routeChange'
    },
    created() { //生命周期函数
        var that = this;
        that.routeChange();
    }
}
</script>

<style scoped>
.sharelistBox {
    font-size: 15px;
}

/* ---------- 卡片 ---------- */
.article-card {
    position: relative;
    display: flex;
    gap: 20px;
    margin-bottom: 22px;
    padding: 16px;
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: var(--radius);
    box-shadow: var(--shadow-1);
    overflow: hidden;
}

/* 左侧渐变装饰条 */
.article-card::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 3px;
    background: linear-gradient(180deg, var(--brand), var(--brand-2), var(--accent));
    opacity: 0;
    transition: opacity .35s var(--ease);
}

.article-card:hover::before {
    opacity: 1;
}

.card-cover {
    position: relative;
    flex: 0 0 240px;
    height: 156px;
    border-radius: var(--radius-sm);
    overflow: hidden;
    background: var(--bg-soft);
}

.card-cover img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    transition: transform .6s var(--ease);
}

.article-card:hover .card-cover img {
    transform: scale(1.07);
}

/* 无封面时的哈希渐变兜底图 */
.cover-fallback {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    transition: transform .6s var(--ease);

    i {
        font-style: normal;
        font-size: 52px;
        font-weight: 800;
        color: rgba(255, 255, 255, .92);
        text-shadow: 0 6px 18px rgba(0, 0, 0, .18);
        letter-spacing: 2px;
    }
}

.article-card:hover .cover-fallback {
    transform: scale(1.06);
}

.cover-date {
    position: absolute;
    left: 10px;
    top: 10px;
    padding: 6px 10px;
    border-radius: 10px;
    color: #fff;
    line-height: 1;
    text-align: center;
    background: rgba(20, 24, 40, .55);
    -webkit-backdrop-filter: blur(6px);
    backdrop-filter: blur(6px);
    font-size: 12px;
}

.cover-date b {
    display: block;
    font-size: 13px;
    font-weight: 600;
}

.cover-date i {
    display: block;
    font-style: normal;
    font-size: 17px;
    font-weight: 700;
    margin-top: 2px;
}

.card-body {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
}

.chip.cat {
    align-self: flex-start;
    display: inline-block;
    padding: 3px 12px;
    margin-bottom: 8px;
    font-size: 12px;
    border-radius: 999px;
    color: var(--brand);
    background: color-mix(in srgb, var(--brand) 12%, transparent);
    border: 1px solid color-mix(in srgb, var(--brand) 26%, transparent);
    transition: transform .25s var(--ease), background-color .25s var(--ease);
}

.chip.cat:hover {
    transform: translateY(-2px);
    background: color-mix(in srgb, var(--brand) 20%, transparent);
}

.card-title {
    margin: 0 0 8px;
    font-size: 19px;
    line-height: 1.4;
    font-weight: 700;
}

.card-title a {
    color: var(--text);
    display: inline-block;
    position: relative;
    transition: color .25s var(--ease);
}

/* 标题下划动画 */
.card-title a::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: -2px;
    width: 0;
    height: 2px;
    border-radius: 2px;
    background: linear-gradient(90deg, var(--brand), var(--accent));
    transition: width .35s var(--ease);
}

.article-card:hover .card-title a::after {
    width: 100%;
}

.card-summary {
    margin: 0 0 12px;
    color: var(--text-weak);
    font-size: 14px;
    line-height: 1.8;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.card-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 14px;
    font-size: 12.5px;
    color: var(--text-weak);
    margin-top: auto;
}

.card-meta i {
    margin-right: 4px;
}

.read-more {
    align-self: flex-start;
    margin-top: 12px;
    padding: 7px 16px;
    font-size: 13px;
    border-radius: 999px;
    color: #fff;
    background: linear-gradient(120deg, var(--brand), var(--brand-2));
    box-shadow: 0 6px 18px color-mix(in srgb, var(--brand) 30%, transparent);
    transition: transform .3s var(--ease), box-shadow .3s var(--ease);
}

.read-more:hover {
    transform: translateX(4px);
    box-shadow: 0 10px 26px color-mix(in srgb, var(--brand) 42%, transparent);
}

.read-more .fa {
    margin-left: 6px;
    transition: transform .3s var(--ease);
}

.read-more:hover .fa {
    transform: translateX(3px);
}

/* ---------- 骨架屏 ---------- */
.skeleton-card {
    display: flex;
    gap: 20px;
}

.sk-cover {
    flex: 0 0 240px;
    height: 156px;
    border-radius: var(--radius-sm);
}

.sk-line {
    height: 14px;
    margin-bottom: 10px;
}

/* ---------- 加载更多 ---------- */
.viewmore {
    text-align: center;
    margin: 26px 0 40px;
}

.more-btn {
    display: inline-block;
    padding: 10px 30px;
    border-radius: 999px;
    font-size: 14px;
    color: var(--text);
    background: var(--surface);
    border: 1px solid var(--border);
    box-shadow: var(--shadow-1);
    transition: transform .3s var(--ease), color .3s var(--ease), border-color .3s var(--ease);
}

.more-btn:hover {
    transform: translateY(-3px);
    color: var(--brand);
    border-color: color-mix(in srgb, var(--brand) 45%, transparent);
}

.more-btn.is-end {
    color: var(--text-weak);
    cursor: default;
}

.more-btn.is-end:hover {
    transform: none;
    color: var(--text-weak);
    border-color: var(--border);
}

/* ---------- 移动端 ---------- */
@media (max-width: 768px) {
    .article-card {
        flex-direction: column;
        gap: 12px;
        padding: 12px;
    }

    .card-cover {
        flex: none;
        width: 100%;
        height: 180px;
    }

    .card-title {
        font-size: 17px;
    }

    .skeleton-card {
        flex-direction: column;
    }

    .sk-cover {
        flex: none;
        width: 100%;
        height: 160px;
    }
}
</style>
