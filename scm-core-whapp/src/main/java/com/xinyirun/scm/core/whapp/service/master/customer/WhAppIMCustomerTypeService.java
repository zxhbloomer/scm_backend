package com.xinyirun.scm.core.whapp.service.master.customer;

import com.xinyirun.scm.bean.entity.master.customer.MCustomerTypeEntity;
import com.xinyirun.scm.core.whapp.service.base.v1.WhAppIBaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface WhAppIMCustomerTypeService extends WhAppIBaseService<MCustomerTypeEntity> {

    /**
     * 按企业id删除数据
     */
    public void deleteByCustomerId(Long customerId);

}
