package com.accountquota.vo.resp.user;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SearchResp {
    @ApiModelProperty("用户额度账户类型 0 额度账户0 1 额度账户1 ......")
    private Integer type;

    private Date updateTime;

    private Date createTime;

    @ApiModelProperty("额度上限")
    private BigDecimal accountQuota;

    @ApiModelProperty("剩余额度")
    private BigDecimal currentQuota;

}
