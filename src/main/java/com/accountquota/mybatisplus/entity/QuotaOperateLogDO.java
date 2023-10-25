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
 * 额度操作表
 * </p>
 *
 * @author zql
 * @since 2023-10-23
 */
@Data
@TableName("quota_operate_log")
public class QuotaOperateLogDO {

    @TableId(value = "operate_id", type = IdType.AUTO)
    private Integer operateId;

    @TableField("create_time")
    private Date createTime;

    /**
     * 0 减少额度 1 增加额度
     */
    @TableField("type")
    private Integer type;

    /**
     * 额度账户id
     */
    @TableField("quota_id")
    private Integer quotaId;

    /**
     * 操作额度值
     */
    @TableField("quota_value")
    private BigDecimal quotaValue;


}
