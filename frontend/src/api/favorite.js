import request from '@/utils/request'

// 收藏 / 取消收藏，返回操作后是否已收藏
export function toggleFavorite(articleId) {
    return request({
        url: '/favorite/toggle/' + articleId,
        method: 'post'
    })
}

// 我的收藏列表
export function favoriteList(params) {
    return request({
        url: '/favorite/list',
        method: 'get',
        params: params
    })
}

// 是否已收藏
export function checkFavorite(articleId) {
    return request({
        url: '/favorite/check/' + articleId,
        method: 'get'
    })
}
