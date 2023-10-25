package com.accountquota.controller;

import com.accountquota.annotations.RoleCheck;
import com.accountquota.bean.BaseResult;
import com.accountquota.constant.SystemDefaultConstants;
import com.accountquota.enums.ClientTypeEnum;
import com.accountquota.enums.RetCodeEnum;
import com.accountquota.facade.SalesmanFacade;
import com.accountquota.util.DecimalUtil;
import com.accountquota.vo.req.salesman.AdjustReq;
import com.accountquota.vo.req.salesman.InitReq;
import com.accountquota.vo.resp.salesman.AdjustResp;
import com.accountquota.vo.resp.salesman.InitResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/quota/salesman")
@Api(tags = "业务操作额度账户")
@Validated
public class SalesmanQuotaController {

    @Autowired
    private SalesmanFacade salesmanFacade;

    @ApiOperation("业务初始化用户额度账户")
    @RequestMapping(value = "/init", method = RequestMethod.POST)
    @RoleCheck(role = ClientTypeEnum.SALESMAN)
    public @ResponseBody BaseResult<InitResp> init(@Validated @RequestBody InitReq entry) {
        if (entry.getQuotaValue() == null) {
            entry.setQuotaValue(SystemDefaultConstants.INIT_QUOTA_VALUE);
        }
        if (entry.getQuotaValue().compareTo(new BigDecimal(0)) < 0) {
            return BaseResult.error(RetCodeEnum.PARAMETER_ERROR.getCode(), "初始额度必须大于0");
        }
        if(!DecimalUtil.checkDecimal(entry.getQuotaValue())){
            return BaseResult.error(RetCodeEnum.PARAMETER_ERROR.getCode(), "初始额度精度异常");
        }
        BaseResult<InitResp> baseResult = new BaseResult<>();
        InitResp initResp =salesmanFacade.init(entry);
        baseResult.setData(initResp);
        return baseResult;
    }

    @ApiOperation("业务调整用户额度")
    @RoleCheck(role = ClientTypeEnum.SALESMAN)
    @RequestMapping(value = "/adjust", method = RequestMethod.POST)
    public @ResponseBody BaseResult<AdjustResp> adjust(@Validated @RequestBody AdjustReq entry) {
        if (entry.getQuotaValue().compareTo(new BigDecimal(0)) < 0) {
            return BaseResult.error(RetCodeEnum.PARAMETER_ERROR.getCode(), "调整额度大于0");
        }
        if(!DecimalUtil.checkDecimal(entry.getQuotaValue())){
            return BaseResult.error(RetCodeEnum.PARAMETER_ERROR.getCode(), "初始额度精度异常");
        }
        BaseResult<AdjustResp> baseResult = new BaseResult<>();
        AdjustResp adjustResp= salesmanFacade.adjust(entry);
        baseResult.setData(adjustResp);
        return baseResult;
    }


}
