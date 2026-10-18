import request from '@/utils/request'

// 标签云：标签 + 文章数（前台匿名接口）
export function tagListWithCount(query) {
    return request({
        url: '/tag/listWithCount',
        method: 'get',
        headers: {
            isToken: false
        },
        params: query
    })
}
