package com.xinyirun.scm.core.api.service.master.v1.customer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ApiCustomerService extends IService<MCustomerEntity> {

    /**
     * 通过name查询
     *
     */
    List<MCustomerEntity> selectByName(String name);

    /**
     * 首次所有数据同步
     */
    void syncAll(List<ApiCustomerVo> vo);

    /**
     * 新增同步
     */
    void syncNewOnly(List<ApiCustomerVo> vo);

    /**
     * 修改同步
     */
    void syncUpdateOnly(List<ApiCustomerVo> vo);
}
