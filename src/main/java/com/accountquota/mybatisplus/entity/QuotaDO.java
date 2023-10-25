package com.accountquota.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户额度账户记录表
 * </p>
 *
 * @author zql
 * @since 2023-10-23
 */
@Data
@TableName("quota")
public class QuotaDO {

    @TableId(value = "quota_id", type = IdType.AUTO)
    private Integer quotaId;

    /**
     * 用户额度账户类型 0 额度账户0 1 额度账户1 ......
     */
    @TableField("type")
    private Integer type;

    @TableField("update_time")
    private Date updateTime;

    @TableField("create_time")
    private Date createTime;

    /**
     * 用户id
     */
    @TableField("client_id")
    private Integer clientId;

    /**
     * 账户状态 0 停用 1 正常
     */
    @TableField("status")
    private Integer status;

    /**
     * 账户额度
     */
    @TableField("account_quota")
    private BigDecimal accountQuota;

    /**
     * 当前额度
     */
    @TableField("current_quota")
    private BigDecimal currentQuota;


}
