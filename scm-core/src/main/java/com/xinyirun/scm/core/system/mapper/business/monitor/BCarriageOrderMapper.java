package com.xinyirun.scm.core.system.mapper.business.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.monitor.BCarriageOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.carriage.BCarriageOrderExportVo;
import com.xinyirun.scm.bean.system.vo.business.carriage.BCarriageOrderVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-04
 */
@Repository
public interface BCarriageOrderMapper extends BaseMapper<BCarriageOrderEntity> {

    String comm_select = ""
            + "  SELECT                                                                                                 "
            + "    t.id,                                                                                                "
            + "  	t.order_no,                                                                                         "
            + "  	t.carriage_contract_code,                                                                           "
            + "  	t.sales_contract_code,                                                                              "
            + "  	t.`status`,                                                                                         "
            + "  	t.type_name,                                                                                        "
            + "  	t.company_name,                                                                                     "
            + "     t5.id company_id,                                                                                   "
            + "     t5.code company_code,                                                                               "
            + "  	t.company_credit_no,                                                                                "
            + "     t.org_name,                                                                                         "
            + "  	t.org_credit_no,                                                                                    "
            + "  	t.remark,                                                                                           "
            + "  	t.sign_dt,                                                                                          "
            + "  	t.deadline_dt,                                                                                      "
            + "  	t.total_amount,                                                                                     "
//            + "  	t4.sku_code,                                                                                        "
//            + "  	t4.sku_name,                                                                                        "
//            + "  	t4.unit_name,                                                                                       "
//            + "  	t4.num,                                                                                             "
//            + "  	t4.amount,                                                                                          "
            + "     t.u_time,                                                                                           "
            + "     t.transport_type_name,                                                                              "
            + "     t.origin_place,                                                                                     "
            + "     t.destination_place,                                                                                "
            + "     t.haul_distance,                                                                                    "
            + "     t.pay_type,                                                                                         "
            + "     t.price,                                                                                            "
            + "     t.num,                                                                                              "
            + "     t.transport_amount,                                                                                 "
            + "     t.transport_amount_tax                                                                              "
//            + "  	t4.no                                                                                               "
            + "  FROM b_carriage_order t                                                                                "
            + "  LEFT JOIN m_customer t5 ON t.company_name = t5.name                                                    "
//            + "  LEFT JOIN (                                                                                            "
//            + "    SELECT                                                                                               "
//            + "  	  t1.price,                                                                                         "
//            + "  		t1.num,                                                                                         "
//            + "  		t1.amount,                                                                                      "
//            + "  		@row_num:= @row_num+ 1 as `no`,                                                                 "
//            + "  		t2.`code` sku_code,                                                                             "
//            + "  		t2.`name` sku_name,                                                                             "
//            + "  		t1.unit_name,                                                                                   "
//            + "  		t1.order_id                                                                                     "
//            + "  	FROM b_carriage_order_goods t1                                                                      "
//            + "  	LEFT JOIN m_goods_spec t2 ON t1.sku_id = t2.id                                                      "
//            + "  	,(select @row_num:=0) t3                                                                            "
//            + "  ) t4 ON t.id = t4.order_id                                                                             "
            + "  WHERE TRUE                                                                                             ";

    @Select(""
            + comm_select
            + "  AND (t.order_no like concat ('%', #{p1.order_no}, '%') or #{p1.order_no} is null or #{p1.order_no} = '')   "
            + "  AND (t.carriage_contract_code like concat ('%', #{p1.carriage_contract_code}, '%') or #{p1.carriage_contract_code} is null or #{p1.carriage_contract_code} = '')   "
            + "  AND (t.status like concat ('%', #{p1.status}, '%') or #{p1.status} is null or #{p1.status} = '')       "
            + "  AND (t.company_name like concat ('%', #{p1.company_name}, '%') or #{p1.company_name} is null or #{p1.company_name} = '')  "
            + "  AND (t.origin_place like concat ('%', #{p1.origin_place}, '%') or #{p1.origin_place} is null or #{p1.origin_place} = '')  "
            + "  AND (t.destination_place like concat ('%', #{p1.destination_place}, '%') or #{p1.destination_place} is null or #{p1.destination_place} = '')  "
            + "  AND (t.transport_type_name like concat ('%', #{p1.transport_type_name}, '%') or #{p1.transport_type_name} is null or #{p1.transport_type_name} = '')  "
            + "  AND (DATE_FORMAT(t.u_time, '%Y-%m-%d' ) >= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')  "
//            + "  AND (concat(t4.sku_code, '_', t4.sku_name) like concat ('%', #{p1.sku_name}, '%') or #{p1.sku_name} is null or #{p1.sku_name} = '')   "

    )
    IPage<BCarriageOrderVo> selectPageList(@Param("p1") BCarriageOrderVo param, Page<BCarriageOrderVo> pageCondition);

    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_carriage_order t                                                                                "
            + "  WHERE TRUE                                                                                             "
            + "  AND (t.order_no like concat ('%', #{p1.order_no}, '%') or #{p1.order_no} is null or #{p1.order_no} = '')   "
            + "  AND (t.carriage_contract_code like concat ('%', #{p1.carriage_contract_code}, '%') or #{p1.carriage_contract_code} is null or #{p1.carriage_contract_code} = '')   "
            + "  AND (t.status like concat ('%', #{p1.status}, '%') or #{p1.status} is null or #{p1.status} = '')       "
            + "  AND (t.company_name like concat ('%', #{p1.company_name}, '%') or #{p1.company_name} is null or #{p1.company_name} = '')  "
            + "  AND (t.origin_place like concat ('%', #{p1.origin_place}, '%') or #{p1.origin_place} is null or #{p1.origin_place} = '')  "
            + "  AND (t.destination_place like concat ('%', #{p1.destination_place}, '%') or #{p1.destination_place} is null or #{p1.destination_place} = '')  "
            + "  AND (t.transport_type_name like concat ('%', #{p1.transport_type_name}, '%') or #{p1.transport_type_name} is null or #{p1.transport_type_name} = '')  "
            + "  AND (DATE_FORMAT(t.u_time, '%Y-%m-%d' ) >= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')  "
    )
    int selectExportNum(@Param("p1") BCarriageOrderVo param);

    @Select("<script>"
            + "  SELECT                                                                                                 "
            + "  	t.order_no,                                                                                         "
            + "  	t.carriage_contract_code,                                                                           "
            + "  	t.sales_contract_code,                                                                              "
            + "  	t.`status`,                                                                                         "
            + "  	t.type_name,                                                                                        "
            + "  	t.company_name,                                                                                     "
            + "  	t.company_credit_no,                                                                                "
            + "     t.org_name,                                                                                         "
            + "  	t.org_credit_no,                                                                                    "
            + "  	t.remark,                                                                                           "
            + "     t.u_time,                                                                                           "
            + "     t.transport_type_name,                                                                              "
            + "  	t.sign_dt,                                                                                          "
            + "  	t.deadline_dt,                                                                                      "
            + "     t.origin_place,                                                                                     "
            + "     t.destination_place,                                                                                "
            + "     t.price,                                                                                            "
            + "     t.num,                                                                                              "
            + "     @row_num:= @row_num+ 1 as no,                                                                       "
            + "     t.transport_amount                                                                                  "
            + "  FROM b_carriage_order t                                                                                "
            + "   ,(select @row_num:=0) t1                                                                              "
            + "  WHERE TRUE                                                                                             "
            + "  AND (t.order_no like concat ('%', #{p1.order_no}, '%') or #{p1.order_no} is null or #{p1.order_no} = '')   "
            + "  AND (t.carriage_contract_code like concat ('%', #{p1.carriage_contract_code}, '%') or #{p1.carriage_contract_code} is null or #{p1.carriage_contract_code} = '')   "
            + "  AND (t.status like concat ('%', #{p1.status}, '%') or #{p1.status} is null or #{p1.status} = '')       "
            + "  AND (t.company_name like concat ('%', #{p1.company_name}, '%') or #{p1.company_name} is null or #{p1.company_name} = '')  "
            + "  AND (t.origin_place like concat ('%', #{p1.origin_place}, '%') or #{p1.origin_place} is null or #{p1.origin_place} = '')  "
            + "  AND (t.destination_place like concat ('%', #{p1.destination_place}, '%') or #{p1.destination_place} is null or #{p1.destination_place} = '')  "
            + "  AND (t.transport_type_name like concat ('%', #{p1.transport_type_name}, '%') or #{p1.transport_type_name} is null or #{p1.transport_type_name} = '')  "
            + "  AND (DATE_FORMAT(t.u_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')  "
            + "  <if test='p1.ids != null and p1.ids.length != 0'>                                                      "
            + "  AND t.id in                                                                                            "
            + "  <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>               "
            + "         #{item,jdbcType=INTEGER}                                                                        "
            + "  </foreach>                                                                                             "
            + "  </if>                                                                                                  "
            + " </script>                                                                                               "
    )
    List<BCarriageOrderExportVo> exportList(@Param("p1") BCarriageOrderVo param);

    /**
     * 查询
     * @param orderNo 订单编号
     * @param id 订单id
     * @return
     */
    @Select(""
            + " SELECT                                                                                                  "
            + "   id                                                                                                    "
            + " FROM b_carriage_order                                                                                   "
            + " WHERE order_no = #{p1}                                                                                  "
            + " AND (id != #{p2} OR #{p2} IS NULL)                                                                       "
    )
    List<Integer> select2Validation(@Param("p1") String orderNo,@Param("p2") Integer id);

    /**
     * 根据 id 查询
     * @param id 订单 id
     * @return
     */
    @Select(""
            + comm_select
            + "  AND t.id = #{p1}"
    )
    BCarriageOrderVo selectVoById(@Param("p1") Integer id);
}
