import request from '@/utils/request'

// 订阅者列表
export function subscribeList(params) {
  return request({
    url: '/content/subscribe/list',
    method: 'get',
    params: params
  })
}

// 删除订阅者
export function deleteSubscribe(ids) {
  return request({
    url: '/content/subscribe/' + ids.join(','),
    method: 'delete'
  })
}
