package com.accountquota.mybatisplus.service.impl;

import com.accountquota.mybatisplus.entity.ClientDO;
import com.accountquota.mybatisplus.mapper.ClientMybatisPlusMapper;
import com.accountquota.mybatisplus.service.ClientMybatisPlusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息记录表 服务实现类
 * </p>
 *
 * @author zql
 * @since 2023-10-23
 */
@Service
public class ClientMybatisPlusServiceImpl extends ServiceImpl<ClientMybatisPlusMapper, ClientDO> implements ClientMybatisPlusService {

}
