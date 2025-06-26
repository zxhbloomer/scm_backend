package com.xinyirun.scm.core.system.mapper.wms.in.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.in.order.BInOrderEntity;
import com.xinyirun.scm.bean.system.vo.wms.in.order.BInOrderExportVo;
import com.xinyirun.scm.bean.system.vo.wms.in.order.BInOrderVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BBuyContractReportExportVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BContractReportVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 入库订单 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Repository
public interface BInOrderMapper extends BaseMapper<BInOrderEntity> {

    /**
     * 按订单编号，合同编号获取数据
     * @param vo
     * @return
     */
    @Select("    "
            + "  select t.* from b_in_order t where true                                                                                          "
            + "         and (t.order_no =  #{p1.order_no,jdbcType=VARCHAR} or #{p1.order_no,jdbcType=VARCHAR} is null)                            "
            + "         and (t.contract_no =  #{p1.contract_no,jdbcType=VARCHAR} or #{p1.contract_no,jdbcType=VARCHAR} is null)                   "
            + "      ")
    List<BInOrderEntity> selectOrderByContract(@Param("p1") BInOrderVo vo);

    String common_select = "  "
            + "        SELECT                                                                                   "
            + "                t.u_time,                                                                                                                    "
            + "                t.c_time,                                                                                                                    "
            + "                t.*,                                                                                                                         "
            + "                t9.label as status_name ,                                                                                                    "
            + "                t7.id as in_order_goods_id,                                                                                                  "
            + "	               t7.idx,                                                                                                                       "
            + "	               t7.order_id,                                                                                                                 "
            + "	               t7.sku_id,                                                                                                                   "
            + "	               t7.sku_code,                                                                                                                 "
            + "	               t7.unit_id,                                                                                                                  "
            + "	               t7.unit_code,                                                                                                                "
            + "	               t7.unit_name,                                                                                                                "
            + "	               t7.price,                                                                                                                    "
            + "	               t7.num,                                                                                                                      "
            + "	               t7.amount,                                                                                                                   "
            + "	               t7.rate,                                                                                                                     "
            + "	               t7.delivery_date,                                                                                                            "
            + "	               t7.delivery_type,                                                                                                            "
            + "			       t7.no order_detail_no,                                                                                                       "
            + "                DATE_FORMAT(t.contract_dt , '%Y年%m月%d日') contract_dtf,                                                                     "
            + "                ifnull(t3.short_name,t3.name) as supplier_name,                                                                              "
            + "                ifnull(t4.short_name,t4.name) as owner_name,                                                                                 "
            + "                t8.name as goods_name,                                                                                                       "
            + "                t8.pm ,                                                                                                                      "
            + "                t8.spec ,                                                                                                                    "
            + "                t.source_type ,                                                                                                              "
            + "                t5.label as bill_type_name ,                                                                                                 "
            + "                t6.name as business_type_name ,                                                                                              "
            + "                t1.name as c_name,                                                                                                           "
            + "                t2.name as u_name,                                                                                                           "
            + "                t12.actual_count as in_actual_count,                                                                                         "
            + "                t11.schedule_count                                                                                                           "
            + "           FROM b_in_order t                                                                                                                 "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                    "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                    "
            + "   LEFT JOIN m_customer t3 ON t.supplier_id = t3.id                                                                                          "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   LEFT JOIN v_dict_info AS t5 ON t5.code = '" + DictConstant.DICT_B_IN_PLAN_BUSINESS_TYPE + "' and t5.dict_value = t.bill_type              "
            + "   LEFT JOIN m_business_type t6 ON t.business_type_id = t6.id                                                                                "
            + "   inner join (                                                                                                                              "
            + "   			  select row_number() over(partition by t.order_id                                                                              "
            + "   			                               order by t.sku_code asc) as idx,                                                                 "
            + "   				t.*                                                                                                                         "
            + "   			from b_in_order_goods t                                                                                                         "
            + "     ) t7 on t7.order_id = t.id                                                                                                              "
            + "      left join m_goods_spec t8 on t8.id = t7.sku_id                                                                                         "
            + "   LEFT JOIN v_dict_info AS t9 ON t9.code = '" + DictConstant.DICT_B_IN_ORDER_STATUS + "' and t9.dict_value = t.status                       "
            + "  left join m_goods t10 on t8.goods_id = t10.id                                                                                              "
            + "  left join (                                                                                                                                "
            + "		SELECT                                                                                                                                  "
            + "			t1.id,                                                                                                                              "
            + "			count( t3.id ) schedule_count                                                                                                       "
            + "		FROM                                                                                                                                    "
            + "			b_in_order t1                                                                                                                       "
            + "			INNER JOIN b_order t2 ON t1.id = t2.serial_id                                                                                       "
            + "			AND t2.serial_type = 'b_in_order'                                                                                                   "
            + "			INNER JOIN b_schedule t3 ON t2.id = t3.order_id                                                                                     "
            + "		GROUP BY                                                                                                                                "
            + "			t1.id                                                                                                                               "
            + ") t11  on t11.id = t.id                                                                                                                      "
            // 已入库 数量 显示
            + "  LEFT JOIN (                                                                                                                                "
            +  "  SELECT                                                                                                                                    "
            +  "  	t3.actual_count,                                                                                                                         "
            +  "  	t.id                                                                                                                                    "
            +  "  FROM                                                                                                                                      "
            +  "  	b_in_order_goods t                                                                                                                      "
            +  "  	LEFT JOIN (                                                                                                                             "
            +  "  	SELECT                                                                                                                                  "
            +  "  		SUM( t1.actual_count ) actual_count,                                                                                                "
            +  "  		ifnull(tt1.order_id, t2.order_id) order_id,                                                                                         "
            +  "  		t1.sku_id                                                                                                                           "
            +  "  	FROM                                                                                                                                    "
            +  "  		b_in t1                                                                                                                             "
            +  "  		LEFT JOIN b_in_extra t2 ON t1.id = t2.in_id                                                                                         "
            +  "  		AND t2.order_type = 'b_in_order'                                                                                                    "
            +  "        LEFT JOIN b_in_plan_detail tt1 ON t1.plan_detail_id= tt1.id AND tt1.order_type = 'b_in_order'                                       "
            +  "    WHERE t1.status = '" + DictConstant.DICT_B_IN_STATUS_PASSED + "'                                                                        "
            +  "        AND t1.type = '"+ DictConstant.DICT_B_IN_TYPE_CG +"'                                                                                "
            +  "  	GROUP BY                                                                                                                                "
            +  "  		t1.sku_id,                                                                                                                          "
            +  "  		ifnull( tt1.order_id, t2.order_id )                                                                                                 "
            +  "  	) t3 ON t.order_id = t3.order_id                                                                                                        "
            +  "  	AND t.sku_id = t3.sku_id                                                                                                                "
            +  "  ) t12 ON t7.id = t12.id                                                                                                                   "
            ;

    String common_select2 = "  "
            + "        SELECT  * from (                                                                                                                     "
            + "        SELECT                                                                                                                               "
            + "                t.*,                                                                                                                         "
            + "                '"+ SystemConstants.ORDER.B_IN_ORDER +"'  type,                                                                              "
            + "                t3.id as customer_id,                                                                                                        "
            + "                ifnull(t3.short_name,t3.name) as customer_name,                                                                              "
            + "                ifnull(t4.short_name,t4.name) as owner_name,                                                                                 "
            + "                t5.label as bill_type_name ,                                                                                                 "
            + "                t6.name as business_type_name ,                                                                                              "
            + "                t1.name as c_name,                                                                                                           "
            + "                t2.name as u_name                                                                                                            "
            + "           FROM b_in_order t                                                                                                                 "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                    "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                    "
            + "   LEFT JOIN m_customer t3 ON t.supplier_id = t3.id                                                                                          "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   LEFT JOIN v_dict_info AS t5 ON t3.code = '" + DictConstant.DICT_B_IN_PLAN_BUSINESS_TYPE + "' and t5.dict_value = t.bill_type              "
            + "   LEFT JOIN m_business_type t6 ON t.business_type_id = t6.id                                                                                "

            + "      UNION ALL                                                                                                                              "

            + "      SELECT                                                                                                                                 "
            + "             t.*,                                                                                                                            "
            + "             '"+ SystemConstants.ORDER.B_OUT_ORDER +"'  type,                                                                                "
            + "             t3.id as customer_id,                                                                                                           "
            + "             ifnull(t3.short_name,t3.name) as customer_name,                                                                                 "
            + "             ifnull(t4.short_name,t4.name) as owner_name,                                                                                    "
            + "             t5.label as bill_type_name ,                                                                                                    "
            + "             t6.name as business_type_name ,                                                                                                 "
            + "             t1.name as c_name,                                                                                                              "
            + "             t2.name as u_name                                                                                                               "
            + "        FROM                                                                                                                                 "
            + "   	       b_out_order t                                                                                                                    "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                    "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                    "
            + "   LEFT JOIN m_customer t3 ON t.client_id = t3.id                                                                                            "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                                 "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t5 on t5.dict_value = t.bill_type                       "
            + "   LEFT JOIN m_business_type t6 ON t.business_type_id = t6.id                                                                                "

            + "       )tab                                                                                                                                  "
            ;

    String common_select3 = "  "
            + "        SELECT                                                                                   "
            + "                t.*,                                                                                                                         "
            + "                DATE_FORMAT(t.contract_dt , '%Y年%m月%d日') contract_dtf,                                                                     "
            + "                t9.label as status_name ,                                                                                                    "
            + "                ifnull(t3.short_name,t3.name) as supplier_name,                                                                              "
            + "                ifnull(t4.short_name,t4.name) as owner_name,                                                                                 "
            + "                t5.label as bill_type_name ,                                                                                                 "
//            + "			       t7.num,                                                                                                                         "
            + "                t6.name as business_type_name ,                                                                                              "
//            + "                t8.name as goods_name,                                                                                                       "
//            + "                t8.pm ,                                                                                                                      "
//            + "                t8.spec ,                                                                                                                    "
            + "                t1.name as c_name,                                                                                                           "
            + "                t2.name as u_name                                                                                                            "
            + "           FROM b_in_order t                                                                                                                 "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                    "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                    "
            + "   LEFT JOIN m_customer t3 ON t.supplier_id = t3.id                                                                                          "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   LEFT JOIN v_dict_info AS t5 ON t5.code = '" + DictConstant.DICT_B_IN_PLAN_BUSINESS_TYPE + "' and t5.dict_value = t.bill_type              "
            + "   LEFT JOIN m_business_type t6 ON t.business_type_id = t6.id                                                                                "
//            + "   inner join (                                                                                                                              "
//            + "   			  select row_number() over(partition by t.order_id                                                                              "
//            + "   			                               order by t.sku_code asc) as idx,                                                                 "
//            + "   				t.*                                                                                                                         "
//            + "   			from b_in_order_goods t                                                                                                         "
//            + "     ) t7 on t7.order_id = t.id                                                                                                              "
//            + "      left join m_goods_spec t8 on t8.id = t7.sku_id                                                                                         "
            + "   LEFT JOIN v_dict_info AS t9 ON t9.code = '" + DictConstant.DICT_B_IN_ORDER_STATUS + "' and t9.dict_value = t.status                       "
            + "                                                                                                                                             "
            ;

    String common_selectReport =  ""
            +  "		t1.order_id,                                                                           "
            +  "		SUM(t.contract_num) qty,                                                               "
            +  "		t3.name goods_name,                                                                    "
            +  "        t.contract_no,                                                                         "
            +  "		t.owner_id,                                                                            "
            +  "        t3.code goods_code,                                                                    "
            +  "		t4.name owner,                                                                         "
            +  "        ifnull(t5.short_name, t5.name) supplier_name,                                          "
            +  "        ifnull(t10.actual_count, 0) actual_count,                                              "
            +  "        t11.name goods_prop,                                                                   "
            +  "        t.supplier_id                                                                          "
            +  " FROM                                                                                          "
            +  "    b_in_order t                                                                               "
            +  " LEFT JOIN b_in_order_goods t1 ON t.id = t1.order_id                                           "
            +  " LEFT JOIN m_goods_spec t2 ON t2.id = t1.sku_id                                                "
            +  " LEFT JOIN m_goods t3 ON t3.id = t2.goods_id                                                   "
            +  " LEFT JOIN m_owner t4 ON t4.id = t.owner_id                                                    "
            +  " LEFT JOIN m_customer t5 ON t.supplier_id = t5.id                                              "
            +  " LEFT JOIN m_goods_spec_prop t11 on t11.id = t2.prop_id                                        "
            +   "   LEFT JOIN (                                                                                "
            +   "   	  SELECT                                                                               "
            +   "   		   t6.supplier_id,                                                                          "
            +   "              t6.owner_id,                                                                    "
            +   "   			 t9.goods_id,                                                                  "
            +   "   			 SUM(t8.actual_count) actual_count                                             "
            +   "   		FROM                                                                               "
            +   "   		  b_in_order t6                                                                    "
            +   "   		LEFT JOIN b_in_plan_detail t7 ON t6.id = t7.order_id                               "
            +   "   		LEFT JOIN b_in t8 ON t7.id = t8.plan_detail_id                                     "
            +   "   		LEFT JOIN m_goods_spec t9 ON t9.id = t8.sku_id                                     "
            +   "   		WHERE t6.id IS NOT NULL                                                            "
            +   "                 and t8.status = '" +  DictConstant.DICT_B_IN_STATUS_PASSED + "'              "
            +   "                 and t8.type = '" +  DictConstant.DICT_B_IN_TYPE_CG + "'                      "
            +   " and (DATE_FORMAT(t6.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
            +   " and (DATE_FORMAT(t8.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
            +   "   		GROUP BY t6.supplier_id,  t6.owner_id,  t9.goods_id                                "
            +   "   	) t10 ON t10.supplier_id = t.supplier_id AND t10.goods_id = t3.id  and t10.owner_id = t.owner_id"

            ;


    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("<script>    "
            + common_select
             + "  where true                                                                                                                   "
            + "    and (t.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)           "
            + "    and (t.status != '"+ DictConstant.DICT_B_IN_ORDER_STATUS_ONE +"')                                                           "
            + "    and (t.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)  "
            + "    and (t4.short_name LIKE CONCAT('%', #{p1.owner_name}, '%') or t4.name LIKE CONCAT('%',#{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')                                         "
            + "    and (t10.name LIKE CONCAT('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')                 "
            + "    and (concat(ifnull(t10.code, ''), '_', ifnull(t8.code, '')) LIKE CONCAT('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')                 "
            + "    and (concat(t3.name, '_', t3.short_name) LIKE CONCAT('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or #{p1.supplier_name} = '')               "
            + "    and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')  "
            + "    <if test='p1.status_list != null and p1.status_list.length != 0'>                                   "
            + "    and t.status in                                                                                     "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>"
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>                                                                                                "
            + "</script>")
    IPage<BInOrderVo> selectPage(Page page, @Param("p1") BInOrderVo searchCondition);

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select2
            + "  where true                                                                                                                     "
            + "    and (tab.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)          "
            + "    and (tab.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null) "
            + "      ")
    IPage<BInOrderVo> selectList(Page page, @Param("p1") BInOrderVo searchCondition);
    
    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select3
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    BInOrderVo selectId(@Param("p1") int id);

    /**
     * 按订单号查询数据
     */
    @Select("    "
            + common_select3
            + "  where t.order_no =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    BInOrderEntity selectByOrderNo(@Param("p1") String orderNo);

    /**
     * 查询采购合同汇总
     * @param searchCondition
     * @return
     */
    @Select({"<script>"
                    + "SELECT                                                                                                  "
                    +  " CONCAT(ifnull(t.owner_id, ''), '_', ifnull(t3.id, ''), '_', ifnull(t.supplier_id, '')) as id, "
                    +    common_selectReport
                    +  " WHERE                                                                                         "
                    +  " t.status != '" + DictConstant.DICT_B_IN_ORDER_STATUS_ONE + "'                                 "
                    +  " AND (t.owner_id = #{p1.owner_id} OR #{p1.owner_id} IS NULL)                                   "
                    +  " AND (t3.name LIKE CONCAT('%', #{p1.goods_name}, '%') OR #{p1.goods_name} IS NULL OR #{p1.goods_name} = '')"
                    +  " AND (t11.name LIKE CONCAT('%', #{p1.goods_prop}, '%') OR  #{p1.goods_prop} IS NULL OR  #{p1.goods_prop} = '')"
                    +  " AND (t.supplier_id = #{p1.supplier_id} or #{p1.supplier_id} is null)                          "
                    +  " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    +  "<if test='p1.showTips'>                                                                        "
                    +  "   and t3.`name` != '水稻'                                                                      "
                    +  "</if>                                                                                          "
                    +  " GROUP BY t3.id, t.owner_id, t.supplier_id                                                     "
                    +"</script>"
    })
    IPage<BContractReportVo> queryBuyerContractList(@Param("p1") BContractReportVo searchCondition, Page<BContractReportVo> pageCondition);

    /**
     * 采购合同求和
     * @param param
     * @return
     */
    @Select({ "<script>"
                    + "SELECT SUM(tt1.qty) qty, sum(tt1.actual_count) actual_count from ( "
                    +  " SELECT                                                                                        "
                    +  common_selectReport
                    +  " WHERE                                                                                         "
                    +  " t.status != '" + DictConstant.DICT_B_IN_ORDER_STATUS_ONE + "'                                 "
                    +  " AND (t.owner_id = #{p1.owner_id} OR #{p1.owner_id} IS NULL)                                   "
                    +  " AND (t3.name LIKE CONCAT('%', #{p1.goods_name}, '%') OR #{p1.goods_name} IS NULL OR #{p1.goods_name} = '') "
                    +  " AND (t11.name LIKE CONCAT('%', #{p1.goods_prop}, '%') OR  #{p1.goods_prop} IS NULL OR  #{p1.goods_prop} = '')"
                    +  " AND (t.supplier_id = #{p1.supplier_id} or #{p1.supplier_id} is null)                          "
                    +   " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    +  "<if test='p1.showTips'>                                                                                "
                    +  "   and t3.`name` != '水稻'                                                                     "
                    +  "</if>                                                                                                  "
                    +  " GROUP BY t3.id, t.owner_id, t.supplier_id ) as tt1                                            "
            +"</script>"
    })
    BContractReportVo queryBuyerContractListSum(@Param("p1") BContractReportVo param);

    /**
     * 采购合同导出全部
     * @param param
     * @return
     */
    @Select({"<script> "
                    + " SELECT                                                                                         "
                    + " @row_num:= @row_num+ 1 as no,                                                                  "
                    +  common_selectReport
                    + "  ,(select @row_num:=0) t5                                                                      "
                    +  " WHERE                                                                                         "
                    +  " t.status != '" + DictConstant.DICT_B_IN_ORDER_STATUS_ONE + "'                                 "
                    +  " AND (t.owner_id = #{p1.owner_id} OR #{p1.owner_id} IS NULL)                                   "
                    +  " AND (t3.name LIKE CONCAT('%', #{p1.goods_name}, '%') OR #{p1.goods_name} IS NULL OR #{p1.goods_name} = '')"
                    +  " AND (t11.name LIKE CONCAT('%', #{p1.goods_prop}, '%') OR  #{p1.goods_prop} IS NULL OR  #{p1.goods_prop} = '')"
                    +  " AND (t.supplier_id = #{p1.supplier_id} or #{p1.supplier_id} is null)                          "
                    +  " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    +  "<if test='p1.showTips'>                                                                        "
                    +  "   and t3.`name` != '水稻'                                                                      "
                    +  "</if>                                                                                          "
                    +  " GROUP BY t3.id, t.owner_id, t.supplier_id                                                     "
            + "</script> "
    })
    List<BBuyContractReportExportVo> queryBuyerContractListExportAll(@Param("p1") BContractReportVo param);

    @Select({
            " <script> "
                    +  " SELECT                                                                                        "
                    + " @row_num:= @row_num+ 1 as no,                                                                  "
                    +  common_selectReport
                    + "  ,(select @row_num:=0) t5                                                                      "
                    +  " WHERE                                                                                         "
                    +  " t.status != '" + DictConstant.DICT_B_IN_ORDER_STATUS_ONE + "'                                 "
                    +  " <if test='p1 != null and p1.size != 0'>  "
                    +  "     AND CONCAT(ifnull(t.owner_id, ''), '_', ifnull(t3.id, ''), '_', ifnull(t.supplier_id, '')) in "
                    +  "      <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>     "
                    +  "          #{item.id}"
                    +  "      </foreach>                                                                               "
                    +  " </if>"
                    +  " group by CONCAT(ifnull(t.owner_id, ''), '_', ifnull(t3.id, ''), '_', ifnull(t.supplier_id, ''))"
            +" </script> "
    })
    List<BBuyContractReportExportVo> queryBuyerContractListExport(@Param("p1") List<BContractReportVo> param);

    /**
     * 根据商品编码 查询采购合同
     * @param param
     * @return
     */
    @Select({"SELECT                                                                                          "
//                    +  " CONCAT('采购合同,', t5.name) as type,                                                           "
                    +  " t5.name goods_prop,                                                                            "
                    +  " t3.name goods_name,                                                                            "
                    +  "  SUM(t.contract_num) qty                                                                       "
                    +  " FROM                                                                                          "
                    +  "    b_in_order t                                                                               "
                    +  " LEFT JOIN b_in_order_goods t1 ON t.id = t1.order_id                                           "
                    +  " LEFT JOIN m_goods_spec t2 ON t2.id = t1.sku_id                                                "
                    +  " LEFT JOIN m_goods_spec_prop t5 ON t5.id = t2.prop_id                                          "
                    +  " LEFT JOIN m_goods t3 ON t3.id = t2.goods_id                                                   "
                    +  " LEFT JOIN m_owner t4 ON t4.id = t.owner_id                                                    "
                    +  " WHERE                                                                                         "
                    +  " t.status != '" + DictConstant.DICT_B_IN_ORDER_STATUS_ONE + "'                                 "
                    +  " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) >= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    +  " group by t3.name                                                                              "
    })
    List<BContractReportVo> queryInContractList(@Param("p1") BContractReportVo param);

    @Select({"<script>                                                                                                 "
            +   " SELECT                                                                                               "
//            +  " CONCAT('销售合同,', t5.name) as type,                                                                 "
            +  " t5.name goods_prop,                                                                                   "
            +  " t3.name goods_name,                                                                                   "
            +  "  SUM(t.contract_num) qty                                                                              "
            +  "  FROM                                                                                                 "
            +  "    b_out_order t                                                                                      "
            +  " LEFT JOIN b_out_order_goods t1 ON t.id = t1.order_id                                                  "
            +  " LEFT JOIN m_goods_spec t2 ON t2.id = t1.sku_id                                                        "
            +  " LEFT JOIN m_goods t3 ON t3.id = t2.goods_id                                                           "
            +  " LEFT JOIN m_owner t4 ON t4.id = t.owner_id                                                            "
            +  " LEFT JOIN m_goods_spec_prop t5 ON t5.id = t2.prop_id                                                  "
            +  " where t.status != '" + DictConstant.DICT_B_OUT_ORDER_STATUS_ONE + "'                                  "
            +  " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) >= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
//            +  "<if test='p1.showTips'>                                                                                "
//            +  "   and t3.`code` != 'zlsd-0100511'                                                                     "
//            +  "</if>                                                                                                  "
            +  " group by t3.name                                                                                        "
            + "</script>"
    })
    List<BContractReportVo> queryOutContractList(@Param("p1") BContractReportVo param);



    @Select({"      <script>"
                    + "        SELECT                                                                                   "
                    + "                ifnull(sum(t.contract_num), 0) contract_num ,                                    "
                    + "                ifnull(sum(t12.actual_count),0) in_actual_count                                  "
                    + "           FROM b_in_order t                                                                                                                 "
                    + "   LEFT JOIN m_customer t3 ON t.supplier_id = t3.id                                                                                          "
                    + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
                    + "   inner join (                                                                                                                              "
                    + "   			  select row_number() over(partition by t.order_id                                                                              "
                    + "   			                               order by t.sku_code asc) as idx,                                                                 "
                    + "   				t.*                                                                                                                         "
                    + "   			from b_in_order_goods t                                                                                                         "
                    + "     ) t7 on t7.order_id = t.id                                                                                                              "
                    + "  left join m_goods_spec t8 on t8.id = t7.sku_id                                                                                             "
                    + "  left join m_goods t10 on t8.goods_id = t10.id                                                                                              "
                    + "  LEFT JOIN (                                                                                                                                "
                    +  "  SELECT                                                                                                                                    "
                    +  "  	t3.actual_count,                                                                                                                         "
                    +  "  	t.id                                                                                                                         "
                    +  "  FROM                                                                                                                                      "
                    +  "  	b_in_order_goods t                                                                                                                      "
                    +  "  	LEFT JOIN (                                                                                                                             "
                    +  "  	SELECT                                                                                                                                  "
                    +  "  		SUM( t1.actual_count ) actual_count,                                                                                                "
                    +  "  		ifnull(tt1.order_id, t2.order_id) order_id,                                                                                         "
                    +  "  		t1.sku_id                                                                                                                           "
                    +  "  	FROM                                                                                                                                    "
                    +  "  		b_in t1                                                                                                                             "
                    +  "  		LEFT JOIN b_in_extra t2 ON t1.id = t2.in_id                                                                                        "
                    +  "  		AND t2.order_type = 'b_in_order'                                                                                                    "
                    +  "        LEFT JOIN b_in_plan_detail tt1 ON t1.plan_detail_id= tt1.id AND tt1.order_type = 'b_in_order'                                       "
                    +  "    WHERE t1.status = '" + DictConstant.DICT_B_IN_STATUS_PASSED + "'                                                                        "
                    +  "        AND t1.type = '"+ DictConstant.DICT_B_IN_TYPE_CG +"'                                                                                "
                    +  "  	GROUP BY                                                                                                                                "
                    +  "  		t1.sku_id,                                                                                                                          "
                    +  "  		ifnull( tt1.order_id, t2.order_id )                                                                                                 "
                    +  "  	) t3 ON t.order_id = t3.order_id                                                                                                        "
                    +  "  	AND t.sku_id = t3.sku_id                                                                                                                "
                    +  "  ) t12 ON t7.id = t12.id                                                                                                                   "
                    + "  where true                                                                                                                                 "
                    + "    and (t.status != '"+ DictConstant.DICT_B_IN_ORDER_STATUS_ONE +"')                                                                        "
                    + "    and (t.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)                        "
                    + "    and (t.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)               "
                    + "    and (t4.short_name LIKE CONCAT('%', #{p1.owner_name}, '%') or t4.name LIKE CONCAT('%',#{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')                                         "
                    + "    and (t10.name LIKE CONCAT('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')               "
                    + "    and (concat(ifnull(t10.code, ''), '_', ifnull(t8.code, '')) LIKE CONCAT('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')                 "
                    + "    and (concat(t3.name, '_', t3.short_name) LIKE CONCAT('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or #{p1.supplier_name} = '')               "
                    + "    and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')  "
                    + "    <if test='p1.status_list != null and p1.status_list.length != 0'>                                   "
                    + "    and t.status in                                                                                     "
                    + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>"
                    + "         #{item}                                                                                        "
                    + "        </foreach>                                                                                      "
                    + "   </if>                                                                                                "
                    + "   </script>                                                                                            "

    })
    BInOrderVo getListSum(@Param("p1") BInOrderVo searchCondition);

    /**
     * 查询导出数量
     * @param param
     * @return
     */
    @Select("<script>    "
            + "        SELECT                                                                                                                               "
            + "            count(1)                                                                                                                         "
            + "   FROM b_in_order t                                                                                                                         "
            + "   LEFT JOIN m_customer t3 ON t.supplier_id = t3.id                                                                                          "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   inner join (                                                                                                                              "
            + "   			  select row_number() over(partition by t.order_id                                                                              "
            + "   			                               order by t.sku_code asc) as idx,                                                                 "
            + "   				t.*                                                                                                                         "
            + "   			from b_in_order_goods t                                                                                                         "
            + "     ) t7 on t7.order_id = t.id                                                                                                              "
            + "      left join m_goods_spec t8 on t8.id = t7.sku_id                                                                                         "
            + "  left join m_goods t10 on t8.goods_id = t10.id                                                                                              "
            + "  where true                                                                                                                                 "
            + "    and (t.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)                        "
            + "    and (t.status != '"+ DictConstant.DICT_B_IN_ORDER_STATUS_ONE +"')                                                                        "
            + "    and (t.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)               "
            + "    and (t4.short_name LIKE CONCAT('%', #{p1.owner_name}, '%') or t4.name LIKE CONCAT('%',#{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')                                         "
            + "    and (t10.name LIKE CONCAT('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')                              "
            + "    and (concat(ifnull(t10.code, ''), '_', ifnull(t8.code, '')) LIKE CONCAT('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')"
            + "    and (concat(t3.name, '_', t3.short_name) LIKE CONCAT('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or #{p1.supplier_name} = '') "
            + "    and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')  "
            + "    <if test='p1.status_list != null and p1.status_list.length != 0'>                                   "
            + "    and t.status in                                                                                     "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>"
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>                                                                                                "
            + "</script>")
    int exportCount(@Param("p1") BInOrderVo param);

    /**
     * 导出
     * @param param
     * @return
     */
    @Select("<script>                                                                                                                                       "
            + "        SELECT                                                                                                                               "
            + "                @row_num:= @row_num+ 1 as no,                                                                                                "
            + "                t.contract_no,                                                                                                               "
            + "                t.order_no,                                                                                                                  "
            + "                ifnull(t11.schedule_count, 0) schedule_count,                                                                                "
            + "                t9.label as status_name ,                                                                                                    "
            + "	               t7.idx,                                                                                                                      "
            + "	               t7.amount,                                                                                                                   "
            + "                t5.label as bill_type_name ,                                                                                                 "
            + "                t.contract_dt,                                                                                                               "
            + "                t.contract_expire_dt,                                                                                                        "
            + "                ifnull(t3.short_name,t3.name) as supplier_name,                                                                              "
            + "                ifnull(t4.short_name,t4.name) as owner_name,                                                                                 "
            + "                t8.name as goods_name,                                                                                                       "
            + "                t8.pm ,                                                                                                                      "
            + "                t8.spec ,                                                                                                                    "
            + "                t.contract_num,                                                                                                              "
            + "                t1.name as c_name,                                                                                                           "
            + "                t.c_time,                                                                                                                    "
            + "                t2.name as u_name,                                                                                                           "
            + "                t12.actual_count,                                                                                                            "
            + "                t.u_time                                                                                                                     "
            + "           FROM b_in_order t                                                                                                                 "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                    "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                    "
            + "   LEFT JOIN m_customer t3 ON t.supplier_id = t3.id                                                                                          "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   LEFT JOIN v_dict_info AS t5 ON t5.code = '" + DictConstant.DICT_B_IN_PLAN_BUSINESS_TYPE + "' and t5.dict_value = t.bill_type              "
            + "   inner join (                                                                                                                              "
            + "   			  select row_number() over(partition by t.order_id                                                                              "
            + "   			                               order by t.sku_code asc) as idx,                                                                 "
            + "   				t.*                                                                                                                         "
            + "   			from b_in_order_goods t                                                                                                         "
            + "     ) t7 on t7.order_id = t.id                                                                                                              "
            + "      left join m_goods_spec t8 on t8.id = t7.sku_id                                                                                         "
            + "   LEFT JOIN v_dict_info AS t9 ON t9.code = '" + DictConstant.DICT_B_IN_ORDER_STATUS + "' and t9.dict_value = t.status                       "
            + "  left join m_goods t10 on t8.goods_id = t10.id                                                                                              "
            + "  left join (                                                                                                                                "
            + "		SELECT                                                                                                                                  "
            + "			t1.id,                                                                                                                              "
            + "			count( t3.id ) schedule_count                                                                                                       "
            + "		FROM                                                                                                                                    "
            + "			b_in_order t1                                                                                                                       "
            + "			INNER JOIN b_order t2 ON t1.id = t2.serial_id                                                                                       "
            + "			AND t2.serial_type = 'b_in_order'                                                                                                   "
            + "			INNER JOIN b_schedule t3 ON t2.id = t3.order_id                                                                                     "
            + "		GROUP BY                                                                                                                                "
            + "			t1.id                                                                                                                               "
            + ") t11  on t11.id = t.id                                                                                                                      "
            + "  LEFT JOIN (                                                                                                                                "
            +  "  SELECT                                                                                                                                    "
            +  "  	t3.actual_count,                                                                                                                        "
            +  "  	t.id                                                                                                                                    "
            +  "  FROM                                                                                                                                      "
            +  "  	b_in_order_goods t                                                                                                                      "
            +  "  	LEFT JOIN (                                                                                                                             "
            +  "  	SELECT                                                                                                                                  "
            +  "  		SUM( t1.actual_count ) actual_count,                                                                                                "
            +  "  		ifnull(tt1.order_id, t2.order_id) order_id,                                                                                         "
            +  "  		t1.sku_id                                                                                                                           "
            +  "  	FROM                                                                                                                                    "
            +  "  		b_in t1                                                                                                                             "
            +  "  		LEFT JOIN b_in_extra t2 ON t1.id = t2.in_id                                                                                        "
            +  "  		AND t2.order_type = 'b_in_order'                                                                                                    "
            +  "        LEFT JOIN b_in_plan_detail tt1 ON t1.plan_detail_id= tt1.id AND tt1.order_type = 'b_in_order'                                       "
            +  "    WHERE t1.status = '" + DictConstant.DICT_B_IN_STATUS_PASSED + "'                                                                        "
            +  "        AND t1.type = '"+ DictConstant.DICT_B_IN_TYPE_CG +"'                                                                                "
            +  "  	GROUP BY                                                                                                                                "
            +  "  		t1.sku_id,                                                                                                                          "
            +  "  		ifnull( tt1.order_id, t2.order_id )                                                                                                 "
            +  "  	) t3 ON t.order_id = t3.order_id                                                                                                        "
            +  "  	AND t.sku_id = t3.sku_id                                                                                                                "
            +  "  ) t12 ON t7.id = t12.id                                                                                                                   "
            + "   ,(select @row_num:=0) t6                                                                                                                  "
            + "  where true                                                                                                                   "
            + "    and (t.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)           "
            + "    and (t.status != '"+ DictConstant.DICT_B_IN_ORDER_STATUS_ONE +"')                                                           "
            + "    and (t.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)  "
            + "    and (t4.short_name LIKE CONCAT('%', #{p1.owner_name}, '%') or t4.name LIKE CONCAT('%',#{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')                                         "
            + "    and (t10.name LIKE CONCAT('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')                 "
            + "    and (concat(ifnull(t10.code, ''), '_', ifnull(t8.code, '')) LIKE CONCAT('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')     "
            + "    and (concat(t3.name, '_', t3.short_name) LIKE CONCAT('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or #{p1.supplier_name} = '')               "
            + "    and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')  "
            + "    <if test='p1.status_list != null and p1.status_list.length != 0'>                                   "
            + "    and t.status in                                                                                     "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>"
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>                                                                                                "
            + "    <if test='p1.ids != null and p1.ids.size != 0'>                                                     "
            + "    and concat(t.id, '_', t7.sku_id) in                                                                                         "
            + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>"
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>                                                                                                "
            + "</script>")
    List<BInOrderExportVo> exportList(@Param("p1") BInOrderVo param);

    /**
     * 采购合同导出数量查询
     * @param param
     * @return
     */
    @Select({" <script>                                                                                                "
                    + " SELECT count(1) from (                                                                         "
                    +  " select t.id                                                                                   "
                    +  " FROM                                                                                          "
                    +  "    b_in_order t                                                                               "
                    +  " LEFT JOIN b_in_order_goods t1 ON t.id = t1.order_id                                           "
                    +  " LEFT JOIN m_goods_spec t2 ON t2.id = t1.sku_id                                                "
                    +  " LEFT JOIN m_goods t3 ON t3.id = t2.goods_id                                                   "
                    +  " LEFT JOIN m_goods_spec_prop t11 on t11.id = t2.prop_id                                        "
                    +  " WHERE                                                                                         "
                    +  " t.status != '" + DictConstant.DICT_B_IN_ORDER_STATUS_ONE + "'                                 "
                    +  " AND (t.owner_id = #{p1.owner_id} OR #{p1.owner_id} IS NULL)                                   "
                    +  " AND (t3.name LIKE CONCAT('%', #{p1.goods_name}, '%') OR #{p1.goods_name} IS NULL OR #{p1.goods_name} = '')"
                    +  " AND (t11.name LIKE CONCAT('%', #{p1.goods_prop}, '%') OR  #{p1.goods_prop} IS NULL OR  #{p1.goods_prop} = '')"
                    +  " AND (t.supplier_id = #{p1.supplier_id} or #{p1.supplier_id} is null)                          "
                    +  " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    +  "<if test='p1.showTips'>                                                                        "
                    +  "   and t3.`name` != '水稻'                                                                      "
                    +  "</if>                                                                                          "
                    +  " GROUP BY t3.id, t.owner_id, t.supplier_id ) tt1                                               "
            + "</script>"
    })
    int selectExportNum(@Param("p1") BContractReportVo param);
}
