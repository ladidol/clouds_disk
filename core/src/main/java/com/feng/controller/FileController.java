package com.feng.controller;

import cn.hutool.core.util.StrUtil;
import com.feng.exception.AppException;
import com.feng.result.Result;
import com.feng.result.ResultUtil;
import com.feng.service.FilesService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: ladidol
 * @date: 2022/9/29 20:42
 * @description:
 */


@RestController
@Validated  //多少还是有点迷惑@Validated有啥子用。
public class FileController {


    @Resource
    FilesService filesService;


    /*
     * 参数：[file, request, path]文件，请求，文件要存的路径（会添加到绝对路径上去）
     * 返回值：com.feng.result.Result<java.lang.String>
     * 作者： ladidol
     * 描述：
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 HttpServletRequest request,
                                 @RequestParam("path") String path) {
        String userId = (String) request.getAttribute("userId");
        check(userId);//检查是不是为空。
        filesService.upload(file, Long.parseLong(userId), path);
        return ResultUtil.success();
    }

    // TODO: 2022/9/29 把文件夹创建查询在弄清楚一下。 











    /**
     * 检查
     *
     * @param data 数据
     */
    private void check(String... data) {
        for (String datum : data) {
            if (StrUtil.isBlank(datum)) {
                throw new AppException("发生了未知错误，参数异常，请联系管理员或稍后重试");
            }
        }
    }
}