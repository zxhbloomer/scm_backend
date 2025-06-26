package com.xinyirun.scm.core.bpm.serviceimpl.base.v1;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.core.bpm.service.base.v1.BpmIBaseService;
import org.springframework.stereotype.Service;

/**
 * 扩展Mybatis-Plus接口
 *
 * @author
 */
public class BpmBaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BpmIBaseService<T> {

}
