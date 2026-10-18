<!-- 命令面板：⌘K / Ctrl+K / ? 唤起，搜索文章 + 快捷导航 + 主题切换 -->
<template>
  <div>
    <button class="cmd-trigger glass" type="button" title="搜索与快捷命令（⌘K）" @click="open">
      <i class="fa fa-search"></i><kbd>{{ mac ? '⌘' : 'Ctrl' }} K</kbd>
    </button>

    <transition name="cmd">
      <div class="cmd-mask" v-if="visible" @click.self="close">
        <div class="cmd-panel glass" @keydown.stop>
          <div class="cmd-input">
            <i class="fa fa-search"></i>
            <input ref="input" v-model="keyword" placeholder="搜索文章，或输入命令…" @input="onInput"
              @keydown.down.prevent="move(1)" @keydown.up.prevent="move(-1)" @keydown.enter.prevent="enter"
              @keydown.esc.prevent="close" />
            <kbd class="esc">ESC</kbd>
          </div>

          <div class="cmd-body">
            <template v-if="results.length">
              <div class="cmd-group">文章</div>
              <div v-for="(item, i) in results" :key="'a' + item.id" :class="['cmd-item', { active: cursor === i }]"
                @mouseenter="cursor = i" @click="goArticle(item)">
                <i class="fa fa-file-text-o"></i>
                <span class="cmd-label">{{ item.title }}</span>
                <span class="cmd-hint">{{ item.categoryName }}</span>
              </div>
            </template>

            <template v-if="commands.length">
              <div class="cmd-group">命令</div>
              <div v-for="(c, i) in commands" :key="'c' + c.key" :class="['cmd-item', { active: cursor === results.length + i }]"
                @mouseenter="cursor = results.length + i" @click="runCommand(c)">
                <i :class="'fa ' + c.icon"></i>
                <span class="cmd-label">{{ c.label }}</span>
                <span class="cmd-hint">{{ c.hint }}</span>
              </div>
            </template>

            <div class="cmd-empty" v-if="!results.length && !commands.length">
              <span v-if="loading"><i class="fa fa-spinner fa-spin"></i> 正在加载…</span>
              <span v-else>没有找到匹配的内容</span>
            </div>
          </div>

          <div class="cmd-foot">
            <span><kbd>↑</kbd><kbd>↓</kbd> 选择</span>
            <span><kbd>Enter</kbd> 打开</span>
            <span><kbd>Esc</kbd> 关闭</span>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { articleList } from '../api/article.js'
import { getStoredTheme, setStoredTheme, applyTheme } from '../utils/theme.js'

const NAV = [
  { key: 'home', label: '首页', icon: 'fa-home', path: '/Home' },
  { key: 'share', label: '文章归档', icon: 'fa-archive', path: '/Share?classId=0' },
  { key: 'word', label: '留言板', icon: 'fa-comments', path: '/WordBoard' },
  { key: 'friends', label: '友情链接', icon: 'fa-link', path: '/FriendsLink' },
  { key: 'reward', label: '赞赏支持', icon: 'fa-coffee', path: '/Reward' },
  { key: 'me', label: '关于我 / 个人中心', icon: 'fa-user', path: '/UserInfo' }
]

export default {
  name: 'CommandPalette',
  data() {
    return {
      visible: false,
      keyword: '',
      cursor: 0,
      loading: false,
      loaded: false,
      pool: [],
      mac: /Mac|iPhone|iPad/.test(navigator.platform || navigator.userAgent)
    }
  },
  computed: {
    results: function () {
      const kw = this.keyword.trim().toLowerCase()
      let list = this.pool
      if (kw) {
        list = list.filter(function (a) {
          return (a.title || '').toLowerCase().indexOf(kw) > -1 ||
            (a.summary || '').toLowerCase().indexOf(kw) > -1 ||
            (a.categoryName || '').toLowerCase().indexOf(kw) > -1
        })
      }
      return list.slice(0, 8)
    },
    commands: function () {
      const kw = this.keyword.trim()
      const lower = kw.toLowerCase()
      const cmds = NAV.concat([{ key: 'theme', label: '切换主题（亮/暗/跟随系统）', icon: 'fa-moon-o', hint: 'T' }])
      let list = lower
        ? cmds.filter(function (c) { return c.label.toLowerCase().indexOf(lower) > -1 }).slice(0, 6)
        : cmds.slice(0, 6)
      // 本地结果只匹配标题/摘要，关键词存在时提供「全文搜索」入口（走后端标题+正文匹配）
      if (kw) {
        list = [{
          key: 'fullsearch',
          label: '全文搜索：' + kw,
          icon: 'fa-search',
          path: '/Search?keyword=' + encodeURIComponent(kw)
        }].concat(list)
      }
      return list
    }
  },
  methods: {
    open: function () {
      this.visible = true
      this.cursor = 0
      const self = this
      this.$nextTick(function () {
        if (self.$refs.input) self.$refs.input.focus()
        self.loadPool()
      })
    },
    close: function () {
      this.visible = false
      this.keyword = ''
    },
    onInput: function () {
      this.cursor = 0
    },
    loadPool: function () {
      if (this.loaded || this.loading) return
      this.loading = true
      const self = this
      articleList({ pageNum: 1, pageSize: 100, categoryId: 0 }).then(function (res) {
        self.pool = (res && res.rows) || []
        self.loaded = true
      }).catch(function () {
        self.pool = []
      }).finally(function () {
        self.loading = false
      })
    },
    move: function (step) {
      const total = this.results.length + this.commands.length
      if (!total) return
      this.cursor = (this.cursor + step + total) % total
    },
    enter: function () {
      if (this.cursor < this.results.length) {
        this.goArticle(this.results[this.cursor])
      } else {
        this.runCommand(this.commands[this.cursor - this.results.length])
      }
    },
    goArticle: function (item) {
      this.close()
      this.$router.push({ path: '/DetailArticle', query: { aid: item.id } })
    },
    runCommand: function (c) {
      if (c.key === 'theme') {
        const order = ['auto', 'light', 'dark']
        const next = order[(order.indexOf(getStoredTheme()) + 1) % order.length]
        setStoredTheme(next)
        const real = applyTheme(next)
        this.$message && this.$message.success('已切换到' + (real === 'dark' ? '暗色' : '亮色') + '主题')
        return
      }
      this.close()
      this.$router.push({ path: c.path })
    },
    onKey: function (e) {
      const tag = (e.target && e.target.tagName) || ''
      const typing = tag === 'INPUT' || tag === 'TEXTAREA' || (e.target && e.target.isContentEditable)

      if ((e.metaKey || e.ctrlKey) && (e.key === 'k' || e.key === 'K')) {
        e.preventDefault()
        this.visible ? this.close() : this.open()
        return
      }
      if (this.visible && e.key === 'Escape') {
        this.close()
        return
      }
      if (!this.visible && !typing && !e.metaKey && !e.ctrlKey && !e.altKey && (e.key === '/' || e.key === '?')) {
        e.preventDefault()
        this.open()
      }
    }
  },
  mounted() {
    window.addEventListener('keydown', this.onKey)
  },
  beforeDestroy() {
    window.removeEventListener('keydown', this.onKey)
  }
}
</script>

<style lang="less" scoped>
.cmd-trigger {
  position: fixed;
  right: 22px;
  top: 66px;
  border: none;
  z-index: 1200;
  height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-weak);
  cursor: pointer;
  box-shadow: var(--shadow-1);
  transition: color .25s var(--ease), transform .25s var(--ease);

  kbd {
    font-size: 11px;
    font-family: inherit;
    padding: 1px 6px;
    border-radius: 6px;
    border: 1px solid var(--border-strong);
    color: var(--text-weak);
  }

  &:hover {
    color: var(--brand);
    transform: translateY(-1px);
  }
}

.cmd-mask {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 1400;
  background: rgba(10, 14, 26, .5);
  -webkit-backdrop-filter: blur(6px);
  backdrop-filter: blur(6px);
  display: flex;
  justify-content: center;
  padding-top: 12vh;
}

.cmd-panel {
  width: 92vw;
  max-width: 640px;
  max-height: 66vh;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--surface);
}

.cmd-input {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--border);

  i { color: var(--text-weak); }

  input {
    flex: 1;
    border: 0;
    outline: none;
    background: transparent;
    font-size: 15px;
    color: var(--text);
  }

  .esc {
    font-size: 11px;
    padding: 2px 7px;
    border-radius: 6px;
    border: 1px solid var(--border-strong);
    color: var(--text-weak);
  }
}

.cmd-body {
  overflow-y: auto;
  padding: 8px 0;
}

.cmd-group {
  font-size: 11px;
  letter-spacing: 1px;
  color: var(--text-weak);
  padding: 8px 18px 4px;
}

.cmd-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 18px;
  cursor: pointer;
  font-size: 14px;
  color: var(--text);

  i { color: var(--brand); width: 16px; text-align: center; }

  .cmd-label {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .cmd-hint {
    font-size: 11px;
    color: var(--text-weak);
  }

  &.active {
    background: linear-gradient(90deg, rgba(79, 140, 255, .14), transparent);
  }
}

.cmd-empty {
  padding: 26px 18px;
  text-align: center;
  font-size: 13px;
  color: var(--text-weak);
}

.cmd-foot {
  display: flex;
  gap: 16px;
  padding: 10px 18px;
  border-top: 1px solid var(--border);
  font-size: 11px;
  color: var(--text-weak);

  kbd {
    font-family: inherit;
    padding: 1px 5px;
    margin-right: 4px;
    border: 1px solid var(--border-strong);
    border-radius: 5px;
  }
}

.cmd-enter-active, .cmd-leave-active { transition: opacity .2s var(--ease); }
.cmd-enter, .cmd-leave-to { opacity: 0; }

@media (max-width: 768px) {
  .cmd-trigger {
    right: 12px;
    top: 52px;
    height: 30px;
    font-size: 12px;

    kbd { display: none; }
  }

  .cmd-mask { padding-top: 8vh; }
}
</style>
