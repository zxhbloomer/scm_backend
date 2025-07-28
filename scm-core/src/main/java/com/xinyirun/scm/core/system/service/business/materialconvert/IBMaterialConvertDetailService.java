package com.xinyirun.scm.core.system.service.business.materialconvert;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.materialconvert.BMaterialConvertDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertDetailVo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertVo;

import java.util.List;

/**
 * <p>
 * 库存调整 服务类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
public interface IBMaterialConvertDetailService extends IService<BMaterialConvertDetailEntity> {

//    /**
//     * 插入一条记录（选择字段，策略插入）
//     */
//    InsertResultAo<Integer> insert(BMaterialConvertVo vo);
//
//    /**
//     * 修改数据
//     * @param vo
//     * @return
//     */
//    UpdateResultAo<Integer> update(BMaterialConvertVo vo);

    List<BMaterialConvertDetailVo> getList(BMaterialConvertVo vo);

}
