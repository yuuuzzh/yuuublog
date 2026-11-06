// 常用邮箱后缀（按国内使用频率排序），用于邮箱输入的自动补全建议
export const COMMON_EMAIL_DOMAINS = [
    'qq.com',
    '163.com',
    '126.com',
    'gmail.com',
    'foxmail.com',
    'outlook.com',
    'hotmail.com',
    'sina.com',
    '139.com',
    'icloud.com',
    'yeah.net',
    'aliyun.com',
    'live.com',
    'yahoo.com'
]

/**
 * 依据当前输入生成完整邮箱候选，直接供 el-autocomplete 的 fetch-suggestions 回调使用：
 *   "zhangsan"        → zhangsan@qq.com、zhangsan@163.com …
 *   "zhangsan@"       → 同上
 *   "zhangsan@g"      → zhangsan@gmail.com
 *   "zhangsan@qq.com" → 已写完整，返回空数组
 * 自定义域名（无匹配后缀）不会弹出建议，不干扰正常输入。
 */
export function getEmailSuggestions(input) {
    var value = (input || '').trim();
    if (!value) return [];

    var at = value.indexOf('@');
    if (at !== value.lastIndexOf('@')) return [];

    var local = at < 0 ? value : value.slice(0, at);
    if (!local) return [];
    var domain = at < 0 ? '' : value.slice(at + 1).toLowerCase();

    return COMMON_EMAIL_DOMAINS
        .filter(function (d) {
            return domain ? (d.indexOf(domain) === 0 && d !== domain) : true;
        })
        .map(function (d) {
            return { value: local + '@' + d };
        });
}
