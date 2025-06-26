package com.xinyirun.scm.core.system.mapper.master.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerTypeEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-09-10
 */
@Repository
public interface MCustomerTypeMapper extends BaseMapper<MCustomerTypeEntity> {


    /**
     * 通过customerId查询
     *
     */
    @Select("select * from m_customer_type where customer_id = #{p1}")
    List<MCustomerTypeEntity> selectByCustomerId(@Param("p1") Integer id);
}
