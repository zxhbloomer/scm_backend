package com.xinyirun.scm.core.tenant.mapper.business.login;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.tenant.manager.user.STenantManagerEntity;
import com.xinyirun.scm.bean.system.bo.tenant.manager.user.STenantManagerBo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  租户管理 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-05-14
 */
@Repository
public interface STenantManagerMapper extends BaseMapper<STenantManagerEntity> {
    
    /**
     * 根据租户名称查询租户信息
     * @param tenant 租户名称
     * @return 租户信息列表
     */
    @Select(" SELECT * FROM s_tenant_manager WHERE tenant = #{tenant} ")
    STenantManagerBo selectByTenant(@Param("tenant") String tenant);
}
