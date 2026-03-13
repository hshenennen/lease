package com.atguigu.lease.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "创建时间")
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @JsonIgnore
    private Date createTime;

    @Schema(description = "更新时间")
    //字段配置触发填充的时机，@TableField`注解中的fill属性
    @TableField(value = "update_time",fill = FieldFill.UPDATE)
    @JsonIgnore//只需在实体类中的相应字段添加`@JsonIgnore`注解，该字段就会在序列化时被忽略
    private Date updateTime;

    @Schema(description = "逻辑删除")
    @TableField("is_deleted")
    @JsonIgnore
    @TableLogic//逻辑删除功能
    //在实体类中的删除标识字段上增加`@TableLogic`注解
    //它可以自动为查询操作增加`is_deleted=0`过滤条件，并将删除操作转为更新语句
    private Byte isDeleted;

}