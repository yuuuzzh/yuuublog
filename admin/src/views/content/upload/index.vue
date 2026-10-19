<template>
  <div class="app-container">
    <div class="filter-bar">
      <el-button size="small" type="primary" @click="getList">刷新</el-button>
      <el-button v-if="marker" size="small" @click="loadMore">加载更多</el-button>
      <span class="total-tip">当前已加载 {{ list.length }} 个文件</span>
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column align="center" label="预览" width="110">
        <template slot-scope="scope">
          <img :src="scope.row.url" class="thumb" alt="预览" />
        </template>
      </el-table-column>
      <el-table-column label="路径 key" min-width="220">
        <template slot-scope="scope">
          <span class="file-key">{{ scope.row.key }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="大小" width="110">
        <template slot-scope="scope">{{ formatSize(scope.row.size) }}</template>
      </el-table-column>
      <el-table-column align="center" label="上传时间" width="170">
        <template slot-scope="scope">{{ scope.row.time || '-' }}</template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="140">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="copyUrl(scope.row.url)">复制链接</el-button>
          <el-button type="text" size="mini" style="color: #f56c6c" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { uploadedList, deleteUploaded } from '@/api/content/upload'

export default {
  name: 'UploadManage',
  data() {
    return {
      list: [],
      marker: '',
      listLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      const self = this
      self.list = []
      self.marker = ''
      self.requestList('')
    },
    loadMore() {
      this.requestList(this.marker)
    },
    requestList(marker) {
      const self = this
      self.listLoading = true
      uploadedList({ marker: marker || '', limit: 50 }).then(function (res) {
        if (res && res.items) {
          self.list = self.list.concat(res.items)
        }
        self.marker = (res && res.marker) || ''
      }).catch(function () { }).finally(function () {
        self.listLoading = false
      })
    },
    formatSize(size) {
      if (!size) return '-'
      const kb = size / 1024
      if (kb < 1024) return kb.toFixed(1) + ' KB'
      return (kb / 1024).toFixed(2) + ' MB'
    },
    copyUrl(url) {
      const input = document.createElement('input')
      input.value = url
      document.body.appendChild(input)
      input.select()
      try {
        document.execCommand('copy')
        this.$message.success('链接已复制')
      } catch (e) {
        this.$message.info(url)
      }
      document.body.removeChild(input)
    },
    handleDelete(row) {
      const self = this
      this.$confirm('确认删除该文件？已引用该图片的文章会变成死链。', '提示', { type: 'warning' }).then(function () {
        deleteUploaded(row.key).then(function () {
          self.$message.success('删除成功')
          self.list = self.list.filter(function (item) {
            return item.key !== row.key
          })
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

.thumb {
  width: 72px;
  height: 48px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.file-key {
  word-break: break-all;
  font-size: 12px;
}
</style>
