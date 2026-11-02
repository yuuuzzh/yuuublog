package com.yuuu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuuu.domain.entity.Headline;
import com.yuuu.mapper.HeadlineMapper;
import com.yuuu.service.HeadlineService;
import org.springframework.stereotype.Service;

/**
 * 首屏文案池服务实现
 */
@Service
public class HeadlineServiceImpl extends ServiceImpl<HeadlineMapper, Headline> implements HeadlineService {
}
