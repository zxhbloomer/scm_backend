package com.xinyirun.scm.core.system.service.business.rtwo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoMaterialEntity;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoMaterialVo;

import java.util.List;

/**
 * <p>
 *  生产管理_原材料
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
public interface IBRtWoMaterialService extends IService<BRtWoMaterialEntity> {

    /**
     * 检验 原材料配比是不是 100%
     * @param material_list 原材料列表
     * @param result 返回错误信息, map键值 error_msg
     */
//    void checkMaterialRouter(List<BRtWoMaterialVo> material_list, List<Map<String, String>> result);

    /**
     * 新增原材料
     * @param material_list 原材料列表
     * @param wo_id wo_id
     */
    void insertAll(List<BRtWoMaterialVo> material_list, Integer wo_id);

    /**
     * 根据 wo_id 查询详情
     * @param wo_id wo_id
     * @return
     */
    List<BRtWoMaterialVo> selectByWoId(Integer wo_id);
}
