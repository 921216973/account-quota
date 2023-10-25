package com.accountquota.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 用户信息记录表
 * </p>
 *
 * @author zql
 * @since 2023-10-23
 */
@Data
@TableName("client")
public class ClientDO {

    @TableId(value = "client_id", type = IdType.AUTO)
    private Integer clientId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    /**
     * 0 停用 1 正常
     */
    @TableField("status")
    private Integer status;
    /**
     * 0 停用 1 正常
     */
    @TableField("type")
    private Integer type;


}
