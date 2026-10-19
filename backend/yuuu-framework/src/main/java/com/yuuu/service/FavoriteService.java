package com.yuuu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Favorite;

/**
 * 文章收藏(Favorite)表服务接口
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 收藏或取消收藏（已收藏则取消，返回操作后是否收藏）
     */
    ResponseResult toggle(Long articleId);

    /**
     * 当前用户的收藏列表（含文章标题等信息）
     */
    ResponseResult myList(Integer pageNum, Integer pageSize);

    /**
     * 是否已收藏
     */
    ResponseResult isFavorited(Long articleId);
}
