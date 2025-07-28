package com.xinyirun.scm.core.system.mapper.business.wms.outplan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanTotalVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * <p>
 * 出库计划汇总 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Repository
public interface BOutPlanTotalMapper extends BaseMapper<BOutPlanTotalEntity> {

    /**
     * 分页查询出库计划汇总
     */
    IPage<BOutPlanTotalVo> selectPage(Page<BOutPlanTotalVo> page, @Param("searchCondition") BOutPlanTotalVo searchCondition);

    /**
     * 查询出库计划汇总列表
     */
    List<BOutPlanTotalVo> selectList(@Param("searchCondition") BOutPlanTotalVo searchCondition);

    /**
     * 根据出库计划主表ID查询出库计划汇总信息
     */
    @Select("select * from b_out_plan_total where out_plan_id = #{outPlanId}")
    BOutPlanTotalVo selectByOutPlanId(@Param("outPlanId") Integer outPlanId);

    /**
     * 更新出库计划汇总数据（优化版）
     * 根据计划ID汇总其下所有明细的数据到计划级别
     * 使用一次JOIN子查询汇总所有字段，避免多次重复查询
     * @param out_plan_ids 出库计划ID集合
     * @return 更新记录数
     */
    @Update("<script>                                                                                                                                           " +
            "    UPDATE b_out_plan_total t1                                                                                                                       " +
            "    INNER JOIN (                                                                                                                                    " +
            "        SELECT                                                                                                                                      " +
            "            t2.out_plan_id,                                                                                                                          " +
            "            COALESCE(SUM(t2.processing_qty), 0) AS sum_processing_qty_total,                                                                      " +
            "            COALESCE(SUM(t2.processing_weight), 0) AS sum_processing_weight_total,                                                                 " +
            "            COALESCE(SUM(t2.processing_volume), 0) AS sum_processing_volume_total,                                                                 " +
            "            COALESCE(SUM(t2.unprocessed_qty), 0) AS sum_unprocessed_qty_total,                                                                    " +
            "            COALESCE(SUM(t2.unprocessed_weight), 0) AS sum_unprocessed_weight_total,                                                               " +
            "            COALESCE(SUM(t2.unprocessed_volume), 0) AS sum_unprocessed_volume_total,                                                               " +
            "            COALESCE(SUM(t2.processed_qty), 0) AS sum_processed_qty_total,                                                                         " +
            "            COALESCE(SUM(t2.processed_weight), 0) AS sum_processed_weight_total,                                                                   " +
            "            COALESCE(SUM(t2.processed_volume), 0) AS sum_processed_volume_total,                                                                   " +
            "            COALESCE(SUM(t2.cancel_qty), 0) AS sum_cancel_qty_total,                                                                               " +
            "            COALESCE(SUM(t2.cancel_weight), 0) AS sum_cancel_weight_total,                                                                         " +
            "            COALESCE(SUM(t2.cancel_volume), 0) AS sum_cancel_volume_total                                                                          " +
            "        FROM b_out_plan_detail t2                                                                                                                    " +
            "        WHERE t2.out_plan_id IN                                                                                                                     " +
            "            <foreach collection='out_plan_ids' item='id' open='(' separator=',' close=')'>                                                         " +
            "                #{id}                                                                                                                               " +
            "            </foreach>                                                                                                                              " +
            "        GROUP BY t2.out_plan_id                                                                                                                      " +
            "    ) AS summary ON t1.out_plan_id = summary.out_plan_id                                                                                             " +
            "    SET                                                                                                                                             " +
            "        t1.processing_qty_total = summary.sum_processing_qty_total,                                                                                " +
            "        t1.processing_weight_total = summary.sum_processing_weight_total,                                                                          " +
            "        t1.processing_volume_total = summary.sum_processing_volume_total,                                                                          " +
            "        t1.unprocessed_qty_total = summary.sum_unprocessed_qty_total,                                                                              " +
            "        t1.unprocessed_weight_total = summary.sum_unprocessed_weight_total,                                                                        " +
            "        t1.unprocessed_volume_total = summary.sum_unprocessed_volume_total,                                                                        " +
            "        t1.processed_qty_total = summary.sum_processed_qty_total,                                                                                  " +
            "        t1.processed_weight_total = summary.sum_processed_weight_total,                                                                            " +
            "        t1.processed_volume_total = summary.sum_processed_volume_total,                                                                            " +
            "        t1.cancel_qty_total = summary.sum_cancel_qty_total,                                                                                        " +
            "        t1.cancel_weight_total = summary.sum_cancel_weight_total,                                                                                  " +
            "        t1.cancel_volume_total = summary.sum_cancel_volume_total                                                                                   " +
            "</script>                                                                                                                                           ")
    int updateOutPlanTotalData(@Param("out_plan_ids") LinkedHashSet<Integer> out_plan_ids);

}