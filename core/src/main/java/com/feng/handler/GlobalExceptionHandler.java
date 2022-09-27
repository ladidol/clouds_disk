package com.feng.handler;


import com.feng.exception.AppException;
import com.feng.result.Result;
import com.feng.result.ResultUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * @author: ladidol
 * @date: 2022/9/27 23:13
 * @description:
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<String> error(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ResultUtil.fail();
    }

    @ExceptionHandler(AppException.class)
    public Result<String> error(AppException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ResultUtil.fail(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> error(ConstraintViolationException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuilder res = new StringBuilder("参数异常: ");
        constraintViolations.forEach(c -> res.append(c.getMessage()).append(" "));
        return ResultUtil.fail(res.toString().trim());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public Result<String> argumentError(Exception e) {
        e.printStackTrace();
        BindingResult bindingResult = null;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        }
        StringBuilder msg = new StringBuilder();
        assert bindingResult != null;
        bindingResult.getFieldErrors().forEach((fieldError) ->
                msg.append(fieldError.getDefaultMessage()).append(" ")
        );
        log.error(msg);
        return ResultUtil.fail(msg.toString().trim());
    }

    @ExceptionHandler(NullPointerException.class)
    public Result<String> error(NullPointerException e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return ResultUtil.fail("发生了空指针异常,请联系管理员解决");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> error(DuplicateKeyException e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return ResultUtil.fail("重复的参数名，请改名后重新提交");

    }
}
