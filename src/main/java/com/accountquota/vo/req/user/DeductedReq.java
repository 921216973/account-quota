package com.accountquota.vo.req.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class DeductedReq {
    @ApiModelProperty("用户额度账户类型 0 额度账户0 1 额度账户1 ......")
    @NotNull(message = "参数type不得为空")
    private Integer type;
    @ApiModelProperty("扣减额度值")
    @NotNull(message = "参数quotaValue不得为空")
    private BigDecimal quotaValue;
}
