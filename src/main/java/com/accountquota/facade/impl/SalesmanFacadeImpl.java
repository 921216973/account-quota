package com.accountquota.facade.impl;

import com.accountquota.aspect.RoleCheckAspect;
import com.accountquota.config.ThreadContext;
import com.accountquota.constant.RedisCacheConstant;
import com.accountquota.enums.AdjustTypeEnum;
import com.accountquota.enums.InfoStatusEnum;
import com.accountquota.enums.RetCodeEnum;
import com.accountquota.exception.ScException;
import com.accountquota.facade.SalesmanFacade;
import com.accountquota.facade.SystemFacade;
import com.accountquota.mybatisplus.entity.QuotaDO;
import com.accountquota.mybatisplus.entity.QuotaOperateLogDO;
import com.accountquota.mybatisplus.service.QuotaMybatisPlusService;
import com.accountquota.mybatisplus.service.QuotaOperateLogMybatisPlusService;
import com.accountquota.vo.req.salesman.AdjustReq;
import com.accountquota.vo.req.salesman.InitReq;
import com.accountquota.vo.resp.salesman.AdjustResp;
import com.accountquota.vo.resp.salesman.InitResp;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(rollbackFor = Exception.class)
public class SalesmanFacadeImpl implements SalesmanFacade {
    private static Logger logger = LoggerFactory.getLogger(SalesmanFacadeImpl.class);
    @Autowired
    private QuotaMybatisPlusService quotaService;
    @Autowired
    private SystemFacade systemFacade;
    @Autowired
    private QuotaOperateLogMybatisPlusService quotaOperateLogService;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public InitResp init(InitReq entry) {
        //判断操作的用户是否正常
        if (!systemFacade.isNormalUser(entry.getClientId())) {
            throw new ScException(RetCodeEnum.USER_ERROR.getCode(), "用户异常，无法操作");
        }
        RLock redissonLock = redissonClient.getLock(RedisCacheConstant.USER_KEY_LOCK + entry.getClientId() + "-" + entry.getType());
        try {
            boolean isLockBoolean = redissonLock.tryLock(3, 10, TimeUnit.SECONDS);
            if (isLockBoolean) {
                //查询额度账户是否存在
                LambdaQueryWrapper<QuotaDO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(QuotaDO::getType, entry.getType());
                queryWrapper.eq(QuotaDO::getClientId, entry.getClientId());
                queryWrapper.eq(QuotaDO::getStatus, InfoStatusEnum.NORMAL.getCode());
                QuotaDO searchQuota = quotaService.getOne(queryWrapper);
                if (searchQuota != null) {
                    throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "额度账户已存在");
                }
                //初始化额度账户
                QuotaDO quotaDO = new QuotaDO();
                quotaDO.setType(entry.getType());
                quotaDO.setClientId(entry.getClientId());
                quotaDO.setStatus(InfoStatusEnum.NORMAL.getCode());
                quotaDO.setAccountQuota(entry.getQuotaValue());
                quotaDO.setCurrentQuota(entry.getQuotaValue());
                boolean saveQuota = quotaService.save(quotaDO);
                if (!saveQuota) {
                    throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "初始化额度账户异常");
                }
                //额度账户操作记录
                QuotaOperateLogDO quotaOperateLogDO = new QuotaOperateLogDO();
                quotaOperateLogDO.setQuotaId(quotaDO.getQuotaId());
                quotaOperateLogDO.setType(AdjustTypeEnum.ADD.getCode());
                quotaOperateLogDO.setQuotaValue(entry.getQuotaValue());
                boolean saveQuotaOperateLog = quotaOperateLogService.save(quotaOperateLogDO);
                if (!saveQuotaOperateLog) {
                    throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "初始化额度账户异常");
                }
            }
        } catch (Exception e) {
            logger.error("初始化账户异常：{}", e.getMessage(), e);
            throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), e.getMessage());
        } finally {
            redissonLock.unlock();
        }
        InitResp initResp = new InitResp();
        initResp.setAccountQuota(entry.getQuotaValue());
        initResp.setCurrentQuota(entry.getQuotaValue());
        return initResp;
    }

    @Override
    public AdjustResp adjust(AdjustReq entry) {
        if (!systemFacade.isNormalUser(entry.getClientId())) {
            throw new ScException(RetCodeEnum.USER_ERROR.getCode(), "用户异常，无法操作");
        }
        AdjustResp adjustResp = new AdjustResp();
        RLock redissonLock = redissonClient.getLock(RedisCacheConstant.QUOTA_KEY_LOCK + entry.getClientId() + "-" + entry.getAccountType());
        try {
            boolean isLockBoolean = redissonLock.tryLock(3, 10, TimeUnit.SECONDS);
            if (isLockBoolean) {
                //判断账户是否存在
                LambdaQueryWrapper<QuotaDO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(QuotaDO::getType, entry.getAccountType());
                queryWrapper.eq(QuotaDO::getClientId, entry.getClientId());
                queryWrapper.eq(QuotaDO::getStatus, InfoStatusEnum.NORMAL.getCode());
                QuotaDO searchQuota = quotaService.getOne(queryWrapper);
                if (searchQuota == null) {
                    throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "额度账户不存在");
                }
                //增加额度
                if (entry.getAdjustType() == AdjustTypeEnum.ADD.getCode()) {
                    LambdaUpdateWrapper<QuotaDO> quotaUpdate = new LambdaUpdateWrapper<>();
                    quotaUpdate.eq(QuotaDO::getQuotaId, searchQuota.getQuotaId());
                    quotaUpdate.set(QuotaDO::getCurrentQuota, searchQuota.getCurrentQuota().add(entry.getQuotaValue()));
                    quotaUpdate.set(QuotaDO::getAccountQuota, searchQuota.getAccountQuota().add(entry.getQuotaValue()));
                    boolean update = quotaService.update(quotaUpdate);
                    if (!update) {
                        throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "增加额度异常");
                    }
                    adjustResp.setAccountQuota(searchQuota.getAccountQuota().add(entry.getQuotaValue()));
                    adjustResp.setCurrentQuota(searchQuota.getCurrentQuota().add(entry.getQuotaValue()));
                }
                //扣减额度
                if (entry.getAdjustType() == AdjustTypeEnum.SUBTRACT.getCode()) {
                    //判断扣减额度是否大于余额，大于余额则无法继续进行扣减
                    if (entry.getQuotaValue().compareTo(searchQuota.getCurrentQuota()) > 0) {
                        throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "扣减额度大于所剩额度，无法继续进行扣减");
                    }
                    //扣减额度上限和所剩余额
                    LambdaUpdateWrapper<QuotaDO> quotaUpdate = new LambdaUpdateWrapper<>();
                    quotaUpdate.eq(QuotaDO::getQuotaId, searchQuota.getQuotaId());
                    quotaUpdate.set(QuotaDO::getCurrentQuota, searchQuota.getCurrentQuota().subtract(entry.getQuotaValue()));
                    quotaUpdate.set(QuotaDO::getAccountQuota, searchQuota.getAccountQuota().subtract(entry.getQuotaValue()));
                    boolean update = quotaService.update(quotaUpdate);
                    if (!update) {
                        throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "增加额度异常");
                    }
                }
                adjustResp.setAccountQuota(searchQuota.getAccountQuota().subtract(entry.getQuotaValue()));
                adjustResp.setCurrentQuota(searchQuota.getCurrentQuota().subtract(entry.getQuotaValue()));
                //添加额度增加记录
                QuotaOperateLogDO quotaOperateLogDO = new QuotaOperateLogDO();
                quotaOperateLogDO.setQuotaId(searchQuota.getQuotaId());
                quotaOperateLogDO.setType(entry.getAdjustType());
                quotaOperateLogDO.setQuotaValue(entry.getQuotaValue());
                boolean saveQuotaOperateLog = quotaOperateLogService.save(quotaOperateLogDO);
                if (!saveQuotaOperateLog) {
                    throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "额度操作异常");
                }
            }
        } catch (Exception e) {
            logger.error("调整账户额度异常：{}", e.getMessage(), e);
            throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), e.getMessage());
        } finally {
            redissonLock.unlock();
        }
        return adjustResp;
    }

}