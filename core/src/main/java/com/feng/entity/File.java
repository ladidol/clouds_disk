package com.feng.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: ladidol
 * @date: 2022/9/29 20:54
 * @description:
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_files")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，唯一，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 阿里云OSS对象名（即路径）
     */
    private String filePath;

    /**
     * 拥有者的id
     */
    private Long ownerId;

    /**
     * 类型
     */
    private String type;

    /**
     * 逻辑删除，默认0，1表示该文件已被删除，存在回收站中
     */
    private Integer isDeleted;

    /**
     * 文件大小，默认0，单位byte
     */
    private Long size;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 删除时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
