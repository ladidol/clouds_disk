package com.feng.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.feng.entity.File;
import com.feng.entity.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author: ladidol
 * @date: 2022/9/29 20:54
 * @description:
 */
public interface FilesService extends IService<File> {
    /**
     * 上传
     * 上传文件
     *
     * @param file   文件
     * @param userId 用户id
     * @param path   路径
     */
    void upload(MultipartFile file, Long userId, String path);

    /**
     * 查询文件信息
     *
     * @param path   路径
     * @param userId 用户id
     * @return {@code FileVO}
     */
//    FileVO queryFile(String path, Long userId);
//
//    /**
//     * 查询文件列表
//     *
//     * @param path   路径
//     * @param userId 用户id
//     * @return {@code List<FileVO>}
//     */
//    List<FileVO> queryFiles(String path, Long userId);
//
//    /**
//     * 查询文件从垃圾
//     *
//     * @param path   路径
//     * @param userId 用户id
//     * @return {@code List<FileVO>}
//     */
//    List<FileVO> queryFilesFromGarbage(String path, Long userId);
//
//    /**
//     * 更新文件夹名字
//     *
//     * @param newFileName  文件名称
//     * @param originalName 原来的名字
//     * @param userId       用户id
//     */
//    void updateFolderName(String newFileName, String originalName, Long userId);
//
//
//    /**
//     * 更新文件名字
//     *
//     * @param newFileName  新文件名字
//     * @param originalName 原来名字
//     * @param userId       用户id
//     */
//    void updateFileName(String newFileName, String originalName, Long userId);
//
//    /**
//     * 删除文件或文件夹
//     *
//     * @param originalFileName 原始文件名字
//     * @param userId           用户id
//     */
//    void deleteFileOrFolder(String originalFileName, Long userId);
//
//    /**
//     * 移动文件夹,意思就是将dest文件夹移动到bar目录下，移动后的目录为foo/bar/dest/xxx
//     *
//     * @param originalPath 原始路径 eg:foo/dest/
//     * @param targetPath   目标路径 eg:foo/bar/
//     * @param userId       用户id
//     */
//    void moveFolder(String originalPath, String targetPath, Long userId);
//
//    /**
//     * 移动文件
//     *
//     * @param originalPath 原始路径
//     * @param targetPath   目标路径
//     * @param userId       用户id
//     */
//    void moveFile(String originalPath, String targetPath, Long userId);
//
//    /**
//     * 永远删除文件
//     *
//     * @param targetFileName 目标文件名字
//     * @param userId         用户id
//     */
//    void deleteFileForever(String targetFileName, Long userId);
//
//    /**
//     * 复制文件或目录
//     *
//     * @param originalName 原来名字
//     * @param targetName   目标名称
//     * @param userId       用户id
//     */
//    void copyFileOrDirectory(String originalName, String targetName, Long userId);
//
//    /**
//     * 添加文件夹
//     *
//     * @param folderName 文件夹名称
//     * @param userId     用户id
//     */
//    void addFolder(String folderName, Long userId);
//
//    /**
//     * 恢复文件
//     *
//     * @param fileName 文件名称
//     * @param userId   用户id
//     */
//    void recoverFile(String fileName, Long userId);
//
//    /**
//     * 恢复文件夹
//     *
//     * @param fileName 文件名称
//     * @param userId   用户id
//     */
//    void recoverFolder(String fileName, Long userId);

}
