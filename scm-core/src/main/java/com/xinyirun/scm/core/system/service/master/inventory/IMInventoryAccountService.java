package com.xinyirun.scm.core.system.service.master.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryAccountEntity;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryAccountExportVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryAccountVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMInventoryAccountService extends IService<MInventoryAccountEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MInventoryAccountVo> selectPage(MInventoryAccountVo searchCondition);

    /**
     * 导出查询
     * @param searchCondition 查询条件
     * @return List<MInventoryAccountExportVo>
     */
    List<MInventoryAccountExportVo> selectExportList(MInventoryAccountVo searchCondition);

    /**
     * 查询合计
     * @param searchCondition
     * @return
     */
    MInventoryAccountVo selectListSum(MInventoryAccountVo searchCondition);
}
