package com.xinyirun.scm.core.system.service.business.materialconvert;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.materialconvert.BConvertRecordEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BConvertRecordVo;

/**
 * @author Wang Qianfeng
 * @date 2022/11/23 16:02
 */
public interface IBMaterialConvertRecordService extends IService<BConvertRecordEntity> {
    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    IPage<BConvertRecordVo> selectPage(BConvertRecordVo searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(BConvertRecordVo vo);
}
