package com.xinyirun.scm.core.system.mapper.master.enterpise;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerTypeEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseTypesEntity;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseTypesVo;
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
public interface MEnterpriseTypesMapper extends BaseMapper<MEnterpriseTypesEntity> {


    /**
     * 通过customerId查询
     *
     */
    @Select("""
            select * from m_enterprise_types where enterprise_id = #{p1}
            """)
    List<MEnterpriseTypesVo> selectEnterpriseId(@Param("p1") Integer id);
}
