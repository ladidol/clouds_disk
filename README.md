# clouds_disk
基于jwt登录的云盘系统（后端代码）
主要基于springboot+mybatis-plus+mysql+阿里云oss实现的文件管理系统。

## 登录模块



### **登录逻辑：**



通过username+password+邮箱验证码登录：

1. 验证前端邮箱code是否等于redis中的邮箱code。
2. 验证数据库中是否存在该用户。

### 注册逻辑：

通过

```json
{
    "username": "3455243515@qq.com",
    "password": "123456",
    "nickname": "小号",
    "avatar": "可以为空的头像url",
    "verifyCode":"邮箱验证码"
}
```

注册，通过qq邮箱发送邮箱验证码，在通过上面的json传给后端验证一下。







**redis：**

图片验证码形式 = uid（用户标识）："fasdf2"（图片验证码）

邮箱验证码形式 = 599426945@qq.com（邮箱）："8B6eNm"（邮箱验证码）

jwt存储形式        = eyiJ9.eH0.W3（token字符串）："1"（uid号码）

（本系统中jwt的过期时间是和redis中字段过期时间是设定为一致的）







**现在有点迷惑，就是可能uid（用户标识）是前端生成的，用来生成图片验证码**





### 登录拦截：

通过携带的Bearer Token，得到jwt中的用户id，需要鉴权就可以通过这里开始了。比如本系统正在用的鉴权就是通过：

```java
//进行简单的鉴权
String uri = request.getRequestURI();
if (uri.contains(StringEnum.ADMIN_INTERFACES.getValue())) {// 如果uri中包含admin字样，说明改访问路径必须要鉴权。
    checkIdentity(Long.parseLong(userId));
}


//查看用户是不是admin的方法。
private void checkIdentity(Long userId) {
    UserVO userVO = userService.queryUserById(userId);
    if (!userVO.getIdentity().equals(IdentityEnum.ADMIN.getIdentity())) {
        throw new AppException("权限不足，你没有该权限");
    }
}
```





## 文件管理模块



本模块原来的代码是用的阿里云oss，所以或多或少和普通的文件夹有点不一样。但是可以尝试弄一下。

















### 接口测试：

#### 接口①-文件上传：

未带参数path的：

![image-20220929211832622](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20220929211832622.png)

oss上的结果：

![image-20220929211251414](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20220929211251414.png)



带有参数path的：

![image-20220929211802085](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20220929211802085.png)

oss结果：

![image-20220929211728441](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20220929211728441.png)

值得一提的是，这里的用户1，在阿里云oss中以一个文件夹的形式存在。



#### 接口②-单个文件查询：



![image-20221001220414470](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001220414470.png)





#### 接口③-文件夹下文件列表查询：

![image-20221001220349524](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001220349524.png)



#### 接口④-删除文件or文件夹：

**非永久性删除，是进回收站。**

删除单个文件，直接输入文件名字：

![image-20221001220445449](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001220445449.png)

删除文件夹，直接输入文件夹名字：

![image-20221001220552273](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001220552273.png)

值得注意的是：

![image-20221001220152410](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001220152410.png)



#### 接口⑤-新建文件夹：

新建一个空文件夹。

![image-20221001220812109](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001220812109.png)



#### 接口⑥-移动文件夹：

初始目录有以下文件：![image-20221001221246429](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001221246429.png)



访问接口，执行操作：

![image-20221001221517332](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001221517332.png)

移动后的结果：

![image-20221001221551600](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001221551600.png)



#### 接口⑦-移动文件：

同理移动文件：

![image-20221001221905342](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001221905342.png)



#### 接口⑧-复制文件or文件夹：



![image-20221001222436418](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001222436418.png)

![image-20221001222453720](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001222453720.png)



#### 接口⑨-修改文件名字：

其实就是移动一样！

![image-20221001223043876](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001223043876.png)

结果：

![image-20221001223128697](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001223128697.png)





#### 接口⑩-修改文件夹名字：

其实就是移动一样！



![image-20221001223426238](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001223426238.png)



![image-20221001223432572](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001223432572.png)



#### 接口①①-未完待续：

还有一些从garbage中查询删除的操作。

未完待续。。。









## 一些bug和解决方案：



1. 在调用查询单个文件信息和文件列表等接口是出现：

`访问STS服务获取临时凭证失败，失败原因:You are not authorized to do this action. You should be authorized by RAM.`or

[STS中临时授权访问OSS资源时出现“You are not authorized to do this action. You should be authorized by RAM“报错 (aliyun.com)](https://help.aliyun.com/document_detail/180996.html)，给对应ram用户创建对应的角色，同时将rolearn字符串赋值到代码中去如下。



2. 报错：`the parameter rolearn is wrongly formed`解决方案是：

![image-20221001213335816](https://figurebed-ladidol.oss-cn-chengdu.aliyuncs.com/img/image-20221001213335816.png)


