package com.xinyirun.scm.core.system.service.business.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.order.BOrderInvertedEntity;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderInvertedExportVo;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderInvertedVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-08-20
 */
public interface IBOrderInvertedService extends IService<BOrderInvertedEntity> {


    /**
     * 稻谷出库计划倒排表
     */
    List<BOrderInvertedVo> queryInvertedOrderOutPlan(BOrderInvertedVo searchCondition);

    /**
     * 稻谷出库计划倒排表 日期组件数据获取
     */
    BOrderInvertedVo getBadgeDate(BOrderInvertedVo searchCondition);

    /**
     * 稻谷出库计划倒排表导出全部
     */
    List<BOrderInvertedExportVo> queryInvertedExportAll(@Param("param") BOrderInvertedVo param);

    /**
     * 稻谷出库计划倒排表导出部分
     */
    List<BOrderInvertedExportVo> queryInvertedExport(BOrderInvertedVo param);

    /**
     * 稻谷出库计划倒排表获取竞拍下拉列表
     */
    BOrderInvertedVo getAuctionDateList(BOrderInvertedVo searchCondition);
}
