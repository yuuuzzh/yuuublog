package com.yuuu.controller;

import com.yuuu.domain.ResponseResult;
import com.yuuu.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章收藏（需登录）
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /** 收藏 / 取消收藏，返回操作后是否已收藏 */
    @PostMapping("/toggle/{articleId}")
    public ResponseResult toggle(@PathVariable("articleId") Long articleId) {
        return favoriteService.toggle(articleId);
    }

    /** 我的收藏列表 */
    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize) {
        return favoriteService.myList(pageNum, pageSize);
    }

    /** 是否已收藏 */
    @GetMapping("/check/{articleId}")
    public ResponseResult check(@PathVariable("articleId") Long articleId) {
        return favoriteService.isFavorited(articleId);
    }
}
