import request from '@/utils/request'

export function headlineList(type) {
  return request({
    url: '/content/headline/list',
    method: 'get',
    params: { type: type }
  })
}

export function addHeadline(data) {
  return request({
    url: '/content/headline',
    method: 'post',
    data: data
  })
}

export function updateHeadline(data) {
  return request({
    url: '/content/headline',
    method: 'put',
    data: data
  })
}

export function toggleHeadline(id) {
  return request({
    url: '/content/headline/toggle/' + id,
    method: 'put'
  })
}

export function deleteHeadline(id) {
  return request({
    url: '/content/headline/' + id,
    method: 'delete'
  })
}

export function uploadImg(data) {
  return request({
    url: '/upload',
    method: 'post',
    data: data
  })
}
