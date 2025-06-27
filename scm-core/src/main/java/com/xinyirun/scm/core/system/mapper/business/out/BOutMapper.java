package com.xinyirun.scm.core.system.mapper.business.out;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.out.BOutEntity;
import com.xinyirun.scm.bean.system.vo.business.inventory.BQtyLossScheduleReportVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BWarehouseGoodsOutExportVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BWarehouseGoodsVo;
import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutSumVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutVo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleVo;
import com.xinyirun.scm.bean.system.vo.excel.out.BOutExportVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 出库单 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface BOutMapper extends BaseMapper<BOutEntity> {
    String common_select = "  "
            + "      SELECT                                                                                                                          "
            + "             @row_num:= @row_num+ 1 as excel_no,                                                                                      "
            + "             t.*,                                                                                                                     "
            + "             t13.no,                                                                                                                  "
            + "             t3.release_order_code,                                                                                                   "
            + "             t13.type_gauge,                                                                                                          "
            + "             t13.alias,                                                                                                               "
            + "             ifnull(t19.contract_dt, t10.contract_dt) contract_dt,                                                                    "
            + "             ifnull(t19.contract_num, t10.contract_num) contract_num,                                                                 "
            + "             ifnull(t19.contract_no, t10.contract_no) contract_no,                                                                    "
            + "             t19.order_no,                                                                                                            "
            + "             t19.bill_type,                                                                                                           "
            + "             t10.pound_file,                                                                                                          "
            + "             t10.out_photo_file,                                                                                                      "
            + "             t13.code as detail_code,                                                                                                 "
            + "             t3.code as plan_code,                                                                                                    "
            + "             t3.extra_code,                                                                                                           "
            + "             t15.label as status_name,                                                                                                "
            + "             ifnull(t16.label, t10.label) as bill_type_name,                                                                          "
            + "             ifnull(t17.label, t22.label) as type_name,                                                                               "
            + "             ifnull(t4.short_name,t4.name) as consignor_name,                                                                         "
            + "             ifnull(t5.short_name,t5.name) as owner_name,                                                                             "
            + "             ifnull(t9.short_name,t9.name) as warehouse_name,                                                                         "
            + "             t9.name as warehouse_full_name,                                                                                          "
            + "             t7.name as bin_name,                                                                                                     "
            + "             t8.name as location_name,                                                                                                "
            + "             t11.spec,                                                                                                                "
            + "             t11.pm,                                                                                                                  "
            + "             t11.code as sku_code,                                                                                                    "
            + "             t12.name as goods_name,                                                                                                  "
            + "             t20.name as unit_name,                                                                                                   "
            + "             t26.calc as calc,                                                                                                        "
            + "             t1.name as c_name,                                                                                                       "
            + "             t14.name as e_name,                                                                                                      "
            + "             ifnull(tt2.name, t10.name) as client_name,                                                                               "
            + "             t2.name as u_name,                                                                                                       "
            + "             t21.remark as cancel_remark,                                                                                             "
            + "             t.vehicle_no,                                                                                                            "
            + "             t27.name cancel_audit_name,                                                                                              "
            + "             t.cancel_audit_dt,                                                                                                       "
            + "             (t.actual_count - ifnull(t.return_qty,0)) as actual_count_return,                                                        "
            + "             t26.calc as calc,                                                                                              "
            + "             t26.src_unit as src_unit,                                                                                              "
            + "             t26.tgt_unit as tgt_unit,                                                                                              "
            + "             (t.actual_weight -  (ifnull(t.return_qty,0)  * t26.calc) ) as actual_weight_return                                                             "
            + "        FROM                                                                                                                          "
            + "   	       b_out t                                                                                                                   "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                             "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                             "
            + "   LEFT JOIN m_staff t14 ON t.e_id = t14.id                                                                                           "
            + "   LEFT JOIN b_out_plan_detail t13 ON t.plan_detail_id = t13.id                                                                       "
            + "   LEFT JOIN b_out_plan t3 ON t.plan_id = t3.id                                                                                       "
            + "   LEFT JOIN b_order t19 on t13.order_id = t19.serial_id and t13.order_type = t19.serial_type                                         "
            + "   LEFT JOIN b_out_order tt1 ON tt1.id = t13.order_id                                                                                 "
            + "   LEFT JOIN m_customer tt2 ON tt2.id = tt1.client_id                                                                                 "
            + "   LEFT JOIN (select tab1.out_photo_file, tab1.pound_file, tab1.contract_no, tab1.contract_num, tab1.contract_dt, tab1.out_id, tab1.bill_type, tab3.label, tab2.name from b_out_extra tab1 left join m_customer tab2 on tab1.client_id = tab2.id "
            + "              left join (select tab1.dict_value, tab1.label from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id              "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "') tab3 ON tab3.dict_value = tab1.bill_type ) t10 ON t10.out_id = t.id              "
            + "   LEFT JOIN m_customer t4 ON t.consignor_id = t4.id                                                                                  "
            + "   LEFT JOIN m_owner t5 ON t.owner_id = t5.id                                                                                         "
            + "   LEFT JOIN m_bin t7 ON t.bin_id = t7.id                                                                                             "
            + "   LEFT JOIN m_location t8 ON t.location_id = t8.id                                                                                   "
            + "   LEFT JOIN m_warehouse t9 ON t.warehouse_id = t9.id                                                                                 "
            + "   LEFT JOIN m_goods_spec t11 ON t11.id = t.sku_id                                                                                    "
            + "   LEFT JOIN m_goods_spec_prop t24 ON t11.prop_id = t24.id      "
            + "   LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                                                     "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_STATUS + "')t15 on t15.dict_value = t.status                               "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t16 ON t16.dict_value = t19.bill_type              "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t17 ON t17.dict_value = t3.type                             "
            + "   LEFT JOIN m_unit t20 ON t20.id = t.unit_id                                                                                         "
            + "   LEFT JOIN m_goods_unit_calc t26 ON t20.id = t26.src_unit_id AND t26.sku_id = t.sku_id                                              "
            + "   LEFT JOIN m_cancel t21 ON t21.serial_id = t.id and t21.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                       "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t22 ON t22.dict_value = t.type                              "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                                     "
            + "   left join m_staff t27 ON t27.id = t.cancel_audit_id                                                                                "
            + "     ,(select @row_num:=0) t23                                                                                                        ";

    String count_select = "  "
            + "      SELECT                                                                                                                          "
            + "             count(1) c                                                                                                               "
            + "        FROM                                                                                                                          "
            + "   	       b_out t                                                                                                                   "
            + "   LEFT JOIN b_out_plan_detail t13 ON t.plan_detail_id = t13.id                                                                       "
            + "              LEFT JOIN (                                                                                                             "
            + "              SELECT                                                                                                                   "
            + "              	( @i := CASE WHEN @now_plan_id=t1.plan_id THEN @i + 1 ELSE 1 END ) idx,                                              "
            + "              	( @now_plan_id := t1.plan_id ),                                                                                      "
            + "              	t1.*                                                                                                                 "
            + "              FROM                                                                                                                     "
            + "              	b_out_plan_detail t1,                                                                                                "
            + "              	( SELECT @i := 0, @now_plan_id := '' ) AS a                                                                          "
            + "              ) tt   on t13.id = tt.id                                                                                                "
            + "   LEFT JOIN b_out_plan t3 ON t.plan_id = t3.id                                                                                       "
            + "  LEFT JOIN (                                                                                                                         "
            + "		SELECT                                                                                                                           "
            + "			t1.*,'b_in_order' order_type,t1.supplier_id customer_id                                                                      "
            + "		FROM                                                                                                                             "
            + "			 b_in_order t1                                                                                                               "
            + "			union all                                                                                                                    "
            + "		SELECT                                                                                                                           "
            + "			t2.*,'b_out_order' order_type,t2.client_id customer_id                                                                       "
            + "		FROM                                                                                                                             "
            + "			 b_out_order t2                                                                                                              "
            + "     )t19 on t13.order_id = t19.id and  t13.order_type = t19.order_type                                                               "
            + "   left join m_customer tt1 ON tt1.id = t19.customer_id                                                                                  "
            + "   LEFT JOIN m_customer t4 ON t.consignor_id = t4.id                                                                                  "
            + "   LEFT JOIN m_owner t5 ON t.owner_id = t5.id                                                                                         "
            + "   LEFT JOIN m_bin t7 ON t.bin_id = t7.id                                                                                             "
            + "   LEFT JOIN m_location t8 ON t.location_id = t8.id                                                                                   "
            + "   LEFT JOIN m_warehouse t9 ON t.warehouse_id = t9.id                                                                                 "
            + "   LEFT JOIN b_out_extra t10 ON t10.out_id = t.id                                                                                     "
            + "   LEFT JOIN m_goods_spec t11 ON t11.id = t.sku_id                                                                                    "
            + "   LEFT JOIN m_goods_spec_prop t24 ON t11.prop_id = t24.id      "
            + "   LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                                                     "
            + "   LEFT JOIN b_sync_status t25 ON t.sync_id = t25.id                                                                                     "
            + "                                                                                                                                      ";

    String sum_select = "  "
            + "      SELECT                                                                                                                          "
            + "             sum(t.actual_count) actual_count,                                                                                        "
            + "             sum(t.amount) amount,                                                                                                    "
            + "             sum(t26.sync_error_count) sync_error_count,                                                                              "
            + "             ifnull(sum(t27.return_qty),0) count_return_qty,                                                                          "
            + "             (sum(t.actual_count) - ifnull(sum(t27.return_qty),0)) actual_count_return                               "
            + "        FROM                                                                                                                          "
            + "   	       b_out t                                                                                                                   "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                             "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                             "
            + "   LEFT JOIN m_staff t14 ON t.e_id = t14.id                                                                                           "
            + "   LEFT JOIN b_out_plan_detail t13 ON t.plan_detail_id = t13.id                                                                       "
            + "              LEFT JOIN (                                                                                                             "
            + "              SELECT                                                                                                                   "
            + "              	( @i := CASE WHEN @now_plan_id=t1.plan_id THEN @i + 1 ELSE 1 END ) idx,                                              "
            + "              	( @now_plan_id := t1.plan_id ),                                                                                      "
            + "              	t1.*                                                                                                                 "
            + "              FROM                                                                                                                     "
            + "              	b_out_plan_detail t1,                                                                                                "
            + "              	( SELECT @i := 0, @now_plan_id := '' ) AS a                                                                          "
            + "              ) tt   on t13.id = tt.id                                                                                                "
            + "   LEFT JOIN b_out_plan t3 ON t.plan_id = t3.id                                                                                       "
            + "  LEFT JOIN (                                                                                                                         "
            + "		SELECT                                                                                                                           "
            + "			t1.*,'b_in_order' order_type,t1.supplier_id customer_id                                                                      "
            + "		FROM                                                                                                                             "
            + "			 b_in_order t1                                                                                                               "
            + "			union all                                                                                                                    "
            + "		SELECT                                                                                                                           "
            + "			t2.*,'b_out_order' order_type,t2.client_id customer_id                                                                       "
            + "		FROM                                                                                                                             "
            + "			 b_out_order t2                                                                                                              "
            + "     )t19 on t13.order_id = t19.id and  t13.order_type = t19.order_type                                                               "
            + "   left join m_customer tt1 ON tt1.id = t19.customer_id                                                                                  "
            + "   LEFT JOIN m_customer t4 ON t.consignor_id = t4.id                                                                                  "
            + "   LEFT JOIN m_owner t5 ON t.owner_id = t5.id                                                                                         "
            + "   LEFT JOIN m_bin t7 ON t.bin_id = t7.id                                                                                             "
            + "   LEFT JOIN m_location t8 ON t.location_id = t8.id                                                                                   "
            + "   LEFT JOIN m_warehouse t9 ON t.warehouse_id = t9.id                                                                                 "
            + "   LEFT JOIN (select  tab1.contract_no, tab1.out_id, tab1.bill_type, tab2.name from b_out_extra tab1 left join m_customer tab2 on tab1.client_id = tab2.id "
            + "              ) t10 ON t10.out_id = t.id                                                                                              "
            + "   LEFT JOIN m_goods_spec t11 ON t11.id = t.sku_id                                                                                    "
            + "   LEFT JOIN m_goods_spec_prop t24 ON t11.prop_id = t24.id      "
            + "   LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                                                     "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_STATUS + "')t15 on t15.dict_value = t.status                               "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t16 ON t16.dict_value = t19.bill_type              "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t17 ON t17.dict_value = t3.type                             "
            + "   LEFT JOIN m_unit t20 ON t20.id = t.unit_id                                                                                         "
            + "   LEFT JOIN m_cancel t21 ON t21.serial_id = t.id and t21.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                       "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t22 ON t22.dict_value = t.type                              "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                                     "
            + "   left join (select count(tt1.id) sync_error_count, tt1.id, tt2.status from b_out tt1 inner join b_sync_status_error tt2 on tt1.sync_id = tt2.id where tt2.status = '0' group by tt1.id) t26 "
            + "             on t26.id = t.id                                                                                                            "
            + "  LEFT JOIN (select t.return_qty,t.id from b_out t where t.status in('"+DictConstant.DICT_B_OUT_STATUS_SUBMITTED+"','"+DictConstant.DICT_B_OUT_STATUS_PASSED+"')) t27 ON t27.id = t.id                                                                        "
            + "                                                                                                                                      ";

    String common_select_update = "  SELECT  t.*,t2.* from b_out t LEFT JOIN b_out_plan t2 ON t.plan_detail_id = t2.id     ";

    /**
     * 页面查询列表
     *
     * @param page
     * @param searchCondition
     * @return
     */
    @Select(" <script>   "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + "      SELECT                                                                                                                          "
            + "             t.*,                                                                                                                     "
            + "             t3.release_order_code,                                                                                                   "
            + "             t13.no,                                                                                                                  "
            + "             t13.type_gauge,                                                                                                          "
            + "             t13.alias,                                                                                                               "
            + "             ifnull(t19.contract_dt, t10.contract_dt) contract_dt,                                                                    "
            + "             ifnull(t19.contract_num, t10.contract_num) contract_num,                                                                 "
            + "             ifnull(t19.contract_no, t10.contract_no) contract_no,                                                                    "
            + "             t19.order_no,                                                                                                            "
            + "             t19.bill_type,                                                                                                           "
            + "             t10.pound_file,                                                                                                          "
            + "             t10.out_photo_file,                                                                                                      "
            + "             t13.code as detail_code,                                                                                                 "
            + "             t3.code as plan_code,                                                                                                    "
            + "             t3.extra_code,                                                                                                           "
            + "             t15.label as status_name,                                                                                                "
            + "             ifnull(t16.label, t10.label) as bill_type_name,                                                                          "
            + "             ifnull(t17.label, t22.label) as type_name,                                                                               "
            + "             ifnull(t4.short_name,t4.name) as consignor_name,                                                                         "
            + "             ifnull(t5.short_name,t5.name) as owner_name,                                                                             "
            + "             t9.short_name as warehouse_name,                                                                                         "
            + "             t9.name as warehouse_full_name,                                                                                          "
            + "             t7.name as bin_name,                                                                                                     "
            + "             t8.name as location_name,                                                                                                "
            + "             t11.spec,                                                                                                                "
            + "             t11.pm,                                                                                                                  "
            + "             t11.code as sku_code,                                                                                                    "
            + "             t12.name as goods_name,                                                                                                  "
            + "             t20.name as unit_name,                                                                                                   "
            + "             t1.name as c_name,                                                                                                       "
            + "             t14.name as e_name,                                                                                                      "
            + "             ifnull(tt2.name, t10.name) as client_name,                                                                               "
            + "             t2.name as u_name,                                                                                                       "
            + "             t26.id as monitor_out_id,                                                                                                "
            + "             t21.remark as cancel_remark                                                                                              "
            + "        FROM                                                                                                                          "

            + "       <choose>                                                                                                                                                                                                      "
            + "         <when test='p1.todo_status == 0'>                                                                                                                                                                           "
            + "	            b_todo subt1                                                                                                                                                                                          "
            + "	            INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                       "
            + "	            AND subt1.position_id = subt2.position_id                                                                                                                                                             "
            + "	            AND subt2.operation_perms = subt1.perms                                                                                                                                                               "
            + "	            AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                                                                                                       "
            + "	            AND subt1.STATUS = '" + DictConstant.DICT_B_TODO_STATUS_TODO + "'                                                                                                                                         "
            + "	            INNER JOIN b_out t ON t.id = subt1.serial_id                                                                                                                                                           "
            + "         </when>                                                                                                                                                                                                     "

            + "          <when test='p1.todo_status == 1'>                                                                                                                                                                          "
            + "	            b_already_do subt1                                                                                                                                                                                    "
            + "	            INNER JOIN b_out t ON t.id = subt1.serial_id                                                                                                                                                           "
            + "	            AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                                                                                                       "
            + "	            AND t.id = subt1.serial_id                                                                                                                                                                            "
            + "	            AND subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                                                                  "
            + "         </when>                                                                                                                                                                                                     "

            + "        <otherwise>                                                                                                                                                                                                  "
            + "         	  b_out t                                                                                                                                                                                                  "
            + "         </otherwise>                                                                                                                                                                                                "
            + "       </choose>                                                                                                                                                                                                     "

            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                             "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                             "
            + "   LEFT JOIN m_staff t14 ON t.e_id = t14.id                                                                                           "
            + "   LEFT JOIN b_out_plan_detail t13 ON t.plan_detail_id = t13.id                                                                       "
            + "   LEFT JOIN b_out_plan t3 ON t.plan_id = t3.id                                                                                       "
            + "   LEFT JOIN b_order t19 on t13.order_id = t19.serial_id and t13.order_type = t19.serial_type                                         "
//            + "   LEFT JOIN b_out_order tt1 ON tt1.id = t13.order_id                     "
            + "   LEFT JOIN m_customer tt2 ON tt2.id = t19.customer_id                                                                               "
            + "   LEFT JOIN m_customer t4 ON t.consignor_id = t4.id                                                                                  "
            + "   LEFT JOIN m_owner t5 ON t.owner_id = t5.id                                                                                         "
            + "   LEFT JOIN m_bin t7 ON t.bin_id = t7.id                                                                                             "
            + "   LEFT JOIN m_location t8 ON t.location_id = t8.id                                                                                   "
            + "   LEFT JOIN m_warehouse t9 ON t.warehouse_id = t9.id                                                                                 "
            + "   LEFT JOIN m_goods_spec t11 ON t11.id = t.sku_id                                                                                    "
            + "   LEFT JOIN m_goods_spec_prop t24 ON t11.prop_id = t24.id      "
            + "   LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                                                     "
            + "   LEFT JOIN (select tab1.out_photo_file, tab1.pound_file, tab1.contract_no, tab1.contract_num, tab1.contract_dt, tab1.out_id, tab1.bill_type, tab3.label, tab2.name from b_out_extra tab1 left join m_customer tab2 on tab1.client_id = tab2.id "
            + "              left join (select tab1.dict_value, tab1.label from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id              "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "') tab3 ON tab3.dict_value = tab1.bill_type ) t10 ON t10.out_id = t.id              "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_STATUS + "')t15 on t15.dict_value = t.status                             "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t16 ON t16.dict_value = t19.bill_type            "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t17 ON t17.dict_value = t3.type                           "
            + "   LEFT JOIN m_unit t20 ON t20.id = t.unit_id                                                                                         "
            + "   LEFT JOIN m_cancel t21 ON t21.serial_id = t.id and t21.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                   "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t22 ON t22.dict_value = t.type                            "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                            "
            // 判断是否来自监管任务
            + "   left join b_monitor_out t26 ON t26.out_id = t.id                                                                                   "
//            + "   LEFT JOIN b_release_order t6 ON t13.extra_code = t6.extra_code                                                                      "
            + "   where true                                                                                                                                                                                    "
            + "      and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null or #{p1.code,jdbcType=VARCHAR} = '')                                                  "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null or #{p1.plan_code,jdbcType=VARCHAR} = '')                                  "
            + "      and (CONCAT(t5.name,t5.short_name,t5.name_pinyin,t5.short_name_pinyin) like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)               "
            + "      and (CONCAT(t4.name,t4.short_name,t4.name_pinyin,t4.short_name_pinyin) like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)       "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                                  "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                                     "
            + "      and (t13.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%') or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                        "
            + "      and (t13.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%') or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                            "
            + "      and (ifnull(t19.bill_type, t10.bill_type) = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} ='')                                                 "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                                              "
            + "      and (t12.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                      "
            + "           or t11.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                   "
            + "           or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                 "
            + "           or t12.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null or #{p1.goods_name,jdbcType=VARCHAR} = ''  )                         "
            + "      and (ifnull(t19.contract_no, t10.contract_no) like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%')  or #{p1.contract_no,jdbcType=VARCHAR} is null)                                                               "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                                     "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &gt;= #{p1.ed_dt_start} or #{p1.ed_dt_start} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &lt;= #{p1.ed_dt_end} or #{p1.ed_dt_end} is null)                                       "
            + "      and (t24.name like concat('%', #{p1.prop}, '%') or #{p1.prop} is null or #{p1.prop} = '')                                    "
            + "      and (ifnull(tt2.name, t10.name) like concat('%', #{p1.client_name}, '%') or #{p1.client_name} is null or #{p1.client_name} = '')                  "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                    "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.c_time_start}, '%Y-%m-%d') or #{p1.c_time_start} is null)                                                                         "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.c_time_end}, '%Y-%m-%d') or #{p1.c_time_end} is null)                                                                             "
            + "      and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                         "
            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "       and t.type in                                                                                                                                              "
            + "           <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      and (t.vehicle_no like concat('%', #{p1.vehicle_no}, '%') or #{p1.vehicle_no} is null or #{p1.vehicle_no} = '')                                               "
            + "      and (t9.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "
            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                 "
            + "    </script>   ")
    IPage<BOutVo> selectPage(Page page, @Param("p1") BOutVo searchCondition);

    /**
     * 页面查询列表
     *
     * @param searchCondition
     * @return
     */
    @Select(" <script>   "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + "      SELECT                                                                                                                          "
            + "             count(t.id) c                                                                                                            "
            + "        FROM                                                                                                                          "
            + "	            b_todo subt1                                                                                                             "
            + "	            INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                          "
            + "	            AND subt1.position_id = subt2.position_id                                                                                "
            + "	            AND subt2.operation_perms = subt1.perms                                                                                  "
            + "	            AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                         "
            + "	            AND subt1.STATUS = '" + DictConstant.DICT_B_TODO_STATUS_TODO + "'                                                            "
            + "	            INNER JOIN b_out t ON t.id = subt1.serial_id                                                                             "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                             "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                             "
            + "   LEFT JOIN m_staff t14 ON t.e_id = t14.id                                                                                           "
            + "   LEFT JOIN b_out_plan_detail t13 ON t.plan_detail_id = t13.id                                                                       "
            + "   LEFT JOIN b_out_plan t3 ON t.plan_id = t3.id                                                                                       "
            + "   LEFT JOIN b_order t19 on t13.order_id = t19.serial_id and t13.order_type = t19.serial_type                                         "
            + "   LEFT JOIN b_out_order tt1 ON tt1.id = t13.order_id                     "
            + "   LEFT JOIN m_customer tt2 ON tt2.id = tt1.client_id                  "
            + "   LEFT JOIN m_customer t4 ON t.consignor_id = t4.id                                                                                  "
            + "   LEFT JOIN m_owner t5 ON t.owner_id = t5.id                                                                                         "
            + "   LEFT JOIN m_bin t7 ON t.bin_id = t7.id                                                                                             "
            + "   LEFT JOIN m_location t8 ON t.location_id = t8.id                                                                                   "
            + "   LEFT JOIN m_warehouse t9 ON t.warehouse_id = t9.id                                                                                 "
            + "   LEFT JOIN m_goods_spec t11 ON t11.id = t.sku_id                                                                                    "
            + "   LEFT JOIN m_goods_spec_prop t24 ON t11.prop_id = t24.id      "
            + "   LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                                                     "
            + "   LEFT JOIN (select tab1.out_photo_file, tab1.pound_file, tab1.contract_no, tab1.contract_num, tab1.contract_dt, tab1.out_id, tab1.bill_type, tab3.label, tab2.name from b_out_extra tab1 left join m_customer tab2 on tab1.client_id = tab2.id "
            + "              left join (select tab1.dict_value, tab1.label from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id              "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "') tab3 ON tab3.dict_value = tab1.bill_type ) t10 ON t10.out_id = t.id              "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_STATUS + "')t15 on t15.dict_value = t.status                               "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t16 ON t16.dict_value = t19.bill_type              "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t17 ON t17.dict_value = t3.type                             "
            + "   LEFT JOIN m_unit t20 ON t20.id = t.unit_id                                                                                         "
            + "   LEFT JOIN m_cancel t21 ON t21.serial_id = t.id and t21.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                       "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t22 ON t22.dict_value = t.type                              "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                            "
//            + "   LEFT JOIN b_release_order t6 ON t13.extra_code = t6.extra_code                                                                      "
            + "   where true                                                                                                                                                                                    "
            + "      and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null or #{p1.code,jdbcType=VARCHAR} = '')                                                  "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null or #{p1.plan_code,jdbcType=VARCHAR} = '')                                  "
            + "      and (CONCAT(t5.name,t5.short_name,t5.name_pinyin,t5.short_name_pinyin) like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)               "
            + "      and (CONCAT(t4.name,t4.short_name,t4.name_pinyin,t4.short_name_pinyin) like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)       "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                                  "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                                     "
            + "      and (t13.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%') or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                        "
            + "      and (t13.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%') or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                            "
            + "      and (ifnull(t19.bill_type, t10.bill_type) = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} ='')                                                 "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                                              "
            + "      and (t12.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                      "
            + "           or t11.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                   "
            + "           or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                 "
            + "           or t12.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null or #{p1.goods_name,jdbcType=VARCHAR} = ''  )                         "
            + "      and (ifnull(t19.contract_no, t10.contract_no) like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%')  or #{p1.contract_no,jdbcType=VARCHAR} is null)                                                               "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                                     "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &gt;= #{p1.ed_dt_start} or #{p1.ed_dt_start} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &lt;= #{p1.ed_dt_end} or #{p1.ed_dt_end} is null)                                       "
            + "      and (t24.name like concat('%', #{p1.prop}, '%') or #{p1.prop} is null or #{p1.prop} = '')                                    "
            + "      and (ifnull(tt2.name, t10.name) like concat('%', #{p1.client_name}, '%') or #{p1.client_name} is null or #{p1.client_name} = '')                  "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                    "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.c_time_start}, '%Y-%m-%d') or #{p1.c_time_start} is null)                                                                         "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.c_time_end}, '%Y-%m-%d') or #{p1.c_time_end} is null)                                                                             "
            + "      and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                              "
            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "       and t.type in                                                                                                                                              "
            + "           <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      and (t.vehicle_no like concat('%', #{p1.vehicle_no}, '%') or #{p1.vehicle_no} is null or #{p1.vehicle_no} = '')                                               "
            + "      and (t9.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "

              // 包含放货指令
            + "      <if test='p1.out_release_status == 1' >                                                                                                                                                    "
            + "      and exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               FROM b_release_order t1                                                                                                                                                           "
            + "               WHERE t3.release_order_code = t1.CODE                                                                                                                                            "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "

            // 不包含放货指令
            + "      <if test='p1.out_release_status == 2' >                                                                                                                                                    "
            + "      and not exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               FROM b_release_order t1                                                                                                                                                           "
            + "               WHERE t3.release_order_code = t1.CODE                                                                                                                                            "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "


            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                 "
            + "    </script>   ")
    Integer selectTodoCount(@Param("p1") BOutVo searchCondition);

    /**
     * 页面查询列表
     *
     * @param searchCondition
     * @return
     */
    @Select(" <script>   "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + "      SELECT                                                                                                                          "
            + "             count(1) c                                                                                                               "
            + "        FROM                                                                                                                          "
            + "       <choose>                                                                                                                                                                                                      "
            + "         <when test='p1.todo_status == 0'>                                                                                                                                                                           "
            + "	            b_todo subt1                                                                                                                                                                                          "
            + "	            INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                       "
            + "	            AND subt1.position_id = subt2.position_id                                                                                                                                                             "
            + "	            AND subt2.operation_perms = subt1.perms                                                                                                                                                               "
            + "	            AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                                                                                                       "
            + "	            AND subt1.STATUS = '" + DictConstant.DICT_B_TODO_STATUS_TODO + "'                                                                                                                                         "
            + "	            INNER JOIN b_out t ON t.id = subt1.serial_id                                                                                                                                                           "
            + "         </when>                                                                                                                                                                                                     "

            + "          <when test='p1.todo_status == 1'>                                                                                                                                                                          "
            + "	            b_already_do subt1                                                                                                                                                                                    "
            + "	            INNER JOIN b_out t ON t.id = subt1.serial_id                                                                                                                                                           "
            + "	            AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                                                                                                       "
            + "	            AND t.id = subt1.serial_id                                                                                                                                                                            "
            + "	            AND subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                                                                  "
            + "         </when>                                                                                                                                                                                                     "

            + "        <otherwise>                                                                                                                                                                                                  "
            + "         	  b_out t                                                                                                                                                                                                  "
            + "         </otherwise>                                                                                                                                                                                                "
            + "       </choose>                                                                                                                                                                                                     "
            + "   LEFT JOIN b_out_plan_detail t13 ON t.plan_detail_id = t13.id                                                                       "
            + "              LEFT JOIN (                                                                                                             "
            + "              SELECT                                                                                                                   "
            + "              	( @i := CASE WHEN @now_plan_id=t1.plan_id THEN @i + 1 ELSE 1 END ) idx,                                              "
            + "              	( @now_plan_id := t1.plan_id ),                                                                                      "
            + "              	t1.*                                                                                                                 "
            + "              FROM                                                                                                                     "
            + "              	b_out_plan_detail t1,                                                                                                "
            + "              	( SELECT @i := 0, @now_plan_id := '' ) AS a                                                                          "
            + "              ) tt   on t13.id = tt.id                                                                                                "
            + "   LEFT JOIN b_out_plan t3 ON t.plan_id = t3.id                                                                                       "
            + "  LEFT JOIN (                                                                                                                         "
            + "		SELECT                                                                                                                           "
            + "			t1.*,'b_in_order' order_type,t1.supplier_id customer_id                                                                      "
            + "		FROM                                                                                                                             "
            + "			 b_in_order t1                                                                                                               "
            + "			union all                                                                                                                    "
            + "		SELECT                                                                                                                           "
            + "			t2.*,'b_out_order' order_type,t2.client_id customer_id                                                                       "
            + "		FROM                                                                                                                             "
            + "			 b_out_order t2                                                                                                              "
            + "     )t19 on t13.order_id = t19.id and  t13.order_type = t19.order_type                                                               "
            + "   left join m_customer tt1 ON tt1.id = t19.customer_id                                                                               "
            + "   LEFT JOIN m_customer t4 ON t.consignor_id = t4.id                                                                                  "
            + "   LEFT JOIN m_owner t5 ON t.owner_id = t5.id                                                                                         "
            + "   LEFT JOIN m_bin t7 ON t.bin_id = t7.id                                                                                             "
            + "   LEFT JOIN m_location t8 ON t.location_id = t8.id                                                                                   "
            + "   LEFT JOIN m_warehouse t9 ON t.warehouse_id = t9.id                                                                                 "
           + "   LEFT JOIN m_goods_spec t11 ON t11.id = t.sku_id                                                                                    "
            + "   LEFT JOIN m_goods_spec_prop t24 ON t11.prop_id = t24.id      "
            + "   LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                                                     "
            + "   LEFT JOIN (select  tab1.contract_no, tab1.out_id, tab1.bill_type, tab2.name from b_out_extra tab1 left join m_customer tab2 on tab1.client_id = tab2.id "
            + "              ) t10 ON t10.out_id = t.id                                                                                              "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                                     "
            + "   where true                                                                                                                                                                                    "
            + "      and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null or #{p1.code,jdbcType=VARCHAR} = '')                                                  "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null or #{p1.plan_code,jdbcType=VARCHAR} = '')                                  "
            + "      and (CONCAT(t5.name,t5.short_name,t5.name_pinyin,t5.short_name_pinyin) like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)               "
            + "      and (CONCAT(t4.name,t4.short_name,t4.name_pinyin,t4.short_name_pinyin) like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)       "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                                  "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                                     "
            + "      and (t13.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%') or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                                       "
            + "      and (t13.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%') or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                       "
            + "      and (ifnull(t19.bill_type, t10.bill_type) = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} ='')                                                 "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                                              "
            + "      and (t12.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                      "
            + "           or t11.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                   "
            + "           or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                 "
            + "           or t12.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null or #{p1.goods_name,jdbcType=VARCHAR} = ''  )                         "
            + "      and (ifnull(t19.contract_no, t10.contract_no) like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)                                                               "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                                     "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &gt;= #{p1.ed_dt_start} or #{p1.ed_dt_start} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &lt;= #{p1.ed_dt_end} or #{p1.ed_dt_end} is null)                                       "
            + "      and (t24.name like concat('%', #{p1.prop}, '%') or #{p1.prop} is null or #{p1.prop} = '')                                    "
            + "      and (ifnull(tt1.name, t10.name) like concat('%', #{p1.client_name}, '%') or #{p1.client_name} is null or #{p1.client_name} = '')                  "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                    "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.c_time_start}, '%Y-%m-%d') or #{p1.c_time_start} is null)                                                                         "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.c_time_end}, '%Y-%m-%d') or #{p1.c_time_end} is null)                                                                             "
            + "      and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                              "
            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "       and t.type in                                                                                                                                              "
            + "           <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      and (t.vehicle_no like concat('%', #{p1.vehicle_no}, '%') or #{p1.vehicle_no} is null or #{p1.vehicle_no} = '')                                               "
            + "      and (t9.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "

            // 包含放货指令
            + "      <if test='p1.out_release_status == 1' >                                                                                                                                                    "
            + "      and exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               FROM b_release_order t1                                                                                                                                                           "
            + "               WHERE t3.release_order_code = t1.CODE                                                                                                                                            "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "

            // 不包含放货指令
            + "      <if test='p1.out_release_status == 2' >                                                                                                                                                    "
            + "      and not exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               FROM b_release_order t1                                                                                                                                                           "
            + "               WHERE t3.release_order_code = t1.CODE                                                                                                                                            "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "


            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                 "
            + "    </script>   ")
    Long selectPageMyCount(@Param("p1") BOutVo searchCondition);


    /**
     * 页面查询列表
     *
     * @param searchCondition
     * @return
     */
    @Select(" <script>   "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + sum_select
            + "   where true                                                                                                                                                                                    "
            + "      and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null or #{p1.code,jdbcType=VARCHAR} = '')                                                  "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null or #{p1.plan_code,jdbcType=VARCHAR} = '')                                  "
            + "      and (CONCAT(t5.name,t5.short_name,t5.name_pinyin,t5.short_name_pinyin) like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)               "
            + "      and (CONCAT(t4.name,t4.short_name,t4.name_pinyin,t4.short_name_pinyin) like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)       "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                                  "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                                     "
            + "      and (ifnull(t19.bill_type, t10.bill_type) = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} ='')                                                 "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                                              "
            + "      and (t12.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                      "
            + "           or t11.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                   "
            + "           or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                 "
            + "           or t12.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null or #{p1.goods_name,jdbcType=VARCHAR} = ''  )                         "
            + "      and (ifnull(t19.contract_no, t10.contract_no) like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)                                                               "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                                     "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &gt;= #{p1.ed_dt_start} or #{p1.ed_dt_start} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &lt;= #{p1.ed_dt_end} or #{p1.ed_dt_end} is null)                                       "
            + "      and (t24.name like concat('%', #{p1.prop}, '%') or #{p1.prop} is null or #{p1.prop} = '')                                    "
            + "      and (ifnull(tt1.name, t10.name) like concat('%', #{p1.client_name}, '%') or #{p1.client_name} is null or #{p1.client_name} = '')                  "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                    "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.c_time_start}, '%Y-%m-%d') or #{p1.c_time_start} is null)                                                                         "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.c_time_end}, '%Y-%m-%d') or #{p1.c_time_end} is null)                                                                             "
            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "       and t.type in                                                                                                                                              "
            + "           <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      and (t.vehicle_no like concat('%', #{p1.vehicle_no}, '%') or #{p1.vehicle_no} is null or #{p1.vehicle_no} = '')                                               "
            + "      and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                              "
            + "      and (t9.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "
            + "      <if test='p1.todo_status == 0' >                                                                                                                                           "
            + " and exists (                                                                                                                                                                    "
            + "		SELECT                                                                                                                                                                      "
            + "				1                                                                                                                                                                   "
            + "			FROM                                                                                                                                                                    "
            + "				b_todo subt1                                                                                                                                                        "
            + "				INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                     "
            + "				AND subt1.position_id = subt2.position_id                                                                                                                           "
            + "				AND subt2.operation_perms = subt1.perms                                                                                                                             "
            + "			WHERE                                                                                                                                                                   "
            + "				t.id = subt1.serial_id                                                                                                                                              "
            + "				AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                                                        "
            + "				AND subt1.STATUS = '" + DictConstant.DICT_B_TODO_STATUS_TODO + "'                                                                                                       "
            + "  )                                                                                                                                                                              "
            + "      </if>                                                                                                                                                                      "

            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                                                                                                           "
            + "      and exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               from b_already_do subt1                                                                                                                                                           "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                                                                              "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}"
            + "                and serial_id = t.id                                                                                                                                                             "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "

            // 包含放货指令
            + "      <if test='p1.out_release_status == 1' >                                                                                                                                                    "
            + "      and exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               FROM b_release_order t1                                                                                                                                                           "
            + "               WHERE t3.release_order_code = t1.CODE                                                                                                                                            "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "

            // 不包含放货指令
            + "      <if test='p1.out_release_status == 2' >                                                                                                                                                    "
            + "      and not exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               FROM b_release_order t1                                                                                                                                                           "
            + "               WHERE t3.release_order_code = t1.CODE                                                                                                                                            "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "

            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                "
            + "    </script>   ")
    BOutSumVo selectSumData(@Param("p1") BOutVo searchCondition);

    /**
     * 页面查询列表
     *
     * @param searchCondition
     * @return
     */
    @Select(" <script>   "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + "      SELECT                                                                                                                          "
            + "             @row_num:= @row_num+ 1 as excel_no,                                                                                      "
            + "             t.code,                                                                                                                  "
            + "             t3.code as plan_code,                                                                                                    "
            + "             t13.no,                                                                                                                  "
            + "             t3.release_order_code,                                                                                                   "
            + "             t15.label as status_name,                                                                                                "
            + "             ifnull(t17.label, t22.label) as type_name,                                                                               "
            + "             t9.short_name as warehouse_name,                                                                                         "
            + "             t9.name as warehouse_name,                                                                                               "
            + "             ifnull(t9.short_name,t9.name) as warehouse_name,                                                                         "
            + "             t9.name as warehouse_full_name,                                                                                          "
            + "             ifnull(t4.short_name,t4.name) as consignor_name,                                                                         "
            + "             ifnull(t5.short_name,t5.name) as owner_name,                                                                             "
            + "             ifnull(tt2.name, t10.name) as client_name,                                                                               "
            + "             ifnull(t19.contract_no, t10.contract_no) contract_no,                                                                    "
            + "             ifnull(t16.label, t10.label) as bill_type_name,                                                                          "
            + "             ifnull(t19.contract_dt, t10.contract_dt) contract_dt,                                                                    "
            + "             ifnull(t19.contract_num, t10.contract_num) contract_num,                                                                 "
            + "             t12.name as goods_name,                                                                                                  "
            + "             t11.pm,                                                                                                                  "
            + "             t11.spec,                                                                                                                "
            + "             t11.code as sku_code,                                                                                                    "
            + "             t13.type_gauge,                                                                                                          "
            + "             t13.alias,                                                                                                               "
            + "             t.actual_count,                                                                                                          "
            + "             t20.name as unit_name,                                                                                                   "
            + "             t.actual_weight,                                                                                                         "
            + "             t.price,                                                                                                                 "
            + "             t.amount,                                                                                                                "
            + "             t.outbound_time,                                                                                                         "
            + "             t21.remark as cancel_remark,                                                                                             "
            + "             t1.name as c_name,                                                                                                       "
            + "             t.c_time,                                                                                                                "
            + "             t14.name as e_name,                                                                                                      "
            + "             t.e_dt,                                                                                                                  "
            + "             t2.name as u_name,                                                                                                       "
            + "             t.u_time,                                                                                                                "
            + "             t.vehicle_no,                                                                                                            "
            + "             t.tare_weight,                                                                                                           "
            + "             t.gross_weight,                                                                                                          "
            + "             t3.extra_code,                                                                                                           "
            + "             t.cancel_audit_dt,                                                                                                       "
            + "             t27.name cancel_audit_name                                                                                               "
            + "        FROM                                                                                                                          "
            + "       <choose>                                                                                                                                                                                                      "
            + "         <when test='p1.todo_status == 0'>                                                                                                                                                                           "
            + "	            b_todo subt1                                                                                                                                                                                          "
            + "	            INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                       "
            + "	            AND subt1.position_id = subt2.position_id                                                                                                                                                             "
            + "	            AND subt2.operation_perms = subt1.perms                                                                                                                                                               "
            + "	            AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                                                                                                       "
            + "	            AND subt1.STATUS = '" + DictConstant.DICT_B_TODO_STATUS_TODO + "'                                                                                                                                         "
            + "	            INNER JOIN b_out t ON t.id = subt1.serial_id                                                                                                                                                           "
            + "         </when>                                                                                                                                                                                                     "

            + "          <when test='p1.todo_status == 1'>                                                                                                                                                                          "
            + "	            b_already_do subt1                                                                                                                                                                                    "
            + "	            INNER JOIN b_out t ON t.id = subt1.serial_id                                                                                                                                                           "
            + "	            AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                                                                                                       "
            + "	            AND t.id = subt1.serial_id                                                                                                                                                                            "
            + "	            AND subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                                                                  "
            + "         </when>                                                                                                                                                                                                     "

            + "        <otherwise>                                                                                                                                                                                                  "
            + "         	  b_out t                                                                                                                                                                                                  "
            + "         </otherwise>                                                                                                                                                                                                "
            + "       </choose>                                                                                                                                                                                                     "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                             "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                             "
            + "   LEFT JOIN m_staff t14 ON t.e_id = t14.id                                                                                           "
            + "   LEFT JOIN b_out_plan_detail t13 ON t.plan_detail_id = t13.id                                                                       "
            + "   LEFT JOIN b_out_plan t3 ON t.plan_id = t3.id                                                                                       "
            + "   LEFT JOIN b_order t19 on t13.order_id = t19.serial_id and t13.order_type = t19.serial_type                                         "
            + "   LEFT JOIN b_out_order tt1 ON tt1.id = t13.order_id                   "
            + "   LEFT JOIN m_customer tt2 ON tt2.id = tt1.client_id                  "
            + "   LEFT JOIN m_customer t4 ON t.consignor_id = t4.id                                                                                  "
            + "   LEFT JOIN m_owner t5 ON t.owner_id = t5.id                                                                                         "
            + "   LEFT JOIN m_warehouse t9 ON t.warehouse_id = t9.id                                                                                 "
            + "   LEFT JOIN m_goods_spec t11 ON t11.id = t.sku_id                                                                                    "
            + "   LEFT JOIN m_goods_spec_prop t24 ON t11.prop_id = t24.id      "
            + "   LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                                                     "
            + "   LEFT JOIN (select tab1.out_photo_file, tab1.pound_file, tab1.contract_no, tab1.contract_num, tab1.contract_dt, tab1.out_id, tab1.bill_type, tab3.label, tab2.name from b_out_extra tab1 left join m_customer tab2 on tab1.client_id = tab2.id "
            + "              left join (select tab1.dict_value, tab1.label from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id              "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "') tab3 ON tab3.dict_value = tab1.bill_type ) t10 ON t10.out_id = t.id              "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_STATUS + "')t15 on t15.dict_value = t.status                               "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t16 ON t16.dict_value = t19.bill_type              "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t17 ON t17.dict_value = t3.type                             "
            + "   LEFT JOIN m_unit t20 ON t20.id = t.unit_id                                                                                         "
            + "   LEFT JOIN m_cancel t21 ON t21.serial_id = t.id and t21.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                       "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t22 ON t22.dict_value = t.type                              "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                                     "
            + "   left join m_staff t27 ON t27.id = t.cancel_audit_id                                                                                "
            + "     ,(select @row_num:=0) t23                                                                                                        "
            + "   where true                                                                                                                                                                                    "
            + "      and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null or #{p1.code,jdbcType=VARCHAR} = '')                                                  "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null or #{p1.plan_code,jdbcType=VARCHAR} = '')                                  "
            + "      and (CONCAT(t5.name,t5.short_name,t5.name_pinyin,t5.short_name_pinyin) like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)               "
            + "      and (CONCAT(t4.name,t4.short_name,t4.name_pinyin,t4.short_name_pinyin) like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)       "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                                  "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                                     "
            + "      and (t13.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%') or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                        "
            + "      and (t13.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%') or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                            "
            + "      and (ifnull(t19.bill_type, t10.bill_type) = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} ='')                                                 "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                                              "
            + "      and (t12.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                      "
            + "           or t11.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                   "
            + "           or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                 "
            + "           or t12.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null or #{p1.goods_name,jdbcType=VARCHAR} = ''  )                         "
            + "      and (ifnull(t19.contract_no, t10.contract_no) like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%')  or #{p1.contract_no,jdbcType=VARCHAR} is null)                                                               "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                                     "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &gt;= #{p1.ed_dt_start} or #{p1.ed_dt_start} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &lt;= #{p1.ed_dt_end} or #{p1.ed_dt_end} is null)                                       "
            + "      and (t24.name like concat('%', #{p1.prop}, '%') or #{p1.prop} is null or #{p1.prop} = '')                                    "
            + "      and (ifnull(tt2.name, t10.name) like concat('%', #{p1.client_name}, '%') or #{p1.client_name} is null or #{p1.client_name} = '')                  "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                    "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.c_time_start}, '%Y-%m-%d') or #{p1.c_time_start} is null)                                                                         "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.c_time_end}, '%Y-%m-%d') or #{p1.c_time_end} is null)                                                                             "
            + "      and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                              "
            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "       and t.type in                                                                                                                                              "
            + "           <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      and (t.vehicle_no like concat('%', #{p1.vehicle_no}, '%') or #{p1.vehicle_no} is null or #{p1.vehicle_no} = '')                                               "
            + "      and (t9.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "

            // 包含放货指令
            + "      <if test='p1.out_release_status == 1' >                                                                                                                                                    "
            + "      and exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               FROM b_release_order t1                                                                                                                                                           "
            + "               WHERE t3.release_order_code = t1.CODE                                                                                                                                            "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "

            // 不包含放货指令
            + "      <if test='p1.out_release_status == 2' >                                                                                                                                                    "
            + "      and not exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               FROM b_release_order t1                                                                                                                                                           "
            + "               WHERE t3.release_order_code = t1.CODE                                                                                                                                            "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "

            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                 "
            + "    </script>   ")
    List<BOutExportVo> selectExportAllList(@Param("p1") BOutVo searchCondition);

    /**
     * 页面查询列表
     *
     * @param plan_id
     * @return
     */
    @Select("    "
            + common_select
            + "   where true                                                                                                                                         "
            + "     and (t.plan_id = #{p1,jdbcType=INTEGER})                         "
            + "       ")
    List<BOutVo> selectOutByPlanId(@Param("p1") Integer plan_id);

    /**
     * 页面查询列表
     *
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "   where true                                                                                                                                         "
            + "     and (t.id = #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                         "
            + "       ")
    List<BOutVo> selectOutList(@Param("p1") BOutVo searchCondition);
//    /**
//     * 页面查询列表
//     * @param searchCondition
//     * @return
//     */
//    @Select("    "
//            + "     select id from b_temp_patch_1                        "
//            + "       ")
//    List<BOutVo> selectOutList(@Param("p1") BOutVo searchCondition);


    /**
     * 没有分页，按id筛选条件
     *
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
            + common_select_update
            + "  where t.id in "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item.id,jdbcType=INTEGER}  "
            + "        </foreach>    "
            + "  </script>    ")
    List<BOutEntity> selectIdsOut(@Param("p1") List<BOutVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     *
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    BOutVo selectId(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     *
     * @param id
     * @return
     */
    @Select("    "
            + "  select count(1)                 "
            + "    from b_out t1                  "
            + "   where t1.lot = #{p1}           "
            + "                                  ")
    Integer countLot(@Param("p1") String id);

    /**
     * 按计划id修改明细为过期
     */
    @Select(
            "    "
                    + "  update                                                                                                                                          "
                    + "     b_out t1                                                                                                                                     "
                    + "     left join b_out_plan_detail t2 on t1.plan_detail_id = t2.id                                                                                  "
                    + "     left join b_out_plan t3 on t1.plan_id = t3.id                                                                                                "
                    + "     set t1.status = '" + DictConstant.DICT_B_OUT_STATUS_EXPIRES + "'                                                                                 "
                    + "      where (t1.status = '" + DictConstant.DICT_B_OUT_STATUS_SAVED + "'  or t1.status = '" + DictConstant.DICT_B_OUT_STATUS_SUBMITTED + "' )    "
                    + "      and t3.extra_code =  #{p1,jdbcType=VARCHAR}                                                                                                 "
                    + "      ")
    void expiresOut(@Param("p1") String code);


    /**
     * 按计划id修改明细为中止
     */
    @Select(
            "    "
                    + "  update                                                                                                                                          "
                    + "     b_out t1                                                                                                                                     "
                    + "     left join b_out_plan_detail t2 on t1.plan_detail_id = t2.id                                                                                  "
                    + "     left join b_out_plan t3 on t1.plan_id = t3.id                                                                                                "
                    + "     set t1.status = '" + DictConstant.DICT_B_OUT_STATUS_CANCEL + "'                                                                                  "
                    + "      where (t1.status = '" + DictConstant.DICT_B_OUT_STATUS_SAVED + "'  or t1.status = '" + DictConstant.DICT_B_OUT_STATUS_SUBMITTED + "' or t1.status = '" + DictConstant.DICT_B_OUT_STATUS_RETURN + "' )    "
                    + "      and t3.extra_code =  #{p1,jdbcType=VARCHAR}                                                                                                 "
                    + "      ")
    void discontinueOut(@Param("p1") String code);

    /**
     * 按计划id修改明细为中止
     */
    @Select(""
            + "		UPDATE b_out t1                                                                                     "
            + "		INNER JOIN b_todo t2 ON t1.id = t2.serial_id                                                        "
            + "		AND t2.serial_type = 'b_out'                                                                        "
            + "		INNER JOIN b_out_plan_detail t3 ON t1.plan_detail_id = t3.id                                        "
            + "		INNER JOIN b_out_plan t4 ON t1.plan_id = t4.id                                                      "
            + "		SET t2.STATUS = '1'                                                                                 "
            + "		WHERE                                                                                               "
            + "			true                                                                                            "
            + "      and t4.extra_code =  #{p1,jdbcType=VARCHAR}                                                        "
            + "      ")
    void updateTodoData(@Param("p1") String code);


    /**
     * 查询出库通知下未完成的物流订单数量
     *
     * @param code
     * @return
     */
    @Select(
            "    "
                    + "			SELECT                                                                                              "
                    + "				count(t3.id) c,                                                                                 "
                    + "				t3.code                                                                                         "
                    + "			FROM                                                                                                "
                    + "				b_out_plan t1                                                                                   "
                    + "				LEFT JOIN b_out_plan_detail t2 ON t1.id = t2.plan_id                                            "
                    + "				LEFT JOIN b_schedule t3 ON t3.out_plan_detail_id = t2.id                                        "
                    + "			WHERE                                                                                               "
                    + "			TRUE                                                                                                "
                    + "				AND t3.STATUS IN ('0','2','3')                                                                  "
                    + "				AND t3.IS_DELETE = '0'                                                                          "
                    + "				AND t1.extra_code = #{p1,jdbcType=VARCHAR}                                                      "
                    + "				having c > 0                                                                                    "
                    + "      ")
    BScheduleVo selectScheduleCount(@Param("p1") String code);

    /**
     * 查询出库通知下未完成的监管任务数量
     *
     * @param code
     * @return
     */
    @Select("    "
            + "	SELECT                                                                                                  "
            + "		count(t3.id) c,                                                                                     "
            + "		group_concat(t4.code) code                                                                          "
            + "	FROM                                                                                                    "
            + "		b_out_plan t1                                                                                       "
            + "		LEFT JOIN b_out_plan_detail t2 ON t1.id = t2.plan_id                                                "
            + "		LEFT JOIN b_schedule t3 ON t3.out_plan_detail_id = t2.id                                            "
            + "		left join b_monitor t4 on t4.schedule_id = t3.id                                                    "
            + "	WHERE                                                                                                   "
            + "	TRUE                                                                                                    "
            + "		AND t4.status not in ('" + DictConstant.DICT_B_MONITOR_STATUS_SEVEN + "',                           "
            + "		                      '" + DictConstant.DICT_B_MONITOR_STATUS_FOUR + "',                            "
            + "		                      '" + DictConstant.DICT_B_MONITOR_STATUS_FIVE + "',                            "
            + "		                      '" + DictConstant.DICT_B_MONITOR_STATUS_SIX + "',                             "
            + "                           '" + DictConstant.DICT_B_MONITOR_STATUS_EIGHT + "')                           "
            + "		AND t1.extra_code = #{p1,jdbcType=VARCHAR}                                                          "
            + "		having c > 0                                                                                        "
            + "      ")
    BMonitorVo selectMonitorCount(@Param("p1") String code);

    /**
     * 查询出库通知下未完成的监管任务数量
     *
     * @param code
     * @return
     */
    @Select("    "
            + "	SELECT                                                                                                  "
            + "		count(t3.id) c,                                                                                     "
            + "		t4.code                                                                                             "
            + "	FROM                                                                                                    "
            + "		b_out_plan t1                                                                                       "
            + "		LEFT JOIN b_out_plan_detail t2 ON t1.id = t2.plan_id                                                "
            + "		LEFT JOIN b_schedule t3 ON t3.out_plan_detail_id = t2.id                                            "
            + "		left join b_monitor t4 on t4.schedule_id = t3.id                                                    "
            + "	WHERE                                                                                                   "
            + "	TRUE                                                                                                    "
            + "		AND t4.status not in ('" + DictConstant.DICT_B_MONITOR_STATUS_EIGHT + "')                               "
            + "		AND t1.extra_code = #{p1,jdbcType=VARCHAR}                                                          "
            + "		having c > 0                                                                                        "
            + "      ")
    BMonitorVo selectMonitorCount1(@Param("p1") String code);

    /**
     * 查询出库通知下未完成的监管任务数量
     *
     * @param code
     * @return
     */
    @Select("    "
            + "	SELECT                                                                                                  "
            + "		count(t1.id) c,                                                                                     "
            + "		t1.code                                                                                             "
            + "	FROM                                                                                                    "
            + "		b_out t1                                                                                            "
            + "		LEFT JOIN b_out_plan t2 ON t1.plan_id = t2.id                                                       "
            + "	WHERE                                                                                                   "
            + "	TRUE                                                                                                    "
            + "		AND t1.status <> '" + DictConstant.DICT_B_OUT_STATUS_CANCEL + "'                                        "
            + "		AND t2.extra_code = #{p1,jdbcType=VARCHAR}                                                          "
            + "		having c > 0                                                                                        "
            + "      ")
    BOutVo selectUnAuditCount(@Param("p1") String code);


    /**
     * 按计划id修改明细为过期
     */
    @Select("    "
            + "  select t1.* from                                                                                                                                "
            + "     b_out t1                                                                                                                                     "
            + "     left join b_out_plan_detail t2 on t1.plan_detail_id = t2.id                                                                                  "
            + "     left join b_out_plan t3 on t1.plan_id = t3.id                                                                                                "
            + "      where (t1.status = '" + DictConstant.DICT_B_OUT_STATUS_SAVED + "'  or t1.status = '" + DictConstant.DICT_B_OUT_STATUS_SUBMITTED + "' or t1.status = '" + DictConstant.DICT_B_OUT_STATUS_CANCEL + "' )              "
            + "      and t3.extra_code =  #{p1,jdbcType=VARCHAR}                                                                                                 "
            + "      ")
    List<BOutVo> selectOutByExtraCode(@Param("p1") String code);

    /**
     * 查询入库计划底下的全部出库单
     */
    @Select("    "
            + "  select t1.* from                                                                                                                                "
            + "     b_out t1                                                                                                                                     "
            + "     left join b_out_plan_detail t2 on t1.plan_detail_id = t2.id                                                                                  "
            + "     left join b_out_plan t3 on t1.plan_id = t3.id                                                                                                "
            + "      where  true                                                                                                                                 "
            + "      and t3.extra_code =  #{p1,jdbcType=VARCHAR}                                                                                                 "
            + "      ")
    List<BOutVo> selectAllOutByExtraCode(@Param("p1") String code);

    /**
     * 悲观锁
     *
     * @param id
     * @return
     */
    @Select("    "
            + "  select *                        "
            + "    from b_out t1                  "
            + "   where t1.id = #{p1}            "
            + "     for update                   "
            + "                                  ")
    BOutEntity setBillOutForUpdate(@Param("p1") Integer id);

    /**
     * 监管任务出库id查询出库单数据
     */
    @Select("                                                                                                                 "
            + "     SELECT                                                                                                    "
            + "            t4.type,                                                                                           "
            + "            t4.owner_id,                                                                                       "
            + "            t4.owner_code,                                                                                     "
            + "            t4.consignor_id,                                                                                   "
            + "            t4.consignor_code,                                                                                 "
            + "            t2.qty actual_weight,                                                                              "
            + "            t2.qty actual_count,                                                                               "
            + "            t3.weight plan_weight,                                                                             "
            + "            t3.weight plan_count,                                                                              "
            + "            '" + DictConstant.DICT_B_OUT_STATUS_SUBMITTED + "' as status,                                         "
            + "            t4.id as plan_id,                                                                                  "
            + "            t3.id as plan_detail_id,                                                                           "
            + "            t.out_warehouse_id warehouse_id,                                                                   "
            + "            t.out_location_id as location_id,                                                                  "
            + "            t.out_bin_id as bin_id,                                                                            "
            + "            t3.sku_id,                                                                                         "
            + "            t3.sku_code,                                                                                       "
            + "            t3.count as plan_count,                                                                            "
            + "            t3.weight as plan_weight,                                                                          "
            + "            t3.unit_id,                                                                                        "
            + "            t5.price,                                                                                          "
            + "            t5.price*t2.qty amount                                                                             "
            + "       FROM                                                                                                    "
            + "  	       b_schedule t                                                                                       "
            + "  LEFT JOIN b_monitor  t1 ON t1.schedule_id = t.id                                                             "
            + "  LEFT JOIN b_monitor_out  t2 ON t2.monitor_id = t1.id                                                         "
            + "  LEFT JOIN b_out_plan_detail  t3 ON t3.id = t.out_plan_detail_id                                              "
            + "  LEFT JOIN b_out_plan  t4 ON t4.id = t3.plan_id                                                               "
            + "  LEFT JOIN (                                                                                                  "
            + "		SELECT                                                                                                    "
            + "			t1.id,ifnull(t3.price, 0) price,t3.sku_id,t3.no                                                       "
            + "		FROM                                                                                                      "
            + "			b_order t1                                                                                            "
            + "			JOIN b_in_order t2 ON t1.serial_id = t2.id                                                            "
            + "			AND t1.serial_type = 'b_in_order'                                                                     "
            + "			LEFT JOIN b_in_order_goods t3 ON t2.id = t3.order_id                                                  "
            + "			union all                                                                                             "
            + "		SELECT                                                                                                    "
            + "			t1.id,ifnull(t3.price, 0) price,t3.sku_id,t3.no                                                       "
            + "		FROM                                                                                                      "
            + "			b_order t1                                                                                            "
            + "			JOIN b_out_order t2 ON t1.serial_id = t2.id                                                           "
            + "			AND t1.serial_type = 'b_out_order'                                                                    "
            + "			LEFT JOIN b_out_order_goods t3 ON t2.id = t3.order_id                                                 "
            + "     )t5 on t.order_id = t5.id   and t.sku_id = t5.sku_id and t3.order_detail_no = t5.no                       "
            + "     where true                                                                                                "
            + "     and t2.id = #{p1,jdbcType=INTEGER}                                                                        "
            + "  ")
    BOutEntity selectByMonitorOutId(@Param("p1") Integer id);

    /**
     * 页面查询列表
     *
     * @param searchCondition
     * @return
     */
    @Select(" <script>   "
            + common_select
            + "   where true                                                                                   "
            + "   <if test='p1 != null and p1.length!=0' >                                                     "
            + "    and t.id in                                                                                 "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item.id,jdbcType=INTEGER}  "
            + "        </foreach>    "
            + "   </if>                                                                                        "
            + "    </script>    ")
    List<BOutExportVo> selectExportList(@Param("p1") BOutVo[] searchCondition);

    @Select(
            "<script>"
                    + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
                    + "		SELECT                                                                                        "
                    + "			t1.warehouse_type,                                                                        "
                    + "			t3.`name` goods_name,                                                                     "
                    + "			t1.`name` warehouse_name,                                                                 "
                    + "			SUM(ifnull(actual_weight,0)) - SUM(ifnull(t.return_qty,0))   qty,                         "
                    + "         t5.label as warehouse_type_name  ,                                                        "
                    + "         t1.id warehouse_id ,                                                                      "
                    + "         t3.code goods_code,                                                                       "
                    + "         concat(t1.id, '_', t3.id) id                                                              "
                    + "		FROM                                                                                          "
                    + "			b_out t                                                                                   "
                    + "		LEFT JOIN m_warehouse t1 ON t.warehouse_id = t1.id                                            "
                    + "		LEFT JOIN m_goods_spec t2 ON t.sku_id = t2.id                                                 "
                    + "		LEFT JOIN m_goods t3 on t2.goods_id = t3.id                                                   "
                    + "       LEFT JOIN v_dict_info t5 ON t5.code = '" + DictConstant.DICT_M_WAREHOUSE_TYPE + "' and t1.warehouse_type = t5.dict_value"
                    + "	    WHERE t.`status` = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                            "
                    + "<if test='p1.warehouse_ids != null and p1.warehouse_ids.length != 0'>                              "
                    + "     and t1.id in"
                    + "     <foreach collection='p1.warehouse_ids' item='item' index='index' open='(' separator=',' close=')'>"
                    + "         #{item}                                                                                   "
                    + "     </foreach>                                                                                    "
                    + "</if>                                                                                              "
                    + " and (t1.warehouse_type = #{p1.type} or #{p1.type} is null or #{p1.type} = '' )                    "
                    + " and (t3.name like concat('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')"
                    + " and (t3.code like concat('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')"
                    + " and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    + " AND NOT (t1.warehouse_type = '"+ DictConstant.DICT_M_WAREHOUSE_TYPE_WD +"' AND t.type = '" + DictConstant.DICT_B_OUT_TYPE_LL + "')"
                    + "${p1.params.dataScopeAnnotation}                                                                   "
                    + "		GROUP BY t.warehouse_id, t3.id                                                                "
                    + "</script>                                                                                          "
    )
    IPage<BWarehouseGoodsVo> queryOutInventory(@Param("p1") BWarehouseGoodsVo searchCondition, Page<BOutEntity> pageCondition);

    @Select(
            "<script>"
                    + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
                    + "		SELECT                                                                               "
                    + "			SUM(actual_weight) - SUM(ifnull(t.return_qty,0)) qty                             "
                    + "		FROM                                                                                 "
                    + "			b_out t                                                                          "
                    + "		LEFT JOIN m_warehouse t1 ON t.warehouse_id = t1.id                                   "
                    + "		LEFT JOIN m_goods_spec t2 ON t.sku_id = t2.id                                        "
                    + "		LEFT JOIN m_goods t3 on t2.goods_id = t3.id                                          "
                    + "       LEFT JOIN v_dict_info t5 ON t5.code = '" + DictConstant.DICT_M_WAREHOUSE_TYPE + "' and t1.warehouse_type = t5.dict_value"
                    + "	    WHERE t.`status` = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                   "
                    + "<if test='p1.warehouse_ids != null and p1.warehouse_ids.length != 0'>                     "
                    + "     and t1.id in                                                                         "
                    + "     <foreach collection='p1.warehouse_ids' item='item' index='index' open='(' separator=',' close=')'>"
                    + "         #{item}                                                                          "
                    + "     </foreach>                                                                           "
                    + "</if>                                                                                     "
                    + " and (t1.warehouse_type = #{p1.type} or #{p1.type} is null or #{p1.type} = '' )           "
                    + " and (t3.name like concat('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')"
                    + " and (t3.code like concat('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')"
                    + " and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    + " AND NOT (t1.warehouse_type = '"+ DictConstant.DICT_M_WAREHOUSE_TYPE_WD +"' AND t.type = '" + DictConstant.DICT_B_OUT_TYPE_LL + "')"
                    + "${p1.params.dataScopeAnnotation}                                                          "
                    + "</script>"
    )
    BWarehouseGoodsVo queryOutInventorySum(@Param("p1") BWarehouseGoodsVo searchCondition);

    @Select({
            "<script>"
                    + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
                    + "		SELECT                                                                                     "
                    + "			t3.`name` goods_name,                                                                  "
                    + "			t3.`code` goods_code,                                                                  "
                    + "         @row_num:= @row_num+ 1 as no,                                                          "
                    + "			t1.`name` warehouse_name,                                                              "
                    + "			SUM(actual_weight) - SUM(ifnull(t.return_qty,0)) qty,                                  "
                    + "         t5.label as warehouse_type_name                                                        "
                    + "		FROM                                                                                       "
                    + "			b_out t                                                                                "
                    + "		LEFT JOIN m_warehouse t1 ON t.warehouse_id = t1.id                                         "
                    + "		LEFT JOIN m_goods_spec t2 ON t.sku_id = t2.id                                              "
                    + "		LEFT JOIN m_goods t3 on t2.goods_id = t3.id                                                "
                    + "     LEFT JOIN v_dict_info t5 ON t5.code = '" + DictConstant.DICT_M_WAREHOUSE_TYPE + "' and t1.warehouse_type = t5.dict_value"
                    + "     ,(select @row_num:=0) t6                                                                   "
                    + "	    WHERE t.`status` = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                         "
                    + "<if test='p1.warehouse_ids != null and p1.warehouse_ids.length != 0'>                           "
                    + "     and t1.id in"
                    + "     <foreach collection='p1.warehouse_ids' item='item' index='index' open='(' separator=',' close=')'>"
                    + "         #{item}                                                                                "
                    + "     </foreach>                                                                                 "
                    + "</if>                                                                                           "
                    + " and (t1.warehouse_type = #{p1.type} or #{p1.type} is null or #{p1.type} = '' )                 "
                    + " and (t3.name like concat('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')"
                    + " and (t3.code like concat('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')"
                    + " and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    + " AND NOT (t1.warehouse_type = '"+ DictConstant.DICT_M_WAREHOUSE_TYPE_WD +"' AND t.type = '" + DictConstant.DICT_B_OUT_TYPE_LL + "')"
                    + "${p1.params.dataScopeAnnotation}                                                                "
                    + "		GROUP BY t.warehouse_id, t3.id                                                             "
                    + "</script>"
    })
    List<BWarehouseGoodsOutExportVo> queryOutInventoryExportAll(@Param("p1") BWarehouseGoodsVo searchCondition);

    @Select({
            "<script>"
                    + "		SELECT                                                                                     "
                    + "			t3.`name` goods_name,                                                                  "
                    + "			t3.`code` goods_code,                                                                  "
                    + "         @row_num:= @row_num+ 1 as no,                                                          "
                    + "			t1.`name` warehouse_name,                                                              "
                    + "			SUM(actual_weight) - SUM(ifnull(t.return_qty,0))  qty,                                                               "
                    + "         t5.label as warehouse_type_name                                                        "
                    + "		FROM                                                                                       "
                    + "			b_out t                                                                                "
                    + "		LEFT JOIN m_warehouse t1 ON t.warehouse_id = t1.id                                         "
                    + "		LEFT JOIN m_goods_spec t2 ON t.sku_id = t2.id                                              "
                    + "		LEFT JOIN m_goods t3 on t2.goods_id = t3.id                                                "
                    + "     LEFT JOIN v_dict_info t5 ON t5.code = '" + DictConstant.DICT_M_WAREHOUSE_TYPE + "' and t1.warehouse_type = t5.dict_value"
                    + "     ,(select @row_num:=0) t6                                                                   "
                    + "	    WHERE t.`status` = '" + DictConstant.DICT_B_IN_STATUS_TWO + "'                         "
                    + "<if test='p1 != null and p1.size != 0'>                                                         "
                    + "    and concat(t1.id, '_', t3.id) in                                                            "
                    + "    <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>        "
                    + "       #{item.id}                                                                               "
                    + "    </foreach>"
                    + "</if>                                                                                           "
                    + " AND NOT (t1.warehouse_type = '"+ DictConstant.DICT_M_WAREHOUSE_TYPE_WD +"' AND t.type = '" + DictConstant.DICT_B_OUT_TYPE_LL + "')"
                    + " GROUP BY t.warehouse_id, t3.id                                                                 "
                    + "</script>                                                                                       "
    })
    List<BWarehouseGoodsOutExportVo> queryOutInventoryExport(@Param("p1") List<BWarehouseGoodsVo> searchCondition);

    /**
     * 当日累计出库数量
     *
     * @param param
     * @return
     */
    @Select({
//            "		SELECT                                                                                     "
//                    + "			SUM(actual_weight)  out_qty,                                                           "
//                    + "         CONCAT(t2.name, '，', t3.label) audit_status_name,                                      "
//                    + "         DATE_FORMAT(now(), '%Y-%m-%d') date,                                                    "
//                    + "         t2.name prop_name,                                                                     "
//                    + "         t2.code code,                                                                          "
//                    + "         t.type                                                                                 "
//                    + "		FROM                                                                                       "
//                    + "			b_out t                                                                                "
//                    + "		LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                              "
//                    + "     LEFT JOIN m_goods_spec_prop t2 ON t2.id = t1.prop_id                                       "
//                    + "     LEFT JOIN v_dict_info t3 ON t3.code = '" + DictConstant.DICT_B_OUT_TYPE + "' and t.type = t3.dict_value"
//                    + "	    WHERE t.`status` =  '" + DictConstant.DICT_B_OUT_STATUS_PASSED + " '                     "
//                    + "    and DATE_FORMAT(t.e_dt, '%Y%m%d' ) =     DATE_FORMAT(now(), '%Y%m%d' )                      "
//                    + " ${p1.params.dataScopeAnnotation}                                                               "
//                    + "		GROUP BY t.type, t2.id                                                                     "
                    " ${p1.params.dataScopeAnnotation_with}                                                                                               "
                    + "  SELECT                                                                                           "
                    + "  	 ifnull(tab2.out_qty, 0) out_qty,                                               "
                    + "  	 tab1.audit_status_name,                                                        "
                    + "  	 DATE_FORMAT( now(), '%Y-%m-%d' ) date,                                         "
                    + "  	 tab1.goods_prop prop_name,                                                     "
                    + "  	 tab1.serial_type type,                                                                      "
                    + "  	 tab2.CODE                                                                     "
                    + "  FROM                                                                             "
                    + "  	s_bill_type tab1                                                                "
                    + "  	LEFT JOIN (                                                                     "
                    + "  	SELECT                                                                          "
                    + "  		SUM( actual_weight-ifnull(return_qty,0)) out_qty,                           "
                    + "  		CONCAT( t2.NAME, ',', t3.label ) audit_status_name,                         "
                    + "  		t2.NAME prop_name,                                                          "
                    + "  		t2.CODE CODE,                                                               "
                    + "  		t.type                                                                      "
                    + "  	FROM                                                                            "
                    + "  		b_out t                                                                     "
                    + "  		LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                               "
                    + "  		LEFT JOIN m_goods_spec_prop t2 ON t2.id = t1.prop_id                        "
                    + "  		LEFT JOIN v_dict_info t3 ON t3.CODE = '" + DictConstant.DICT_B_OUT_TYPE + "'                     "
                    + "  		AND t.type = t3.dict_value                                                  "
                    + "  	WHERE                                                                           "
                    + "  		 t.`status` =  '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                                                           "
                    + "  		AND DATE_FORMAT( t.e_dt, '%Y%m%d' ) = DATE_FORMAT( now(), '%Y%m%d' )        "
                    + " ${p1.params.dataScopeAnnotation}                                                               "
                    + "  	GROUP BY                                                                        "
                    + "  		t.type,                                                                     "
                    + "  		t2.id                                                                       "
                    + "  	) tab2 ON tab2.audit_status_name = tab1.audit_status_name                       "
                    + "  	WHERE tab1.type = '1'                                                           "
    })
    List<BQtyLossScheduleReportVo> getOutStatistics(@Param("p1") BQtyLossScheduleReportVo param);

    /**
     * 按仓库类型仓库商品 出库 导出数量查询
     *
     * @param searchCondition 入参
     * @return int
     */
    @Select({
            "<script>"
                    + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
                    + "  select count(1) from (                                                                        "
                    + "		SELECT                                                                                     "
                    + "         t.id                                                                                   "
                    + "		FROM                                                                                       "
                    + "			b_out t                                                                                "
                    + "		LEFT JOIN m_warehouse t1 ON t.warehouse_id = t1.id                                         "
                    + "		LEFT JOIN m_goods_spec t2 ON t.sku_id = t2.id                                              "
                    + "		LEFT JOIN m_goods t3 on t2.goods_id = t3.id                                                "
                    + "	    WHERE t.`status` = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                         "
                    + "<if test='p1.warehouse_ids != null and p1.warehouse_ids.length != 0'>                           "
                    + "     and t1.id in"
                    + "     <foreach collection='p1.warehouse_ids' item='item' index='index' open='(' separator=',' close=')'>"
                    + "         #{item}                                                                                "
                    + "     </foreach>                                                                                 "
                    + "</if>                                                                                           "
                    + " and (t1.warehouse_type = #{p1.type} or #{p1.type} is null or #{p1.type} = '' )                 "
                    + " and (t3.name like concat('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')"
                    + " and (t3.code like concat('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')"
                    + " and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    + " ${p1.params.dataScopeAnnotation}                                                               "
                    + "		GROUP BY t.warehouse_id, t3.id) tt1                                                        "
                    + "</script>"
    })
    int selectExportNum(@Param("p1") BWarehouseGoodsVo searchCondition);

    /**
     * 根据 计划 id 查询 主键id
     *
     * @param plan_id 计划id集合
     * @return
     */
    @Select("<script>"
            + "SELECT id, status from b_out where plan_id in                                                            "
            + "     <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                "
            + "         #{item}                                                                                         "
            + "     </foreach>                                                                                          "
            + "     and status not in ('"+ DictConstant.DICT_B_OUT_STATUS_CANCEL +"', '"+ DictConstant.DICT_B_OUT_STATUS_CANCEL_BEING_AUDITED +"')"
            + "</script>"
    )
    List<BOutVo> selectIdsByOutPlanIds(@Param("p1") List<Integer> plan_id);

    /**
     * 查询 出库单 商品 code 和 审核时间
     *
     * @param id 出库单 id
     * @return
     */
    @Select(""
            + " SELECT                                                                                                  "
            + "   t.e_dt,                                                                                               "
            + "   t.id,                                                                                                 "
            + "   t1.goods_code                                                                                         "
            + " FROM b_out t                                                                                            "
            + " LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                                           "
            + " WHERE t.id = #{p1}                                                                                      "
    )
    BOutVo selectEdtAndGoodsCode(@Param("p1") Integer id);

    /**
     * 当日累计物流统计区域，增加原粮出库数量，取值采购合同关联的，且审批通过时间是当天的，且仓库类型是直属库的出库单.出库数量(换算前)
     *
     * @return
     */

    @Select(
            " ${p1.params.dataScopeAnnotation_with}                                                                    "
            +  "  SELECT                                                                                               "
            +  "  	SUM( tab2.actual_count )                                                                           "
            +  "  FROM                                                                                                 "
            +  "  	(                                                                                                  "
            +  "  	SELECT                                                                                             "
            +  "  		t.id,                                                                                          "
            +  "  		t1.order_id                                                                                    "
            +  "  	FROM                                                                                               "
            +  "  		b_out t                                                                                        "
            +  "  		LEFT JOIN b_out_plan_detail t1 ON t.plan_detail_id = t1.id                                     "
            +  "  	WHERE                                                                                              "
            +  "  		t1.order_type = '" + DictConstant.DICT_SYS_CODE_TYPE_B_IN_ORDER + "'                          "
            +  "  		AND t.`status` = '"+ DictConstant.DICT_B_OUT_STATUS_PASSED +"'                                 "
            +  "  		AND DATE_FORMAT( t.e_dt, '%Y%m%d' ) = DATE_FORMAT( NOW(), '%Y%m%d' ) UNION                     "
            +  "  	SELECT                                                                                             "
            +  "  		t3.id,                                                                                         "
            +  "  		t4.order_id                                                                                    "
            +  "  	FROM                                                                                               "
            +  "  		b_out t3                                                                                       "
            +  "  		LEFT JOIN b_out_extra t4 ON t3.id = t4.out_id                                                  "
            +  "  	WHERE                                                                                              "
            +  "  		t4.order_type = '" + DictConstant.DICT_SYS_CODE_TYPE_B_IN_ORDER + "'                          "
            +  "  		AND t3.`status` = '"+ DictConstant.DICT_B_OUT_STATUS_PASSED +"'                                "
            +  "  		AND DATE_FORMAT( t3.e_dt, '%Y%m%d' ) = DATE_FORMAT( NOW(), '%Y%m%d' )                          "
            +  "  	) tab1                                                                                             "
            +  "  	LEFT JOIN b_out tab2 ON tab1.id = tab2.id                                                          "
            +  "  	LEFT JOIN m_warehouse tab3 ON tab2.warehouse_id = tab3.id                                          "
            +  "  	LEFT JOIN b_in_order tab4 ON tab4.id = tab1.order_id                                               "
            +  "  WHERE                                                                                                "
            +  "  	tab3.warehouse_type = '"+ DictConstant.DICT_M_WAREHOUSE_TYPE_ZX +"'                                "
            +  "  	AND tab4.`status` != '"+ DictConstant.DICT_B_IN_ORDER_STATUS_ONE +"'                              "
            +  " ${p1.params.dataScopeAnnotation}                                                                       "
    )
    BigDecimal selectOutRawGrainCount(@Param("p1") BQtyLossScheduleReportVo param);

    @Select(" <script>   "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                                "
            + "      SELECT                                                                                                                          "
            + "             t.*,                                                                                                                     "
            + "             t3.release_order_code,                                                                                                   "
            + "             t13.no,                                                                                                                  "
            + "             t13.type_gauge,                                                                                                          "
            + "             t13.alias,                                                                                                               "
            + "             ifnull(t19.contract_dt, t10.contract_dt) contract_dt,                                                                    "
            + "             ifnull(t19.contract_num, t10.contract_num) contract_num,                                                                 "
            + "             ifnull(t19.contract_no, t10.contract_no) contract_no,                                                                    "
            + "             t19.order_no,                                                                                                            "
            + "             t19.bill_type,                                                                                                           "
            + "             t10.pound_file,                                                                                                          "
            + "             t10.out_photo_file,                                                                                                      "
            + "             t13.code as detail_code,                                                                                                 "
            + "             t3.code as plan_code,                                                                                                    "
            + "             t3.extra_code,                                                                                                           "
            + "             t15.label as status_name,                                                                                                "
            + "             ifnull(t16.label, t10.label) as bill_type_name,                                                                          "
            + "             ifnull(t17.label, t22.label) as type_name,                                                                               "
            + "             ifnull(t4.short_name,t4.name) as consignor_name,                                                                         "
            + "             ifnull(t5.short_name,t5.name) as owner_name,                                                                             "
            + "             t9.short_name as warehouse_name,                                                                                         "
            + "             t9.name as warehouse_full_name,                                                                                          "
            + "             t7.name as bin_name,                                                                                                     "
            + "             t8.name as location_name,                                                                                                "
            + "             t11.spec,                                                                                                                "
            + "             t11.pm,                                                                                                                  "
            + "             t11.code as sku_code,                                                                                                    "
            + "             t12.name as goods_name,                                                                                                  "
            + "             t20.name as unit_name,                                                                                                   "
            + "             t1.name as c_name,                                                                                                       "
            + "             t14.name as e_name,                                                                                                      "
            + "             ifnull(tt2.name, t10.name) as client_name,                                                                               "
            + "             t2.name as u_name,                                                                                                       "
            + "             t26.id as monitor_out_id,                                                                                                "
            + "             t21.remark as cancel_remark,                                                                                             "
            + "             t27.name as cancel_audit_name,                                                                                           "
            + "             t.cancel_audit_dt,                                                                                                       "
            + "             (t.actual_count - ifnull(t.return_qty,0)) as actual_count_return,                                                        "
            + "             t28.calc as calc,                                                                                              "
            + "             t28.src_unit as src_unit,                                                                                              "
            + "             t28.tgt_unit as tgt_unit,                                                                                              "
            + "             (t.actual_weight -  (ifnull(t.return_qty,0)  * t28.calc) ) as actual_weight_return                                                             "
            + "        FROM                                                                                                                          "
            + "       <choose>                                                                                                                                                                                                      "
            + "         <when test='p1.todo_status == 0'>                                                                                                                                                                           "
            + "	            b_todo subt1                                                                                                                                                                                          "
            + "	            INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                       "
            + "	            AND subt1.position_id = subt2.position_id                                                                                                                                                             "
            + "	            AND subt2.operation_perms = subt1.perms                                                                                                                                                               "
            + "	            AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                                                                                                       "
            + "	            AND subt1.STATUS = '" + DictConstant.DICT_B_TODO_STATUS_TODO + "'                                                                                                                                         "
            + "	            INNER JOIN b_out t ON t.id = subt1.serial_id                                                                                                                                                           "
            + "         </when>                                                                                                                                                                                                     "

            + "          <when test='p1.todo_status == 1'>                                                                                                                                                                          "
            + "	            b_already_do subt1                                                                                                                                                                                    "
            + "	            INNER JOIN b_out t ON t.id = subt1.serial_id                                                                                                                                                           "
            + "	            AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                                                                                                                                       "
            + "	            AND t.id = subt1.serial_id                                                                                                                                                                            "
            + "	            AND subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                                                                  "
            + "         </when>                                                                                                                                                                                                     "

            + "        <otherwise>                                                                                                                                                                                                  "
            + "         	  b_out t                                                                                                                                                                                                  "
            + "         </otherwise>                                                                                                                                                                                                "
            + "       </choose>                                                                                                                                                                                                     "

            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                             "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                             "
            + "   LEFT JOIN m_staff t14 ON t.e_id = t14.id                                                                                           "
            + "   LEFT JOIN b_out_plan_detail t13 ON t.plan_detail_id = t13.id                                                                       "
            + "   LEFT JOIN b_out_plan t3 ON t.plan_id = t3.id                                                                                       "
            + "   LEFT JOIN b_order t19 on t13.order_id = t19.serial_id and t13.order_type = t19.serial_type                                         "
//            + "   LEFT JOIN b_out_order tt1 ON tt1.id = t13.order_id                     "
            + "   LEFT JOIN m_customer tt2 ON tt2.id = t19.customer_id                                                                               "
            + "   LEFT JOIN m_customer t4 ON t.consignor_id = t4.id                                                                                  "
            + "   LEFT JOIN m_owner t5 ON t.owner_id = t5.id                                                                                         "
            + "   LEFT JOIN m_bin t7 ON t.bin_id = t7.id                                                                                             "
            + "   LEFT JOIN m_location t8 ON t.location_id = t8.id                                                                                   "
            + "   LEFT JOIN m_warehouse t9 ON t.warehouse_id = t9.id                                                                                 "
            + "   LEFT JOIN m_goods_spec t11 ON t11.id = t.sku_id                                                                                    "
            + "   LEFT JOIN m_goods_spec_prop t24 ON t11.prop_id = t24.id      "
            + "   LEFT JOIN m_goods t12 ON t12.id = t11.goods_id                                                                                     "
            + "   LEFT JOIN (select tab1.out_photo_file, tab1.pound_file, tab1.contract_no, tab1.contract_num, tab1.contract_dt, tab1.out_id, tab1.bill_type, tab3.label, tab2.name from b_out_extra tab1 left join m_customer tab2 on tab1.client_id = tab2.id "
            + "              left join (select tab1.dict_value, tab1.label from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id              "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "') tab3 ON tab3.dict_value = tab1.bill_type ) t10 ON t10.out_id = t.id              "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_STATUS + "')t15 on t15.dict_value = t.status                             "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t16 ON t16.dict_value = t19.bill_type            "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t17 ON t17.dict_value = t3.type                           "
            + "   LEFT JOIN m_unit t20 ON t20.id = t.unit_id                                                                                         "
            + "   LEFT JOIN m_cancel t21 ON t21.serial_id = t.id and t21.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT + "'                   "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                          "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "')t22 ON t22.dict_value = t.type                            "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                            "
            // 判断是否来自监管任务
            + "   left join b_monitor_out t26 ON t26.out_id = t.id                                                                                   "
            + "   left join m_staff t27 ON t27.id = t.cancel_audit_id                                                                                "
            + "   LEFT JOIN m_goods_unit_calc t28 ON t20.id = t28.src_unit_id AND t28.sku_id = t.sku_id                                              "
//            + "   LEFT JOIN b_release_order t6 ON t13.extra_code = t6.extra_code                                                                      "
            + "   where true                                                                                                                                                                                    "
            + "      and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null or #{p1.code,jdbcType=VARCHAR} = '')                                                  "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null or #{p1.plan_code,jdbcType=VARCHAR} = '')                                  "
            + "      and (CONCAT(t5.name,t5.short_name,t5.name_pinyin,t5.short_name_pinyin) like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)               "
            + "      and (CONCAT(t4.name,t4.short_name,t4.name_pinyin,t4.short_name_pinyin) like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)       "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                                  "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                                     "
            + "      and (t13.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%') or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                        "
            + "      and (t13.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%') or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                            "
            + "      and (ifnull(t19.bill_type, t10.bill_type) = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} ='')                                                 "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                                              "
            + "      and (t12.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                      "
            + "           or t11.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                   "
            + "           or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                                 "
            + "           or t12.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null or #{p1.goods_name,jdbcType=VARCHAR} = ''  )                         "
            + "      and (ifnull(t19.contract_no, t10.contract_no) like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%')  or #{p1.contract_no,jdbcType=VARCHAR} is null)                                                               "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                                     "
            + "      and (date_format(t.outbound_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &gt;= #{p1.ed_dt_start} or #{p1.ed_dt_start} is null)                                       "
            + "      and (date_format(t.e_dt, '%Y-%m-%d') &lt;= #{p1.ed_dt_end} or #{p1.ed_dt_end} is null)                                       "
            + "      and (t24.name like concat('%', #{p1.prop}, '%') or #{p1.prop} is null or #{p1.prop} = '')                                    "
            + "      and (ifnull(tt2.name, t10.name) like concat('%', #{p1.client_name}, '%') or #{p1.client_name} is null or #{p1.client_name} = '')                  "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                    "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.c_time_start}, '%Y-%m-%d') or #{p1.c_time_start} is null)                                                                         "
            + "      and (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.c_time_end}, '%Y-%m-%d') or #{p1.c_time_end} is null)                                                                             "
            + "      and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                         "
            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "       and t.type in                                                                                                                                              "
            + "           <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "
            + "      and (t.vehicle_no like concat('%', #{p1.vehicle_no}, '%') or #{p1.vehicle_no} is null or #{p1.vehicle_no} = '')                                               "
            + "      and (t9.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "

            // 包含放货指令
            + "      <if test='p1.out_release_status == 1' >                                                                                                                                                    "
            + "      and exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               FROM b_release_order t1                                                                                                                                                           "
            + "               WHERE t3.release_order_code = t1.CODE                                                                                                                                            "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "

            // 不包含放货指令
            + "      <if test='p1.out_release_status == 2' >                                                                                                                                                    "
            + "      and not exists (                                                                                                                                                                               "
            + "             select 1                                                                                                                                                                            "
            + "               FROM b_release_order t1                                                                                                                                                           "
            + "               WHERE t3.release_order_code = t1.CODE                                                                                                                                            "
            + "       )                                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                                      "

            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                 "
            + "    ORDER BY ${p2} ${p3} ${p4}                                                                                                                                   "
            + "    limit ${(p1.pageCondition.current-1)*p1.pageCondition.size},  #{p1.pageCondition.size}                                                                       "
            + "    </script>   ")
    List<BOutVo> selectPageListNotCount(@Param("p1") BOutVo searchCondition,@Param("p2") String sort,@Param("p3") String sortType,@Param("p4") String defaultSort);
    @Select("    "
            + "			SELECT                                                                                              "
            + "				t3.id                                                                                           "
            + "			FROM                                                                                                "
            + "				b_out_plan t1                                                                                   "
            + "				LEFT JOIN b_out_plan_detail t2 ON t1.id = t2.plan_id                                            "
            + "				LEFT JOIN b_schedule t3 ON t3.out_plan_detail_id = t2.id                                        "
            + "			WHERE                                                                                               "
            + "			TRUE                                                                                                "
            + "				AND t3.STATUS != '"+ DictConstant.DICT_B_SCHEDULE_STATUS_FIVE +"'                               "
            + "				AND t3.IS_DELETE = '0'                                                                          "
            + "				AND t1.extra_code = #{p1,jdbcType=VARCHAR}                                                      "
            + "      ")
    List<Integer> selectScheduleIdByOutExtraCode(String code);


    /**
     * 监管任务出库id查询出库单数据
     */
    @Select("                                                                                            "
            + "SELECT                                                                                    "
            + "	 t2.*                                                                                    "
            + " FROM                                                                                     "
            + "	(select * from b_monitor_out tab1 union all select * from b_monitor_delivery tab2) t1    "
            + "	LEFT JOIN b_out t2 on t2.id = t1.out_id                                                  "
            + "WHERE                                                                                     "
            + "	t1.monitor_id = #{p1}                                                                    ")
    BOutEntity selectByMonitorId(@Param("p1") Integer id);

    /**
     * 查询退货总量
     */
    @Select("   SELECT ifnull(SUM(return_qty),0) FROM b_out WHERE plan_id = #{p1} AND                                  "
            +" status IN ('"+DictConstant.DICT_B_OUT_STATUS_PASSED+"','"+DictConstant.DICT_B_OUT_STATUS_SUBMITTED+"')  ")
    BigDecimal selectByPlanIdCount(@Param("p1") Integer id);


    @Select("   SELECT * FROM b_out WHERE plan_id = #{p1}                                                               ")
    List<BOutVo> selectListByPlanId(@Param("p1") Integer planId);

    /**
     * 查询货主公司和委托公司 的出库单
     */
    @Select("SELECT * FROM b_out where owner_code = #{p1} or consignor_code = #{p1}")
    List<BOutEntity> selectByCustomerCode(@Param("p1")String code);
}
