package com.feng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.feng.constant.StringEnum;
import com.feng.entity.File;
import com.feng.entity.vo.FileVO;
import com.feng.exception.AppException;
import com.feng.mapper.FilesMapper;
import com.feng.service.FilesService;
import com.feng.util.AliyunUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * @author: ladidol
 * @date: 2022/9/29 20:54
 * @description:
 */
@Service
@Slf4j
public class FilesServiceImpl extends ServiceImpl<FilesMapper, File> implements FilesService {

    private final static String SEPARATOR = "/";

    @Override
    public void upload(MultipartFile file, Long userId, String path) {
        log.info("开始上传文件........");
        String name = file.getOriginalFilename();
        log.info("文件名为==>[{}]", name);
        while (path != null && path.startsWith(SEPARATOR)) {
            path = path.substring(1, path.length());
        }
        String objectName = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + path + name;

        //上传文件到阿里云OSS
        log.info("文件的完整路径为==>[{}],开始上传文件到阿里云OSS...", objectName);
        AliyunUtils.upload(objectName, file);
        log.info("文件上传成功");
    }

    @Override
    public FileVO queryFile(String path, Long userId) {
        while (path.startsWith(SEPARATOR)) {
            path = path.substring(1);
        }
        //拼接完整的文件名
        String objectName = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + path;
        log.info("完整的路径名为==>[{}]", objectName);
        FileVO fileInfo = AliyunUtils.findFileInfo(objectName);
        fileInfo.setFileName(fileInfo.getFileName().substring(fileInfo.getFileName().lastIndexOf("/") + 1));
        return fileInfo;
    }

    //
//    @Override
//    @SuppressWarnings("all")
//    public List<FileVO> queryFilesFromGarbage(String path, Long userId) {
//        while (path.startsWith(SEPARATOR)) {
//            path = path.substring(1);
//        }
//        path = StringEnum.FILE_DEFAULT_GARBAGE_PREFIX.getValue() + userId + SEPARATOR + path;
//        if (!path.endsWith(SEPARATOR)) {
//            path += SEPARATOR;
//        }
//        log.info("完整的查找路径为==>{}", path);
//        List<FileVO> vos = AliyunUtils.listFile(path);
//        for (FileVO file : vos) {
//            file.setFileName(file.getFileName().replaceAll(path, ""));
//        }
//        return vos;
//    }
//
    @Override
    public List<FileVO> queryFiles(String path, Long userId) {
        while (path.startsWith(SEPARATOR)) {
            path = path.substring(1);
        }
        path = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + path;
        if (!path.endsWith(SEPARATOR)) {
            path += SEPARATOR;
        }
        log.info("完整的查找路径为==>{}", path);
        List<FileVO> vos = AliyunUtils.listFile(path);
        for (FileVO file : vos) {
            file.setFileName(file.getFileName().replaceAll(path, ""));
        }
        return vos;
    }

    @Override
    public void updateFolderName(String newFileName, String originalName, Long userId) {
        //因为阿里云OSS不支持更新文件名称，只能重新上传，所以我们需要对文件进行拷贝再删除
        while (originalName.startsWith(SEPARATOR)) {
            originalName = originalName.substring(1, originalName.length());
        }
        while (newFileName.startsWith(SEPARATOR)) {
            newFileName = newFileName.substring(1, newFileName.length());
        }
        if (!originalName.endsWith(SEPARATOR) || !newFileName.endsWith(SEPARATOR)) {
            throw new AppException("文件夹不符合规范");
        }
        String sourcePath = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + originalName;
        String destPath = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + newFileName;
        moveFolder(sourcePath, destPath);
    }

    @Override
    public void updateFileName(String newFileName, String originalName, Long userId) {
        moveFile(originalName, newFileName, userId);
    }

    @Override
    public void deleteFileOrFolder(String originalFileName, Long userId) {
        //校验文件名
        while (originalFileName.startsWith("/")) {
            originalFileName = originalFileName.substring(1);
        }

        //拼接文件名
        String originalPath = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + originalFileName;
        String targetPath = StringEnum.FILE_DEFAULT_GARBAGE_PREFIX.getValue() + userId + SEPARATOR + originalFileName;
        AliyunUtils.copyFolder(originalPath, targetPath);

        //开始将文件移动到回收站
        if (originalPath.contains(".")) {
            moveFile(originalPath, targetPath);
        } else {
            moveFolder(originalPath, targetPath);
        }
        //因为阿里云OSS不支持文件夹的概念，一旦删除，不会保留空文件夹，所以需要创建一个空的文件夹占位
        makeIgnoreFile(originalPath);
    }


    @Override
    public void moveFolder(String originalPath, String targetPath, Long userId) {
        while (originalPath.startsWith(SEPARATOR)) {
            originalPath = originalPath.substring(1, originalPath.length());
        }
        while (targetPath.startsWith(SEPARATOR)) {
            targetPath = targetPath.substring(1, targetPath.length());
        }
        if (!originalPath.endsWith(SEPARATOR)) {
            throw new AppException("文件夹不符合规范");
        }
        String sourcePath = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + originalPath;
        String name = originalPath.substring(originalPath.lastIndexOf("/", originalPath.lastIndexOf("/") - 1) + 1);
        String destPath = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + targetPath + name;
        moveFolder(sourcePath, destPath);
    }


    @Override
    @SuppressWarnings("all")
    public void moveFile(String originalPath, String targetPath, Long userId) {
        while (originalPath.startsWith(SEPARATOR)) {
            originalPath = originalPath.substring(1, originalPath.length());
        }
        while (targetPath.startsWith(SEPARATOR)) {
            targetPath = targetPath.substring(1, targetPath.length());
        }
        if (!originalPath.contains(".") || !targetPath.contains(".")) {
            throw new AppException("文件名不符合规范");
        }
        String sourcePath = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + originalPath;
        String destPath = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + targetPath;
        moveFile(sourcePath, destPath);
    }

//    /**
//     * 永远删除文件
//     *
//     * @param targetFileName 目标文件名字
//     * @param userId         用户id
//     */
//    @Override
//    public void deleteFileForever(String targetFileName, Long userId) {
//        while (targetFileName.startsWith("/")) {
//            targetFileName = targetFileName.substring(1);
//        }
//        //拼接文件名
//        String targetPath = StringEnum.FILE_DEFAULT_GARBAGE_PREFIX.getValue() + userId + SEPARATOR + targetFileName;
//        log.info("目标文件的完整路径名为==>[{}]", targetPath);
//        //开始删除
//        AliyunUtils.deleteDirAndFiles(targetPath);
//        //因为阿里云OSS不支持文件夹的概念，一旦删除，不会保留空文件夹，所以需要创建一个空的文件夹占位
//        makeIgnoreFile(targetPath);
//    }

    /**
     * 复制文件或目录
     *
     * @param originalName 原来名字
     * @param targetName   目标名称
     * @param userId       用户id
     */
    @Override
    public void copyFileOrDirectory(String originalName, String targetName, Long userId) {
        //检验文件名是否合法
        while (originalName.startsWith(SEPARATOR)) {
            originalName = originalName.substring(1);
        }
        while (targetName.startsWith(SEPARATOR)) {
            targetName = targetName.substring(1);
        }
        //拼接文件名
        String originalPath = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + originalName;
        String targetPath = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + targetName;
        //开始拷贝
        AliyunUtils.copyFolder(originalPath, targetPath);
    }

    @Override
    public void addFolder(String folderName, Long userId) {
        //检验文件名是否合法
        while (folderName.startsWith(SEPARATOR)) {
            folderName = folderName.substring(1);
        }
        if (!folderName.endsWith(SEPARATOR)) {
            folderName += SEPARATOR;
        }
        //拼接文件名
        String originalPath = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + folderName;
        AliyunUtils.makeFile(originalPath);
    }

    /**
     * 创建忽略文件
     *
     * @param originalPath 原始路径
     */
    private void makeIgnoreFile(String originalPath) {
        String prefix = null;
        if (originalPath.contains(".")) {
            //文件
            prefix = originalPath.substring(0, originalPath.lastIndexOf(SEPARATOR) + 1);
        } else {
            //文件夹的情况
            prefix = originalPath.substring(0, originalPath.lastIndexOf(SEPARATOR, originalPath.lastIndexOf(SEPARATOR) - 1) + 1);
        }
        AliyunUtils.makeFile(prefix);
    }

    /**
     * 得到文件链接
     *
     * @param path 路径
     * @return {@code String}
     */
    private String getFileLink(String path) {
        return AliyunUtils.findFileInfo(path).getLink();
    }

    private void moveFile(String originalPath, String targetPath) {
        log.info("开始移动文件.......\n将文件[{}]移动到[{}]", originalPath, targetPath);
        AliyunUtils.copyFile(originalPath, targetPath);
        //删除源文件
        AliyunUtils.deleteDirAndFiles(originalPath);
        //因为阿里云OSS不支持文件夹的概念，一旦删除，不会保留空文件夹，所以需要创建一个空的文件夹占位
        makeIgnoreFile(originalPath);
    }

    private void moveFolder(String originalPath, String targetPath) {
        log.info("开始移动文件夹......\n将文件夹[{}]移动到[{}]", originalPath, targetPath);
        AliyunUtils.copyFolder(originalPath, targetPath);
        AliyunUtils.deleteDirAndFiles(originalPath);
        //因为阿里云OSS不支持文件夹的概念，一旦删除，不会保留空文件夹，所以需要创建一个空的文件夹占位
        makeIgnoreFile(originalPath);
    }
//
//    @Override
//    public void recoverFolder(String fileName, Long userId) {
//        log.info("开始恢复文件.......");
//        if (!fileName.endsWith(SEPARATOR)) {
//            throw new AppException("文件名不符合规范");
//        }
//        //拼接文件名
//        String file = StringEnum.FILE_DEFAULT_GARBAGE_PREFIX.getValue() + userId + SEPARATOR + fileName;
//        log.info("要恢复的文件为==>{}", file);
//        String target = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + fileName;
//        moveFolder(file, target);
//    }
//
//    @Override
//    public void recoverFile(String fileName, Long userId) {
//        log.info("开始恢复文件.......");
//        //拼接文件名
//        String file = StringEnum.FILE_DEFAULT_GARBAGE_PREFIX.getValue() + userId + SEPARATOR + fileName;
//        log.info("要恢复的文件为==>{}", file);
//        String target = StringEnum.FILE_DEFAULT_PREFIX.getValue() + userId + SEPARATOR + fileName;
//        moveFile(file, target);
//    }
}
