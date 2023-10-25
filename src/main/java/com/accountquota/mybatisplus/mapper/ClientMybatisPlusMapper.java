package com.accountquota.mybatisplus.mapper;

import com.accountquota.mybatisplus.entity.ClientDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息记录表 Mapper 接口
 * </p>
 *
 * @author zql
 * @since 2023-10-23
 */
@Mapper
public interface ClientMybatisPlusMapper extends BaseMapper<ClientDO> {

}
