package com.xinyirun.scm.core.system.service.business.releaseorder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.releaseorder.BReleaseFilesEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseFilesVo;

import java.util.List;

/**
 * <p>
 * 放货指令/借货指令附件表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-17
 */
public interface IBReleaseFilesService extends IService<BReleaseFilesEntity> {

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BReleaseFilesVo vo);

    /**
     *  获取放货指令附件
     */
    List<BReleaseFilesVo> selectByReleaseOrderId(Integer id);
}
