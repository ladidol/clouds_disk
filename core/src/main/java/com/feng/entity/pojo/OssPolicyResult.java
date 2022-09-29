package com.feng.entity.pojo;


import lombok.Data;


/**
 * @author: ladidol
 * @date: 2022/9/29 20:11
 * @description: 获取OSS上传文件授权返回结果
 */

@Data
public class OssPolicyResult {
    private String accessKeyId;
    private String policy;
    private String signature;
    private String dir;
    private String host;
    private String callback;
}
