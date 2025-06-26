package com.xinyirun.scm.core.whapp.serviceimpl.master.customer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerTypeEntity;
import com.xinyirun.scm.core.whapp.mapper.master.customer.WhAppMCustomerTypeMapper;
import com.xinyirun.scm.core.whapp.service.master.customer.WhAppIMCustomerTypeService;
import com.xinyirun.scm.core.whapp.serviceimpl.base.v1.WhAppBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class WhAppMCustomerTypeServiceImpl extends WhAppBaseServiceImpl<WhAppMCustomerTypeMapper, MCustomerTypeEntity> implements WhAppIMCustomerTypeService {

    @Autowired
    private WhAppMCustomerTypeMapper mapper;

    @Override
    public void deleteByCustomerId(Long customerId) {
        // 根据customer_id 删除数据
        mapper.delete(new QueryWrapper<MCustomerTypeEntity>().eq("customer_id",customerId));
    }
}
