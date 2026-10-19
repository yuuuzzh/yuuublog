import request from '@/utils/request'

// 评论列表
export function commentList(params) {
  return request({
    url: '/content/comment/list',
    method: 'get',
    params: params
  })
}

// 评论审核：ids + status（0待审核 1通过 2屏蔽）
export function auditComment(ids, status) {
  return request({
    url: '/content/comment/audit',
    method: 'post',
    data: { ids: ids, status: status }
  })
}

// 删除评论
export function deleteComment(ids) {
  return request({
    url: '/content/comment/' + ids.join(','),
    method: 'delete'
  })
}
