<template>
  <div class="app-container">
    <div class="filter-bar">
      <el-select v-model="status" placeholder="状态" clearable size="small" style="width: 140px" @change="getList">
        <el-option label="订阅中" value="0" />
        <el-option label="已退订" value="1" />
      </el-select>
      <el-button size="small" type="primary" @click="getList">查询</el-button>
      <span class="total-tip">共 {{ total }} 位订阅者</span>
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column align="center" label="序号" width="70">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column align="center" label="邮箱" min-width="180">
        <template slot-scope="scope">{{ scope.row.email }}</template>
      </el-table-column>
      <el-table-column align="center" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="mini">
            {{ scope.row.status === '0' ? '订阅中' : '已退订' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="来源" width="100">
        <template slot-scope="scope">{{ scope.row.source || '-' }}</template>
      </el-table-column>
      <el-table-column align="center" label="订阅时间" width="180">
        <template slot-scope="scope">{{ scope.row.createTime || '-' }}</template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="100">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-show="total > 0" style="margin-top: 15px; text-align: right" background
      layout="total, prev, pager, next" :total="total" :current-page.sync="pageNum" :page-size="pageSize"
      @current-change="getList" />
  </div>
</template>

<script>
import { subscribeList, deleteSubscribe } from '@/api/content/subscribe'

export default {
  name: 'Subscribe',
  data() {
    return {
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      status: '',
      listLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      const self = this
      self.listLoading = true
      subscribeList({
        pageNum: self.pageNum,
        pageSize: self.pageSize,
        status: self.status
      }).then(function (res) {
        self.list = (res && res.rows) || []
        self.total = (res && res.total) || 0
      }).catch(function () {
        self.list = []
        self.total = 0
      }).finally(function () {
        self.listLoading = false
      })
    },
    handleDelete(row) {
      const self = this
      this.$confirm('确认删除该订阅者？', '提示', { type: 'warning' }).then(function () {
        deleteSubscribe([row.id]).then(function () {
          self.$message.success('删除成功')
          self.getList()
        })
      }).catch(function () { })
    }
  }
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.total-tip {
  margin-left: auto;
  font-size: 13px;
  color: #909399;
}
</style>
