package com.feng.entity.pojo;

import lombok.Data;

/**
 * @author: ladidol
 * @date: 2022/9/29 20:11
 * @description:
 */
@Data
public class StsMessage {
    private String expiration;
    private String keyId;
    private String secret;
    private String token;
    private String requestId;
}
