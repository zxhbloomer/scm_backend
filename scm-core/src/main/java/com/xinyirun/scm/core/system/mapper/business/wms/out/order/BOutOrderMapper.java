package com.xinyirun.scm.core.system.mapper.business.wms.out.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BContractReportVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BDirectlyWarehouseVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BOutContractReportExportVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutOrderExportVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 出库订单合同信息 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-10-18
 */
@Repository
public interface BOutOrderMapper extends BaseMapper<BOutOrderEntity> {

    String common_select = "  "
            + "      SELECT                                                                                                                                 "
            + "             t.u_time,                                                                                                                       "
            + "             t.c_time,                                                                                                                       "
            + "             t.*,                                                                                                                            "
            + "             t9.label as status_name ,                                                                                                       "
            + "             t7.id as out_order_goods_id,                                                                                                    "
            + "			    t7.id,                                                                                                                          "
            + "			    t7.idx,                                                                                                                         "
            + "			    t7.order_id,                                                                                                                    "
            + "			    t7.sku_id,                                                                                                                      "
            + "			    t7.sku_code,                                                                                                                    "
            + "			    t7.unit_id,                                                                                                                     "
            + "			    t7.unit_code,                                                                                                                   "
            + "			    t7.unit_name,                                                                                                                   "
            + "			    t7.price,                                                                                                                       "
            + "			    t7.num,                                                                                                                         "
            + "			    t7.amount,                                                                                                                      "
            + "			    t7.rate,                                                                                                                        "
            + "			    t7.delivery_date,                                                                                                               "
            + "			    t7.delivery_type,                                                                                                               "
            + "			    t7.no order_detail_no,                                                                                                          "
            + "             DATE_FORMAT(t.contract_dt , '%Y年%m月%d日') contract_dtf,                                                                        "
            + "             ifnull(t3.short_name,t3.name) as client_name,                                                                                   "
            + "             ifnull(t4.short_name,t4.name) as owner_name,                                                                                    "
            + "             t8.name as goods_name,                                                                                                          "
            + "             t8.pm ,                                                                                                                         "
            + "             t8.spec ,                                                                                                                       "
            + "             t5.label as bill_type_name ,                                                                                                    "
            + "             t6.name as business_type_name ,                                                                                                 "
            + "             t1.name as c_name,                                                                                                              "
            + "             t2.name as u_name,                                                                                                              "
            + "             t11.schedule_count,                                                                                                             "
            + "             t12.out_actual_count                                                                                                            "
            + "        FROM                                                                                                                                 "
            + "   	       b_out_order t                                                                                                                    "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                    "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                    "
            + "   LEFT JOIN m_customer t3 ON t.client_id = t3.id                                                                                            "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                                 "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t5 on t5.dict_value = t.bill_type                       "
            + "   LEFT JOIN m_business_type t6 ON t.business_type_id = t6.id                                                                                "
            + "   inner join (                                                                                                                              "
            + "   			  select row_number() over(partition by t.order_id                                                                              "
            + "   			                               order by t.sku_code asc) as idx,                                                                 "
            + "   				t.*                                                                                                                         "
            + "   			from b_out_order_goods t                                                                                                        "
            + "     ) t7 on t7.order_id = t.id                                                                                                              "
            + "      left join m_goods_spec t8 on t8.id = t7.sku_id                                                                                         "
            + "   LEFT JOIN v_dict_info AS t9 ON t9.code = '" + DictConstant.DICT_B_OUT_ORDER_STATUS + "' and t9.dict_value = t.status                      "
            + "  left join m_goods t10 on t8.goods_id = t10.id                                                                                              "
            + "  left join (                                                                                                                                "
            + "		SELECT                                                                                                                                  "
            + "			t1.id,                                                                                                                              "
            + "			count( t3.id ) schedule_count                                                                                                       "
            + "		FROM                                                                                                                                    "
            + "			b_out_order t1                                                                                                                      "
            + "			INNER JOIN b_order t2 ON t1.id = t2.serial_id                                                                                       "
            + "			AND t2.serial_type = 'b_out_order'                                                                                                  "
            + "			INNER JOIN b_schedule t3 ON t2.id = t3.order_id                                                                                     "
            + "		GROUP BY                                                                                                                                "
            + "			t1.id                                                                                                                               "
            + ") t11  on t11.id = t.id                                                                                                                      "
            + "  left join (                                                                                                                                "
            + "      select                                                                                                                                 "
            + "          sum(t1.actual_count) out_actual_count,                                                                                             "
            + "          t2.order_id,                                                                                                                       "
            + "          t1.sku_id                                                                                                                          "
            + "      from b_out t1                                                                                                                          "
            + "      left join b_out_extra t2 ON t1.id = t2.out_id and t2.order_type = 'b_out_order'                                                        "
            + "      where t1.status = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                                                                      "
            + "      AND t1.type = '" + DictConstant.DICT_B_OUT_TYPE_XS + "'                                                                                "
            + "      group by t2.order_id, t1.sku_id) t12 ON t12.sku_id = t7.sku_id and t12.order_id = t7.order_id                                              ";


    String common_select2 = "  "
            + "        SELECT * from (                                                                                                                      "
            + "        SELECT                                                                                                                               "
            + "                t.*,                                                                                                                         "
            + "                '"+ SystemConstants.ORDER.B_IN_ORDER +"'  type,                                                                              "
            + "                t3.id as customer_id,                                                                                                        "
            + "                ifnull(t3.short_name,t3.name) as customer_name,                                                                              "
            + "                ifnull(t4.short_name,t4.name) as owner_name,                                                                                 "
            + "                NULL client_id,                                                                                                              "
            + "                t5.label as bill_type_name ,                                                                                                 "
            + "                t6.name as business_type_name ,                                                                                              "
            + "                t1.name as c_name,                                                                                                           "
            + "                t2.name as u_name                                                                                                            "
            + "           FROM b_in_order t                                                                                                                 "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                    "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                    "
            + "   LEFT JOIN m_customer t3 ON t.supplier_id = t3.id                                                                                          "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                                 "
            + "              where tab2.code = '" + DictConstant.DICT_B_IN_PLAN_BUSINESS_TYPE + "')t5 on t5.dict_value = t.bill_type                       "
            + "   LEFT JOIN m_business_type t6 ON t.business_type_id = t6.id                                                                                "

            + "      UNION ALL                                                                                                                              "

            + "      SELECT                                                                                                                                 "
            + "             t.*,                                                                                                                            "
            + "             '"+ SystemConstants.ORDER.B_OUT_ORDER +"'  type,                                                                                "
            + "             t3.id as customer_id,                                                                                                           "
            + "             ifnull(t3.short_name,t3.name) as customer_name,                                                                                 "
            + "             ifnull(t4.short_name,t4.name) as owner_name,                                                                                    "
            + "             t.client_id,                                                                                    "
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

            + "    )tab                                                                                                                                         "
            ;

    String common_select3 = "  "
            + "      SELECT                                                                                                               "
            + "             t.*,                                                                                                          "
            + "             ifnull(t3.short_name,t3.name) as client_name,                                                                 "
            + "             ifnull(t4.short_name,t4.name) as owner_name,                                                                  "
            + "             t5.label as bill_type_name ,                                                                                  "
            + "             t6.name as business_type_name ,                                                                               "
            + "             t1.name as c_name,                                                                                            "
//            + "			    t7.num,                                                                                                                         "
//            + "             t8.name as goods_name,                                                                                        "
//            + "             t8.pm ,                                                                                                       "
//            + "             t8.spec ,                                                                                                     "
            + "             t9.label as status_name ,                                                                                     "
            + "             t2.name as u_name                                                                                             "
            + "        FROM                                                                                                               "
            + "   	       b_out_order t                                                                                                  "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                  "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                  "
            + "   LEFT JOIN m_customer t3 ON t.client_id = t3.id                                                                          "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                              "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id               "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t5 on t5.dict_value = t.bill_type     "
            + "   LEFT JOIN m_business_type t6 ON t.business_type_id = t6.id                                                              "
//            + "   inner join (                                                                                                                              "
//            + "   			  select row_number() over(partition by t.order_id                                                                              "
//            + "   			                               order by t.sku_code asc) as idx,                                                                 "
//            + "   				t.*                                                                                                                         "
//            + "   			from b_out_order_goods t                                                                                                        "
//            + "     ) t7 on t7.order_id = t.id                                                                                                              "
//            + "      left join m_goods_spec t8 on t8.id = t7.sku_id                                                                                         "
            + "   LEFT JOIN v_dict_info AS t9 ON t9.code = '" + DictConstant.DICT_B_OUT_ORDER_STATUS + "' and t9.dict_value = t.status                      "
            + "                                                                        "
            ;

    String common_selectReport =  " "
            +  "		t1.order_id,                                                                           "
            +  "        t.contract_no,                                                                         "
            +  "		SUM(t.contract_num) qty,                                                               "
            +  "		t3.name goods_name,                                                                    "
            +  "		t.owner_id,                                                                            "
            +  "        t3.code goods_code,                                                                    "
            +  "		t4.name owner,                                                                         "
            +  "        ifnull(t10.actual_count, 0) actual_count,                                              "
            +  "        t11.name goods_prop,                                                                   "
            + "         ifnull(t5.short_name,t5.name) as supplier_name                                         "
            +  "  FROM                                                                                         "
            +  "    b_out_order t                                                                              "
            +  " LEFT JOIN b_out_order_goods t1 ON t.id = t1.order_id                                          "
            +  " LEFT JOIN m_goods_spec t2 ON t2.id = t1.sku_id                                                "
            +  " LEFT JOIN m_goods t3 ON t3.id = t2.goods_id                                                   "
            +  " LEFT JOIN m_owner t4 ON t4.id = t.owner_id                                                    "
            +  " LEFT JOIN m_customer t5 ON t.client_id = t5.id                                                "
            +  " LEFT JOIN m_goods_spec_prop t11 on t11.id = t2.prop_id                                        "
            +   "   LEFT JOIN (                                                                                "
            +   "   	  SELECT                                                                               "
            +   "   		   t6.client_id,                                                                   "
            +   "              t6.owner_id,                                                                    "
            +   "   			 t9.goods_id,                                                                  "
            +   "   			 SUM(t8.actual_count) -sum(ifnull(t8.return_qty,0)) actual_count               "
            +   "   		FROM                                                                               "
            +   "   		  b_out_order t6                                                                   "
            +   "   		LEFT JOIN b_out_plan_detail t7 ON t6.id = t7.order_id                              "
            +   "   		LEFT JOIN b_out t8 ON t7.id = t8.plan_detail_id                                    "
            +   "   		LEFT JOIN m_goods_spec t9 ON t9.id = t8.sku_id                                     "
            +   "   		WHERE t6.id IS NOT NULL                                                            "
            +   "                 and t8.status = '" +  DictConstant.DICT_B_OUT_STATUS_PASSED + "'             "
            +   "                 and t8.type = '" +  DictConstant.DICT_B_OUT_TYPE_XS + "'                     "
            +   " and (DATE_FORMAT(t6.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
            +   " and (DATE_FORMAT(t8.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
            +   "   		GROUP BY  t6.client_id,  t6.owner_id,  t9.goods_id                               "
            +   "   	) t10 ON t10.client_id = t.client_id AND t10.goods_id = t3.id  and t10.owner_id = t.owner_id"


            ;

    /**
     * 按订单编号，合同编号，来源获取数据
     * @param vo
     * @return
     */
    @Select("    "
            + "  select t.* from b_out_order t where true                                                                                          "
            + "         and t.order_no =  #{p1.order_no,jdbcType=VARCHAR}                                                                          "
//            + "         and t.contract_no =  #{p1.contract_no,jdbcType=VARCHAR}                                                                    "
            + "      ")
    List<BOutOrderEntity> selectOrderByContract(@Param("p1") BOutOrderVo vo);

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("  <script>  "
            + common_select
            + "  where true                                                                                                                       "
            + "    and t.status != '"+ DictConstant.DICT_B_OUT_ORDER_STATUS_ONE +"'                                                               "
            + "    and (t.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)              "
            + "    and (t.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)     "
            + "    and (t4.short_name LIKE CONCAT('%', #{p1.owner_name}, '%') or t4.name LIKE CONCAT('%',#{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')                                         "
            + "    and (t10.name LIKE CONCAT('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')               "
            + "    and (concat(ifnull(t10.code, ''), '_', ifnull(t8.code, '')) LIKE CONCAT('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')                 "
            + "    and (concat(t3.name, '_', t3.short_name) LIKE CONCAT('%', #{p1.customer_name}, '%') or #{p1.customer_name} is null or #{p1.customer_name} = '')               "
            + "      and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                              "
            + "    <if test='p1.status_list != null and p1.status_list.length != 0'>                                   "
            + "    and t.status in                                                                                     "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>"
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>  "
            + "   </script>   ")
    IPage<BOutOrderVo> selectPage(Page page, @Param("p1") BOutOrderVo searchCondition);

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select2
            + "  where true                                                                                                                         "
            + "    and (tab.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)              "
            + "    and (tab.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)     "
            + "      ")
    IPage<BOutOrderVo> selectList(Page page, @Param("p1") BOutOrderVo searchCondition);

    /**
     * 按条件获取数据，没有分页
     * @param id
     * @return
     */
    @Select("                                        "
            + common_select3
            + "  where t.id =  #{p1,jdbcType=INTEGER}")
    BOutOrderVo selectId(@Param("p1") int id);

    /**
     * 按订单号查询数据
     */
    @Select("                                        "
            + common_select3
            + "  where t.order_no =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    BOutOrderEntity selectByOrderNo(@Param("p1") String orderNo);

    /**
     * 销售合同 汇总
     * @param pageCondition
     * @param searchCondition
     * @return
     */
    @Select({"<script>"
            + " SELECT                                                                                                 "
            +  " CONCAT(ifnull(t.owner_id, ''), '_', ifnull(t3.id, ''), '_', ifnull(t.client_id, '')) as id,           "
            +  common_selectReport
            +  " where t.status != '" + DictConstant.DICT_B_OUT_ORDER_STATUS_ONE + "'                                  "
            +  " AND (t.owner_id = #{p1.owner_id} OR #{p1.owner_id} IS NULL)                                           "
            +  " AND (t3.name LIKE CONCAT('%', #{p1.goods_name}, '%') OR #{p1.goods_name} IS NULL OR #{p1.goods_name} = '')"
            +  " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
            +  "<if test='p1.showTips'>                                                                                "
            +  "   and t3.`code` != 'zlsd-0100511'                                                                     "
            +  "</if>                                                                                                  "
            +  " AND (t11.name LIKE CONCAT('%', #{p1.goods_prop}, '%') OR  #{p1.goods_prop} IS NULL OR  #{p1.goods_prop} = '')"
            +  " AND (t.client_id = #{p1.supplier_id} or #{p1.supplier_id} is null)                                    "
            +  " GROUP BY t3.id, t.owner_id, t.client_id                                                               "
            +  "</script>"
    })
    IPage<BContractReportVo> queryOutContractList(Page<BContractReportVo> pageCondition, @Param("p1") BContractReportVo searchCondition);

    /**
     * 销售合同汇总 求和
     * @param param
     * @return
     */
    @Select({"<script>"
                    +"SELECT SUM(tt1.qty) qty, SUM(tt1.actual_count) actual_count from (                               "
                    +  " SELECT                                                                                        "
                    +  common_selectReport
                    +  " WHERE                                                                                         "
                    +  " t.status != '" + DictConstant.DICT_B_OUT_ORDER_STATUS_ONE + "'                                "
                    +  " AND (t.owner_id = #{p1.owner_id} OR #{p1.owner_id} IS NULL)                                   "
                    +  " AND (t3.name LIKE CONCAT('%', #{p1.goods_name}, '%') OR #{p1.goods_name} IS NULL OR #{p1.goods_name} = '') "
                    +  " AND (t11.name LIKE CONCAT('%', #{p1.goods_prop}, '%') OR  #{p1.goods_prop} IS NULL OR  #{p1.goods_prop} = '')"
                    +  " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    +  "<if test='p1.showTips'>                                                                                "
                    +  "   and t3.`code` != 'zlsd-0100511'                                                                     "
                    +  "</if>                                                                                                  "
                    +  " AND (t.client_id = #{p1.supplier_id} or #{p1.supplier_id} is null)                            "
                    +  " GROUP BY t3.id, t.owner_id, t.client_id  ) as tt1                                             "
                    +  "</script>"

    })
    BContractReportVo queryOutContractListSum(@Param("p1") BContractReportVo param);

    /**
     * 销售合同汇总， 部分导出
     * @param param
     * @return
     */
    @Select({
            " <script> "
                    +  " SELECT                                                                                        "
                    + " @row_num:= @row_num+ 1 as no,                                                                  "
                    +  common_selectReport
                    + "  ,(select @row_num:=0) t5                                                                      "
                    +  " WHERE                                                                                         "
                    +  " t.status != '" + DictConstant.DICT_B_OUT_ORDER_STATUS_ONE + "'                                "
                    +  " <if test='p1 != null and p1.size != 0'>  "
                    +  "     AND CONCAT(ifnull(t.owner_id, ''), '_', ifnull(t3.id, ''), '_', ifnull(t.client_id, '')) in "
                    +  "      <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>     "
                    +  "          #{item.id}"
                    +  "      </foreach>                                                                               "
                    +  " </if>"
                    +  " group by CONCAT(ifnull(t.owner_id, ''), '_', ifnull(t3.id, ''), '_', ifnull(t.client_id, ''))                                                       "
                    +" </script> "
    })
    List<BOutContractReportExportVo> queryOutContractListExport(@Param("p1") List<BContractReportVo> param);

    /**
     * 销售合同汇总， 全部导出
     * @param param
     * @return
     */
    @Select({"<script>"
                    + " SELECT                                                                                                   "
                    + " @row_num:= @row_num+ 1 as no,                                                                  "
                    +  common_selectReport
                    + "  ,(select @row_num:=0) t5                                                                      "
                    +  " WHERE                                                                                         "
                    +  " t.status != '" + DictConstant.DICT_B_OUT_ORDER_STATUS_ONE + "'                                "
                    +  " AND (t.owner_id = #{p1.owner_id} OR #{p1.owner_id} IS NULL)                                   "
                    +  "<if test='p1.showTips'>                                                                                "
                    +  "   and t3.`code` != 'zlsd-0100511'                                                                     "
                    +  "</if>                                                                                                  "
                    +  " AND (t3.name LIKE CONCAT('%', #{p1.goods_name}, '%') OR #{p1.goods_name} IS NULL OR #{p1.goods_name} = '')"
                    +  " AND (t11.name LIKE CONCAT('%', #{p1.goods_prop}, '%') OR  #{p1.goods_prop} IS NULL OR  #{p1.goods_prop} = '')"
                    +  " AND (t.client_id = #{p1.supplier_id} or #{p1.supplier_id} is null)                            "
                    +  " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
                    +  " GROUP BY t3.id, t.owner_id, t.client_id                                                       "
                    +  "</script>"
    })
    List<BOutContractReportExportVo> queryOutContractListExportAll(@Param("p1") BContractReportVo param);

    /**
     * 直属库合同统计
     * @param param
     * @return
     */
    @Select({"  SELECT                                                                                                                                                                                 "
            +  "    	tab1.`name` warehouse_name,                                                                                                                                                      "
            +  "    	tab3.file_json,                                                                                                                                                                  "
            +  "    	tab3.contract_num,                                                                                                                                                               "
            +  "    	ifnull( tab4.num, 0 ) vehicle_count_today,                                                                                                                                       "
            +  "    	ifnull( tab3.num, 0 ) vehicle_count,                                                                                                                                             "
//                    +  "    	ifnull( tab4.actual_count, 0 ) actual_count_today,                                                                                                                               "
            +  "    	ifnull( tab3.actual_count, 0 ) actual_count,                                                                                                                                     "
            +  "    	ifnull( tab3.pending_count, 0 ) pending_count,                                                                                                                                   "
            +  "    	tab4.qty actual_count_today,                                                                                                                                                                        "
            +  "    	IFNULL( tab1.in_actual_count, 0 ) in_actual_count                                                                                                                                "
            +  "    FROM                                                                                                                                                                                 "
            +  "    	(                                                                                                                                                                                "
            +  "    	SELECT                                                                                                                                                                           "
            +  "    		t2.`name`,                                                                                                                                                                   "
            +  "    		t2.id warehouse_id,                                                                                                                                                          "
            +  "    		SUM( t3.actual_count ) in_actual_count                                                                                                                                       "
            +  "    	FROM                                                                                                                                                                             "
            +  "    		b_in_order t                                                                                                                                                                 "
            +  "    		LEFT JOIN b_in_plan_detail t1 ON t1.order_id = t.id                                                                                                                          "
            +  "    		LEFT JOIN m_warehouse t2 ON t1.warehouse_id = t2.id                                                                                                                          "
            +  "    		LEFT JOIN b_in t3 ON t1.id = t3.plan_detail_id                                                                                                                               "
            +  "    		AND t3.`status` = '2'                                                                                                                                                        "
            +  "    	WHERE                                                                                                                                                                            "
            +  "    		t2.warehouse_type = '1'                                                                                                                                                      "
            +  "    		AND t.`status` = '0'                                                                                                                                                         "
            +  "    		AND t3.type != '2'                                                                                                                                                           "
            +  "    	GROUP BY                                                                                                                                                                         "
            +  "    		t2.id                                                                                                                                                                        "
            +  "    	) tab1                                                                                                                                                                           "
            +  "    	LEFT JOIN (                                                                                                                                                                      "
            +  "    	SELECT                                                                                                                                                                           "
            +  "    		tab3.actual_count,                                                                                                                                                           "
            +  "    		tab3.contract_num,                                                                                                                                                           "
            +  "    		sum( tab3.contract_num - tab3.actual_count ) pending_count,                                                                                                                  "
            +  "    		tab3.file_json,                                                                                                                                                              "
            +  "    		tab3.warehouse_id,                                                                                                                                                           "
            +  "    		tab3.num                                                                                                                                                                     "
            +  "    	FROM                                                                                                                                                                             "
            +  "    		(                                                                                                                                                                            "
            +  "    		SELECT                                                                                                                                                                       "
            +  "    			IFNULL( SUM( tab1.actual_count ), 0 ) actual_count,                                                                                                                      "
            +  "    			SUM( tab2.contract_num ) contract_num,                                                                                                                                   "
            +  "    			SUM( tab1.num ) num,                                                                                                                                                     "
            +  "    			JSON_ARRAYAGG((                                                                                                                                                          "
            +  "    					JSON_OBJECT(                                                                                                                                                     "
            +  "    						'contract_no',                                                                                                                                               "
            +  "    						tab2.contract_no,                                                                                                                                            "
//                 +  "    						'actual_count',                                                                                                                                              "
//                 +  "    						IFNULL( tab1.actual_count, 0 ),                                                                                                                              "
            +  "    						'contract_expire_dt',                                                                                                                                        "
            +  "    						DATE_FORMAT(tab2.contract_expire_dt, '%Y-%m-%d'),                                                                                         "
            +  "    						'pending_count',                                                                                                                                             "
            +  "    						tab2.contract_num - IFNULL( tab1.actual_count, 0 )                                                                                                           "
            +  "    					))) file_json,                                                                                                                                                   "
            +  "    			tab1.warehouse_id                                                                                                                                                        "
            +  "    		FROM                                                                                                                                                                         "
            +  "    			(                                                                                                                                                                        "
            +  "    			SELECT                                                                                                                                                                   "
            +  "    				at1.contract_num,                                                                                                                                                    "
            +  "    				at1.contract_no,                                                                                                                                                     "
            +  "    				at1.id,                                                                                                                                                              "
            +  "    				at1.contract_expire_dt,                                                                                                                                                            "
            +  "    				at6.warehouse_id                                                                                                                                                     "
            +  "    			FROM                                                                                                                                                                     "
            +  "    				b_in_order at1                                                                                                                                                       "
            +  "    				LEFT JOIN b_in_plan_detail at6 ON at1.id = at6.order_id                                                                                                              "
            +  "    				LEFT JOIN b_in at7 ON at7.plan_detail_id = at6.id                                                                                                                    "
            +  "    				LEFT JOIN m_warehouse at8 ON at6.warehouse_id = at8.id                                                                                                               "
            +  "    			WHERE                                                                                                                                                                    "
            +  "    				at8.warehouse_type = '1'                                                                                                                                             "
            +  "    				AND at7.`status` = '2'                                                                                                                                               "
            +  "    			GROUP BY                                                                                                                                                                 "
            +  "    				at1.id,                                                                                                                                                              "
            +  "    				at6.warehouse_id                                                                                                                                                     "
            +  "    			) tab2                                                                                                                                                                   "
            +  "    			LEFT JOIN (                                                                                                                                                              "
            +  "    			SELECT                                                                                                                                                                   "
            +  "    				SUM( at9.actual_count ) actual_count,                                                                                                                                "
            +  "    				at4.id,                                                                                                                                                              "
            +  "    				at5.warehouse_id,                                                                                                                                                    "
            +  "    				t4.num                                                                                                                                                               "
            +  "    			FROM                                                                                                                                                                     "
            +  "    				b_in_order at4                                                                                                                                                       "
            +  "    				LEFT JOIN b_order at2 ON at4.id = at2.serial_id                                                                                                                      "
            +  "    				AND at2.serial_type = 'b_in_order'                                                                                                                                   "
            +  "    				LEFT JOIN b_schedule at3 ON at3.order_id = at2.id                                                                                                                    "
            +  "    				LEFT JOIN b_out_plan_detail at5 ON at3.out_plan_detail_id = at5.id                                                                                                   "
            +  "    				LEFT JOIN b_out at9 ON at9.plan_detail_id = at5.id                                                                                                                   "
            +  "    				LEFT JOIN m_warehouse at10 ON at5.warehouse_id = at10.id                                                                                                             "
            +  "    				LEFT JOIN ( SELECT at14.schedule_id, count( 1 ) num FROM b_monitor at14 WHERE at14.`status` != '8' GROUP BY at14.schedule_id ) t4 ON at3.id = t4.schedule_id         "
            +  "    			WHERE                                                                                                                                                                    "
            +  "    				at10.warehouse_type = '1'                                                                                                                                            "
            +  "    				AND at9.`status` = '2'                                                                                                                                               "
            +  "    			GROUP BY                                                                                                                                                                 "
            +  "    				at4.id,                                                                                                                                                              "
            +  "    				at5.warehouse_id                                                                                                                                                     "
            +  "    			) tab1 ON tab1.id = tab2.id                                                                                                                                              "
            +  "    			AND tab1.warehouse_id = tab2.warehouse_id                                                                                                                                "
            +  "    		WHERE                                                                                                                                                                        "
            +  "    			tab2.contract_num > tab1.actual_count                                                                                                                                    "
            +  "    		GROUP BY                                                                                                                                                                     "
            +  "    			tab1.warehouse_id                                                                                                                                                        "
            +  "    		) tab3                                                                                                                                                                       "
            +  "    	GROUP BY                                                                                                                                                                         "
            +  "    		tab3.warehouse_id                                                                                                                                                            "
            +  "    	) tab3 ON tab1.warehouse_id = tab3.warehouse_id                                                                                                                                  "
            +  "    	LEFT JOIN (                                                                                                                                                                      "
            +  "    	SELECT                                                                                                                                                                           "
            +  "    		tab3.actual_count,                                                                                                                                                           "
            +  "    		tab3.contract_num,                                                                                                                                                           "
            +  "    		sum( tab3.contract_num - tab3.actual_count ) pending_count,                                                                                                                  "
            +  "    		tab3.num,                                                                                                                                                                    "
            +  "    		tab3.warehouse_id,                                                                                                                                                           "
            +  "    		tab3.qty                                                                                                                                                                     "
            +  "    	FROM                                                                                                                                                                             "
            +  "    		(                                                                                                                                                                            "
            +  "    		SELECT                                                                                                                                                                       "
            +  "    			IFNULL( SUM( tab1.actual_count ), 0 ) actual_count,                                                                                                                      "
            +  "    			SUM( tab2.contract_num ) contract_num,                                                                                                                                   "
            +  "    			SUM( tab1.num ) num,                                                                                                                                                     "
            +  "    			SUM(tab1.qty) qty,                                                                                                                                                       "
            +  "    			tab1.warehouse_id                                                                                                                                                        "
            +  "    		FROM                                                                                                                                                                         "
            +  "    			(                                                                                                                                                                        "
            +  "    			SELECT                                                                                                                                                                   "
            +  "    				at1.contract_num,                                                                                                                                                    "
            +  "    				at1.contract_no,                                                                                                                                                     "
            +  "    				at1.id,                                                                                                                                                              "
            +  "    				at6.warehouse_id                                                                                                                                                     "
            +  "    			FROM                                                                                                                                                                     "
            +  "    				b_in_order at1                                                                                                                                                       "
            +  "    				LEFT JOIN b_in_plan_detail at6 ON at1.id = at6.order_id                                                                                                              "
            +  "    				LEFT JOIN b_in at7 ON at7.plan_detail_id = at6.id                                                                                                                    "
            +  "    				LEFT JOIN m_warehouse at8 ON at6.warehouse_id = at8.id                                                                                                               "
            +  "    			WHERE                                                                                                                                                                    "
            +  "    				at8.warehouse_type = '1'                                                                                                                                             "
            +  "    				AND at7.`status` = '2'                                                                                                                                               "
            +  "    			GROUP BY                                                                                                                                                                 "
            +  "    				at1.id,                                                                                                                                                              "
            +  "    				at6.warehouse_id                                                                                                                                                     "
            +  "    			) tab2                                                                                                                                                                   "
            +  "    			LEFT JOIN (                                                                                                                                                              "
            +  "    			SELECT                                                                                                                                                                   "
            +  "    				SUM( at9.actual_count ) actual_count,                                                                                                                                "
            +  "    				at4.id,                                                                                                                                                              "
            +  "    				at5.warehouse_id,                                                                                                                                                    "
            +  "    				t4.num,                                                                                                                                                              "
            +  "    				t4.qty                                                                                                                                                               "
            +  "    			FROM                                                                                                                                                                     "
            +  "    				b_in_order at4                                                                                                                                                       "
            +  "    				LEFT JOIN b_order at2 ON at4.id = at2.serial_id                                                                                                                      "
            +  "    				AND at2.serial_type = 'b_in_order'                                                                                                                                   "
            +  "    				LEFT JOIN b_schedule at3 ON at3.order_id = at2.id                                                                                                                    "
            +  "    				LEFT JOIN b_out_plan_detail at5 ON at3.out_plan_detail_id = at5.id                                                                                                   "
            +  "    				LEFT JOIN b_out at9 ON at9.plan_detail_id = at5.id                                                                                                                   "
            +  "    				LEFT JOIN m_warehouse at10 ON at5.warehouse_id = at10.id                                                                                                             "
            +  "    				INNER JOIN (                                                                                                                                                         "
            +  "    				SELECT                                                                                                                                                               "
            +  "    					at14.schedule_id,                                                                                                                                                "
            +  "    					count( 1 ) num,                                                                                                                                                  "
            +  "    					at14.out_time,                                                                                                                                                   "
            +  "    					SUM(IFNULL(at15.qty,at16.qty)) qty                                                                                                                               "
            +  "    				FROM                                                                                                                                                                 "
            +  "    					b_monitor at14                                                                                                                                                   "
            +  "    					LEFT JOIN b_monitor_out at15 ON at15.monitor_id = at14.id                                                                                                        "
            +  "    					LEFT JOIN b_monitor_delivery at16 ON at16.monitor_id = at14.id                                                                                                   "
            +  "    				WHERE                                                                                                                                                                "
            +  "    					at14.`status` != '8'                                                                                                                                             "
            +  "    					AND DATE_FORMAT( at14.out_time, '%Y%m%d' ) = DATE_FORMAT( NOW(), '%Y%m%d' )                                                                                      "
            +  "    				GROUP BY                                                                                                                                                             "
            +  "    					at14.schedule_id                                                                                                                                                 "
            +  "    				) t4 ON at3.id = t4.schedule_id                                                                                                                                      "
            +  "    			WHERE                                                                                                                                                                    "
            +  "    				at10.warehouse_type = '1'                                                                                                                                            "
            +  "    				AND at9.`status` = '2'                                                                                                                                               "
            +  "    			GROUP BY                                                                                                                                                                 "
            +  "    				at4.id,                                                                                                                                                              "
            +  "    				at5.warehouse_id                                                                                                                                                     "
            +  "    			) tab1 ON tab1.id = tab2.id                                                                                                                                              "
            +  "    			AND tab1.warehouse_id = tab2.warehouse_id                                                                                                                                "
            +  "    		WHERE                                                                                                                                                                        "
            +  "    			tab2.contract_num > tab1.actual_count                                                                                                                                    "
            +  "    		GROUP BY                                                                                                                                                                     "
            +  "    			tab1.warehouse_id                                                                                                                                                        "
            +  "    		) tab3                                                                                                                                                                       "
            +  "    	GROUP BY                                                                                                                                                                         "
            +  "    		tab3.warehouse_id                                                                                                                                                            "
            +  "    	) tab4 ON tab1.warehouse_id = tab4.warehouse_id                                                                                                                                  "
            +  "    WHERE                                                                                                                                                                                "
            +  "    	tab4.num > 0                                                                                                                                                                     "
            + "       and (tab1.name like concat('%', #{p1.warehouse_name}, '%') or #{p1.warehouse_name} is null or #{p1.warehouse_name} = '')"
            + "       and tab3.pending_count > 0"
            +  "    ORDER BY                                                                                                                                                                             "
            +  "    	tab3.pending_count DESC                                                                                                                                                          "
            +  "    	                                                                                                                                                                                 "
    })
    @Results({
            @Result(property = "file_json", column = "file_json", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    IPage<BDirectlyWarehouseVo> getDirectlyWarehouseList(@Param("p1") BDirectlyWarehouseVo param, Page<BContractReportVo> pageCondition);

    @Select("<script>"
            + "      SELECT                                                                                                                                 "
            + "             sum(t7.num) contract_num                                                                                                "
            + "        FROM                                                                                                                                 "
            + "   	       b_out_order t                                                                                                                    "
            + "   LEFT JOIN m_customer t3 ON t.client_id = t3.id                                                                                            "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   inner join (                                                                                                                              "
            + "   			  select row_number() over(partition by t.order_id                                                                              "
            + "   			                               order by t.sku_code asc) as idx,                                                                 "
            + "   				t.*                                                                                                                         "
            + "   			from b_out_order_goods t                                                                                                        "
            + "     ) t7 on t7.order_id = t.id                                                                                                              "
            + "      left join m_goods_spec t8 on t8.id = t7.sku_id                                                                                         "
            + "  left join m_goods t10 on t8.goods_id = t10.id                                                         "
            + "  where true                                                                                                                       "
            + "    and t.status != '"+ DictConstant.DICT_B_OUT_ORDER_STATUS_ONE +"'                                                               "
            + "    and (t.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)              "
            + "    and (t.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)     "
            + "    and (t4.short_name LIKE CONCAT('%', #{p1.owner_name}, '%') or t4.name LIKE CONCAT('%',#{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')                                         "
            + "    and (t10.name LIKE CONCAT('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')               "
            + "    and (concat(ifnull(t10.code, ''), '_', ifnull(t8.code, '')) LIKE CONCAT('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')                 "
            + "    and (concat(t3.name, '_', t3.short_name) LIKE CONCAT('%', #{p1.customer_name}, '%') or #{p1.customer_name} is null or #{p1.customer_name} = '')               "
            + "      and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                              "
            + "    <if test='p1.status_list != null and p1.status_list.length != 0'>                                   "
            + "    and t.status in                                                                                     "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>"
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>  "
            + "   </script>   ")
    BOutOrderVo getListSum(@Param("p1") BOutOrderVo searchCondition);

    /**
     * 导出条数查询
     * @param param
     * @return
     */
    @Select("  <script>  "
            + "      SELECT                                                                                                                                 "
            + "             count(1)                                                                                                                        "
            + "        FROM                                                                                                                                 "
            + "   	       b_out_order t                                                                                                                    "
            + "   LEFT JOIN m_customer t3 ON t.client_id = t3.id                                                                                            "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   inner join (                                                                                                                              "
            + "   			  select row_number() over(partition by t.order_id                                                                              "
            + "   			                               order by t.sku_code asc) as idx,                                                                 "
            + "   				t.*                                                                                                                         "
            + "   			from b_out_order_goods t                                                                                                        "
            + "     ) t7 on t7.order_id = t.id                                                                                                              "
            + "      left join m_goods_spec t8 on t8.id = t7.sku_id                                                                                         "
            + "  left join m_goods t10 on t8.goods_id = t10.id                                                                                              "
            + "  where true                                                                                                                       "
            + "    and t.status != '"+ DictConstant.DICT_B_OUT_ORDER_STATUS_ONE +"'                                                               "
            + "    and (t.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)              "
            + "    and (t.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)     "
            + "    and (t4.short_name LIKE CONCAT('%', #{p1.owner_name}, '%') or t4.name LIKE CONCAT('%',#{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')                                         "
            + "    and (t10.name LIKE CONCAT('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')               "
            + "    and (concat(ifnull(t10.code, ''), '_', ifnull(t8.code, '')) LIKE CONCAT('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')                 "
            + "    and (concat(t3.name, '_', t3.short_name) LIKE CONCAT('%', #{p1.customer_name}, '%') or #{p1.customer_name} is null or #{p1.customer_name} = '')               "
            + "      and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                              "
            + "    <if test='p1.status_list != null and p1.status_list.length != 0'>                                   "
            + "    and t.status in                                                                                     "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>"
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>  "
            + "   </script>   ")
    int exportCount(@Param("p1") BOutOrderVo param);

    /**
     * 导出
     * @param param
     * @return
     */
    @Select("  <script>  "
            + "      SELECT                                                                                                                                 "
            + "                @row_num:= @row_num+ 1 as no,                                                                                                "
            + "             t.contract_no,                                                                                                                  "
            + "             t.order_no,                                                                                                                     "
            + "             ifnull(t11.schedule_count, 0) schedule_count,                                                                                   "
            + "             t9.label as status_name ,                                                                                                       "
            + "			    t7.idx,                                                                                                                         "
            + "			    t7.amount,                                                                                                                      "
            + "             t5.label as bill_type_name ,                                                                                                    "
            + "             t.contract_dt,                                                                                                                  "
            + "             t.contract_expire_dt,                                                                                                           "
            + "             ifnull(t3.short_name,t3.name) as client_name,                                                                                   "
            + "             ifnull(t4.short_name,t4.name) as owner_name,                                                                                    "
            + "             t8.name as goods_name,                                                                                                          "
            + "             t8.pm ,                                                                                                                         "
            + "             t8.spec ,                                                                                                                       "
            + "			    t7.num,                                                                                                                         "
            + "             t1.name as c_name,                                                                                                              "
            + "             t.c_time,                                                                                                                       "
            + "             t2.name as u_name,                                                                                                              "
            + "             t.u_time,                                                                                                                       "
            + "             t12.out_actual_count                                                                                                            "
            + "        FROM                                                                                                                                 "
            + "   	       b_out_order t                                                                                                                    "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                    "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                    "
            + "   LEFT JOIN m_customer t3 ON t.client_id = t3.id                                                                                            "
            + "   LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                                "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                                 "
            + "              where tab2.code = '" + DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE + "')t5 on t5.dict_value = t.bill_type                       "
            + "   inner join (                                                                                                                              "
            + "   			  select row_number() over(partition by t.order_id                                                                              "
            + "   			                               order by t.sku_code asc) as idx,                                                                 "
            + "   				t.*                                                                                                                         "
            + "   			from b_out_order_goods t                                                                                                        "
            + "     ) t7 on t7.order_id = t.id                                                                                                              "
            + "      left join m_goods_spec t8 on t8.id = t7.sku_id                                                                                         "
            + "   LEFT JOIN v_dict_info AS t9 ON t9.code = '" + DictConstant.DICT_B_OUT_ORDER_STATUS + "' and t9.dict_value = t.status                      "
            + "  left join m_goods t10 on t8.goods_id = t10.id                                                                                              "
            + "  left join (                                                                                                                                "
            + "		SELECT                                                                                                                                  "
            + "			t1.id,                                                                                                                              "
            + "			count( t3.id ) schedule_count                                                                                                       "
            + "		FROM                                                                                                                                    "
            + "			b_out_order t1                                                                                                                      "
            + "			INNER JOIN b_order t2 ON t1.id = t2.serial_id                                                                                       "
            + "			AND t2.serial_type = 'b_out_order'                                                                                                  "
            + "			INNER JOIN b_schedule t3 ON t2.id = t3.order_id                                                                                     "
            + "		GROUP BY                                                                                                                                "
            + "			t1.id                                                                                                                               "
            + ") t11  on t11.id = t.id                                                                                                                      "
            + "  left join (                                                                                                                                "
            + "      select                                                                                                                                 "
            + "          sum(t1.actual_count) out_actual_count,                                                                                             "
            + "          t2.order_id,                                                                                                                       "
            + "          t1.sku_id                                                                                                                          "
            + "      from b_out t1                                                                                                                          "
            + "      left join b_out_extra t2 ON t1.id = t2.out_id and t2.order_type = 'b_out_order'                                                        "
            + "      where t1.status = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                                                                      "
            + "      AND t1.type = '" + DictConstant.DICT_B_OUT_TYPE_XS + "'                                                                                "
            + "      group by t2.order_id, t1.sku_id) t12 ON t12.sku_id = t7.sku_id and t12.order_id = t7.order_id                                          "
            + "   ,(select @row_num:=0) t6                                                                                                                  "
            + "  where true                                                                                                                       "
            + "    and t.status != '"+ DictConstant.DICT_B_OUT_ORDER_STATUS_ONE +"'                                                               "
            + "    and (t.order_no like CONCAT ('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)              "
            + "    and (t.contract_no like CONCAT ('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)     "
            + "    and (t4.short_name LIKE CONCAT('%', #{p1.owner_name}, '%') or t4.name LIKE CONCAT('%',#{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')                                         "
            + "    and (t10.name LIKE CONCAT('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')               "
            + "    and (concat(ifnull(t10.code, ''), '_', ifnull(t8.code, '')) LIKE CONCAT('%', #{p1.goods_code}, '%') or #{p1.goods_code} is null or #{p1.goods_code} = '')                 "
            + "    and (concat(t3.name, '_', t3.short_name) LIKE CONCAT('%', #{p1.customer_name}, '%') or #{p1.customer_name} is null or #{p1.customer_name} = '')               "
            + "      and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                              "
            + "    <if test='p1.status_list != null and p1.status_list.length != 0'>                                   "
            + "    and t.status in                                                                                     "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>"
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>  "
            + "    <if test='p1.ids != null and p1.ids.size != 0'>                                                     "
            + "    and concat(t.id, '_', t7.sku_id) in                                                                                         "
            + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>        "
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>                                                                                                "
            + "   </script>   ")
    List<BOutOrderExportVo> selectExportList(@Param("p1") BOutOrderVo param);

    /**
     * 销售合同数量查询
     * @param param
     * @return
     */
    @Select({"<script>"
            + " SELECT count(1) from (                                                                                 "
            + "       select  t.id                                                                                     "
            +  "  FROM                                                                                                 "
            +  "    b_out_order t                                                                                      "
            +  " LEFT JOIN b_out_order_goods t1 ON t.id = t1.order_id                                                  "
            +  " LEFT JOIN m_goods_spec t2 ON t2.id = t1.sku_id                                                        "
            +  " LEFT JOIN m_goods t3 ON t3.id = t2.goods_id                                                           "
            +  " LEFT JOIN m_goods_spec_prop t11 on t11.id = t2.prop_id                                                "
            +  " where t.status != '" + DictConstant.DICT_B_OUT_ORDER_STATUS_ONE + "'                                  "
            +  " AND (t.owner_id = #{p1.owner_id} OR #{p1.owner_id} IS NULL)                                           "
            +  " AND (t3.name LIKE CONCAT('%', #{p1.goods_name}, '%') OR #{p1.goods_name} IS NULL OR #{p1.goods_name} = '')"
            +  " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')"
            +  "<if test='p1.showTips'>                                                                                "
            +  "   and t3.`code` != 'zlsd-0100511'                                                                     "
            +  "</if>                                                                                                  "
            +  " AND (t11.name LIKE CONCAT('%', #{p1.goods_prop}, '%') OR  #{p1.goods_prop} IS NULL OR  #{p1.goods_prop} = '')"
            +  " AND (t.client_id = #{p1.supplier_id} or #{p1.supplier_id} is null)                                    "
            +  " GROUP BY t3.id, t.owner_id, t.client_id) tt1                                                          "
            +  "</script>"
    })
    int selectExportNum(@Param("p1") BContractReportVo param);
}
