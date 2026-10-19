import request from '@/utils/request'

// 获取验证码：返回 { captchaKey, captchaImageBase64 }
// captchaKey 是本次验证码的令牌，提交时需要一起带上（验证码只对本次获取者生效）
export function getCaptchaImg() {
    return new Promise((resolve, reject) => {
        request({
            url: '/captcha',
            headers: {
                isToken: false
            },

            method: 'get'
        }).then(response => {

            // 从响应数据中获取验证码令牌与图片 Base64 编码
            resolve({
                captchaKey: response.captchaKey,
                captchaImageBase64: response.captchaImageBase64
            });
        }).catch(error => {
            console.error(error); // 打印错误信息
            reject(error);
        });
    });
}
