package com.feng.constant;

/**
 * @author: ladidol
 * @date: 2022/9/27 23:13
 * @description:
 */
public enum IdentityEnum {
    /**
     * 用户
     */
    USER(0),
    /**
     * 管理员
     */
    ADMIN(1);
    private Integer identity;

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    IdentityEnum() {
    }

    IdentityEnum(Integer identity) {
        this.identity = identity;
    }
}
