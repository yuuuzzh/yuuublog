<!-- 文章归档 -->
<template>
    <div>
        <sg-nav></sg-nav>
        <div class="container">
            <el-row :gutter="30">
                <el-col :sm="24" :md="16" style="transition:all .5s ease-out;margin-bottom:30px;">
                    <div class="archiveBox tcommonBox">
                        <header>
                            <h1>文章归档<span class="archive-total">共 {{ total }} 篇</span></h1>
                        </header>
                        <section>
                            <div class="archive-group" v-for="(group,index) in archiveList" :key="'g'+index">
                                <h2 class="archive-month"><i class="fa fa-fw fa-calendar"></i>{{ group.yearMonth }}</h2>
                                <ul class="archive-list">
                                    <li v-for="(item,i) in group.articles" :key="'a'+i">
                                        <span class="archive-day">{{ showInitDate(item.createTime,'date') }}</span>
                                        <a :href="'/DetailArticle?aid='+item.id" target="_blank">{{ item.title }}</a>
                                    </li>
                                </ul>
                            </div>
                            <p class="archive-empty" v-if="!archiveList.length">还没有发布的文章～</p>
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
import tagcloud from '../components/tagcloud.vue'
import { archives } from '../api/article.js'
import { initDate } from '../utils/server.js'

export default {
    name: 'Archives',
    data() {
        return {
            archiveList: []
        }
    },
    computed: {
        total() {
            var n = 0;
            for (var i = 0; i < this.archiveList.length; i++) {
                n += this.archiveList[i].total || 0;
            }
            return n;
        }
    },
    methods: {
        showInitDate(oldDate, full) {
            return initDate(oldDate, full)
        },
        getList() {
            var that = this;
            archives().then((response) => {
                that.archiveList = response ? response : [];
            }).catch(() => {
                that.archiveList = [];
            });
        }
    },
    components: {
        'sg-nav': header,
        'yuuu-tagcloud': tagcloud,
        'yuuu-rightlist': rightlist,
    },
    created() {
        this.getList()
    }
}
</script>

<style>
.archiveBox {
    background: var(--surface);
    border-radius: var(--radius, 5px);
    padding: 0 0 15px;
    margin-bottom: 40px;
    overflow: hidden;
}

.archiveBox header h1 {
    font-size: 22px;
    font-weight: bold;
    color: var(--text);
    padding: 18px 30px;
    border-bottom: 1px solid var(--border);
}

.archive-total {
    float: right;
    font-size: 13px;
    font-weight: normal;
    color: var(--text-weak);
}

.archive-group {
    padding: 10px 30px 0;
}

.archive-month {
    font-size: 16px;
    font-weight: bold;
    color: var(--text);
    margin: 14px 0 8px;
}

.archive-list {
    padding: 0;
}

.archive-list li {
    display: flex;
    align-items: baseline;
    gap: 10px;
    padding: 6px 0;
    border-bottom: 1px dashed var(--border);
    font-size: 14px;
}

.archive-day {
    width: 28px;
    flex-shrink: 0;
    color: var(--text-weak);
    font-size: 12px;
}

.archive-list a {
    color: var(--text);
    text-decoration: none;
    transition: color .25s var(--ease, ease);
}

.archive-list a:hover {
    color: var(--brand);
}

.archive-empty {
    padding: 20px 30px;
    color: var(--text-weak);
    font-size: 14px;
}

@media screen and (max-width: 768px) {
    .archiveBox header h1 {
        padding: 14px 18px;
        font-size: 19px;
    }

    .archive-group {
        padding: 8px 18px 0;
    }
}
</style>
