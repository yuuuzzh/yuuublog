package com.yuuu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuuu.domain.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章收藏(Favorite)表数据库访问层
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
