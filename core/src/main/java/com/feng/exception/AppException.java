package com.feng.exception;

/**
 * @author: ladidol
 * @date: 2022/9/27 23:13
 * @description:
 */
public class AppException extends RuntimeException {
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(String message) {
        super(message);
    }
}
