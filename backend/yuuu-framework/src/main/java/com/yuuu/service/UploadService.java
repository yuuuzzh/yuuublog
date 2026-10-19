package com.yuuu.service;

import com.yuuu.domain.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @BelongsProject:YuuuBlog
 * @BelongsPackage:com.yuuu.service
 * @Author:Yuuu。
 * @CreateTime:2023-03-06 16:31
 * @Description:
 * @Version:1.0
 */
public interface UploadService {
    ResponseResult uploadImg(MultipartFile img);

    /**
     * 已上传文件列表（分页游标 marker）
     */
    ResponseResult listUploaded(String marker, Integer limit);

    /**
     * 删除已上传文件（按 key）
     */
    ResponseResult deleteUploaded(String key);
}
