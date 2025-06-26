package com.xinyirun.scm.core.whapp.mapper.master.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface WhAppCustomerInfoMapper extends BaseMapper<MCustomerInfoEntity> {

    /**
     * 查询承运商详情
     */
    @Select("    "
            + "  select * from m_customer_info t "
            + "    where  t.customer_id = #{p1,jdbcType=INTEGER}"
            + "      ")
    MCustomerInfoEntity getByCustomerId(@Param("p1") Integer id);
}
