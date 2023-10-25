package com.accountquota.mybatisplus.service.impl;

import com.accountquota.mybatisplus.entity.QuotaDO;
import com.accountquota.mybatisplus.mapper.QuotaMybatisPlusMapper;
import com.accountquota.mybatisplus.service.QuotaMybatisPlusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户额度账户记录表 服务实现类
 * </p>
 *
 * @author zql
 * @since 2023-10-23
 */
@Service
public class QuotaMybatisPlusServiceImpl extends ServiceImpl<QuotaMybatisPlusMapper, QuotaDO> implements QuotaMybatisPlusService {

}
