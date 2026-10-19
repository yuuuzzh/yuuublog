package com.yuuu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Article;
import com.yuuu.domain.entity.Subscribe;

import java.util.List;

/**
 * 邮件订阅(Subscribe)表服务接口
 */
public interface SubscribeService extends IService<Subscribe> {

    /**
     * 前台订阅（邮箱校验 + 去重）
     */
    ResponseResult subscribe(String email, String source);

    /**
     * 一键退订
     */
    ResponseResult unsubscribe(String token);

    /**
     * 后台订阅者列表
     */
    ResponseResult adminList(Integer pageNum, Integer pageSize, String status);

    /**
     * 后台删除订阅者
     */
    ResponseResult deleteByIds(List<Long> ids);

    /**
     * 新文章通知：异步群发给所有订阅中的邮箱（失败不影响文章发布流程）
     */
    void notifyNewArticle(Article article);
}
