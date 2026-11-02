<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-click="loadList">
      <el-tab-pane label="诗句池" name="quote" />
      <el-tab-pane label="背景图池" name="bg" />
    </el-tabs>

    <!-- 添加行 -->
    <div class="add-row">
      <el-input v-if="activeTab === 'quote'" v-model="quoteText" placeholder="输入诗句，如：且将新火试新茶，诗酒趁年华"
        maxlength="200" style="width: 420px; margin-right: 10px" />
      <el-upload v-else class="upload-inline" :show-file-list="false" :http-request="doUpload"
        accept="image/png,image/jpg,image/jpeg,image/webp">
        <el-button size="small" type="primary">{{ bgText ? '重新选择图片' : '选择图片' }}</el-button>
      </el-upload>
      <el-input v-if="activeTab === 'bg'" v-model="bgText" placeholder="或直接填写图片地址" style="width: 360px; margin: 0 10px" />
      <img v-if="activeTab === 'bg' && bgText" :src="bgText" class="bg-preview" />
      <el-button type="primary" icon="el-icon-plus" :disabled="!canAdd" @click="addItem">添加到池</el-button>
    </div>

    <el-table v-loading="loading" :data="list" border fit highlight-current-row style="width: 100%; margin-top: 15px">
      <el-table-column align="center" label="ID" width="70">
        <template slot-scope="scope">{{ scope.row.id }}</template>
      </el-table-column>
      <el-table-column v-if="activeTab === 'bg'" align="center" label="预览" width="200">
        <template slot-scope="scope">
          <img :src="scope.row.content" class="bg-thumb" />
        </template>
      </el-table-column>
      <el-table-column :label="activeTab === 'quote' ? '诗句' : '图片地址'" min-width="260">
        <template slot-scope="scope">
          <span v-if="editingId !== scope.row.id">{{ scope.row.content }}</span>
          <el-input v-else v-model="editText" size="small" maxlength="500" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="small">
            {{ scope.row.status === '0' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="220">
        <template slot-scope="scope">
          <template v-if="editingId === scope.row.id">
            <el-button type="text" size="mini" @click="saveEdit(scope.row)">保存</el-button>
            <el-button type="text" size="mini" @click="cancelEdit">取消</el-button>
          </template>
          <template v-else>
            <el-button type="text" size="mini" @click="startEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="mini" @click="toggleItem(scope.row)">{{ scope.row.status === '0' ? '停用' : '启用' }}</el-button>
            <el-button type="text" size="mini" style="color: #f56c6c" @click="deleteItem(scope.row)">删除</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <div class="form-tip">
      说明：启用的条目会进入随机池，每次有人访问首页，都会随机展示其中一条诗句与一张背景图；停用即移出随机池。
    </div>
  </div>
</template>

<script>
import { headlineList, addHeadline, updateHeadline, toggleHeadline, deleteHeadline, uploadImg } from '@/api/content/headline'

export default {
  name: 'HeadlineManage',
  data() {
    return {
      activeTab: 'quote',
      list: [],
      loading: false,
      quoteText: '',
      bgText: '',
      editingId: null,
      editText: ''
    }
  },
  computed: {
    canAdd() {
      return this.activeTab === 'quote' ? !!this.quoteText.trim() : !!this.bgText.trim()
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    loadList() {
      const self = this
      self.loading = true
      self.editingId = null
      const type = self.activeTab === 'quote' ? '1' : '2'
      headlineList(type).then(function (res) {
        self.list = res || []
      }).catch(function () {
        self.list = []
      }).finally(function () {
        self.loading = false
      })
    },
    addItem() {
      const self = this
      const content = (self.activeTab === 'quote' ? self.quoteText : self.bgText).trim()
      if (!content) return
      addHeadline({ type: self.activeTab === 'quote' ? '1' : '2', content: content, status: '0' }).then(function () {
        self.$modal.msgSuccess('已加入随机池')
        self.quoteText = ''
        self.bgText = ''
        self.loadList()
      })
    },
    startEdit(row) {
      this.editingId = row.id
      this.editText = row.content
    },
    cancelEdit() {
      this.editingId = null
      this.editText = ''
    },
    saveEdit(row) {
      const self = this
      if (!self.editText.trim()) return
      updateHeadline({ id: row.id, content: self.editText.trim() }).then(function () {
        row.content = self.editText.trim()
        self.cancelEdit()
        self.$modal.msgSuccess('已更新')
      })
    },
    toggleItem(row) {
      const self = this
      toggleHeadline(row.id).then(function () {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    deleteItem(row) {
      const self = this
      self.$confirm('确认从随机池删除？', '提示', { type: 'warning' }).then(function () {
        deleteHeadline(row.id).then(function () {
          self.loadList()
        })
      }).catch(function () { })
    },
    doUpload(img) {
      const self = this
      uploadImg(img.file).then(function (response) {
        self.bgText = response
        self.$modal.msgSuccess('上传成功')
      })
    }
  }
}
</script>

<style scoped>
.add-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.upload-inline {
  display: inline-block;
  margin-right: 10px;
}

.bg-preview {
  height: 44px;
  border-radius: 6px;
  margin-right: 10px;
  border: 1px solid #ebeef5;
}

.bg-thumb {
  width: 160px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.form-tip {
  margin-top: 15px;
  font-size: 13px;
  color: #909399;
}
</style>
