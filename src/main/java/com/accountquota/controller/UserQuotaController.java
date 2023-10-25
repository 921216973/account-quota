package com.accountquota.controller;

import com.accountquota.annotations.RoleCheck;
import com.accountquota.bean.BaseResult;
import com.accountquota.enums.RetCodeEnum;
import com.accountquota.enums.ClientTypeEnum;
import com.accountquota.facade.UserFacade;
import com.accountquota.util.DecimalUtil;
import com.accountquota.vo.req.user.DeductedReq;
import com.accountquota.vo.req.user.SearchReq;
import com.accountquota.vo.resp.user.DeductedResp;
import com.accountquota.vo.resp.user.SearchResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/quota/user")
@Api(tags = "用户操作额度账户")
@Validated
public class UserQuotaController {

    @Autowired
    private UserFacade userFacade;

    @ApiOperation("用户查询用户额度")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @RoleCheck(role = ClientTypeEnum.USER)
    public @ResponseBody BaseResult<SearchResp> search(@Validated @RequestBody SearchReq entry) {
        BaseResult<SearchResp> baseResult = new BaseResult<>();
        SearchResp searchResp = userFacade.search(entry);
        baseResult.setData(searchResp);
        return baseResult;
    }

    @ApiOperation("用户扣减额度")
    @RequestMapping(value = "/deducted", method = RequestMethod.POST)
    @RoleCheck(role = ClientTypeEnum.USER)
    public @ResponseBody BaseResult<DeductedResp> deducted(@Validated @RequestBody DeductedReq entry) {
        if (entry.getQuotaValue().compareTo(new BigDecimal(0)) < 0) {
            return BaseResult.error(RetCodeEnum.PARAMETER_ERROR.getCode(), "扣减额度必须大于0");
        }
        if(!DecimalUtil.checkDecimal(entry.getQuotaValue())){
            return BaseResult.error(RetCodeEnum.PARAMETER_ERROR.getCode(), "初始额度精度异常");
        }
        BaseResult<DeductedResp> baseResult = new BaseResult<>();
        DeductedResp searchResp = userFacade.deducted(entry);
        baseResult.setData(searchResp);
        return baseResult;
    }

}
