import request from '@/utils/request'

// 已上传文件列表
export function uploadedList(params) {
  return request({
    url: '/content/upload/list',
    method: 'get',
    params: params
  })
}

// 删除已上传文件
export function deleteUploaded(key) {
  return request({
    url: '/content/upload/delete',
    method: 'delete',
    params: { key: key }
  })
}
