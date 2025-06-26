package com.xinyirun.scm.core.whapp.mapper.master.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerTypeEntity;
import com.xinyirun.scm.bean.whapp.vo.master.customer.WhAppMCustomerTypeVo;
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
public interface WhAppMCustomerTypeMapper extends BaseMapper<MCustomerTypeEntity> {

    /**
     * 查询承运商详情
     */
    @Select("    "
            + "  select * from m_customer_type t "
            + "    where  t.customer_id = #{p1,jdbcType=INTEGER}"
            + "      ")
    List<WhAppMCustomerTypeVo> selectCustomerTypeList(@Param("p1") Integer id);

}
