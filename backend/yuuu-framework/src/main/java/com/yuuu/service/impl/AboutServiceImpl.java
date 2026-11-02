package com.yuuu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuuu.constants.SystemConstants;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.About;
import com.yuuu.mapper.AboutMapper;
import com.yuuu.service.AboutService;
import com.yuuu.utils.BlogCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 关于我(About)表服务实现类
 */
@Service("aboutService")
public class AboutServiceImpl extends ServiceImpl<AboutMapper, About> implements AboutService {

    @Autowired
    private BlogCacheManager blogCacheManager;

    @Override
    public ResponseResult getAbout() {
        // 后台读取：不做缓存，避免后台改完看不到最新值
        LambdaQueryWrapper<About> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(About::getId);
        // 只取第一条：站点关于我只有一份配置
        queryWrapper.last("limit 1");
        About about = getOne(queryWrapper);
        return ResponseResult.okResult(about == null ? new About() : about);
    }

    @Override
    public ResponseResult saveAbout(About about) {
        if (about == null) {
            return ResponseResult.errorResult(500, "参数不能为空");
        }
        // id 为空或非持久化的 id 时按新增处理
        boolean result;
        if (about.getId() == null || getById(about.getId()) == null) {
            about.setId(null);
            result = save(about);
        } else {
            result = updateById(about);
        }
        if (!result) {
            return ResponseResult.errorResult(500, "保存失败");
        }
        // 保存后失效前台缓存（后台保存是唯一写入口）
        blogCacheManager.evictAbout();
        return ResponseResult.okResult(about);
    }

    @Override
    public ResponseResult getAboutForFront() {
        // 前台展示：单条配置，读写比极高，缓存后失效在 saveAbout 里
        About cache = blogCacheManager.get(SystemConstants.ABOUT_KEY, About.class);
        if (cache != null) {
            return ResponseResult.okResult(cache);
        }
        LambdaQueryWrapper<About> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(About::getStatus, "0");
        queryWrapper.orderByAsc(About::getId);
        queryWrapper.last("limit 1");
        About about = getOne(queryWrapper);
        About result = about == null ? new About() : about;
        // 保存时会立即失效；这里用较短 TTL 兜底，避免任何未触发失效的场景下长时间陈旧
        blogCacheManager.set(SystemConstants.ABOUT_KEY, result, SystemConstants.CACHE_TTL_SHORT);
        return ResponseResult.okResult(result);
    }
}
