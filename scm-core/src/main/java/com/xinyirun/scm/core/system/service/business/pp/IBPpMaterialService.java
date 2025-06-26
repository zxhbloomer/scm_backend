package com.xinyirun.scm.core.system.service.business.pp;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.pp.BPpMaterialEntity;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpMaterialVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoMaterialVo;

import java.util.List;

/**
 * <p>
 * 生产计划_原材料 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
public interface IBPpMaterialService extends IService<BPpMaterialEntity> {

    /**
     *新增原材料
     */
    void insertAll(List<BPpMaterialVo> materialList, Integer id);

    /**
     *查询生产计划_原材料
     */
    List<BPpMaterialVo> selectByWoId(Integer id);


    /**
     * 删除生产计划表关联信息
     */
    void deleteByPpId(Integer id);
}
