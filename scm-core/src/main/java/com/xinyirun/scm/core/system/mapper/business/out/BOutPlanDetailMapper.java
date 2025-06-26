package com.xinyirun.scm.core.system.mapper.business.out;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutPlanEntity;
import com.xinyirun.scm.bean.system.vo.business.out.*;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 出库计划详情 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface BOutPlanDetailMapper extends BaseMapper<BOutPlanDetailEntity> {
    String common_select = "  "
            + "     SELECT                                                                       "
            + "            t10.code,t.*,                                                         "
            + "            ifnull(t4.short_name,t4.name) as warehouse_name,                      "
            + "            ifnull(t5.short_name,t5.name) as location_name,                       "
            + "            t10.spec,                                                             "
            + "            t10.pm,                                                               "
            + "            t11.name as goods_name,                                               "
            + "             '吨' as unit,                                                        "
            + "            t12.name as unit_name,                                                "
            + "            t6.name as bin_name                                                   "
            + "       FROM                                                                       "
            + "  	       b_out_plan_detail t                                                   "
            + "  LEFT JOIN b_out_plan t3 ON t.plan_id = t3.id                                    "
            + "  LEFT JOIN m_warehouse t4 ON t.warehouse_id = t4.id                              "
            + "  LEFT JOIN m_location t5 ON t5.id = t.location_id                                "
            + "  LEFT JOIN m_bin t6 ON t6.id = t.bin_id                                          "
            + "  LEFT JOIN m_goods_spec t10 ON t10.id = t.sku_id                                 "
            + "  LEFT JOIN m_goods t11 ON t11.id = t10.goods_id                                  "
            + "  LEFT JOIN m_unit t12 ON t12.id = t.unit_id                                      "
            + "                                                                                  "
            ;

    /**
     * 查询出库计划待出库等数量
     */
    @Select(""
            + "	SELECT                                                                                                  "
            + "		t1.id,                                                                                              "
            + "		ifnull( t3.actual_weight, 0 ) has_handle_weight,                                                    "
            + "	CASE                                                                                                    "
            + "			                                                                                                "
            + "			WHEN t1.weight - ifnull( t3.actual_weight, 0 ) < 0 THEN                                         "
            + "			0 ELSE t1.weight - ifnull( t3.actual_weight, 0 )                                                "
            + "		END pending_weight,                                                                                 "
            + "		ifnull( t3.actual_count, 0 ) has_handle_count,                                                      "
            + "		t1.count - ifnull( t3.actual_count, 0 ) pending_count                                               "
            + "	FROM                                                                                                    "
            + "		b_out_plan_detail t1                                                                                "
            + "		LEFT JOIN b_out_plan t2 ON t1.plan_id = t2.id                                                       "
            + "		LEFT JOIN (                                                                                         "
            + "		SELECT                                                                                              "
            + "			tt.plan_detail_id,                                                                              "
            + "			sum( actual_weight ) actual_weight,                                                             "
            + "			sum( actual_count ) actual_count                                                                "
            + "		FROM                                                                                                "
            + "			b_out tt                                                                                        "
            + "		WHERE                                                                                               "
            + "			tt.STATUS IN ('1', '2', '5', '7')                                                               "
            + "		GROUP BY                                                                                            "
            + "		tt.plan_detail_id                                                                                   "
            + "		) t3 ON t3.plan_detail_id = t1.id                                                                   "
            + "  where true                                                                                             "
            + "     and (t1.id =  #{p1,jdbcType=INTEGER})                                                               "
            + "")
    BOutPlanDetailVo selectPlanDetailCount(@Param("p1") Integer id);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                       "
            + common_select
            + "  where true                                                                                         "
            + "    and (t3.id =  #{p1.plan_id,jdbcType=INTEGER} or #{p1.plan_id,jdbcType=INTEGER} is null)          "
            + "      ")
    List<BOutPlanDetailVo> selectOutGoodsList(@Param("p1") BOutPlanSaveVo searchCondition);

    /**
     * 删除状态为制单和驳回的明细数据
     * @param plan_id
     */
    @Select("    "
            + "  delete from b_in_plan_detail                                                                                              "
            + "  where plan_id = #{p1,jdbcType=INTEGER}                                                                                    "
            + "  and status in ( '" + DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED+"','" +DictConstant.DICT_B_OUT_PLAN_STATUS_RETURN +"' )    ")
    void statusDelete(@Param("p1") int plan_id);

    /**
     * 查询指定出库计划明细id的待出库数量
     */
    @Select("    "
            + "				SELECT                                                                                      "
            + "					t1.weight - IFNULL( t2.weight, 0 ) AS weight                                            "
            + "				FROM                                                                                        "
            + "					b_out_plan_detail t1                                                                    "
            + "					LEFT JOIN (                                                                             "
            + "				SELECT                                                                                      "
            + "					tab1.plan_detail_id,                                                                    "
            + "					sum( tab1.actual_weight ) weight                                                        "
            + "				FROM                                                                                        "
            + "					b_out tab1                                                                              "
            + "				GROUP BY                                                                                    "
            + "					tab1.plan_detail_id                                                                     "
            + "					) t2 ON t1.id = t2.plan_detail_id                                                       "
            + "				WHERE                                                                                       "
            + "				TRUE                                                                                        "
            + "					AND t1.id = #{p1,jdbcType=INTEGER}                                                      "
            + "                                  ")
    BigDecimal selectWaitOperateCount(@Param("p1") Integer id);



    /**
     * 查询指定出库计划明细id的已出库数量
     */
    @Select("    "
            + "				SELECT                                                                                      "
            + "				ifnull(sum(t1.actual_count),0)  actual_count                                                "
            + "				FROM                                                                                        "
            + "					b_out t1                                                                                "
            + "					LEFT JOIN b_out_plan_detail t2                                                          "
            + "				ON t1.plan_detail_id = t2.id                                                                "
            + "				WHERE                                                                                       "
            + "				TRUE                                                                                        "
            + "					AND t2.id = #{p1,jdbcType=INTEGER}                                                      "
            + "				    AND t1.status  in ('0','1','2','5','7')                                                     "
            + "                                  ")
    BigDecimal selectWaitOperatedCount1(@Param("p1") Integer id);

    /**
     * 查询指定出库计划明细id的已出库数量
     */
    @Select("    "
            + "		SELECT                                                                                                                                                                                              "
            + "			ifnull( sum( t1.actual_count ), 0 ) weight                                                                                                                                                      "
            + "		FROM                                                                                                                                                                                                "
            + "			b_out t1                                                                                                                                                                                        "
            + "			LEFT JOIN b_out_plan_detail t2 ON t1.plan_detail_id = t2.id                                                                                                                                     "
            + "			LEFT JOIN ( SELECT t1.id, 'b_in_order' AS order_type FROM b_in_order t1 UNION ALL SELECT t2.id, 'b_out_order' AS order_type FROM b_out_order t2 ) t3 ON t2.order_id = t3.id                     "
            + "			AND t2.order_type = t3.order_type                                                                                                                                                               "
            + "		WHERE                                                                                                                                                                                               "
            + "		TRUE                                                                                                                                                                                                "
            + "			AND t1.STATUS IN ( '0', '1', '2', '5','7' )                                                                                                                                                         "
            + "			AND t3.id = #{p1,jdbcType=INTEGER}                                                                                                                                                              "
            + "			AND t3.order_type = #{p2,jdbcType=VARCHAR}                                                                                                                                                      "
            + "                                  ")
    BigDecimal selectWaitOperatedCount(@Param("p1") Integer order_id, @Param("p2") String order_type);

    /**
     * 查询指定出库计划明细id的待出库数量
     */
    @Select("    "
            + "				SELECT                                                                                      "
            + "				sum(t1.actual_weight)  weight                                                               "
            + "				FROM                                                                                        "
            + "					b_out t1                                                                                "
            + "					LEFT JOIN b_out_plan_detail t2                                                          "
            + "				ON t1.plan_detail_id = t2.id                                                                "
            + "				WHERE                                                                                       "
            + "				TRUE                                                                                        "
            + "				    AND t1.lock_inventory = true                                                            "
            + "				    AND t1.status  not in (3,4)                                                             "
            + "					AND t1.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER}                               "
            + "					AND t1.location_id = #{p1.location_id,jdbcType=INTEGER}                                 "
            + "					AND t1.bin_id = #{p1.bin_id,jdbcType=INTEGER}                                           "
            + "					AND t1.sku_id = #{p1.sku_id,jdbcType=INTEGER}                                           "
            + "					AND t1.owner_id = #{p1.owner_id,jdbcType=INTEGER}                                       "
            + "                                  ")
    BigDecimal selectWaitOperateCountLock(@Param("p1") BOutPlanOperateVo vo);

    @Select("    "
            + "  select t1.*,t2.extra_code plan_extra_code                                                              "
            + "    from b_out_plan_detail t1                                                                            "
            + "    INNER JOIN b_out_plan t2 on t1.plan_id = t2.id                                                       "
            + "   where t1.id = #{p1,jdbcType=INTEGER}                                                                  "
            + "    ")
    BOutPlanDetailVo selectDataById(@Param("p1")Integer id);

    @Select("    "
            + "  select t1.*                                                                                            "
            + "    from b_out_plan_detail t1                                                                            "
            + "   where t1.plan_id = #{p1,jdbcType=INTEGER}                                                                  "
            + "    ")
    List<BOutPlanListVo> selectByPlanId(@Param("p1")Integer plan_id);

    /**
     * 查看页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and (t3.code =  #{p1.plan_code,jdbcType=VARCHAR} )                                                   "
            + "    and (t.sku_code =  #{p1.sku_code,jdbcType=VARCHAR} )                                                 "
            + "      ")
    BOutPlanDetailEntity selectPlanByCode(@Param("p1") BOutPlanDetailVo searchCondition);

    /**
     * 监管入库id查询出库计划主表
     */
    @Select("                                                                                                          "
            + "     SELECT                                                                                                                          "
            + "            t.*                                                                                              "
            + "       FROM                                                                                                                          "
            + "  	       b_out_plan t                                                                                                      "
            + "  LEFT JOIN b_out_plan_detail  t4 ON t.id = t4.plan_id                                                               "
            + "  LEFT JOIN b_schedule  t3 ON t4.id = t3.out_plan_detail_id                                                               "
            + "  LEFT JOIN b_monitor  t1 ON t1.schedule_id = t3.id                                                                                      "
            + "  LEFT JOIN b_monitor_in  t2 ON t2.monitor_id = t1.id                                                                                      "
            + "     where true                                                                                                                      "
            + "     and t2.id = #{p1,jdbcType=INTEGER}                                                                           "
            + "     ")
    BOutPlanEntity selectMonitorInId(@Param("p1") Integer id);

    /**
     * 监管出库id查询出库计划从表
     */
    @Select("                                                                                                          "
            + "     SELECT                                                                                                                          "
            + "            t.*                                                                                              "
            + "       FROM                                                                                                                          "
            + "  	       b_out_plan_detail t                                                                                                      "
            + "  LEFT JOIN b_schedule  t3 ON t3.out_plan_detail_id = t.id                                                               "
            + "  LEFT JOIN b_monitor  t1 ON t1.schedule_id = t3.id                                                                                      "
            + "  LEFT JOIN b_monitor_out  t2 ON t2.monitor_id = t1.id                                                                                      "
            + "  LEFT JOIN b_out_plan  t4 ON t4.id = t.plan_id                                                               "
            + "     where true                                                                                                                      "
            + "     and t2.id = #{p1,jdbcType=INTEGER}                                                                           "
            + "     ")
    BOutPlanDetailEntity selectMonitorOutId(@Param("p1") Integer id);

    /**
     * 根据 IDS 查询
     * @param detailId
     * @return
     */
    @Select("<script>                                                                                                   "
            + " SELECT                                                                                                  "
            + "   t1.code,                                                                                              "
            + "   t.status                                                                                              "
            + " FROM b_out_plan_detail t                                                                                "
            + " LEFT JOIN b_out_plan t1 ON t.plan_id = t1.id                                                            "
            + " WHERE t.id = #{detailId}                                                                                "
            + " </script>                                                                                               "
    )
    BOutPlanVo selectByPlanDetailId(Integer detailId);

    @Select("<script>                                                                                                   "
            + " SELECT                                                                                                  "
            + "   t.id,                                                                                                 "
            + "   t.plan_id                                                                                             "
            + " FROM b_out_plan_detail t                                                                                "
            + " WHERE t.plan_id in                                                                                      "
            + "     <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                "
            + "         #{item}                                                                                         "
            + "     </foreach>                                                                                          "
            + " AND t.status NOT IN ('"+ DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL +"', '"+ DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL_BEING_AUDITED +"')"
            + " </script>"
    )
    List<BOutPlanListVo> selectByPlanIds(@Param("p1") List<Integer> plan_id);

    /**
     * 根据 plan_id 查询详情 ID
     * @param plan_id
     * @return List
     */
    @Select(""
            + "        SELECT                                                                             "
            + "               t.id,                                                                       "
            + "               t.plan_id                                                                   "
            + "          FROM                                                                             "
            + "     	       b_out_plan_detail t                                                        "
            + "  where true                                                                               "
            + "    and (t.plan_id =  #{p1,jdbcType=INTEGER} )                                         "
            + "      ")
    List<Integer> selectOutGoodsIdList(Integer plan_id);

    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.id                                                                                                "
            +  "  FROM                                                                                                  "
            +  "    b_out_plan_detail t                                                                                 "
            +  "  LEFT JOIN b_out_plan t1 ON t1.id = t.plan_id                                                          "
            +  "  WHERE t.order_id = #{p1}                                                                              "
            +  "  AND t.order_type = #{p2}                                                                              "
            +  "  AND t.`status` != '"+ DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL +"'                                 "
    )
    List<BOutPlanListVo> selectOutPlanByOrderIdAndOrderType(@Param("p1") Integer orderId,@Param("p2") String orderType);

    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.id                                                                                                "
            +  "  FROM                                                                                                  "
            +  "    b_out t                                                                                             "
            +  "  LEFT JOIN b_out_extra t1 ON t1.out_id = t.id                                                          "
            +  "  LEFT JOIN b_order t2 ON t1.order_id = t2.serial_id AND t2.serial_type = t1.order_type                 "
            +  "  WHERE t1.order_id = #{p1}                                                                              "
            +  "  AND t1.order_type = #{p2}                                                                              "
            +  "  AND t.`status` != '"+ DictConstant.DICT_B_OUT_STATUS_CANCEL +"'                                      "
    )
    List<BOutPlanListVo> selectOutByOrderIdAndOrderType(@Param("p1") Integer orderId,@Param("p2") String orderType);


    @Select(""
            + "	SELECT                                                                                                  "
            + "		t1.id,                                                                                              "
            + "		ifnull( t3.actual_weight, 0 ) has_handle_weight,                                                    "
            + "	CASE                                                                                                    "
            + "			                                                                                                "
            + "			WHEN t1.weight - ifnull( t3.actual_weight, 0 ) < 0 THEN                                         "
            + "			0 ELSE t1.weight - ifnull( t3.actual_weight, 0 )                                                "
            + "		END pending_weight,                                                                                 "
            + "		ifnull( t3.actual_count, 0 ) has_handle_count,                                                      "
            + "		t1.count - ifnull( t3.actual_count, 0 ) pending_count                                               "
            + "	FROM                                                                                                    "
            + "		b_out_plan_detail t1                                                                                "
            + "		LEFT JOIN b_out_plan t2 ON t1.plan_id = t2.id                                                       "
            + "		LEFT JOIN (                                                                                         "
            + "		SELECT                                                                                              "
            + "			tt.plan_detail_id,                                                                              "
            + "			sum( actual_weight ) actual_weight,                                                             "
            + "			sum( actual_count ) actual_count                                                                "
            + "		FROM                                                                                                "
            + "			b_receive tt                                                                                        "
            + "		WHERE                                                                                               "
            + "			tt.STATUS IN ('1', '2', '5', '7')                                                               "
            + "		GROUP BY                                                                                            "
            + "		tt.plan_detail_id                                                                                   "
            + "		) t3 ON t3.plan_detail_id = t1.id                                                                   "
            + "  where true                                                                                             "
            + "     and (t1.id =  #{p1,jdbcType=INTEGER})                                                               "
            + "")
    BOutPlanDetailVo selectPlanDetailCountByReceive(@Param("p1") Integer id);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                       "
            + common_select
            + "  where true                                                                                         "
            + "    and (t3.id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)          "
            + "      ")
    List<BOutPlanDetailVo> newSelectOutGoodsList(@Param("p1") int id);
}
