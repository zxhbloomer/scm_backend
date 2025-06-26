package com.xinyirun.scm.core.system.service.sys.schedule.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertVo;

import java.util.List;

/**
 * <p>
 *  物料转换
 * </p>
 *
 * @author wwl
 * @since 2022-05-09
 */
public interface ISBMaterialConvertV2Service extends IService<MInventoryEntity> {

    /**
     * 物料转换
     *
     */
    void materialConvert(String parameterClass , String parameter);

    /**
     * 物料转换
     *
     */
    void execute(List<BMaterialConvertVo> list);

    /**
     * 物料转换
     *
     */
    void materialConvert1(String parameterClass , String parameter);
}
