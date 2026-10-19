import request from '@/utils/request'

// 订阅本站更新（匿名）
export function subscribe(email) {
    return request({
        url: '/subscribe',
        method: 'post',
        headers: {
            isToken: false
        },
        params: { email: email }
    })
}
