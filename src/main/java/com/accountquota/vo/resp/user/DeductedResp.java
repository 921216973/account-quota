package com.accountquota.vo.resp.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class DeductedResp {
    @ApiModelProperty("额度上限")
    private BigDecimal accountQuota;

    @ApiModelProperty("剩余额度")
    private BigDecimal currentQuota;
}
