package com.xinyirun.scm.core.api.serviceimpl.base.v1;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.core.api.service.base.v1.ApiIBaseService;

/**
 * 扩展Mybatis-Plus接口
 *
 * @author
 */
public class ApiBaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements ApiIBaseService<T> {

}
