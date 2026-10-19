package com.yuuu.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.yuuu.service.UploadService;
import com.yuuu.constants.SystemConstants;
import com.yuuu.domain.ResponseResult;
import com.yuuu.enums.AppHttpCodeEnum;
import com.yuuu.exception.SystemException;
import com.yuuu.utils.PathUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject:YuuuBlog
 * @BelongsPackage:com.yuuu.service.impl
 * @Author:Yuuu。
 * @CreateTime:2023-03-06 16:32
 * @Description:
 * @Version:1.0
 */
@Service
@Data
@ConfigurationProperties(prefix = "oss")
public class OssUploadService implements UploadService {
    /** 图片最大宽度，超过则等比缩放 */
    private static final int MAX_WIDTH = 1920;
    /** 压缩质量 */
    private static final float COMPRESS_QUALITY = 0.82f;

    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        //判断文件类型
        //获取原始文件名
        String originalFilename = img.getOriginalFilename();
        //对原始文件名进行判断
        if (!originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpg")) {
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }

        //如果判断通过上传文件到OSS
        String filePath = PathUtils.generateFilePath(originalFilename);
        // 统一按 jpg 存储（体积更小），因此把路径后缀改成 .jpg
        filePath = filePath.replaceAll("\\.(png|jpg)$", ".jpg");
        String url = uploadOss(img, filePath);
        return ResponseResult.okResult(url);
    }

    private String accessKey;
    private String secretKey;
    private String bucket;


    @Override
    public ResponseResult listUploaded(String marker, Integer limit) {
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            BucketManager bucketManager = new BucketManager(auth, new Configuration(Region.autoRegion()));
            int size = (limit == null || limit < 1) ? 50 : Math.min(limit, 200);
            FileListing listing = bucketManager.listFiles(bucket, "", marker, size, null);
            List<Map<String, Object>> items = new ArrayList<>();
            if (listing != null && listing.items != null) {
                for (FileInfo info : listing.items) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("key", info.key);
                    item.put("url", SystemConstants.URL_PATH + info.key);
                    item.put("size", info.fsize);
                    item.put("time", new Date(info.putTime / 10000));
                    items.add(item);
                }
            }
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("items", items);
            data.put("marker", listing == null ? null : listing.marker);
            return ResponseResult.okResult(data);
        } catch (Exception e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(), "读取文件列表失败");
        }
    }

    @Override
    public ResponseResult deleteUploaded(String key) {
        if (key == null || key.trim().isEmpty()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.INPUT_ERROR);
        }
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            BucketManager bucketManager = new BucketManager(auth, new Configuration(Region.autoRegion()));
            bucketManager.delete(bucket, key);
            return ResponseResult.okResult();
        } catch (Exception e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(), "删除文件失败");
        }
    }

    /**
     * 上传前压缩：等比缩放到最大宽度以内，并按 jpg 重新编码，减少 OSS 存储与流量
     */
    private InputStream compressImage(MultipartFile imgFile) {
        try {
            BufferedImage source = ImageIO.read(imgFile.getInputStream());
            if (source == null) {
                return imgFile.getInputStream();
            }
            int width = source.getWidth();
            int height = source.getHeight();
            if (width > MAX_WIDTH) {
                int newHeight = Math.round(height * (MAX_WIDTH * 1f / width));
                BufferedImage scaled = new BufferedImage(MAX_WIDTH, newHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = scaled.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(source, 0, 0, MAX_WIDTH, newHeight, null);
                g.dispose();
                source = scaled;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(COMPRESS_QUALITY);
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(bos)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(source, null, null), param);
            } finally {
                writer.dispose();
            }
            byte[] data = bos.toByteArray();
            // 压缩结果异常小说明处理有问题，直接用原图
            if (data.length < 1024) {
                return imgFile.getInputStream();
            }
            return new ByteArrayInputStream(data);
        } catch (Exception e) {
            // 压缩失败不影响上传，回退原图
            try {
                return imgFile.getInputStream();
            } catch (IOException ioException) {
                return null;
            }
        }
    }

    private String uploadOss(MultipartFile imgFile, String filePath) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = filePath;
        try {
            InputStream inputStream = compressImage(imgFile);
            if (inputStream == null) {
                return "wrong";
            }
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(inputStream, key, upToken, null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return SystemConstants.URL_PATH + key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return "wrong";
    }
}

