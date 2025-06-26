package com.xinyirun.scm.core.system.service.business.allocate;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.busniess.allocate.BAllocateDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.allocate.BAllocateVo;

/**
 * <p>
 * 库存调整 服务类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
public interface IBAllocateDetailService extends IService<BAllocateDetailEntity> {

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(BAllocateVo vo);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> update(BAllocateVo vo);
}
