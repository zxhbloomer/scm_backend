package com.xinyirun.scm.core.api.service.business.v1.releaseorder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseFilesEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseFilesVo;

/**
 * <p>
 * 放货指令/借货指令附件表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-17
 */
public interface ApiIBReleaseFilesService extends IService<BReleaseFilesEntity> {

    /**
     * 按release_order_code删除数据
     */
    void deleteByReleaseOrderCode(String releaseOrderCode);


}
