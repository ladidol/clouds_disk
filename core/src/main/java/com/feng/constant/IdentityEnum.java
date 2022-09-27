package com.feng.constant;


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
