import request from '@/utils/request'

// 查询文章列表
export function articleList(query) {
    return request({
        url: '/article/articleList',
        method: 'get',
        headers: {
          isToken: false
        },
        params: query
    })
}

//查询最热文章
export function hotArticleList() {
    return request({
        url: '/article/hotArticleList',
        headers: {
          isToken: false
        },
        method: 'get'
    })
}

//获取文章详情
export function getArticle(articleId) {
    return request({
        url: '/article/' + articleId,
        headers: {
          isToken: false
        },
        method: 'get'
    })
}

// 文章归档（按年月聚合）
export function archives() {
    return request({
        url: '/article/archives',
        method: 'get',
        headers: {
            isToken: false
        }
    })
}

// 相关文章推荐
export function relatedArticles(articleId, size) {
    return request({
        url: '/article/related',
        method: 'get',
        headers: {
            isToken: false
        },
        params: { articleId: articleId, size: size }
    })
}

export function updateViewCount(articleId) {
    return request({
        url: '/article/updateViewCount/' + articleId,
        headers: {
          isToken: false
        },
        method: 'put'
    })
    
}
