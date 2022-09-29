package com.feng.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @projectName: jwtlogin
 * @package: com.feng.controller
 * @className: HelloController
 * @author: Ladidol
 * @description:
 * @date: 2022/9/27 20:52
 * @version: 1.0
 */

@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping
    public String hello() {
        System.out.println("nihao = " + new Date(System.currentTimeMillis()));
        return "hello, now is "+ new Date(System.currentTimeMillis());
    }
    @GetMapping("/admin")
    public String helloAdmin() {
        System.out.println("nihao = " + new Date(System.currentTimeMillis()));
        return "hello admin, now is "+ new Date(System.currentTimeMillis());
    }
}
