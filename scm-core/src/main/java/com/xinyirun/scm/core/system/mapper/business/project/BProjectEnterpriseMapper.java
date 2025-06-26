package com.xinyirun.scm.core.system.mapper.business.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.project.BProjectEnterpriseEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 项目管理-企业 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@Repository
public interface BProjectEnterpriseMapper extends BaseMapper<BProjectEnterpriseEntity> {

    /**
     * 根据项目ID删除企业记录
     * @param projectId 项目ID
     * @return 删除数量
     */
    int deleteByProjectId(String projectId);

}
