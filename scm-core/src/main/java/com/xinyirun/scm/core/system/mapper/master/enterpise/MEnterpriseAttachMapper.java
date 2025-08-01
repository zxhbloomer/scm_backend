package com.xinyirun.scm.core.system.mapper.master.enterpise;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseAttachEntity;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface MEnterpriseAttachMapper extends BaseMapper<MEnterpriseAttachEntity> {


    @Select("""
            select * from m_enterprise_attach where enterprise_id = #{p1}
            """)
    MEnterpriseAttachVo selectEnterpriseId(@Param("p1") Integer id);
}
