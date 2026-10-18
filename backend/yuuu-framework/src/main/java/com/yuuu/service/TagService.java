package com.yuuu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.dto.TagDto;
import com.yuuu.domain.dto.TagListDto;
import com.yuuu.domain.entity.Tag;
import com.yuuu.domain.vo.TagCountVo;

import java.util.List;


/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2023-03-08 15:24:55
 */
public interface TagService extends IService<Tag> {


    ResponseResult pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto);

    ResponseResult addTag(TagListDto tagListDto);

    ResponseResult deleteTag(List<Long> ids);

    ResponseResult getTagById(Long id);

    ResponseResult updateTag(TagDto tagDto);

    ResponseResult listAllTag();

    /**
     * 前台标签云：标签 + 已发布文章数（含缓存）
     */
    List<TagCountVo> listTagWithCount();
}

