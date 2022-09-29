package com.feng.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.constant.IdentityEnum;
import com.feng.constant.IntegerEnum;
import com.feng.constant.StringEnum;
import com.feng.entity.User;
import com.feng.entity.dto.UserDTO;
import com.feng.entity.dto.UserInfoDTO;
import com.feng.entity.vo.UserVO;
import com.feng.exception.AppException;
import com.feng.mapper.UserMapper;
import com.feng.service.UserService;
import com.feng.util.JwtUtils;
import com.feng.util.MailClient;
import com.feng.util.PasswordUtils;
import com.feng.util.RedisUtil;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author: ladidol
 * @date: 2022/9/27 23:13
 * @description:
 */
@Service
@Log4j2
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    RedisUtil redisUtil;

    @Resource
    MailClient mailClient;

    /**
     * 验证码过期时间,单位秒
     */
    private static final long EXPIRATION = 300;

    /**
     * 验证码长度
     */
    private static final int VERIFY_CODE_LENGTH = 6;

    /**
     * 图片验证码复杂度，如果太大图片验证码会很多干扰元素
     */
    private static final int VERIFY_CODE_COMPLEXITY = 30;

    /**
     * 图片验证码宽度
     */
    private static final int VERIFY_CODE_WIDTH = 200;

    /**
     * 图片验证码高度
     */
    private static final int VERIFY_CODE_HEIGHT = 100;


    @Override
    public CircleCaptcha getImageVerificationCode(String uid) {
        log.info("开始生成验证码.........");
        //定义图形验证码的长、宽、验证码字符数、干扰元素个数
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(VERIFY_CODE_WIDTH, VERIFY_CODE_HEIGHT,
                VERIFY_CODE_LENGTH, VERIFY_CODE_COMPLEXITY);
        //获取图片验证码的文本串
        String code = captcha.getCode();
        log.info("验证码====》"+ code);

        //将文本串放入redis,uid作为Key,code作为value
        redisUtil.set(uid, code, EXPIRATION);
        log.info("验证码生成成功");
        return captcha;
    }

    @Override
    public void getEmailVerifyCode(String uid, String code, String email) {
        //先验证图片验证码
        // FIXME: 2022/9/27 开发期间图片验证码不需要验证。
//        Object imageCode = redisUtil.get(uid);
//        if (null == imageCode || "".equals(imageCode)) {
//            throw new AppException("图片验证码错误,请刷新验证码后重试");
//        }
//        redisUtil.del(uid);
//        if (!imageCode.equals(code)) {
//            throw new AppException("图片验证码不匹配，请刷新验证码后重试");
//        }

        //开始生成邮箱验证码
        log.info("图片验证码验证成功,开始生成随机邮箱验证码......");
        String verifyCode = getRandomString(VERIFY_CODE_LENGTH);
        log.info("邮箱验证码为==>{},开始发送邮件......", verifyCode);
        mailClient.sendMail(
                email,
                StringEnum.MAIL_SUBJECT_VERIFY_CODE.getValue(),
                StringEnum.getVerifyMailMessage(verifyCode)
        );
        //将邮箱验证码以email：code的方式存入。
        redisUtil.set(email, verifyCode, EXPIRATION);
    }

    @Override
    public String login(UserDTO userDTO) {
        //验证验证码
        //FIXME 开发阶段，暂时注销验证码功能，可以直接登录
//        log.info("开始验证邮箱验证码==>[{}]......", userDTO.getVerifyCode());
//        String code = (String) redisUtil.get(userDTO.getUsername());
//        redisUtil.del(userDTO.getUsername());
//        if (StrUtil.isBlank(code) || !code.equals(userDTO.getVerifyCode())) {
//            throw new AppException("邮箱验证码错误");
//        }

        //校验账号
        log.info("邮箱验证码验证成功，开始校验用户账号......");
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userDTO.getUsername())
                .eq(User::getPassword, PasswordUtils.encrypt(userDTO.getPassword()));
        User user = baseMapper.selectOne(wrapper);
        if (BeanUtil.isEmpty(user)) {
            throw new AppException("用户名或密码错误，请重新输入");
        }

        //用户校验通过
        log.info("校验通过，开始发放token......");
        String token = JwtUtils.generateToken(user.getId(), user.getNickname());
        //token放入redis,过期时间为token过期时间
        redisUtil.set(token, user.getId().toString(), JwtUtils.getTokenExpiredTime());
        return token;
    }

    @Override
    public Boolean register(UserInfoDTO userInfoDTO) {
        //先验证邮箱验证码
        // FIXME: 2022/9/27 开发期间为了方便，先不用验证邮箱验证码。
//        log.info("开始验证邮箱验证码......");
//        String code = (String) redisUtil.get(userInfoDTO.getUsername());
//        redisUtil.del(userInfoDTO.getUsername());
//        if (null == code || !code.equals(userInfoDTO.getVerifyCode())) {
//            throw new AppException("邮箱验证码错误");
//        }
        //开始注册
        log.info("邮箱验证码注册成功，开始注册用户......");
        if (StrUtil.isBlank(userInfoDTO.getAvatar())) {
            userInfoDTO.setAvatar(StringEnum.USER_DEFAULT_AVATAR.getValue());
        }
        User user = User.builder()
                .username(userInfoDTO.getUsername())
                .nickname(userInfoDTO.getNickname())
                .password(PasswordUtils.encrypt(userInfoDTO.getPassword()))
                .avatar(userInfoDTO.getAvatar())
                .identity(IdentityEnum.USER.getIdentity())
                .maxSpace(Integer.toUnsignedLong(IntegerEnum.MAX_SPACE_SIZE.getValue()))
                .usedSpace(Integer.toUnsignedLong(IntegerEnum.INITIAL_SPACE_SIZE.getValue()))
                .build();
        int result = 0;
        try {
            result = baseMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new AppException("重复的用户名，请重新输入");
        }
        return result == 1;
    }

    @Override
    public UserVO queryUserById(Long id) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getId, id);
        User user = baseMapper.selectOne(wrapper);
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        // TODO: 2022/9/29 这里调用aliyun工具类主要是为了干啥哦。 
//        userVO.setAvatar(AliyunUtils.findFileInfo(user.getAvatar()).getLink());
        return userVO;
    }

//    @Override
//    public String updateAvatar(Long id, MultipartFile file) {
//        //获取文件前缀
//        String prefix = StringEnum.USER_AVATAR_PREFIX.getValue() + id + "/";
//        String name = file.getOriginalFilename();
//        String fileName = prefix + name;
//        log.info("文件对象名为==>[{}],开始上传到阿里云OSS服务.......", fileName);
//        AliyunUtils.upload(fileName, file);
//        log.info("上传成功");
//
//        //写入数据库
//        new LambdaUpdateChainWrapper<>(baseMapper)
//                .eq(User::getId, id)
//                .set(User::getAvatar, fileName)
//                .set(User::getUpdateTime, new Date())
//                .update();
//        return AliyunUtils.findFileInfo(fileName).getLink();
//    }
//
//    @Override
//    public void updateNickname(Long id, String nickname) {
//        new LambdaUpdateChainWrapper<>(baseMapper)
//                .eq(User::getId, id)
//                .set(User::getNickname, nickname)
//                .set(User::getUpdateTime, new Date())
//                .update();
//    }
//
//    @Override
//    public void resetPassword(String username, String verifyCode) {
//        //先验证邮箱密码
//        log.info("开始校验邮箱验证码.......");
//        String code = (String) redisUtil.get(username);
//        redisUtil.del(username);
//        if (null == code || !code.equals(verifyCode)) {
//            throw new AppException("邮箱验证码错误，请重试");
//        }
//
//        //开始生成新的随机密码，通过邮箱发送
//        log.info("邮箱验证码校验成功");
//        String newPassword = getRandomString(IntegerEnum.RESET_PASSWORD_LENGTH.getValue());
//        //更新数据库
//        boolean update = new LambdaUpdateChainWrapper<>(baseMapper)
//                .eq(User::getUsername, username)
//                .set(User::getPassword, PasswordUtils.encrypt(newPassword))
//                .set(User::getUpdateTime, new Date())
//                .update();
//        if (!update) {
//            throw new AppException("不存在该用户");
//        }
//        MailUtil.send(username, StringEnum.MAIL_SUBJECT_RESET_PASSWORD.getValue(),
//                StringEnum.getRestPasswordMessage(newPassword), false);
//        log.info("重置密码成功");
//    }
//
//    @Override
//    public void logout(String token) {
//        if (!redisUtil.del(token)) {
//            throw new AppException("发生未知错误，登出失败");
//        }
//    }
//
//    @Override
//    public void updatePassword(String password, Long userId) {
//        new LambdaUpdateChainWrapper<>(baseMapper)
//                .eq(User::getId, userId)
//                .set(User::getPassword, PasswordUtils.encrypt(password))
//                .set(User::getUpdateTime, new Date())
//                .update();
//    }
//
//    @Override
//    public Page<UserVO> listUsers(Integer currentPage, Integer pageSize) {
//        Page<User> page = new Page<>(currentPage, pageSize);
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        Page<User> userPage = baseMapper.selectPage(page, wrapper);
//        List<UserVO> result = BeanUtil.copyToList(userPage.getRecords(), UserVO.class);
//        for (UserVO userVO : result) {
//            userVO.setAvatar(AliyunUtils.findFileInfo(userVO.getAvatar()).getLink());
//        }
//        Page<UserVO> res = new Page<>();
//        BeanUtil.copyProperties(userPage, res);
//        res.setRecords(result);
//        return res;
//    }
//
//    @Override
//    public void deleteUserById(Long id) {
//        User user = baseMapper.selectById(id);
//        try {
//            if (user.getIdentity().equals(IdentityEnum.ADMIN.getIdentity())) {
//                throw new AppException("删除的目标用户为管理员，你没有权限删除");
//            }
//            if (baseMapper.deleteById(id) != IntegerEnum.SUCCESS.getValue()) {
//                throw new AppException("发生未知错误，删除失败");
//            }
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            throw new AppException("不存在该用户");
//        }
//    }
//
//    @Override
//    public void addAdmin(Long id) {
//        new LambdaUpdateChainWrapper<>(baseMapper)
//                .eq(User::getId, id)
//                .set(User::getIdentity, IdentityEnum.ADMIN.getIdentity())
//                .set(User::getUpdateTime, new Date())
//                .update();
//    }

    /**
     * 得到随机字符串
     *
     * @param length 长度
     * @return {@code String}
     */
    @SuppressWarnings("all")
    private String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
