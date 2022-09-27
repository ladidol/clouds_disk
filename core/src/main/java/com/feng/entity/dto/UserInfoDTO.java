package com.feng.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoDTO {
    /**
     * 用户名
     */
    @Email(message = "格式错误，用户名必须为邮箱")
    private String username;

    /**
     * 密码
     */
    @Length(min = 6, max = 255, message = "密码长度至少为6")
    private String password;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /**
     * 头像，允许空
     */
    private String avatar;

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "邮箱验证码不能为空")
    private String verifyCode;
}
