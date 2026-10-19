import request from '@/utils/request'

// 订阅本站更新（匿名，需验证码）
export function subscribe(email, captcha, captchaKey) {
    return request({
        url: '/subscribe',
        method: 'post',
        headers: {
            isToken: false
        },
        params: { email: email, captcha: captcha, captchaKey: captchaKey }
    })
}
