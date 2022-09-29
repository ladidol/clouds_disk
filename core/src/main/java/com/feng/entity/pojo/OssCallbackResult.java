package com.feng.entity.pojo;

import lombok.Data;


/**
 * @author: ladidol
 * @date: 2022/9/29 20:11
 * @description: oss上传文件的回调结果
 */
@Data
public class OssCallbackResult {
    /**
     * 文件名称
     */
    private String filename;
    /**
     * 文件大小
     */
    private String size;
    /**
     * 文件的mimeType
     */
    private String mimeType;
    /**
     * 图片文件的宽
     */
    private String width;
    /**
     * 图片文件的高
     */
    private String height;
}
