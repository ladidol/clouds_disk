package com.feng.controller;

import cn.hutool.core.util.StrUtil;
import com.feng.entity.vo.FileVO;
import com.feng.exception.AppException;
import com.feng.result.Result;
import com.feng.result.ResultUtil;
import com.feng.service.FilesService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

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
     * 描述：上传单个文件
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

    /**
     * 参数：[request, path]
     * 返回值：com.feng.result.Result<java.lang.String>
     * 作者： ladidol
     * 描述：添加一个空文件夹
     */
    @PostMapping("/folder")
    public Result<String> addFolder(HttpServletRequest request,
                                    @RequestParam("path") String path) {
        String userId = (String) request.getAttribute("userId");
        check(userId);
        filesService.addFolder(path, Long.parseLong(userId));
        return ResultUtil.success();
    }

    // TODO: 2022/9/29 把文件夹创建查询在弄清楚一下。
    // todo 迷惑的地方：`@Validated  //多少还是有点迷惑@Validated有啥子用。`


    // TODO: 2022/10/1 这里有问题，好像是    访问STS服务获取临时凭证失败，失败原因:The parameter RoleArn is wrongly formed
    /**
     * 参数：[path, request]
     * 返回值：com.feng.result.Result<com.feng.entity.vo.FileVO>
     * 作者： ladidol
     * 描述：获取单个文件的信息。
     */
    @GetMapping("/info")
    public Result<FileVO> queryFile(@NotNull(message = "文件名不能为空") @RequestParam("path") String path,
                                    HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        check(userId);
        FileVO file = filesService.queryFile(path, Long.parseLong(userId));
        return ResultUtil.success(file);
    }


    /**
     * 参数：[path, request]
     * 返回值：com.feng.result.Result<java.util.List<com.feng.entity.vo.FileVO>>
     * 作者： ladidol
     * 描述：获取文件列表
     */
    @GetMapping("/list")
    public Result<List<FileVO>> queryFiles(@RequestParam("path") String path,
                                           HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        check(userId);
        List<FileVO> vos = filesService.queryFiles(path, Long.parseLong(userId));
        return ResultUtil.success(vos);
    }

    /**
     * 参数：[request, path]
     * 返回值：com.feng.result.Result<java.lang.String>
     * 作者： ladidol
     * 描述：删除单个文件或者文件夹
     */
    @DeleteMapping("/file")
    public Result<String> deleteFile(HttpServletRequest request,
                                     @RequestParam("path") String path) {
        String userId = (String) request.getAttribute("userId");
        check(userId);
        filesService.deleteFileOrFolder(path, Long.parseLong(userId));
        return ResultUtil.success();
    }

    /**
     * 参数：[originPath, targetPath, request]
     * 返回值：com.feng.result.Result<java.lang.String>
     * 作者： ladidol
     * 描述：移动文件夹
     */
    @PostMapping("/move/folder")
    public Result<String> copyFolder(@NotNull(message = "文件夹的名称不能为空") @RequestParam("originPath") String originPath,
                                     @RequestParam("targetPath") String targetPath,
                                     HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        check(userId);
        if (StrUtil.isBlank(targetPath)) {
            targetPath = "/";
        }
        filesService.moveFolder(originPath, targetPath, Long.parseLong(userId));
        return ResultUtil.success();
    }

    /**
     * 参数：[originPath, targetPath, request]
     * 返回值：com.feng.result.Result<java.lang.String>
     * 作者： ladidol
     * 描述：移动文件
     */
    @PostMapping("/move/file")
    public Result<String> copyFile(@NotNull(message = "文件名不能为空") @RequestParam("originPath") String originPath,
                                   @RequestParam("targetPath") String targetPath,
                                   HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        check(userId);
        if (StrUtil.isBlank(targetPath)) {
            targetPath = "/";
        }
        filesService.moveFile(originPath, targetPath, Long.parseLong(userId));
        return ResultUtil.success();
    }

    /**
     * 参数：[originPath, targetPath, request] 
     * 返回值：com.feng.result.Result<java.lang.String>
     * 作者： ladidol
     * 描述：修改文件名字
     */
    @PatchMapping("/update/file")
    public Result<String> updateFileName(@NotNull(message = "文件名不能为空") @RequestParam("originPath") String originPath,
                                         @NotNull(message = "文件名不能为空") @RequestParam("targetPath") String targetPath,
                                         HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        check(userId);
        filesService.updateFileName(targetPath, originPath, Long.parseLong(userId));
        return ResultUtil.success();
    }

    /**
     * 参数：[originPath, targetPath, request] 
     * 返回值：com.feng.result.Result<java.lang.String>
     * 作者： ladidol
     * 描述：修改文件夹名字
     */
    @PatchMapping("/update/folder")
    public Result<String> updateFolder(@NotNull(message = "文件名不能为空") @RequestParam("originPath") String originPath,
                                       @NotNull(message = "文件名不能为空") @RequestParam("targetPath") String targetPath,
                                       HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        check(userId);
        filesService.updateFolderName(targetPath, originPath, Long.parseLong(userId));
        return ResultUtil.success();
    }

    /**
     * 参数：[originPath, targetPath, request] 
     * 返回值：com.feng.result.Result<java.lang.String>
     * 作者： ladidol
     * 描述：复制文件or文件夹
     */
    @PostMapping("/copy")
    public Result<String> copyFileOrFolder(@NotNull(message = "文件名不能为空") @RequestParam("originPath") String originPath,
                                           @NotNull(message = "文件名不能为空") @RequestParam("targetPath") String targetPath,
                                           HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        check(userId);
        filesService.copyFileOrDirectory(originPath, targetPath, Long.parseLong(userId));
        return ResultUtil.success();
    }


    // TODO: 2022/10/1 查看个人所有文件：
    // TODO: 2022/10/1 一些从garbage中查询删除的操作。 
    
    
    
    


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