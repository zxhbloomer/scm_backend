package com.xinyirun.scm.core.system.service.business.project;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.project.BProjectEnterpriseEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;


/**
 * <p>
 * 项目管理-企业 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
public interface IBProjectEnterpriseService extends IService<BProjectEnterpriseEntity> {

    /**
     * 新增数据
     * @param entity
     * @return
     */
    public InsertResultAo<Integer> insert(BProjectEnterpriseEntity entity);

    /**
     * 修改数据
     * @param entity
     * @return
     */
    public UpdateResultAo<Integer> update(BProjectEnterpriseEntity entity);


}
