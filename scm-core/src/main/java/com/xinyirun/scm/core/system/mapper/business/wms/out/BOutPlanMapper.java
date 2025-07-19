package com.xinyirun.scm.core.system.mapper.business.wms.out;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckVo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.out.*;
import com.xinyirun.scm.bean.system.vo.business.wms.out.receive.BReceiveVo;
import com.xinyirun.scm.bean.system.vo.excel.out.BOutPlanExportVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 出库计划 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface BOutPlanMapper extends BaseMapper<BOutPlanEntity> {
    String common_select = "                                                                                                                             "
            + "     SELECT                                                                                                                               "
            + "            @row_num:= @row_num+ 1 as excel_no,                                                                                           "
            + "            t3.extra_code ,                                                                                                               "
//            + "            t24.price ,                                                                                                                   "
            + "            t.id,                                                                                                                         "
            + "            t.no,                                                                                                                         "
            + "            t.code,                                                                                                                       "
            + "            t.plan_id,                                                                                                                    "
            + "            t.status,                                                                                                                     "
            + "            t.sku_id,                                                                                                                     "
            + "            t.sku_code,                                                                                                                   "
            + "            t.over_release,                                                                                                               "
            + "            t.lock_inventory,                                                                                                             "
            + "            t.price,                                                                                                                      "
            + "            t.unit_id,                                                                                                                    "
            + "            t.count,                                                                                                                      "
            + "            t.weight,                                                                                                                     "
            + "            t.volume,                                                                                                                     "
            + "            t.warehouse_id,                                                                                                               "
            + "            t.location_id,                                                                                                                "
            + "            t.bin_id,                                                                                                                     "
            + "            t.order_id,                                                                                                                   "
            + "            t.order_type,                                                                                                                 "
            + "            t.order_detail_no,                                                                                                            "
            + "            t.pending_count,                                                                                                              "
            + "            t.pending_weight,                                                                                                             "
            + "            t.pending_volume,                                                                                                             "
            + "            t.has_handle_count,                                                                                                           "
            + "            t.has_handle_weight,                                                                                                          "
            + "            t.has_handle_volume,                                                                                                          "
            + "            t.auditor_id,                                                                                                                 "
            + "            t.audit_dt,                                                                                                                   "
            + "            t.e_opinion,                                                                                                                  "
            + "            t.sync_id,                                                                                                                    "
            + "            t.audit_info,                                                                                                                 "
            + "            t.type_gauge,                                                                                                                 "
            + "            t.alias,                                                                                                                      "
            + "            t.order_goods_code,                                                                                                           "
            + "            t.over_inventory_policy,                                                                                                      "
            + "            t.over_inventory_lower,                                                                                                       "
            + "            ifnull(t.over_inventory_upper, t27.over_inventory_upper) over_inventory_upper,                                                "
            + "            t.c_time,                                                                                                                     "
            + "            t.u_time,                                                                                                                     "
            + "            t.c_id,                                                                                                                       "
            + "            t.u_id,                                                                                                                       "
            + "            t.cancel_audit_dt,                                                                                                            "
            + "            t3.remark,                                                                                                                    "
            + "            t.remark detail_remark,                                                                                                       "
            + "            t.count*(1+ifnull(t.over_inventory_upper, 0)) max_count,                                                                      "
            + "			   t3.code as plan_code,                                                                         		                         "
            + "            t3.plan_time ,                                                                                                                "
            + "            t3.type ,                                                                                                                     "
            + "            t3.owner_id ,                                                                                                                 "
            + "            t3.owner_code ,                                                                                                               "
            + "            t3.consignor_id ,                                                                                                             "
            + "            t3.consignor_code ,                                                                                                           "
            + "            t17.order_no ,                                                                                                                "
            + "            t17.bill_type ,                                                                                                               "
            + "            t17.contract_no ,                                                                                                             "
            + "            t17.contract_dt ,                                                                                                             "
            + "            t17.contract_num ,                                                                                                            "
            + "            ifnull(t4.short_name,t4.name) as warehouse_name,                                                                              "
            + "            ifnull(t5.short_name,t5.name) as location_name,                                                                               "
            + "            ifnull(t7.short_name,t7.name) as owner_name,                                                                                  "
            + "            ifnull(t8.short_name,t8.name) as consignor_name,                                                                              "
            + "            ifnull(t9.short_name,t9.name) as customer_name,                                                                               "
            + "            t10.spec,                                                                                                                     "
            + "            t10.pm,                                                                                                                       "
            + "            t11.name as goods_name,                                                                                                       "
            + "            t11.code as goods_code,                                                                                                       "
            + "            t13.label as status_name,                                                                                                     "
            + "            t14.label as bill_type_name,                                                                                                  "
            + "            t15.label as type_name,                                                                                                       "
            + "            t16.qty_avaible,                                                                                                              "
            + "            t6.name as bin_name,                                                                                                          "
            + "            t12.name as e_name,                                                                                                           "
            + "            t1.name as c_name,                                                                                                            "
            + "            t19.name as unit_name,                                                                                                        "
            + "            t18.jl_unit as jl_unit_name,                                                                                                  "
            + "            t18.hs_unit as hs_unit_name,                                                                                                  "
            + "            t18.hs_gx as hs_gx,                                                                                                           "
            + "            t18.id as unit_convert_id,                                                                                                    "
            + "            t2.name as u_name,                                                                                                            "
            + "            t20.remark as cancel_remark,                                                                                                  "
            + "             IFNULL( t21.counts, 0 ) + IFNULL( t29.counts, 0 ) as out_counts ,                                                                                                    "
            + "            t21.counts out_counts,                                                                                                        "
            + "            t26.label warehouse_type_name,                                                                                                "
            + "            t3.release_order_code,                                                                                                        "
            + "            t4.address,                                                                                                                   "
            + "            t22.id allocate_detail_id,                                                                                                    "
            + "            t28.name as cancel_audit_name,                                                                                                 "
            + "            t.return_qty as return_qty                                                                                                 "
            + "       FROM                                                                                                                               "
            + "  	       b_out_plan_detail t                                                                                                           "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                  "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                  "
            + "  LEFT JOIN m_staff t12 ON t.auditor_id = t12.id                                                                                          "
            + "  LEFT JOIN b_out_plan t3 ON t3.id = t.plan_id                                                                                            "
            + "  LEFT JOIN b_order t17 on t.order_id = t17.serial_id and t.order_type = t17.serial_type                                                  "
            + "  LEFT JOIN m_warehouse t4 ON t.warehouse_id = t4.id                                                                                      "
            + "  LEFT JOIN m_location t5 ON t5.id = t.location_id                                                                                        "
            + "  LEFT JOIN m_bin t6 ON t6.id = t.bin_id                                                                                                  "
            + "  LEFT JOIN m_owner t7 ON t3.owner_id = t7.id                                                                                             "
            + "  LEFT JOIN m_customer t8 ON t3.consignor_id = t8.id                                                                                      "
            + "  LEFT JOIN m_customer t9 ON t17.customer_id = t9.id                                                                                      "
            + "  LEFT JOIN m_goods_spec t10 ON t10.id = t.sku_id                                                                                         "
            + "  LEFT JOIN m_goods t11 ON t11.id = t10.goods_id                                                                                          "
            + "  LEFT JOIN v_dict_info t13 ON t13.code = '" + DictConstant.DICT_B_OUT_PLAN_STATUS + "' and t13.dict_value = t.status                     "
            + "  LEFT JOIN v_dict_info t14 ON t14.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "' and t14.dict_value = t17.bill_type         "
            + "  LEFT JOIN v_dict_info t15 ON t15.code = '" + DictConstant.DICT_B_OUT_PLAN_TYPE + "' and t15.dict_value = t3.type                        "
            + "  LEFT JOIN (select warehouse_id,sku_id,owner_id,SUM(qty_avaible) qty_avaible from m_inventory GROUP BY bin_id,sku_id,                    "
            + "owner_id) t16 ON t16.warehouse_id = t.warehouse_id and t16.sku_id=t.sku_id and t16.owner_id=t3.owner_id                                   "
            + "  LEFT JOIN m_unit t19 ON t19.id = t.unit_id                                                                                              "
            + "  LEFT JOIN m_goods_unit_convert t18 ON t18.jl_unit_id = t.unit_id  and t18.sku_id = t.sku_id                                             "
            + "  LEFT JOIN m_cancel t20 ON t20.serial_id = t.id and t20.serial_type = '"+SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL+"'                "
            + "  LEFT JOIN (select plan_detail_id,count(1) counts from b_out                                                                             "
            + "     group by plan_detail_id) t21 ON t.id = t21.plan_detail_id                                                                            "
            + "  LEFT JOIN (select plan_detail_id,count(1) counts from b_receive                                                                             "
            + "     group by plan_detail_id) t29 ON t.id = t29.plan_detail_id                                                                            "
            + "  LEFT JOIN (select t1.id,t1.out_plan_id from b_allocate_detail t1                                                                        "
            + "       inner join b_allocate t2 on t1.allocate_id = t2.id where t2.auto = '1') t22 ON t3.id = t22.out_plan_id                             "
            + "  LEFT JOIN v_dict_info t26 ON t26.code = '" + DictConstant.DICT_M_WAREHOUSE_TYPE + "' and t26.dict_value = t4.warehouse_type             "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                                "
            + "  LEFT JOIN (                                                                                                                             "
            + "		SELECT                                                                                                                               "
            + "			t1.*,'b_in_order' order_type,t1.supplier_id customer_id                                                                          "
            + "		FROM                                                                                                                                 "
            + "			 b_in_order t1                                                                                                                    "
            + "			union all                                                                                                                        "
            + "		SELECT                                                                                                                               "
            + "			t2.*,'b_out_order' order_type,t2.client_id customer_id                                                                           "
            + "		FROM                                                                                                                                 "
            + "			 b_out_order t2                                                                                                                  "
            + "     )t27 on t.order_id = t27.id and  t.order_type = t27.order_type                                                                       "
            + "   LEFT JOIN m_staff t28 ON t.cancel_audit_id = t28.id                                                                                    "
//            + "   LEFT JOIN b_release_order_detail t24 ON t24.release_order_code = t3.release_order_code and t10.code = t24.commodity_spec_code          "
            + " ,(select @row_num:=0) t23                                                                                                                "
            ;

    String common_select_count = "                                                                                                                             "
            + "     SELECT                                                                                                                               "
            + "            count(t.id) c                                                                                                                 "
            + "       FROM                                                                                                                               "
            + "  	       b_out_plan_detail t                                                                                                           "
            + "  LEFT JOIN b_out_plan t3 ON t3.id = t.plan_id                                                                                            "
            + "  LEFT JOIN b_order t17 on t.order_id = t17.serial_id and t.order_type = t17.serial_type                                                  "
            + "  LEFT JOIN m_warehouse t4 ON t.warehouse_id = t4.id                                                                                      "
            + "  LEFT JOIN m_owner t7 ON t3.owner_id = t7.id                                                                                             "
            + "  LEFT JOIN m_customer t8 ON t3.consignor_id = t8.id                                                                                      "
            + "  LEFT JOIN m_customer t9 ON t17.customer_id = t9.id                                                                                      "
            + "  LEFT JOIN m_goods_spec t10 ON t10.id = t.sku_id                                                                                         "
            + "  LEFT JOIN m_goods t11 ON t11.id = t10.goods_id                                                                                          "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                                "
//            + "   LEFT JOIN b_release_order_detail t24 ON t24.release_order_code = t3.release_order_code and t10.code = t24.commodity_spec_code          "
            ;

    String sum_select = "  "
            + "        SELECT                                                                                                                            "
            + "                        sum(t.count) count,                                                                                               "
            + "                        sum(t.has_handle_count) has_handle_count,                                                                         "
            + "                        sum(t23.sync_error_count) sync_error_count,                                                                             "
            + "                        sum(t.pending_count) pending_count,                                                                                "
            + "                        sum(t26.return_qty) count_return_qty                                                                                "
            + "       FROM                                                                                                                               "
            + "  	       b_out_plan_detail t                                                                                                           "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                  "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                  "
            + "  LEFT JOIN m_staff t12 ON t.auditor_id = t12.id                                                                                          "
            + "  LEFT JOIN b_out_plan t3 ON t3.id = t.plan_id                                                                                            "
            + "  LEFT JOIN (                                                                                                                             "
            + "		SELECT                                                                                                                               "
            + "			t1.*,'b_in_order' order_type,t1.supplier_id customer_id                                                                          "
            + "		FROM                                                                                                                                 "
            + "			 b_in_order t1                                                                                                                   "
            + "			union all                                                                                                                        "
            + "		SELECT                                                                                                                               "
            + "			t2.*,'b_out_order' order_type,t2.client_id customer_id                                                                           "
            + "		FROM                                                                                                                                 "
            + "			 b_out_order t2                                                                                                                  "
            + "     )t17 on t.order_id = t17.id and  t.order_type = t17.order_type                                                                       "
            + "  LEFT JOIN m_warehouse t4 ON t.warehouse_id = t4.id                                                                                      "
            + "  LEFT JOIN m_location t5 ON t5.id = t.location_id                                                                                        "
            + "  LEFT JOIN m_bin t6 ON t6.id = t.bin_id                                                                                                  "
            + "  LEFT JOIN m_owner t7 ON t3.owner_id = t7.id                                                                                             "
            + "  LEFT JOIN m_customer t8 ON t3.consignor_id = t8.id                                                                                      "
            + "  LEFT JOIN m_customer t9 ON t17.customer_id = t9.id                                                                                      "
            + "  LEFT JOIN m_goods_spec t10 ON t10.id = t.sku_id                                                                                         "
            + "  LEFT JOIN m_goods t11 ON t11.id = t10.goods_id                                                                                          "
            + "  LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                               "
            + "             where tab2.code = '"+ DictConstant.DICT_B_OUT_PLAN_STATUS +"')t13 on t13.dict_value = t.status                               "
            + "  LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                               "
            + "             where tab2.code = '"+ DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE +"')t14 on t14.dict_value = t17.bill_type                   "
            + "  LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                               "
            + "             where tab2.code = '"+ DictConstant.DICT_B_OUT_PLAN_TYPE +"')t15 on  t15.dict_value = t3.type                                 "
            + "  LEFT JOIN (select warehouse_id,sku_id,owner_id,SUM(qty_avaible) qty_avaible from m_inventory GROUP BY bin_id,sku_id,                    "
            + "owner_id) t16 ON t16.warehouse_id = t.warehouse_id and t16.sku_id=t.sku_id and t16.owner_id=t3.owner_id                                   "
            + "  LEFT JOIN m_unit t19 ON t19.id = t.unit_id                                                                                              "
            + "  LEFT JOIN m_goods_unit_convert t18 ON t18.jl_unit_id = t.unit_id  and t18.sku_id = t.sku_id                                             "
            + "  LEFT JOIN m_cancel t20 ON t20.serial_id = t.id and t20.serial_type = '"+SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL+"'                "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                                     "
            + "   left join (select count(tt1.id) sync_error_count, tt1.id, tt2.status  from b_out_plan_detail tt1 inner join b_sync_status_error tt2 on tt1.sync_id = tt2.id where tt2.status = '0' group by tt1.id) t23 "
            + "             on t23.id = t.id                                                                                                            "
            + "  LEFT JOIN (select plan_detail_id,count(1) counts from b_out                                                                             "
            + "     group by plan_detail_id) t21 ON t.id = t21.plan_detail_id                                                                            "
            + "  LEFT JOIN (select t.return_qty,t.id from b_out_plan_detail t where t.status in('"+DictConstant.DICT_B_OUT_STATUS_SUBMITTED+"','"+DictConstant.DICT_B_OUT_STATUS_PASSED+"')) t26 ON t26.id = t.id                                                                        "
            ;


    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("   <script> "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + common_select
            + "   where true                                                                                                                                                                    "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null)                                                           "
            + "      and (CONCAT(ifnull(t7.name,''),ifnull(t7.short_name,''),ifnull(t7.name_pinyin,''),ifnull(t7.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)                                                               "
            + "      and (CONCAT(ifnull(t8.name,''),ifnull(t8.short_name,''),ifnull(t8.name_pinyin,''),ifnull(t8.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)                                                       "
            + "      and (CONCAT(ifnull(t9.name,''),ifnull(t9.short_name,''),ifnull(t9.name_pinyin,''),ifnull(t9.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.customer_name,jdbcType=VARCHAR},'%') or #{p1.customer_name,jdbcType=VARCHAR} is null)                                                         "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                  "
            + "      and (t.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%')                                                                                                  "
            + "             or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                                                                             "
            + "      and (t.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%')                                                                                                            "
            + "             or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                                                                       "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                     "
            + "      and (t17.bill_type = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} = '')                                "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                              "
            + "      and (t11.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                      "
            + "          or t10.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                    "
            + "          or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                  "
            + "          or t11.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null  or #{p1.goods_name,jdbcType=VARCHAR} = '' )          "
            + "      and (t17.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)                                               "
//            + "      and (t17.client_id = #{p1.client_id,jdbcType=INTEGER} or #{p1.client_id,jdbcType=INTEGER} is null)                                                                       "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                           "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                             "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                                                "
            + "      and (t4.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "

            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "

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
            + "				AND subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL+"'                                                                                        "
            + "				AND subt1.STATUS = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                                                                                       "
            + "				GROUP BY subt1.serial_id, subt1.serial_type                                                                                                                         "
            + "  )                                                                                                                                                                              "
            + "      </if>                                                                                                                                                                      "

            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                                                                                           "
            + "      and exists (                                                                                                                                                               "
            + "             select 1                                                                                                                                                            "
            + "               from b_already_do subt1                                                                                                                                           "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL + "'                                                                                  "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                             "
            + "                and serial_id = t.id                                                                                                                                             "
            + "				GROUP BY subt1.serial_id, subt1.serial_type                                                                                                                         "
            + "       )                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                      "

            + "      <if test='p1.filter_type != null and p1.filter_type.length!=0' >                                                                                              "
            + "       and t3.type not in                                                                                                                                              "
            + "           <foreach collection='p1.filter_type' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "

            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                                                            "
            + "      </script> ")
    IPage<BOutPlanListVo> selectPage(Page page, @Param("p1") BOutPlanListVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("   <script> "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + common_select_count
            + "   where true                                                                                                                                                                    "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null)                                                           "
            + "      and (CONCAT(ifnull(t7.name,''),ifnull(t7.short_name,''),ifnull(t7.name_pinyin,''),ifnull(t7.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)                                                               "
            + "      and (CONCAT(ifnull(t8.name,''),ifnull(t8.short_name,''),ifnull(t8.name_pinyin,''),ifnull(t8.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)                                                       "
            + "      and (CONCAT(ifnull(t9.name,''),ifnull(t9.short_name,''),ifnull(t9.name_pinyin,''),ifnull(t9.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.customer_name,jdbcType=VARCHAR},'%') or #{p1.customer_name,jdbcType=VARCHAR} is null)                                                         "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                  "
            + "      and (t.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%')                                                                                                  "
            + "             or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                                                                             "
            + "      and (t.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%')                                                                                                            "
            + "             or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                                                                       "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                     "
            + "      and (t17.bill_type = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} = '')                                "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                              "
            + "      and (t11.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                      "
            + "          or t10.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                    "
            + "          or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                  "
            + "          or t11.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null  or #{p1.goods_name,jdbcType=VARCHAR} = '' )          "
            + "      and (t17.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)                                               "
//            + "      and (t17.client_id = #{p1.client_id,jdbcType=INTEGER} or #{p1.client_id,jdbcType=INTEGER} is null)                                                                       "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                           "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                             "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                                                "
            + "      and (t4.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "
            + " and exists (                                                                                                                                                                    "
            +"		SELECT                                                                                                                                                                      "
            +"				1                                                                                                                                                                   "
            +"			FROM                                                                                                                                                                    "
            +"				b_todo subt1                                                                                                                                                        "
            +"				INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                     "
            +"				AND subt1.position_id = subt2.position_id                                                                                                                           "
            +"				AND subt2.operation_perms = subt1.perms                                                                                                                             "
            +"			WHERE                                                                                                                                                                   "
            +"				t.id = subt1.serial_id                                                                                                                                              "
            +"				AND subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL+"'                                                                                        "
            +"				AND subt1.STATUS = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                                                                                       "
            + "  )                                                                                                                                                                              "


            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                                                            "
            + "      </script> ")
    Integer selectTodoCount(@Param("p1") BOutPlanListVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("   <script> "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + sum_select
            + "   where true                                                                                                                       "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null)              "
            + "      and (CONCAT(ifnull(t7.name,''),ifnull(t7.short_name,''),ifnull(t7.name_pinyin,''),ifnull(t7.short_name_pinyin,'')) like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)        "
            + "      and (CONCAT(ifnull(t8.name,''),ifnull(t8.short_name,''),ifnull(t8.name_pinyin,''),ifnull(t8.short_name_pinyin,'')) like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)        "
            + "      and (CONCAT(ifnull(t9.name,''),ifnull(t9.short_name,''),ifnull(t9.name_pinyin,''),ifnull(t9.short_name_pinyin,'')) like CONCAT ('%',#{p1.customer_name,jdbcType=VARCHAR},'%') or #{p1.customer_name,jdbcType=VARCHAR} is null)        "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                     "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                            "
            + "      and (t17.bill_type = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} = '')                            "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                       "
            + "      and (t.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%') or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                                       "
            + "      and (t.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%') or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                       "
            + "      and (t11.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                      "
            + "          or t10.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                    "
            + "          or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                  "
            + "          or t11.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null  or #{p1.goods_name,jdbcType=VARCHAR} = '')            "
            + "      and (t17.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)  "
//            + "      and (t17.client_id = #{p1.client_id,jdbcType=INTEGER} or #{p1.client_id,jdbcType=INTEGER} is null)                            "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                                   "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                                     "
            + "      and t.status != '" + DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL + "'                                   "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                                                "
            + "      and (t4.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                                "
            + "      <if test='p1.todo_status == 0' >                                                                                                                                           "
            + " and exists (                                                                                                                                                                    "
            +"		SELECT                                                                                                                                                                      "
            +"				1                                                                                                                                                                   "
            +"			FROM                                                                                                                                                                    "
            +"				b_todo subt1                                                                                                                                                        "
            +"				INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                     "
            +"				AND subt1.position_id = subt2.position_id                                                                                                                           "
            +"				AND subt2.operation_perms = subt1.perms                                                                                                                             "
            +"			WHERE                                                                                                                                                                   "
            +"				t.id = subt1.serial_id                                                                                                                                              "
            +"				AND subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL+"'                                                                                        "
            +"				AND subt1.STATUS = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                                                                                       "
            + "  )                                                                                                                                                                              "
            + "      </if>                                                                                                                                                                      "

            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                                                 "
            + "      and exists (                                                                                                                     "
            + "             select 1                                                                                                                  "
            + "               from b_already_do subt1                                                                                                 "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL + "'                                                     "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}"
            + "                and serial_id = t.id                                                                                                   "
            + "       )                                                                                                                               "
            + "      </if>                                                                                                                            "

            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                "
            + "      </script> ")
    BOutPlanSumVo selectSumData(@Param("p1") BOutPlanListVo searchCondition);

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "   where true                                                                                            "
            + "     and (t.id = #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                         "
            + "       ")
    List<BOutPlanListVo> selectList(@Param("p1") BOutPlanListVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("   <script>                                                                                                                                                 "
            + common_select
            + "  where true                                                                                                                                              "
            + "   <if test='p1 != null and p1.size!=0' >                                                                                                                 "
            + "     and t.id in                                                                                                                                          "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                                                              "
            + "         #{item.id,jdbcType=INTEGER}                                                                                                                      "
            + "        </foreach>                                                                                                                                        "
            + "   </if>                                                                                                                                                  "
            + "  </script>    ")
    List<BOutPlanExportVo> selectExportList(@Param("p1") List<BOutPlanListVo> searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("   <script> "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + common_select
            + "   where true                                                                                                                                                                    "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null)                                                           "
            + "      and (CONCAT(ifnull(t7.name,''),ifnull(t7.short_name,''),ifnull(t7.name_pinyin,''),ifnull(t7.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)                                                               "
            + "      and (CONCAT(ifnull(t8.name,''),ifnull(t8.short_name,''),ifnull(t8.name_pinyin,''),ifnull(t8.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)                                                       "
            + "      and (CONCAT(ifnull(t9.name,''),ifnull(t9.short_name,''),ifnull(t9.name_pinyin,''),ifnull(t9.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.customer_name,jdbcType=VARCHAR},'%') or #{p1.customer_name,jdbcType=VARCHAR} is null)                                                         "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                  "
            + "      and (t.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%')                                                                                                  "
            + "             or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                                                                             "
            + "      and (t.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%')                                                                                                            "
            + "             or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                                                                       "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                     "
            + "      and (t17.bill_type = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} = '')                                "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                              "
            + "      and (t11.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                      "
            + "          or t10.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                    "
            + "          or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                  "
            + "          or t11.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null  or #{p1.goods_name,jdbcType=VARCHAR} = '' )          "
            + "      and (t17.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)                                               "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                           "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                             "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                                                "
            + "      and (t4.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "

            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "

            + "      <if test='p1.todo_status == 0' >                                                                                                                                           "
            + " and exists (                                                                                                                                                                    "
            +"		SELECT                                                                                                                                                                      "
            +"				1                                                                                                                                                                   "
            +"			FROM                                                                                                                                                                    "
            +"				b_todo subt1                                                                                                                                                        "
            +"				INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                     "
            +"				AND subt1.position_id = subt2.position_id                                                                                                                           "
            +"				AND subt2.operation_perms = subt1.perms                                                                                                                             "
            +"			WHERE                                                                                                                                                                   "
            +"				t.id = subt1.serial_id                                                                                                                                              "
            +"				AND subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL+"'                                                                                        "
            +"				AND subt1.STATUS = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                                                                                       "
            + "  )                                                                                                                                                                              "
            + "      </if>                                                                                                                                                                      "
            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                                                                                           "
            + "      and exists (                                                                                                                                                               "
            + "             select 1                                                                                                                                                            "
            + "               from b_already_do subt1                                                                                                                                           "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL + "'                                                                                  "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                             "
            + "                and serial_id = t.id                                                                                                                                             "
            + "       )                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                      "
            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                                                            "
            + "      </script> ")
    List<BOutPlanExportVo> selectAllList(@Param("p1") BOutPlanListVo searchCondition);

    /**
     * 按出库明细id查询
     * @param id
     * @return
     */
    @Select("                                                                                                   "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}                                                           ")
    BOutPlanSaveVo get(@Param("p1") int id);

    /**
     * 出库操作页面查询
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "   where true                                                "
            + "   and t.id =  #{p1.id,jdbcType=INTEGER}                     ")
    BOutPlanDetailVo getPlanDetail(@Param("p1") BOutPlanDetailVo searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
            + "  SELECT * from b_out_plan_detail t                                                              "
            + "  where t.id in                                                                                  "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>     "
            + "         #{item.id,jdbcType=INTEGER}                                                             "
            + "        </foreach>                                                                               "
            + "  </script>    ")
    List<BOutPlanDetailEntity> selectIdsIn(@Param("p1") List<BOutPlanListVo> searchCondition);


    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}")
    BOutPlanVo selectId(@Param("p1") Integer id);


    /**
     * 按条件获取数据，返回更新对象
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}")
    BOutPlanOperateVo selectByOperateId(@Param("p1") int id);


    /**
     * 按条件获取数据，返回更新对象
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t3.id =  #{p1,jdbcType=INTEGER}")
    List<BOutPlanListVo> selectBySaveId(@Param("p1") int id);

    /**
     * 页面查询
     */
    @Select("    "
            + "  select * from b_out_plan                                                                               "
            + "   where true                                                                                            "
            + "  and code =  #{p1,jdbcType=VARCHAR}                                                                     "
            + "     ")
    BOutPlanVo getPlanByCode(@Param("p1") String code);

    /**
     * 导出条数查询
     * @param searchCondition
     * @return
     */
    @Select("   <script> "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + "     SELECT                                                                                                                               "
            + "            count(1)                                                                                                                      "
            + "       FROM                                                                                                                               "
            + "  	       b_out_plan_detail t                                                                                                           "
            + "  LEFT JOIN b_out_plan t3 ON t3.id = t.plan_id                                                                                            "
            + "  LEFT JOIN m_warehouse t4 ON t.warehouse_id = t4.id                                                                                      "
            + "  LEFT JOIN b_order t17 on t.order_id = t17.serial_id and t.order_type = t17.serial_type                                                  "
            + "  LEFT JOIN m_owner t7 ON t3.owner_id = t7.id                                                                                             "
            + "  LEFT JOIN m_customer t8 ON t3.consignor_id = t8.id                                                                                      "
            + "  LEFT JOIN m_customer t9 ON t17.customer_id = t9.id                                                                                      "
            + "  LEFT JOIN m_goods_spec t10 ON t10.id = t.sku_id                                                                                         "
            + "  LEFT JOIN m_goods t11 ON t11.id = t10.goods_id                                                                                          "
            + "   LEFT JOIN b_sync_status_error t25 ON t.sync_id = t25.id                                                                                     "
            + "   where true                                                                                                                                                                    "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null)                                                           "
            + "      and (CONCAT(ifnull(t7.name,''),ifnull(t7.short_name,''),ifnull(t7.name_pinyin,''),ifnull(t7.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)                                                               "
            + "      and (CONCAT(ifnull(t8.name,''),ifnull(t8.short_name,''),ifnull(t8.name_pinyin,''),ifnull(t8.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)                                                       "
            + "      and (CONCAT(ifnull(t9.name,''),ifnull(t9.short_name,''),ifnull(t9.name_pinyin,''),ifnull(t9.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.customer_name,jdbcType=VARCHAR},'%') or #{p1.customer_name,jdbcType=VARCHAR} is null)                                                         "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                  "
            + "      and (t.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%')                                                                                                  "
            + "             or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                                                                             "
            + "      and (t.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%')                                                                                                            "
            + "             or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                                                                       "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                     "
            + "      and (t17.bill_type = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} = '')                                "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                              "
            + "      and (t11.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                      "
            + "          or t10.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                    "
            + "          or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                  "
            + "          or t11.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null  or #{p1.goods_name,jdbcType=VARCHAR} = '' )          "
            + "      and (t17.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)                                               "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                           "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                             "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                                                "
            + "      and (t4.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                                "

            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "

            + "      <if test='p1.todo_status == 0' >                                                                                                                                           "
            + " and exists (                                                                                                                                                                    "
            +"		SELECT                                                                                                                                                                      "
            +"				1                                                                                                                                                                   "
            +"			FROM                                                                                                                                                                    "
            +"				b_todo subt1                                                                                                                                                        "
            +"				INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                     "
            +"				AND subt1.position_id = subt2.position_id                                                                                                                           "
            +"				AND subt2.operation_perms = subt1.perms                                                                                                                             "
            +"			WHERE                                                                                                                                                                   "
            +"				t.id = subt1.serial_id                                                                                                                                              "
            +"				AND subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL+"'                                                                                        "
            +"				AND subt1.STATUS = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                                                                                       "
            + "  )                                                                                                                                                                              "
            + "      </if>                                                                                                                                                                      "
            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                                                                                           "
            + "      and exists (                                                                                                                                                               "
            + "             select 1                                                                                                                                                            "
            + "               from b_already_do subt1                                                                                                                                           "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL + "'                                                                                  "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                             "
            + "                and serial_id = t.id                                                                                                                                             "
            + "       )                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                      "
            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                                                            "
            + "      </script> ")
    int selectExportNum(@Param("p1") BOutPlanListVo searchCondition);

    @Select("   <script>   "
            + "  SELECT * from b_out_plan_detail t                                                                      "
            + "  where t.plan_id in                                                                                     "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>             "
            + "         #{item.plan_id,jdbcType=INTEGER}                                                                "
            + "        </foreach>                                                                                       "
            + "  </script>    ")
    List<BOutPlanDetailEntity> selectByPlanIds(@Param("p1") List<BOutPlanListVo> searchCondition);

    @Select("      "
            + "		SELECT                                                                                              "
            + "			t2.extra_code houseOutPlanCode,                                                                 "
            + "			t2.release_order_code houseOutDirectCode,                                                       "
            + "			t3.order_type orderType                                                                         "
            + "		FROM                                                                                                "
            + "			b_out_plan_detail t1                                                                            "
            + "			INNER JOIN b_out_plan t2 ON t1.plan_id = t2.id                                                  "
            + "			INNER JOIN b_release_order t3 ON t3.code = t2.release_order_code                                "
            + "		WHERE                                                                                               "
            + "		TRUE                                                                                                "
            + "			AND t2.release_order_code IS NOT NULL                                                           "
            + "         AND t1.id =  #{p1,jdbcType=INTEGER}                                                             "
            + "    ")
    ApiOutCheckVo selectOutCheckVo(@Param("p1") Integer id);

    @Select("  <script>    "
            + "		SELECT                                                                                              "
            + "         sum(t0.actual_weight) outNum,                                                                   "
            + "			t2.extra_code houseOutPlanCode,                                                                 "
            + "			t2.release_order_code houseOutDirectCode,                                                       "
            + "			t3.order_type orderType                                                                         "
            + "		FROM                                                                                                "
            + "			b_out t0                                                                                        "
            + "			INNER JOIN b_out_plan_detail t1 on t0.plan_detail_id = t1.id                                    "
            + "			INNER JOIN b_out_plan t2 ON t1.plan_id = t2.id                                                  "
            + "			INNER JOIN b_release_order t3 ON t3.code = t2.release_order_code                                "
            + "		WHERE                                                                                               "
            + "		TRUE                                                                                                "
            + "			AND t2.release_order_code IS NOT NULL                                                           "
            + "			AND t0.id IN                                                                                    "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>             "
            + "         #{item.id}                                                                                      "
            + "        </foreach>                                                                                       "
            + "         group by t2.extra_code, t2.release_order_code                                                   "
            + " </script>   ")
    List<ApiOutCheckVo> selectOutCheckVoByOutBill(@Param("p1") List<BOutVo> beans);

    @Select("      "
            + "		SELECT                                                                                              "
            + "			*                                                                                               "
            + "		FROM                                                                                                "
            + "			b_out_plan t1                                                                                  "
            + "		WHERE                                                                                               "
            + "		TRUE                                                                                                "
            + "         AND t1.extra_code =  #{p1,jdbcType=VARCHAR}                                                             "
            + "    ")
    List<BOutPlanEntity> selectByExtraCode(@Param("p1") String extraCode);

    /**
     * 查詢列表总数量
     * @param param 参数
     * @param sort
     * @param sortType
     * @param defaultSort
     * @return
     */
    @Select("   <script> "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + common_select
            + "   where true                                                                                                                                                                    "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null)                                                           "
            + "      and (CONCAT(ifnull(t7.name,''),ifnull(t7.short_name,''),ifnull(t7.name_pinyin,''),ifnull(t7.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)                                                               "
            + "      and (CONCAT(ifnull(t8.name,''),ifnull(t8.short_name,''),ifnull(t8.name_pinyin,''),ifnull(t8.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)                                                       "
            + "      and (CONCAT(ifnull(t9.name,''),ifnull(t9.short_name,''),ifnull(t9.name_pinyin,''),ifnull(t9.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.customer_name,jdbcType=VARCHAR},'%') or #{p1.customer_name,jdbcType=VARCHAR} is null)                                                         "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                  "
            + "      and (t.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%')                                                                                                  "
            + "             or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                                                                             "
            + "      and (t.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%')                                                                                                            "
            + "             or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                                                                       "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                     "
            + "      and (t17.bill_type = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} = '')                                "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                              "
            + "      and (t11.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                      "
            + "          or t10.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                    "
            + "          or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                  "
            + "          or t11.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null  or #{p1.goods_name,jdbcType=VARCHAR} = '' )          "
            + "      and (t17.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)                                               "
//            + "      and (t17.client_id = #{p1.client_id,jdbcType=INTEGER} or #{p1.client_id,jdbcType=INTEGER} is null)                                                                       "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                           "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                             "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                                                "
            + "      and (t4.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "

            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "

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
            + "				AND subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL+"'                                                                                        "
            + "				AND subt1.STATUS = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                                                                                       "
            + "				GROUP BY subt1.serial_id, subt1.serial_type                                                                                                                         "
            + "  )                                                                                                                                                                              "
            + "      </if>                                                                                                                                                                      "

            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                                                                                           "
            + "      and exists (                                                                                                                                                               "
            + "             select 1                                                                                                                                                            "
            + "               from b_already_do subt1                                                                                                                                           "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL + "'                                                                                  "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                             "
            + "                and serial_id = t.id                                                                                                                                             "
            + "				GROUP BY subt1.serial_id, subt1.serial_type                                                                                                                         "
            + "       )                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                      "

            + "      <if test='p1.filter_type != null and p1.filter_type.length!=0' >                                                                                              "
            + "       and t3.type not in                                                                                                                                              "
            + "           <foreach collection='p1.filter_type' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "

            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                                                            "
            + "    ORDER BY ${p2} ${p3} ${p4}                                                                                                                                   "
            + "    limit ${(p1.pageCondition.current-1)*p1.pageCondition.size},  #{p1.pageCondition.size}                                                                       "
            + "      </script> ")
    List<BOutPlanListVo> selectPageListNotCount(@Param("p1") BOutPlanListVo param,@Param("p2") String sort,@Param("p3") String sortType,@Param("p4") String defaultSort);

    /**
     * 查詢总条数
     * @param searchCondition
     * @return
     */
    @Select("   <script> "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + common_select_count
            + "   where true                                                                                                                                                                    "
            + "      and (t3.code like CONCAT ('%',#{p1.plan_code,jdbcType=VARCHAR},'%') or #{p1.plan_code,jdbcType=VARCHAR} is null)                                                           "
            + "      and (CONCAT(ifnull(t7.name,''),ifnull(t7.short_name,''),ifnull(t7.name_pinyin,''),ifnull(t7.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.owner_name,jdbcType=VARCHAR},'%') or #{p1.owner_name,jdbcType=VARCHAR} is null)                                                               "
            + "      and (CONCAT(ifnull(t8.name,''),ifnull(t8.short_name,''),ifnull(t8.name_pinyin,''),ifnull(t8.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.consignor_name,jdbcType=VARCHAR},'%') or #{p1.consignor_name,jdbcType=VARCHAR} is null)                                                       "
            + "      and (CONCAT(ifnull(t9.name,''),ifnull(t9.short_name,''),ifnull(t9.name_pinyin,''),ifnull(t9.short_name_pinyin,''))                                                         "
            + "             like CONCAT ('%',#{p1.customer_name,jdbcType=VARCHAR},'%') or #{p1.customer_name,jdbcType=VARCHAR} is null)                                                         "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                  "
            + "      and (t.type_gauge like CONCAT ('%',#{p1.type_gauge,jdbcType=VARCHAR},'%')                                                                                                  "
            + "             or #{p1.type_gauge,jdbcType=VARCHAR} is null or #{p1.type_gauge,jdbcType=VARCHAR} = '')                                                                             "
            + "      and (t.alias like CONCAT ('%',#{p1.alias,jdbcType=VARCHAR},'%')                                                                                                            "
            + "             or #{p1.alias,jdbcType=VARCHAR} is null or #{p1.alias,jdbcType=VARCHAR} = '')                                                                                       "
            + "      and (t3.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null or #{p1.type,jdbcType=VARCHAR} = '')                                                     "
            + "      and (t17.bill_type = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null or #{p1.bill_type,jdbcType=VARCHAR} = '')                                "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} = '')                                              "
            + "      and (t11.name like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                      "
            + "          or t10.spec like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                    "
            + "          or t.sku_code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                                                                                                  "
            + "          or t11.code like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null  or #{p1.goods_name,jdbcType=VARCHAR} = '' )          "
            + "      and (t17.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)                                               "
//            + "      and (t17.client_id = #{p1.client_id,jdbcType=INTEGER} or #{p1.client_id,jdbcType=INTEGER} is null)                                                                       "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)                           "
            + "      and (date_format(t3.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)                             "
            + "      and (t25.status = #{p1.sync_status} or #{p1.sync_status} is null or #{p1.sync_status} = '')                                                                                "
            + "      and (t4.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                                                   "

            + "      <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "       and t.status in                                                                                                                                              "
            + "           <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "            #{item}                                                                                                                                                 "
            + "           </foreach>                                                                                                                                               "
            + "      </if>                                                                                                                                                         "

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
            + "				AND subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL+"'                                                                                        "
            + "				AND subt1.STATUS = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                                                                                       "
            + "				GROUP BY subt1.serial_id, subt1.serial_type                                                                                                                         "
            + "  )                                                                                                                                                                              "
            + "      </if>                                                                                                                                                                      "

            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                                                                                           "
            + "      and exists (                                                                                                                                                               "
            + "             select 1                                                                                                                                                            "
            + "               from b_already_do subt1                                                                                                                                           "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OUT_PLAN_DETAIL + "'                                                                                  "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                                             "
            + "                and serial_id = t.id                                                                                                                                             "
            + "				GROUP BY subt1.serial_id, subt1.serial_type                                                                                                                         "
            + "       )                                                                                                                                                                         "
            + "      </if>                                                                                                                                                                      "

            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                                                            "
            + "      </script> ")
    Integer selectPageListCount(@Param("p1") BOutPlanListVo searchCondition);

    @Select("  <script>    "
            + "		SELECT                                                                                              "
            + "         sum(t0.actual_weight) outNum,                                                                   "
            + "			t2.extra_code houseOutPlanCode,                                                                 "
            + "			t2.release_order_code houseOutDirectCode,                                                       "
            + "			t3.order_type orderType                                                                         "
            + "		FROM                                                                                                "
            + "			b_receive t0                                                                                        "
            + "			INNER JOIN b_out_plan_detail t1 on t0.plan_detail_id = t1.id                                    "
            + "			INNER JOIN b_out_plan t2 ON t1.plan_id = t2.id                                                  "
            + "			INNER JOIN b_release_order t3 ON t3.code = t2.release_order_code                                "
            + "		WHERE                                                                                               "
            + "		TRUE                                                                                                "
            + "			AND t2.release_order_code IS NOT NULL                                                           "
            + "			AND t0.id IN                                                                                    "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>             "
            + "         #{item.id}                                                                                      "
            + "        </foreach>                                                                                       "
            + "         group by t2.extra_code, t2.release_order_code                                                   "
            + " </script>   ")
    List<ApiOutCheckVo> selectReceiveCheckVoByOutBill(@Param("p1") List<BReceiveVo> beans);

    /**
     * 按出库计划id查询
     * @param id
     * @return
     */
    @Select("                                                                                                   "
            + common_select
            + "  where t3.id =  #{p1,jdbcType=INTEGER}                                                           ")
    BOutPlanSaveVo newGet(@Param("p1") int id);

    /**
     * 查询货主公司和委托公司 的出库计划
     */
    @Select("SELECT * FROM b_out_plan where owner_code = #{p1} or consignor_code = #{p1}")
    List<BOutPlanEntity> selectByCustomerCode(@Param("p1")String code);
}
