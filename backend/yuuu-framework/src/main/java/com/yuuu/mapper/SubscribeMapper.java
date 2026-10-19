package com.yuuu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuuu.domain.entity.Subscribe;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件订阅(Subscribe)表数据库访问层
 */
@Mapper
public interface SubscribeMapper extends BaseMapper<Subscribe> {
}
