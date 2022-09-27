package com.feng.controller;

import cn.hutool.captcha.CircleCaptcha;
import com.feng.constant.ResultEnum;
import com.feng.entity.dto.UserDTO;
import com.feng.entity.dto.UserInfoDTO;
import com.feng.result.Result;
import com.feng.result.ResultUtil;
import com.feng.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.IOException;


/**
 * @author: ladidol
 * @date: 2022/9/27 23:13
 * @description:
 */
@RestController
@Validated
public class UserController {

    @Resource
    UserService userService;

    @GetMapping("/codes/image/{uid}")
    public void getImageCode(@NotBlank(message = "uid不能为空")
                             @PathVariable("uid") String uid, HttpServletResponse response)
            throws IOException {
        //得到图片验证码
        CircleCaptcha circleCaptcha = userService.getImageVerificationCode(uid);
        //写回客户端
        ServletOutputStream outputStream = response.getOutputStream();
        circleCaptcha.write(outputStream);
        //关闭流
        if (outputStream != null) {
            outputStream.close();
        }
    }

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserDTO userDTO) {
        String token = userService.login(userDTO);
        return ResultUtil.success(token);
    }



    //获取邮箱验证码：需要用户uid+图片code+username
    @GetMapping("/codes/email")
    public Result<String> sendEmailCode(@NotBlank(message = "key不能为空") @RequestParam("key") String uid,
                                        @NotBlank(message = "图片验证码不能为空") @RequestParam("imageCode") String code,
                                        @Email(message = "用户名格式异常,用户名必须为邮箱") @RequestParam("username") String email) {
        userService.getEmailVerifyCode(uid, code, email);
        return ResultUtil.success();
    }



    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody UserInfoDTO userInfoDTO) {
        userService.register(userInfoDTO);
        return ResultUtil.success(ResultEnum.REGISTER_SUCCESS);
    }




}
