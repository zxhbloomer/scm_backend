package com.xinyirun.scm.core.system.mapper.master.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerAdjustInfoEntity;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface MCustomerAdjustInfoMapper extends BaseMapper<MCustomerAdjustInfoEntity> {

}
