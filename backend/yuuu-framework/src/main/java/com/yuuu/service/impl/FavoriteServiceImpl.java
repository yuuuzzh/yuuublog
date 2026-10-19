package com.yuuu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Article;
import com.yuuu.domain.entity.Favorite;
import com.yuuu.domain.vo.FavoriteVo;
import com.yuuu.domain.vo.PageVo;
import com.yuuu.enums.AppHttpCodeEnum;
import com.yuuu.mapper.FavoriteMapper;
import com.yuuu.service.ArticleService;
import com.yuuu.service.FavoriteService;
import com.yuuu.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文章收藏(Favorite)表服务实现类
 */
@Service("favoriteService")
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private ArticleService articleService;

    private Long currentUserId() {
        return SecurityUtils.getUserId();
    }

    @Override
    public ResponseResult toggle(Long articleId) {
        if (articleId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.INPUT_ERROR);
        }
        Long userId = currentUserId();
        if (userId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        Favorite exist = getOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getArticleId, articleId));
        if (exist != null) {
            removeById(exist.getId());
            return ResponseResult.okResult(false);
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setArticleId(articleId);
        favorite.setCreateTime(new Date());
        favorite.setDelFlag(0);
        save(favorite);
        return ResponseResult.okResult(true);
    }

    @Override
    public ResponseResult myList(Integer pageNum, Integer pageSize) {
        Long userId = currentUserId();
        if (userId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime);
        Page<Favorite> page = new Page<>(
                (pageNum == null || pageNum < 1) ? 1 : pageNum,
                (pageSize == null || pageSize < 1) ? 10 : pageSize);
        page(page, wrapper);

        List<FavoriteVo> vos = new ArrayList<>();
        for (Favorite favorite : page.getRecords()) {
            Article article = articleService.getById(favorite.getArticleId());
            FavoriteVo vo = new FavoriteVo();
            vo.setId(favorite.getId());
            vo.setArticleId(favorite.getArticleId());
            vo.setCreateTime(favorite.getCreateTime());
            if (article != null) {
                vo.setTitle(article.getTitle());
                vo.setSummary(article.getSummary());
                vo.setThumbnail(article.getThumbnail());
                vo.setViewCount(article.getViewCount());
            }
            vos.add(vo);
        }
        return ResponseResult.okResult(new PageVo(vos, page.getTotal()));
    }

    @Override
    public ResponseResult isFavorited(Long articleId) {
        Long userId = currentUserId();
        if (userId == null || articleId == null) {
            return ResponseResult.okResult(false);
        }
        long count = count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getArticleId, articleId));
        return ResponseResult.okResult(count > 0);
    }
}
