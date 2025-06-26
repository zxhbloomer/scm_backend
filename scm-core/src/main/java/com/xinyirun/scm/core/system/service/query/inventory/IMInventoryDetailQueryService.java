package com.xinyirun.scm.core.system.service.query.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.system.vo.excel.query.MInventoryDetailExportVo;
import com.xinyirun.scm.bean.system.vo.excel.query.MInventoryStagnationWarningExportVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryDetailQuerySumVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryDetailQueryVo;

import java.util.List;

/**
 * <p>
 * 库存明细查询
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMInventoryDetailQueryService extends IService<MInventoryEntity> {

    /**
     * 库存明细查询
     */
    IPage<MInventoryDetailQueryVo> queryInventoryDetails(MInventoryDetailQueryVo searchCondition);

    /**
     * 库存明细查询
     */
    MInventoryDetailQuerySumVo queryInventoryDetailsSum(MInventoryDetailQueryVo searchCondition);

    /**
     * 库存明细查询导出
     */
    List<MInventoryDetailExportVo> selectExportList(List<MInventoryDetailQueryVo> searchCondition);

    /**
     * 库存明细查询导出
     */
    List<MInventoryDetailExportVo> selectExportAllList(MInventoryDetailQueryVo searchCondition);

    /**
     *
     * @param searchCondition
     * @return
     */
    IPage<MInventoryDetailQueryVo> selectListByOrderId(MInventoryDetailQueryVo searchCondition);

    /**
     *查询港口中转停滞预警数据
     */
    IPage<MInventoryDetailQueryVo> queryInventoryByWarning(MInventoryDetailQueryVo searchCondition);

    /**
     *查询港口中转停滞预警合计数据
     */
    MInventoryDetailQuerySumVo queryInventoryByWarningSum(MInventoryDetailQueryVo searchCondition);

    /**
     *港口中转停滞预警数据导出
     */
    List<MInventoryStagnationWarningExportVo> selectExportDataWarning(List<MInventoryDetailQueryVo> searchCondition);


    /**
     *港口中转停滞预警数据全部导出
     */
    List<MInventoryStagnationWarningExportVo> selectExportAllDataWarning(MInventoryDetailQueryVo searchCondition);
}
