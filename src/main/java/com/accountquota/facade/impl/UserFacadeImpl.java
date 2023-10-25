package com.accountquota.facade.impl;

import com.accountquota.config.ThreadContext;
import com.accountquota.constant.RedisCacheConstant;
import com.accountquota.enums.InfoStatusEnum;
import com.accountquota.enums.RetCodeEnum;
import com.accountquota.exception.ScException;
import com.accountquota.facade.UserFacade;
import com.accountquota.mybatisplus.entity.QuotaDO;
import com.accountquota.mybatisplus.entity.QuotaDetailLogDO;
import com.accountquota.mybatisplus.service.QuotaDetailLogMybatisPlusService;
import com.accountquota.mybatisplus.service.QuotaMybatisPlusService;
import com.accountquota.vo.req.user.DeductedReq;
import com.accountquota.vo.req.user.SearchReq;
import com.accountquota.vo.resp.user.DeductedResp;
import com.accountquota.vo.resp.user.SearchResp;
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
public class UserFacadeImpl implements UserFacade {
    private static Logger logger = LoggerFactory.getLogger(UserFacadeImpl.class);
    @Autowired
    private QuotaMybatisPlusService quotaService;
    @Autowired
    private QuotaDetailLogMybatisPlusService quotaDetailLogService;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public SearchResp search(SearchReq entry) {
        LambdaQueryWrapper<QuotaDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuotaDO::getClientId, ThreadContext.get("clientId"));
        queryWrapper.eq(QuotaDO::getType, entry.getType());
        queryWrapper.eq(QuotaDO::getStatus, InfoStatusEnum.NORMAL.getCode());
        QuotaDO quota = quotaService.getOne(queryWrapper);
        if (quota == null) {
            logger.error("查询账户异常：{}", "额度账户不存在");
            throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "额度账户不存在");
        }
        SearchResp searchResp = new SearchResp();
        searchResp.setType(quota.getType());
        searchResp.setCreateTime(quota.getCreateTime());
        searchResp.setUpdateTime(quota.getUpdateTime());
        searchResp.setAccountQuota(quota.getAccountQuota());
        searchResp.setCurrentQuota(quota.getCurrentQuota());
        //删除当前用户信息
        ThreadContext.remove();
        return searchResp;
    }

    @Override
    public DeductedResp deducted(DeductedReq entry) {
        DeductedResp deductedResp = new DeductedResp();
        RLock redissonLock = redissonClient.getLock(RedisCacheConstant.QUOTA_KEY_LOCK + ThreadContext.get("clientId") + "-" + entry.getType());
        try {
            boolean isLockBoolean = redissonLock.tryLock(3, 10, TimeUnit.SECONDS);
            if (isLockBoolean) {
                //判断账户是否存在
                LambdaQueryWrapper<QuotaDO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(QuotaDO::getType, entry.getType());
                queryWrapper.eq(QuotaDO::getClientId, ThreadContext.get("clientId"));
                queryWrapper.eq(QuotaDO::getStatus, InfoStatusEnum.NORMAL.getCode());
                QuotaDO searchQuota = quotaService.getOne(queryWrapper);
                if (searchQuota == null) {
                    throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "额度账户不存在");
                }
                if (entry.getQuotaValue().compareTo(searchQuota.getCurrentQuota()) > 0) {
                    throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "额度账户余额不足");
                }
                //扣减额度
                LambdaUpdateWrapper<QuotaDO> quotaUpdate = new LambdaUpdateWrapper<>();
                quotaUpdate.eq(QuotaDO::getQuotaId, searchQuota.getQuotaId());
                quotaUpdate.set(QuotaDO::getCurrentQuota, searchQuota.getCurrentQuota().subtract(entry.getQuotaValue()));
                boolean updateQuotaFlag = quotaService.update(quotaUpdate);
                if (!updateQuotaFlag) {
                    throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "扣减额度失败");
                }
                //记录扣减流水
                QuotaDetailLogDO quotaDetailLogDO = new QuotaDetailLogDO();
                quotaDetailLogDO.setQuotaValue(entry.getQuotaValue());
                quotaDetailLogDO.setQuotaId(searchQuota.getQuotaId());
                boolean saveDetailLogFlag = quotaDetailLogService.save(quotaDetailLogDO);
                if (!saveDetailLogFlag) {
                    throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), "扣减额度失败");
                }
                deductedResp.setAccountQuota(searchQuota.getAccountQuota());
                deductedResp.setCurrentQuota(searchQuota.getCurrentQuota().subtract(entry.getQuotaValue()));
            }
        } catch (Exception e) {
            logger.error("扣减额度异常：{}", e.getMessage(), e);
            throw new ScException(RetCodeEnum.QUOTA_ERROR.getCode(), e.getMessage());
        } finally {
            redissonLock.unlock();
        }
        //删除当前用户信息
        ThreadContext.remove();
        return deductedResp;
    }

}
