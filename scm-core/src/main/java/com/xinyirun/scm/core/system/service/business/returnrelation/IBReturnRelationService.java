//package com.xinyirun.scm.core.system.service.business.returnrelation;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.xinyirun.scm.bean.entity.busniess.returnrelation.BReturnRelationEntity;
//import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorVo;
//import com.xinyirun.scm.bean.system.vo.business.returnrelation.BReturnRelationExportVo;
//import com.xinyirun.scm.bean.system.vo.business.returnrelation.BReturnRelationVo;
//import com.xinyirun.scm.bean.system.vo.excel.out.BOutExportVo;
//
//import java.util.List;
//
///**
// * <p>
// * 退货表 服务类
// * </p>
// *
// * @author xinyirun
// * @since 2024-07-26
// */
//public interface IBReturnRelationService extends IService<BReturnRelationEntity> {
//
//    /**
//     * 新增退货单
//     */
//    BMonitorVo insertReturnRelation(BMonitorVo searchCondition);
//
//    /**
//     * 修改退货单
//     */
//    BMonitorVo updateReturnRelation(BMonitorVo searchCondition);
//
//    /**
//     * 分页列表
//     */
//    IPage<BReturnRelationVo> selectPageList(BReturnRelationVo returnRelationVo);
//
//    /**
//     * 查询详情
//     */
//    BReturnRelationVo getDetail(BReturnRelationVo returnRelationVo);
//
//    /**
//     * 部分导出
//     */
//    List<BReturnRelationExportVo> selectExportList(List<BReturnRelationVo> searchCondition);
//
//    /**
//     * 全部导出
//     */
//    List<BReturnRelationExportVo> selectExportAll(BReturnRelationVo searchCondition);
//
//    /**
//     * 作废之前的入库计划，入库单，退货单
//     */
//    void toVoidInPlan(Integer id);
//}
