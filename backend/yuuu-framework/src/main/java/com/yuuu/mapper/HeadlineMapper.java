package com.yuuu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuuu.domain.entity.Headline;
import org.apache.ibatis.annotations.Mapper;

/**
 * 首屏文案池 Mapper
 */
@Mapper
public interface HeadlineMapper extends BaseMapper<Headline> {
}
