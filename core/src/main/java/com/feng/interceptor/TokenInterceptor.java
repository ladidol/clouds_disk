package com.feng.interceptor;

import cn.hutool.core.util.StrUtil;

import com.feng.constant.IdentityEnum;
import com.feng.constant.StringEnum;
import com.feng.entity.vo.UserVO;
import com.feng.exception.AppException;
import com.feng.service.UserService;
import com.feng.util.JwtUtils;
import com.feng.util.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: ladidol
 * @date: 2022/9/27 23:13
 * @description:
 */
@Log4j2
@Component
public class TokenInterceptor implements HandlerInterceptor {
    private static final String BEARER = "Bearer ";
    @Resource
    RedisUtil redisUtil;
    @Resource
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("开始从Authorization获取token......");
        String header = request.getHeader("Authorization");
        if (header != null && !"".equals(header)) {
            if (header.startsWith(BEARER)) {
                //获得token
                String token = header.substring(7);
                log.info("获取到的token==>[{}]", token);
                Claims claims = JwtUtils.verifyJwt(token);
                String userId = (String) redisUtil.get(token);
                if (StrUtil.isBlank(userId)) {
                    throw new AppException("token已过期,请重新登录");
                }
                log.info("获取到的用户ID==>[{}]", userId);
                //进行简单的鉴权
                String uri = request.getRequestURI();
                if (uri.contains(StringEnum.ADMIN_INTERFACES.getValue())) {// 如果uri中包含admin字样，说明改访问路径必须要鉴权。
                    checkIdentity(Long.parseLong(userId));
                }
                request.setAttribute("userId", userId);
                request.setAttribute("token", token);
                return true;
            }
        }
        throw new AppException("请先登录");
    }

    private void checkIdentity(Long userId) {
        UserVO userVO = userService.queryUserById(userId);
        if (!userVO.getIdentity().equals(IdentityEnum.ADMIN.getIdentity())) {
            throw new AppException("权限不足，你没有该权限");
        }
    }
}
