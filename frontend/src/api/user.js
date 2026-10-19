import request from '@/utils/request'

// 登录
export function userLogin(username, password, captcha, captchaKey) {
    return request({
        url: '/login',
        method: 'post',
        headers: {
            isToken: false
        },
        data: { 'username': username, 'password': password, 'captcha': captcha, 'captchaKey': captchaKey }
    })
}
//发送邮箱验证码
export function sendVerificationCode(nemail) {

    return request({
        url: '/user/sendVerificationCode',
        method: 'post',
        headers: {
            isToken: false
        },
        data: { 'nemail': nemail }
    })

}

//修改密码，发送邮箱验证码
export function sendCodeForUpdatePassword(emailPassword) {
    return request({
      url: '/user/sendCodeForUpdatePassword',
      method: 'post',
      headers: {
        isToken: false
      },
      data: { emailPassword }
    }).then(response => {
      // 从响应数据中获取验证码图片Base64编码
      return response;
    });
  }




export function userRegister(username, nickName, email, password, emailCaptcha) {
    return request({
        url: '/user/register',
        method: 'post',
        headers: {
            isToken: false
        },
        data: { "username": username, "nickName": nickName, "email": email, "password": password, "emailCaptcha": emailCaptcha }
    })
}


export function logout() {
    return request({
        url: '/logout',
        method: 'post'
    })
}

export function getUserInfo(userId) {
    return request({
        url: '/user/userInfo',
        method: 'get',
        params: { "userId": userId }
    })
}


export function savaUserInfo(userinfo) {
    return request({
        url: '/user/userInfo',
        method: 'put',
        data: userinfo
    })
}