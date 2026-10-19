<template>
  <div class="bck">
    <div class="stats-wrap">
      <div class="stats-cards">
        <div class="stat-card">
          <span class="stat-label">文章</span><b class="stat-num">{{ overview.articleCount || 0 }}</b>
        </div>
        <div class="stat-card">
          <span class="stat-label">分类</span><b class="stat-num">{{ overview.categoryCount || 0 }}</b>
        </div>
        <div class="stat-card">
          <span class="stat-label">标签</span><b class="stat-num">{{ overview.tagCount || 0 }}</b>
        </div>
        <div class="stat-card">
          <span class="stat-label">评论</span><b class="stat-num">{{ overview.commentCount || 0 }}</b>
        </div>
        <div class="stat-card">
          <span class="stat-label">总浏览</span><b class="stat-num">{{ overview.viewCount || 0 }}</b>
        </div>
        <div class="stat-card">
          <span class="stat-label">总点赞</span><b class="stat-num">{{ overview.likeCount || 0 }}</b>
        </div>
      </div>
      <div class="stats-charts">
        <div class="chart-box">
          <div ref="categoryChart" class="chart"></div>
        </div>
        <div class="chart-box">
          <div ref="topChart" class="chart"></div>
        </div>
      </div>
    </div>

    <div class="dashboard-container" id="#login-img">
      <div class="dashboard-text" style="font-weight: bolder">
        YuuuBlog管理系统
      </div>
      <div class="content">
        <div>
          纳尼？你怎么找到这里来的？！？
        </div>
        <div>
          既然被你发现了，那就尽情探讨吧！有什么问题尽管发送到我的邮箱：864326354@qq.com
        </div>
        <br>
        项目地址：<a href="https://gitee.com/yuuu-zehua">https://gitee.com/yuuu-zehua</a><br>
        前台地址：<a href="https://www.yuuu.online">www.yuuu.online</a>
        <br>
        <br>
        <div>
          项目简介：
          项目采用前后端分离的模式，前端使用Vue框架，后端使用SpringBoot框架。<br><br>
          前台主要有：
          <ul>
            <li>用户功能：用户注册；用户登录，支持Github第三方登录；修改密码</li>
            <li>文章功能：查看文章；点赞文章；评论文章；下载文章</li>
            <li>友链功能：申请友链；查看友链；删除友链</li>
            <li>YuuuChat：聊天机器人</li>
            <li>其他功能：登录信息展示；网络打赏</li>
            <li>注：部分功能需科学上网</li>
          </ul>
          后台主要有：
          <ul>
            <li>用户管理：用户新增；用户修改；用户删除；角色分配</li>
            <li>文章管理：文章新增；文章修改；文章删除；支持MarkDown格式的文本样式</li>
            <li>角色管理：角色新增；角色修改；角色授权等</li>
            <li>菜单管理：新增菜单；修改菜单；删除菜单等</li>
            <li>其他功能：其他功能的CRUD</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getDashboardStats } from '@/api/dashboard'
import echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      overview: {},
      categoryStats: [],
      topArticles: [],
      charts: []
    }
  },
  computed: {
    ...mapGetters([
      'name'
    ])
  },
  mounted() {
    this.loadStats()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    this.disposeCharts()
    window.removeEventListener('resize', this.resizeCharts)
  },
  methods: {
    loadStats() {
      const self = this
      getDashboardStats().then(function (res) {
        self.overview = (res && res.overview) || {}
        self.categoryStats = (res && res.categoryStats) || []
        self.topArticles = (res && res.topArticles) || []
        self.$nextTick(self.renderCharts)
      }).catch(function () {
        // 统计失败不影响首页其它内容展示
      })
    },
    renderCharts() {
      this.renderCategory()
      this.renderTop()
    },
    renderCategory() {
      if (!this.$refs.categoryChart || !this.categoryStats.length) return
      const chart = echarts.init(this.$refs.categoryChart)
      chart.setOption({
        title: { text: '分类文章分布', textStyle: { fontSize: 14 } },
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: ['35%', '58%'],
          data: this.categoryStats.map(function (i) { return { name: i.name, value: i.count } })
        }]
      })
      this.charts.push(chart)
    },
    renderTop() {
      if (!this.$refs.topChart || !this.topArticles.length) return
      const chart = echarts.init(this.$refs.topChart)
      const data = this.topArticles.slice().reverse()
      chart.setOption({
        title: { text: '热门文章 TOP10', textStyle: { fontSize: 14 } },
        tooltip: { trigger: 'axis' },
        grid: { left: 170, right: 40, top: 50 },
        xAxis: { type: 'value' },
        yAxis: {
          type: 'category',
          data: data.map(function (i) { return i.title.length > 20 ? i.title.slice(0, 20) + '…' : i.title })
        },
        series: [{
          type: 'bar',
          data: data.map(function (i) { return i.viewCount }),
          itemStyle: { color: '#4F8CFF' }
        }]
      })
      this.charts.push(chart)
    },
    resizeCharts() {
      this.charts.forEach(function (c) { c.resize() })
    },
    disposeCharts() {
      this.charts.forEach(function (c) { c.dispose() })
      this.charts = []
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }

  &-text {
    font-family: 'Times New Roman', Times, serif;
    font-size: 30px;
    line-height: 46px;
  }
}

@font-face {
  font-family: "Content";
  src: url("../../assets/font/KeLaiYinLanDeYeWan-2.ttf");
}
.content {
  font-family: "Content";
  font-size: large;
}

.bck {
  background-image: url("../../assets/images/backgroundImg.jpg");
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;

  /* 设置透明度为半透明 */
  position: fixed;

  width: 100%;
  height: 100%;
  overflow: auto;
}

/* ---------- 数据概览 ---------- */
.stats-wrap {
  max-width: 1180px;
  margin: 0 auto;
  padding: 20px 20px 0;
}

.stats-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.stat-card {
  flex: 1;
  min-width: 120px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 14px 16px;
  border-radius: 10px;
  background: rgba(255, 255, 255, .92);
  box-shadow: 0 6px 18px rgba(20, 30, 60, .12);
}

.stat-label {
  font-size: 12px;
  color: #64708a;
}

.stat-num {
  font-size: 22px;
  color: #1d2433;
  font-weight: 700;
}

.stats-charts {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
}

.chart-box {
  flex: 1;
  min-width: 320px;
  padding: 10px;
  border-radius: 10px;
  background: rgba(255, 255, 255, .92);
  box-shadow: 0 6px 18px rgba(20, 30, 60, .12);
}

.chart {
  width: 100%;
  height: 300px;
}
</style>
