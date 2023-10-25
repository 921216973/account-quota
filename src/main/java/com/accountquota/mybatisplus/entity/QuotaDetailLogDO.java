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
 * 额度流水表
 * </p>
 *
 * @author zql
 * @since 2023-10-23
 */
@Data
@TableName("quota_detail_log")
public class QuotaDetailLogDO {

    @TableId(value ="detail_id", type = IdType.AUTO)
    private Integer detailId;

    @TableField("create_time")
    private Date createTime;

    /**
     * 额度账户id
     */
    @TableField("quota_id")
    private Integer quotaId;

    /**
     * 额度流水值
     */
    @TableField("quota_value")
    private BigDecimal quotaValue;


}
