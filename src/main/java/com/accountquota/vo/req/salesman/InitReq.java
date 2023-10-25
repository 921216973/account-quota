package com.accountquota.vo.req.salesman;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class InitReq {
    @ApiModelProperty("用户额度账户类型 0 额度账户0 1 额度账户1 ......")
    @NotNull(message = "参数type不得为空")
    private Integer type;
    @ApiModelProperty("用户id")
    @NotNull(message = "参数clientId不得为空")
    private Integer clientId;
    @ApiModelProperty("用户账户额度")
    private BigDecimal quotaValue;
}
