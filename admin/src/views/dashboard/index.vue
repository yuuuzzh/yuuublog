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
        <div class="stat-card">
          <span class="stat-label">累计访客</span><b class="stat-num">{{ visit.total || 0 }}</b>
        </div>
        <div class="stat-card">
          <span class="stat-label">今日访客</span><b class="stat-num">{{ visit.today || 0 }}</b>
        </div>
      </div>
      <div class="stats-charts">
        <div class="chart-box">
          <div ref="categoryChart" class="chart"></div>
        </div>
        <div class="chart-box">
          <div ref="topChart" class="chart"></div>
        </div>
        <div class="chart-box">
          <div ref="trendChart" class="chart"></div>
        </div>
        <div class="chart-box">
          <div ref="osChart" class="chart"></div>
        </div>
        <div class="chart-box chart-box-wide">
          <div ref="dailyChart" class="chart chart-tall"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getDashboardStats } from '@/api/dashboard'
import echarts from 'echarts'

// 统一图表配色（柔和蓝紫系，参考主流后台风格）
const CHART_COLORS = ['#5B8FF9', '#61DDAA', '#F6BD16', '#7262FD', '#78D3F8', '#9661BC', '#F6903D', '#008685', '#FF9D4D', '#5D7092']
// 卡片内标题统一样式
const TITLE_STYLE = { fontSize: 14, color: '#3d4759', fontWeight: 600 }

export default {
  name: 'Dashboard',
  data() {
    return {
      overview: {},
      categoryStats: [],
      topArticles: [],
      visit: {},
      daily: [],
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
        self.visit = (res && res.visit) || {}
        self.daily = (res && res.daily) || []
        self.$nextTick(self.renderCharts)
      }).catch(function () {
        // 统计失败不影响首页其它内容展示
      })
    },
    renderCharts() {
      this.renderCategory()
      this.renderTop()
      this.renderTrend()
      this.renderOs()
      this.renderDaily()
    },
    // GitHub 提交记录风格的发布热力图（近一年按天统计文章发布数）
    renderDaily() {
      if (!this.$refs.dailyChart || !this.daily.length) return
      const chart = echarts.init(this.$refs.dailyChart)
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 364)
      const fmt = function (d) {
        const pad = function (n) { return (n < 10 ? '0' : '') + n }
        return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate())
      }
      let max = 0
      this.daily.forEach(function (i) { if (i.count > max) max = i.count })
      chart.setOption({
        title: { text: '文章发布热力图（近一年）', textStyle: TITLE_STYLE },
        tooltip: {
          formatter: function (p) {
            return p.value[0] + '：发布 ' + (p.value[1] || 0) + ' 篇'
          }
        },
        visualMap: {
          min: 0,
          max: Math.max(max, 1),
          calculable: false,
          orient: 'horizontal',
          left: 'center',
          bottom: 0,
          itemWidth: 12,
          itemHeight: 12,
          textStyle: { fontSize: 11, color: '#64708a' },
          inRange: { color: ['#EDF1F7', '#B8CCF5', '#5B8FF9', '#2E5FD9'] }
        },
        calendar: {
          top: 50,
          left: 60,
          right: 16,
          cellSize: ['auto', 15],
          range: [fmt(start), fmt(end)],
          itemStyle: { color: '#f0f2f5', borderColor: '#fff' },
          splitLine: { show: false },
          yearLabel: { show: false },
          monthLabel: { nameMap: 'cn', fontSize: 11, color: '#64708a' },
          dayLabel: { nameMap: ['日', '一', '二', '三', '四', '五', '六'], fontSize: 11, color: '#64708a' }
        },
        series: [{
          type: 'heatmap',
          coordinateSystem: 'calendar',
          data: this.daily.map(function (i) { return [i.day, i.count] })
        }]
      })
      this.charts.push(chart)
    },
    renderTrend() {
      if (!this.$refs.trendChart || !this.visit.trend || !this.visit.trend.length) return
      const chart = echarts.init(this.$refs.trendChart)
      const trend = this.visit.trend
      chart.setOption({
        title: { text: '近 30 天访客趋势', textStyle: TITLE_STYLE },
        tooltip: { trigger: 'axis' },
        grid: { left: 50, right: 20, top: 50, bottom: 40 },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: trend.map(function (i) { return i.day }),
          axisLabel: { interval: 4, fontSize: 11, color: '#64708a' },
          axisLine: { lineStyle: { color: '#e6e9f0' } }
        },
        yAxis: {
          type: 'value',
          minInterval: 1,
          axisLabel: { fontSize: 11, color: '#64708a' },
          splitLine: { lineStyle: { color: '#f0f2f5' } }
        },
        series: [{
          type: 'line',
          smooth: true,
          showSymbol: false,
          itemStyle: { color: '#5B8FF9' },
          lineStyle: { width: 2.5, color: '#5B8FF9' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(91,143,249,.32)' },
              { offset: 1, color: 'rgba(91,143,249,0)' }
            ])
          },
          data: trend.map(function (i) { return i.count })
        }]
      })
      this.charts.push(chart)
    },
    renderOs() {
      if (!this.$refs.osChart || !this.visit.os || !this.visit.os.length) return
      const chart = echarts.init(this.$refs.osChart)
      chart.setOption({
        title: { text: '访客系统分布 TOP6', textStyle: TITLE_STYLE },
        color: CHART_COLORS,
        tooltip: { trigger: 'item', formatter: '{b}：{c} 次（{d}%）' },
        legend: { bottom: 0, type: 'scroll', icon: 'circle', itemWidth: 8, itemHeight: 8, textStyle: { fontSize: 11, color: '#64708a' } },
        series: [{
          type: 'pie',
          radius: ['42%', '66%'],
          center: ['50%', '44%'],
          label: { show: false },
          data: this.visit.os.map(function (i) { return { name: i.name, value: i.count } })
        }]
      })
      this.charts.push(chart)
    },
    renderCategory() {
      if (!this.$refs.categoryChart || !this.categoryStats.length) return
      const chart = echarts.init(this.$refs.categoryChart)
      chart.setOption({
        title: { text: '分类文章分布', textStyle: TITLE_STYLE },
        color: CHART_COLORS,
        tooltip: { trigger: 'item', formatter: '{b}：{c} 篇（{d}%）' },
        legend: { bottom: 0, type: 'scroll', icon: 'circle', itemWidth: 8, itemHeight: 8, textStyle: { fontSize: 11, color: '#64708a' } },
        series: [{
          type: 'pie',
          radius: ['42%', '66%'],
          center: ['50%', '44%'],
          avoidLabelOverlap: true,
          label: { show: false },
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
        title: { text: '热门文章 TOP10', textStyle: TITLE_STYLE },
        tooltip: { trigger: 'axis', axisPointer: { type: 'none' } },
        grid: { left: 10, right: 30, top: 50, bottom: 10, containLabel: true },
        xAxis: { type: 'value', splitLine: { lineStyle: { color: '#f0f2f5' } } },
        yAxis: {
          type: 'category',
          data: data.map(function (i) { return i.title.length > 14 ? i.title.slice(0, 14) + '…' : i.title }),
          axisTick: { show: false },
          axisLine: { show: false },
          axisLabel: { fontSize: 11, color: '#64708a' }
        },
        series: [{
          type: 'bar',
          barWidth: 10,
          itemStyle: {
            borderRadius: [0, 5, 5, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#5B8FF9' },
              { offset: 1, color: '#7262FD' }
            ])
          },
          data: data.map(function (i) { return i.viewCount })
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

.chart-box-wide {
  flex-basis: 100%;
  min-width: 100%;
}

.chart-tall {
  height: 200px;
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
