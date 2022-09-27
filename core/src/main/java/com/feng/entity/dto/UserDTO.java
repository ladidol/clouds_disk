package com.feng.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


/**
 * @author: ladidol
 * @date: 2022/9/27 23:13
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    @Email(message = "用户名格式错误，必须为邮箱")
    private String username;

    @Length(min = 6, max = 255, message = "密码至少为6个字符")
    private String password;


    @NotBlank(message = "图片验证码不能为空")
    private String imageCode;


    @NotBlank(message = "邮箱验证码不能为空")
    private String verifyCode;
}
