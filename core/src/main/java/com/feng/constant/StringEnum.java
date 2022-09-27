package com.feng.constant;


public enum StringEnum {
    /**
     * 用户默认头像
     */
    USER_DEFAULT_AVATAR("user/avatar/default/default.jpg"),
    /**
     * 用户头像前缀
     */
    USER_AVATAR_PREFIX("user/avatar/"),

    ADMIN_INTERFACES("admin"),

    /**
     * 主题：重置密码，用于重置密码时的邮件主题
     */
    MAIL_SUBJECT_RESET_PASSWORD("重置 密码"),
    MAIL_SUBJECT_VERIFY_CODE("邮箱验证码"),
    MAIL_MESSAGE_VERIFY_CODE_PREFIX("亲爱的用户您好:\n\t您的邮箱验证码是："),
    MAIL_MESSAGE_VERIFY_CODE_SUFFIX("有效期为五分钟,请及时使用!"),
    MAIL_MESSAGE_RESET_PASSWORD_PREFIX("亲爱的用户您好:\n\t您的新密码是："),
    MAIL_MESSAGE_RESET_PASSWORD_SUFFIX("请牢记您的密码，稍后您可以在用户界面更改您的密码。"),

    /**
     * 文件默认前缀
     */
    FILE_DEFAULT_PREFIX("cloud-disk/files/"),
    FILE_DEFAULT_GARBAGE_PREFIX("cloud-disk/garbage/"),
    FILE_TYPE_DIR("dir"),
    FILE_TYPE_DOC("file"),
    /**
     * 忽略文件名
     */
    FILE_IGNORE_NAME("cloud-disk-ignore.txt");

    private String value;

    StringEnum() {
    }

    StringEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 得到验证邮件消息
     *
     * @param verifyCode 验证代码
     * @return {@code String}
     */
    public static String getVerifyMailMessage(String verifyCode) {
        return MAIL_MESSAGE_VERIFY_CODE_PREFIX.getValue()
                + "[" + verifyCode + "] "
                + MAIL_MESSAGE_VERIFY_CODE_SUFFIX.getValue();
    }

    /**
     * 得到重置密码的邮件信息
     *
     * @param password 密码
     * @return {@code String}
     */
    public static String getRestPasswordMessage(String password) {
        return MAIL_MESSAGE_RESET_PASSWORD_PREFIX.getValue()
                + "[" + password + "] "
                + MAIL_MESSAGE_RESET_PASSWORD_SUFFIX.getValue();

    }

    public void setValue(String value) {
        this.value = value;
    }

    public static void main(String[] args) {
        System.out.println(getVerifyMailMessage("wew"));
        System.out.println(getRestPasswordMessage("123456"));
    }

}
