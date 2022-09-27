package com.feng.service;

import cn.hutool.captcha.CircleCaptcha;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.feng.entity.User;
import com.feng.entity.dto.UserDTO;
import com.feng.entity.dto.UserInfoDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends IService<User> {

    /**
     * 得到图像验证码
     * 获得图片验证码
     *
     * @param uid uid,全局唯一标识
     * @return {@code CircleCaptcha}
     */
    CircleCaptcha getImageVerificationCode(String uid);

    /**
     * 获得电子邮件验证代码
     *
     * @param uid   uid
     * @param code  代码
     * @param email 电子邮件
     */
    void getEmailVerifyCode(String uid, String code, String email);

    /**
     * 登录
     *
     * @param userDTO 用户dto
     * @return {@code String}
     */
    String login(UserDTO userDTO);

    /**
     * 注销
     *
     * @param token 令牌
     */
//    void logout(String token);

    /**
     * 注册
     *
     * @param userInfoDTO 用户信息dto
     * @return {@code Boolean}
     */
    @SuppressWarnings("all")
    Boolean register(UserInfoDTO userInfoDTO);

    /**
     * 根据用户ID查询用户
     *
     * @param id id
     * @return {@code UserVO}
     */
//    UserVO queryUserById(Long id);

    /**
     * 更新头像
     *
     * @param id   id
     * @param file 文件
     * @return {@code String}
     */
//    String updateAvatar(Long id, MultipartFile file);
//
//    /**
//     * 更新昵称
//     *
//     * @param id       id
//     * @param nickname 昵称
//     */
//    void updateNickname(Long id, String nickname);
//
//    /**
//     * 重置密码
//     *
//     * @param verifyCode 验证代码
//     * @param username   用户名
//     */
//    void resetPassword(String username, String verifyCode);
//
//    /**
//     * 更新密码
//     *
//     * @param password 密码
//     * @param userId   用户id
//     */
//    void updatePassword(String password, Long userId);
//
//    /**
//     * 用户列表
//     *
//     * @param currentPage 当前页面
//     * @param pageSize    页面大小
//     * @return {@code List<UserVO>}
//     */
////    Page<UserVO> listUsers(Integer currentPage, Integer pageSize);
//
//    /**
//     * 删除用户
//     *
//     * @param id id
//     */
//    void deleteUserById(Long id);
//
//    /**
//     * 添加管理
//     *
//     * @param id id
//     */
//    void addAdmin(Long id);

}
