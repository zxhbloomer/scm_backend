package com.xinyirun.scm.core.system.service.business.wo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoMaterialEntity;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoMaterialVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  生产管理_原材料
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
public interface IBWoMaterialService extends IService<BWoMaterialEntity> {

    /**
     * 检验 原材料配比是不是 100%
     * @param material_list 原材料列表
     * @param result 返回错误信息, map键值 error_msg
     */
    void checkMaterialRouter(List<BWoMaterialVo> material_list, List<Map<String, String>> result);

    /**
     * 新增原材料
     * @param material_list 原材料列表
     * @param wo_id wo_id
     */
    void insertAll(List<BWoMaterialVo> material_list, Integer wo_id);

    /**
     * 根据 wo_id 查询详情
     * @param wo_id wo_id
     * @return
     */
    List<BWoMaterialVo> selectByWoId(Integer wo_id);
}
