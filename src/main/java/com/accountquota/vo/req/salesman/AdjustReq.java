package com.accountquota.vo.req.salesman;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class AdjustReq {
    @ApiModelProperty("用户额度账户类型 0 额度账户0 1 额度账户1 ......")
    @NotNull(message = "参数accountType不得为空")
    private Integer accountType;
    @ApiModelProperty("用户id")
    @NotNull(message = "参数clientId不得为空")
    private Integer clientId;
    @ApiModelProperty("调整的额度值")
    @NotNull(message = "参数quotaValue不得为空")
    private BigDecimal quotaValue;
    @ApiModelProperty("0 减少额度 1 增加额度")
    @Min(value = 0,message = "参数 adjustType 取值范围为 0 减少额度 1 增加额度")
    @Max(value = 1,message = "参数 adjustType 取值范围为 0 减少额度 1 增加额度")
    private Integer adjustType;

}
