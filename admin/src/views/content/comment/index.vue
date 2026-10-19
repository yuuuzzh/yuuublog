<template>
  <div class="app-container">
    <div class="filter-bar">
      <el-select v-model="status" placeholder="审核状态" clearable size="small" style="width: 130px" @change="getList">
        <el-option label="待审核" value="0" />
        <el-option label="通过" value="1" />
        <el-option label="屏蔽" value="2" />
      </el-select>
      <el-select v-model="type" placeholder="评论类型" clearable size="small" style="width: 130px" @change="getList">
        <el-option label="文章评论" value="0" />
        <el-option label="友链评论" value="1" />
      </el-select>
      <el-input v-model="keyword" placeholder="评论内容关键词" size="small" style="width: 200px" clearable
        @keyup.enter.native="getList" />
      <el-button size="small" type="primary" @click="getList">查询</el-button>
      <span class="total-tip">共 {{ total }} 条</span>
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column align="center" label="序号" width="70">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column label="评论内容" min-width="220">
        <template slot-scope="scope">
          <span class="comment-content">{{ scope.row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column label="所属文章" min-width="160">
        <template slot-scope="scope">{{ scope.row.articleTitle || '-' }}</template>
      </el-table-column>
      <el-table-column align="center" label="评论人" width="120">
        <template slot-scope="scope">{{ scope.row.userName || '-' }}</template>
      </el-table-column>
      <el-table-column align="center" label="状态" width="90">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" size="mini">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="时间" width="160">
        <template slot-scope="scope">{{ scope.row.createTime || '-' }}</template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="160">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status !== '1'" type="text" size="mini" @click="handleAudit(scope.row, '1')">通过
          </el-button>
          <el-button v-if="scope.row.status !== '2'" type="text" size="mini" @click="handleAudit(scope.row, '2')">屏蔽
          </el-button>
          <el-button type="text" size="mini" style="color: #f56c6c" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-show="total > 0" style="margin-top: 15px; text-align: right" background
      layout="total, prev, pager, next" :total="total" :current-page.sync="pageNum" :page-size="pageSize"
      @current-change="getList" />
  </div>
</template>

<script>
import { commentList, auditComment, deleteComment } from '@/api/content/comment'

export default {
  name: 'Comment',
  data() {
    return {
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      status: '',
      type: '',
      keyword: '',
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
      commentList({
        pageNum: self.pageNum,
        pageSize: self.pageSize,
        status: self.status,
        type: self.type,
        keyword: self.keyword
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
    statusText(status) {
      if (status === '0') return '待审核'
      if (status === '2') return '屏蔽'
      return '通过'
    },
    statusTagType(status) {
      if (status === '0') return 'warning'
      if (status === '2') return 'danger'
      return 'success'
    },
    handleAudit(row, status) {
      const self = this
      const text = status === '1' ? '通过' : '屏蔽'
      this.$confirm('确认将该评论设为「' + text + '」？', '提示', { type: 'warning' }).then(function () {
        auditComment([row.id], status).then(function () {
          self.$message.success('操作成功')
          self.getList()
        })
      }).catch(function () { })
    },
    handleDelete(row) {
      const self = this
      this.$confirm('确认删除该评论？删除后不可恢复。', '提示', { type: 'warning' }).then(function () {
        deleteComment([row.id]).then(function () {
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

.comment-content {
  word-break: break-all;
}
</style>
