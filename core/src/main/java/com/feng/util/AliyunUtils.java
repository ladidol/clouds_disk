//package com.feng.util;
//
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.json.JSONUtil;
//import com.aliyun.oss.OSS;
//import com.aliyun.oss.OSSClientBuilder;
//import com.aliyun.oss.OSSException;
//import com.aliyun.oss.common.utils.BinaryUtil;
//import com.aliyun.oss.model.*;
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.http.MethodType;
//import com.aliyuncs.profile.DefaultProfile;
//import com.aliyuncs.profile.IClientProfile;
//import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
//import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
//import cuit.pymjl.constant.StringEnum;
//import cuit.pymjl.entity.pojo.OssCallbackParam;
//import cuit.pymjl.entity.pojo.OssCallbackResult;
//import cuit.pymjl.entity.pojo.OssPolicyResult;
//import cuit.pymjl.entity.pojo.StsMessage;
//import cuit.pymjl.entity.vo.FileVO;
//import cuit.pymjl.exception.AppException;
//import lombok.Data;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import javax.servlet.http.HttpServletRequest;
//import java.awt.*;
//import java.io.*;
//import java.net.URL;
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @author: ladidol
// * @date: 2022/9/27 23:13
// * @description:
// */
//@Data
//@Component
//@Log4j2
//@SuppressWarnings("all")
//public class AliyunUtils {
//
//    /**
//     * STS临时访问凭证有效时间，单位秒
//     */
//    private static final Long STS_EXPIRATION = 3600L;
//    /**
//     * RAM账户ARM信息
//     */
//    private static final String ROLE_ARN = "xxxxxxxxxx";
//    /**
//     * STS接入地址，例如sts.cn-hangzhou.aliyuncs.com。
//     */
//    private static final String STS_ENDPOINT = "sts.cn-shanghai.aliyuncs.com";
//
//    /**
//     * regionId表示RAM的地域ID。以华东1（杭州）地域为例，regionID填写为cn-hangzhou。也可以保留默认值，默认值为空字符串（""）。
//     */
//    private static final String REGION_ID = "cn-shanghai";
//
//    /**
//     * 签名直传时设置最大文件大小，单位MB
//     */
//    private static final long ALIYUN_OSS_MAX_SIZE = 100;
//
//    /**
//     * 签名直传时的文件路径前缀
//     */
//    private static final String PREFIX = "example/";
//
//    /**
//     * 签名直传回调URL，确保该URL外网能访问
//     */
//    private static final String ALIYUN_OSS_CALLBACK = "https://prod.iscsp.xyz/competition/callback";
//
//    private static String endpoint;
//
//    private static String keyId;
//
//    private static String keySecret;
//
//    private static String bucketName;
//
//    /**
//     * 上传文件
//     *
//     * @param objectName 阿里云对象存储名
//     * @param file       上传的文件
//     */
//    public static void upload(String objectName, MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new AppException("文件为空");
//        }
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//        //判断容器是否存在，不存在则创建
//        if (!ossClient.doesBucketExist(bucketName)) {
//            createBucket(ossClient);
//        }
//        try {
//            //将文件转化为流
//            InputStream fileInputStream = file.getInputStream();
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, fileInputStream);
//            ossClient.putObject(putObjectRequest);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            ossClient.shutdown();
//        }
//    }
//
//    /**
//     * 这个方法用于在fileName下创建一个隐藏文件占位
//     *
//     * @param fileName 文件名称 eg: eample/dir/file/
//     */
//    public static void makeFile(String fileName) {
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//        log.info("在{}下创建一个{}文件占位.....", fileName, StringEnum.FILE_IGNORE_NAME.getValue());
//        //判断容器是否存在，不存在则创建
//        if (!ossClient.doesBucketExist(bucketName)) {
//            createBucket(ossClient);
//        }
//        if (!fileName.endsWith("/") || fileName.contains(".")) {
//            throw new AppException("文件名异常");
//        }
//        String objectName = fileName + StringEnum.FILE_IGNORE_NAME.getValue();
//        try {
//            String content = "占位文件，无意义";
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName,
//                    new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
//            ossClient.putObject(putObjectRequest);
//        } finally {
//            ossClient.shutdown();
//        }
//    }
//
//    /**
//     * 创建目录
//     *
//     * @param objectName 阿里云对象存储名
//     */
//    public static void makeDir(String objectName) {
//        if (objectName.charAt(objectName.length() - 1) != '/') {
//            objectName += "/";
//        }
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//        //判断容器是否存在，不存在则创建
//        if (!ossClient.doesBucketExist(bucketName)) {
//            createBucket(ossClient);
//        }
//        try {
//            String content = "";
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName,
//                    new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
//            ossClient.putObject(putObjectRequest);
//        } finally {
//            ossClient.shutdown();
//        }
//    }
//
//    /**
//     * 下载文件，需要Object完整路径，Object完整路径中不能包含Bucket名称
//     *
//     * @param objectName 对象路径
//     */
//    public static void download(String objectName, OutputStream outputStream) {
//        if (objectName == null || "".equals(objectName.trim())) {
//            throw new AppException("对象路径为空");
//        }
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//        BufferedInputStream in = null;
//        BufferedOutputStream out = null;
//        try {
//            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
//            log.info("正在读取文件内容");
//            in = new BufferedInputStream(ossObject.getObjectContent());
//            out = new BufferedOutputStream(outputStream);
//            byte[] buffer = new byte[1024];
//            int length = 0;
//            while ((length = in.read(buffer)) != -1) {
//                out.write(buffer, 0, length);
//            }
//            log.info("读取成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new AppException("读取文件失败");
//        } finally {
//            ossClient.shutdown();
//            if (out != null) {
//                try {
//                    out.flush();
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    /**
//     * 获取指定文件后缀下的所有文件和子文件夹
//     *
//     * @param keyPrefix 指定前缀
//     * @return Map
//     */
//    public static List<FileVO> listFile(String keyPrefix) {
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//
//        try {
//            // 构造ListObjectsV2Request请求。
//            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(bucketName);
//
//            // 设置prefix参数来获取目录下的所有文件与文件夹。
//            listObjectsV2Request.setPrefix(keyPrefix);
//            // 设置正斜线（/）为文件夹的分隔符。
//            listObjectsV2Request.setDelimiter("/");
//
//            // 发起列举请求。
//            ListObjectsV2Result result = ossClient.listObjectsV2(listObjectsV2Request);
//
//            StsMessage stsMessage = getStsMessage();
//            List<FileVO> res = new ArrayList<>();
//            // 遍历文件。
//            System.out.println("Objects:");
//            // objectSummaries的列表中给出的是fun目录下的文件。
//            for (OSSObjectSummary objectSummary : result.getObjectSummaries()) {
//                String fileName = objectSummary.getKey();
//                System.out.println(fileName);
//                if (fileName.equals(keyPrefix) || fileName.contains(StringEnum.FILE_IGNORE_NAME.getValue())) {
//                    continue;
//                }
//                FileVO fileVO = new FileVO(StringEnum.FILE_TYPE_DOC.getValue(), fileName,
//                        getFileUrl(fileName, stsMessage));
//                res.add(fileVO);
//            }
//
//            // 遍历commonPrefix。
//            System.out.println("\nCommonPrefixes:");
//            // commonPrefixs列表中显示的是fun目录下的所有子文件夹。由于fun/movie/001.avi和fun/movie/007.avi属于fun文件夹下的movie目录，因此这两个文件未在列表中。
//            for (String commonPrefix : result.getCommonPrefixes()) {
//                System.out.println(commonPrefix);
//                FileVO fileVO = new FileVO(StringEnum.FILE_TYPE_DIR.getValue(), commonPrefix, null);
//                res.add(fileVO);
//            }
//            return res;
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//            throw new AppException(oe.getErrorMessage());
//        } catch (com.aliyun.oss.ClientException ce) {
//            System.out.println("Caught an ClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with OSS, "
//                    + "such as not being able to access the network.");
//            System.out.println("Error Message:" + ce.getMessage());
//            throw new AppException(ce.getMessage());
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//    }
//
//    /**
//     * 列出指定前缀下的所有文件
//     *
//     * @param keyPrefix 关键前缀
//     * @return {@code List<String>}
//     */
//    @SuppressWarnings("all")
//    public static List<String> listAllPath(String keyPrefix) {
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//
//        try {
//            // 列举文件。如果不设置keyPrefix，则列举存储空间下的所有文件。如果设置keyPrefix，则列举包含指定前缀的文件。
//            ListObjectsV2Result result = ossClient.listObjectsV2(bucketName, keyPrefix);
//            List<OSSObjectSummary> ossObjectSummaries = result.getObjectSummaries();
//
//            List<String> res = new ArrayList<>();
//            for (OSSObjectSummary s : ossObjectSummaries) {
//                String fileName = s.getKey();
//                res.add(fileName);
//            }
//            return res;
//        } catch (OSSException oe) {
//            throw new AppException(oe.getErrorMessage());
//        } catch (com.aliyun.oss.ClientException ce) {
//            throw new AppException(ce.getMessage());
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//    }
//
//    /**
//     * 对返回的文件名进行裁剪
//     *
//     * @param objectName 对象完整路径
//     * @param prefix     文件名前缀
//     * @return String
//     */
//    private static String subFileName(String objectName, String prefix) {
//        if (StringUtils.isEmpty(prefix)) {
//            return objectName;
//        }
//        return objectName.substring(prefix.length());
//    }
//
//    /**
//     * 截断目录
//     *
//     * @param objectName 对象名
//     * @param prefix     前缀
//     * @return String
//     */
//    private static String subDirName(String objectName, String prefix) {
//        int star = 0;
//        if (!StringUtils.isEmpty(prefix)) {
//            star = prefix.length();
//        }
//        int end = objectName.indexOf('/', prefix.length());
//        if (end == -1) {
//            return objectName.substring(star).replace("/", "");
//        }
//        return objectName.substring(star, end).replace("/", "");
//    }
//
//    /**
//     * 根据文件详细名称查看文件外网访问URL
//     *
//     * @param objectName 对象存储完整名
//     * @return FileVO
//     */
//    public static FileVO findFileInfo(String objectName) {
//        if (!objectName.contains(".")) {
//            throw new AppException("文件名不符合规范");
//        }
//        //访问STS服务
//        StsMessage stsMessage = getStsMessage();
//        String fileUrl = getFileUrl(objectName, stsMessage);
//        FileVO fileVO = new FileVO();
//        fileVO.setFileName(objectName);
//        fileVO.setLink(fileUrl);
//        fileVO.setType("file");
//        return fileVO;
//    }
//
//    /**
//     * 签名直传获取policy
//     *
//     * @return OssPolicyResult
//     */
//    public static OssPolicyResult getPolicy() {
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//        OssPolicyResult result = new OssPolicyResult();
//        //签名有效期
//        long expireEndTime = System.currentTimeMillis() + STS_EXPIRATION * 1000;
//        Date expiration = new Date(expireEndTime);
//        // 文件大小
//        long maxSize = ALIYUN_OSS_MAX_SIZE * 1024 * 1024;
//        // 回调
//        OssCallbackParam callback = new OssCallbackParam();
//        callback.setCallbackUrl(ALIYUN_OSS_CALLBACK);
//        callback.setCallbackBody("filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
//        callback.setCallbackBodyType("application/x-www-form-urlencoded");
//        // 提交节点
//        String action = "https://" + bucketName + "." + endpoint;
//        try {
//            PolicyConditions policyConds = new PolicyConditions();
//            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxSize);
//            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, PREFIX);
//            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
//            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
//            String policy = BinaryUtil.toBase64String(binaryData);
//            String signature = ossClient.calculatePostSignature(postPolicy);
//            String callbackData = BinaryUtil.toBase64String(JSONUtil.parse(callback).toString().getBytes("utf-8"));
//            // 返回结果
//            result.setAccessKeyId(keyId);
//            result.setPolicy(policy);
//            result.setSignature(signature);
//            result.setDir(PREFIX);
//            result.setCallback(callbackData);
//            result.setHost(action);
//        } catch (Exception e) {
//            log.error("签名生成失败", e);
//        }
//        return result;
//    }
//
//    /**
//     * 用于签名直传后接受服务器回调
//     *
//     * @param request 请求对象
//     * @return OssCallbackResult
//     */
//    public static OssCallbackResult callback(HttpServletRequest request) {
//        OssCallbackResult result = new OssCallbackResult();
//        String filename = request.getParameter("filename");
//        //私密时获取访问路径
//        StsMessage stsMessage = getStsMessage();
//        String fileUrl = getFileUrl(filename, stsMessage);
//        //公共读时文件访问路径
//        //filename = "http://".concat(bucketName).concat(".").concat(endpoint).concat("/").concat(filename);
//        result.setFilename(fileUrl);
//        result.setSize(request.getParameter("size"));
//        result.setMimeType(request.getParameter("mimeType"));
//        result.setWidth(request.getParameter("width"));
//        result.setHeight(request.getParameter("height"));
//        return result;
//    }
//
//    /**
//     * 删除文件
//     *
//     * @param path 完整的文件路径
//     */
//    public static void deleteFile(String path) {
//        if (StringUtils.isEmpty(path)) {
//            throw new AppException("文件名为空");
//        }
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//        try {
//            // 删除文件或目录。如果要删除目录，目录必须为空。
//            ossClient.deleteObject(bucketName, path);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new AppException("删除失败,原因" + e.getMessage());
//        } finally {
//            // 关闭OSSClient。
//            ossClient.shutdown();
//        }
//    }
//
//    /**
//     * 删除目录及目录下的所有文件
//     *
//     * @param prefix 前缀
//     */
//    public static void deleteDirAndFiles(String prefix) {
//        log.info("开始删除前缀名为{}的文件", prefix);
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//
//        try {
//            // 列举所有包含指定前缀的文件并删除。
//            String nextMarker = null;
//            ObjectListing objectListing = null;
//            do {
//                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName)
//                        .withPrefix(prefix)
//                        .withMarker(nextMarker);
//                objectListing = ossClient.listObjects(listObjectsRequest);
//                if (objectListing.getObjectSummaries().size() > 0) {
//                    List<String> keys = new ArrayList<String>();
//                    for (OSSObjectSummary s : objectListing.getObjectSummaries()) {
////                        System.out.println("key name: " + s.getKey());
//                        keys.add(s.getKey());
//                    }
//                    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keys).withEncodingType("url");
//                    DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(deleteObjectsRequest);
//                    List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
//                    try {
//                        for (String obj : deletedObjects) {
//                            String deleteObj = URLDecoder.decode(obj, "UTF-8");
//                            System.out.println(deleteObj);
//                        }
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                nextMarker = objectListing.getNextMarker();
//            } while (objectListing.isTruncated());
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//            throw new AppException("不存在该文件或目录");
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//    }
//
//    /**
//     * 复制文件
//     * 参数示例:sourcePath = test/dir/R-C.jpg, targetPath=test/dir/test/R-C.jpg
//     *
//     * @param sourcePath 源路径
//     * @param targetPath 目标路径
//     */
//    public static void copyFile(String sourcePath, String targetPath) {
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, keyId, keySecret);
//
//        try {
//            // 拷贝文件。
//            CopyObjectResult result = ossClient.copyObject(bucketName, sourcePath, bucketName, targetPath);
//            log.info("ETag: " + result.getETag() + " LastModified: " + result.getLastModified());
//        } catch (OSSException oe) {
//            throw new AppException(oe.getErrorMessage());
//        } catch (com.aliyun.oss.ClientException ce) {
//            throw new AppException(ce.getMessage());
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//    }
//
//    /**
//     * 复制文件夹(递归复制文件夹下面的文件)
//     *
//     * @param sourcePath 源路径 eg: cloud-disk/files/1/foo/bar/
//     * @param targetPath 目标路径 eg: cloud-disk/files/1/foo/dest/
//     */
//    public static void copyFolder(String sourcePath, String targetPath) {
//        List<String> list = listAllPath(sourcePath);
//        //判断文件夹下面是存在文件
//        if (ObjectUtil.isEmpty(list)) {
//            throw new AppException("云盘中还没有该文件夹或者文件");
//        } else {
//            log.info("开始复制文件夹和文件......");
//            //复制文件夹下的所有文件
//            for (String path : list) {
//                String orginalFileName = path.replaceAll(sourcePath, "");
//                String dest = targetPath + orginalFileName;
//                log.info("start copy.....\norginalPath==>[{}]\ntargetPath==>[{}]", path, dest);
//                copyFile(path, dest);
//            }
//        }
//    }
//
//
//    /**
//     * 判断文件是否为图片
//     *
//     * @param inputStream 输入流对象
//     * @return Boolean
//     */
//    private static Boolean isImage(InputStream inputStream) {
//        if (inputStream == null) {
//            return false;
//        }
//        Image img;
//        try {
//            img = ImageIO.read(inputStream);
//            return !(img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
//        } catch (IOException e) {
//            return false;
//        }
//    }
//
//    /**
//     * 获取STS临时凭证
//     *
//     * @return StsMessage
//     */
//    private static StsMessage getStsMessage() {
//        // 填写步骤1生成的访问密钥AccessKey ID和AccessKey Secret。
//        String accessKeyId = keyId;
//        String accessKeySecret = keySecret;
//        // 自定义角色会话名称，用来区分不同的令牌，例如可填写为SessionTest。
//        String roleSessionName = "FileService";
//        // 以下Policy用于限制仅允许使用临时访问凭证向目标存储空间examplebucket上传文件。
//        // 临时访问凭证最后获得的权限是步骤4设置的角色权限和该Policy设置权限的交集，即仅允许将文件上传至目标存储空间examplebucket下的exampledir目录。
////        String policy = "{\n" +
////                "    \"Version\": \"1\", \n" +
////                "    \"Statement\": [\n" +
////                "        {\n" +
////                "            \"Action\": [\n" +
////                "                \"oss:PutObject\"\n" +
////                "            ], \n" +
////                "            \"Resource\": [\n" +
////                "                \"acs:oss:*:*:examplebucket/*\" \n" +
////                "            ], \n" +
////                "            \"Effect\": \"Allow\"\n" +
////                "        }\n" +
////                "    ]\n" +
////                "}";
//        try {
//            //
//            // 添加sts-endpoint。适用于Java SDK 3.12.0及以上版本。
//            DefaultProfile.addEndpoint(REGION_ID, "Sts", STS_ENDPOINT);
//            // 添加endpoint。适用于Java SDK 3.12.0以下版本。
//            // DefaultProfile.addEndpoint("",regionId, "Sts", endpoint);
//            // 构造default profile。
//            IClientProfile profile = DefaultProfile.getProfile(REGION_ID, accessKeyId, accessKeySecret);
//            // 构造client。
//            DefaultAcsClient client = new DefaultAcsClient(profile);
//            final AssumeRoleRequest request = new AssumeRoleRequest();
//            // 适用于Java SDK 3.12.0及以上版本。
//            request.setSysMethod(MethodType.POST);
//            // 适用于Java SDK 3.12.0以下版本。
//            //request.setMethod(MethodType.POST);
//            request.setRoleArn(ROLE_ARN);
//            request.setRoleSessionName(roleSessionName);
//            // 如果policy为空，则用户将获得该角色下所有权限。
//            request.setPolicy(null);
//            // 设置临时访问凭证的有效时间为3600秒。
//            request.setDurationSeconds(STS_EXPIRATION);
//            final AssumeRoleResponse response = client.getAcsResponse(request);
//            StsMessage message = new StsMessage();
//            message.setExpiration(response.getCredentials().getExpiration());
//            log.info("Expiration: " + message.getExpiration());
//            message.setKeyId(response.getCredentials().getAccessKeyId());
//            log.info("Access Key Id: " + message.getKeyId());
//            message.setSecret(response.getCredentials().getAccessKeySecret());
//            log.info("Access Key Secret: " + message.getSecret());
//            message.setToken(response.getCredentials().getSecurityToken());
//            log.info("Security Token: " + message.getToken());
//            message.setRequestId(response.getRequestId());
//            log.info("RequestId: " + message.getRequestId());
//            return message;
//        } catch (ClientException e) {
//            log.error("Error code: " + e.getErrCode());
//            log.error("Error message: " + e.getErrMsg());
//            log.error("RequestId: " + e.getRequestId());
//            throw new AppException("访问STS服务获取临时凭证失败，失败原因:" + e.getErrMsg());
//        }
//    }
//
//    /**
//     * 在bucket权限为private时，获取文件的外网访问URL
//     *
//     * @param objectName 对象名，即文件在OSS中的路径名
//     * @param stsMessage STS服务临时凭证
//     * @return URL
//     */
//    private static String getFileUrl(String objectName, StsMessage stsMessage) {
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, stsMessage.getKeyId(), stsMessage.getSecret(), stsMessage.getToken());
//
//        // 设置签名URL过期时间为3600秒（1小时）。
//        Date expiration = new Date(System.currentTimeMillis() + STS_EXPIRATION * 1000);
//        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
//        URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
//        //如果未开启https访问请注释下面的代码
//        String httpsUrl = url.toString().replaceAll("http", "https");
//        ossClient.shutdown();
//        log.info(url);
//        return httpsUrl;
//    }
//
//    /**
//     * 创建Bucket
//     *
//     * @param ossClient 阿里云操作对象
//     */
//    private static void createBucket(OSS ossClient) {
//        ossClient.createBucket(bucketName);
//        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
//        createBucketRequest.setCannedACL(CannedAccessControlList.Private);
//        ossClient.createBucket(createBucketRequest);
//    }
//
//
//    @Value("${aliyun.oss.file.endpoint}")
//    public void setEndpoint(String endpoint) {
//        AliyunUtils.endpoint = endpoint;
//    }
//
//    @Value("${aliyun.oss.file.keyId}")
//    public void setKeyId(String keyId) {
//        AliyunUtils.keyId = keyId;
//    }
//
//    @Value("${aliyun.oss.file.keySecret}")
//    public void setKeySecret(String keySecret) {
//        AliyunUtils.keySecret = keySecret;
//    }
//
//    @Value("${aliyun.oss.file.bucketName}")
//    public void setBucketName(String bucketName) {
//        AliyunUtils.bucketName = bucketName;
//    }
//
//
//}
