import request from '@/utils/request'

// 后台数据仪表盘统计
export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}
