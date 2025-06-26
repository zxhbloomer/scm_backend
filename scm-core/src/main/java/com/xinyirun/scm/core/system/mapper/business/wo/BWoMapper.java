package com.xinyirun.scm.core.system.mapper.business.wo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoEntity;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoVo;
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
 * Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-29
 */
@Repository
public interface BWoMapper extends BaseMapper<BWoEntity> {

    String comm_select = ""
            + " SELECT                                                                                                  "
            + "   t.code,                                                                                               "
            + "   t.delivery_order_code,                                                                                "
            + "   t.json_material_list,                                                                                 "
            + "   t.json_product_list,                                                                                  "
            + "   t.status,                                                                                             "
            + "   t1.label status_name,                                                                                 "
            + "   t.id,                                                                                                 "
            + "   t.c_time,                                                                                             "
            + "   t2.name c_name,                                                                                       "
            + "   t3.name u_name,                                                                                       "
            + "   t4.name e_name,                                                                                       "
            + "   t.e_time,                                                                                             "
            + "   ifnull(t5.short_name, t5.name) owner_name,                                                            "
            + "   t6.remark,                                                                                            "
            + "   t9.has_product_num,                                                                                   "
            + "   t10.wc_warehouse_name,                                                                                "
            + "   t11.name router_name,                                                                                 "
            + "   t11.code router_code,                                                                                 "
            + "   t.u_time                                                                                              "
            + " FROM b_wo t                                                                                             "
            + " LEFT JOIN s_dict_data t1 ON t.status = t1.dict_value AND t1.code = '"+ DictConstant.DICT_B_WO_STATUS + "'"
            + " LEFT JOIN m_staff t2 ON t2.id = t.c_id                                                                  "
            + " LEFT JOIN m_staff t3 ON t3.id = t.u_id                                                                  "
            + " LEFT JOIN m_staff t4 ON t4.id = t.e_id                                                                  "
            + " LEFT JOIN m_owner t5 ON t5.id = t.owner_id                                                              "
            + " LEFT JOIN m_cancel t6 ON t6.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_WO +"' AND t6.serial_id = t.id"
            + " LEFT JOIN b_wo_router t11 ON t.router_id = t11.id                                                       "
            +  " LEFT JOIN (SELECT sum(t8.wo_qty) has_product_num, t7.delivery_order_detail_id                          "
            +  "            FROM b_wo t7                                                                                "
            +  "            LEFT JOIN b_wo_product t8 ON t7.id = t8.wo_id and t8.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            +  "            WHERE t7.status != '"+ DictConstant.DICT_B_WO_STATUS_5 +"'                                  "
            +  "            group by t7.delivery_order_detail_id) t9 ON t9.delivery_order_detail_id = t.delivery_order_detail_id"
            +  " LEFT JOIN (SELECT t10.name wc_warehouse_name, t8.warehouse_id, t7.id"
            +  "            FROM b_wo t7                                                                                "
            +  "            LEFT JOIN b_wo_product t8 ON t7.id = t8.wo_id and t8.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            +  "            LEFT JOIN m_warehouse t10 ON t10.id = t8.warehouse_id group by t7.id                                       "
            +  "            ) t10 ON t10.id = t.id                                  "
            +  "left join b_pp t12 on t12.id = t.pp_id                                            "
            + " WHERE TRUE                                                                                              "
            ;


    @Select("<script>"
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + comm_select
            + " AND (t.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')                 "
            + " AND (t.delivery_order_code like concat('%', #{p1.delivery_order_code}, '%') or #{p1.delivery_order_code} is null or #{p1.delivery_order_code} = '') "
            + " AND (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                              "
            + " AND (t10.warehouse_id = #{p1.wc_warehouse_id} or #{p1.wc_warehouse_id} is null)                         "
            + " AND (concat(ifnull(t5.name, ''), '_', ifnull(t5.short_name, '')) like concat('%', #{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')"
            + " AND (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)"
            + " AND (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)  "
            + " AND (date_format(t.c_time, '%Y-%m-%d') &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')    "
            + " AND (concat(t11.name, t11.code) like concat('%', #{p1.router_code}, '%') or #{p1.router_code} is null or #{p1.router_code} = '')    "
            + " AND (t12.code = #{p1.pp_code} or #{p1.pp_code} is null or #{p1.pp_code} = '')    "
            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_product sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id                "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.product_goods_name}, '%') or #{p1.product_goods_name} is null or #{p1.product_goods_name} = '')"
            + "     and sub2.wo_id = t.id)                                                                              "
            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_material sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id               "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.material_goods_name}, '%') or #{p1.material_goods_name} is null or #{p1.material_goods_name} = '')"
            + "     and sub2.wo_id = t.id)                                                                              "

            // 多选状态
            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                                                        "
            + "    and t.status in                                                                                                                                                                        "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                                                     "
            + "         #{item}                                                                                                                                                                           "
            + "        </foreach>                                                                                                                                                                         "
            + "   </if>                                                                                                                                                                                   "

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
            + "				AND subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_WO+"'                                                                                        "
            + "				AND subt1.STATUS = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                                                                                       "
            + "				GROUP BY subt1.serial_id, subt1.serial_type                                                                                                                         "
            + "  )                                                                                                                                                                              "
            + "      </if>                                                                                                                                                                      "

            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                   "
            + "      and exists (                                                                                       "
            + "             select 1                                                                                    "
            + "               from b_already_do subt1                                                                   "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_WO + "'                       "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                     "
            + "                and serial_id = t.id                                                                     "
            + "				GROUP BY subt1.serial_id, subt1.serial_type                                                 "
            + "       )                                                                                                 "
            + "      </if>                                                                                              "

            + "     ${p1.params.dataScopeAnnotation}                                                                    "
            + " </script>"
    )
    @Results({
            @Result(property = "material_list", column = "json_material_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "product_list", column = "json_product_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    IPage<BWoVo> selectPageList(@Param("p1") BWoVo param, Page<BWoVo> pageCondition);

    /**
     * 根据 ID 查询分页内容, 新增,更新完后使用
     * @param id
     * @return
     */
    @Select(""
            + comm_select
            + " AND t.id = #{id}"
    )
    @Results({
            @Result(property = "material_list", column = "json_material_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "product_list", column = "json_product_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    BWoVo selectById(Integer id);

    @Select(""
            + "   SELECT                                                                   "
            + "     t.`code`,                                                                                           "
            + "   	t.id,                                                                                               "
//            + "   	t.json_product_list,                                                                                "
//            + "   	t.json_material_list,                                                                               "
            + "   	t.dbversion,                                                                                        "
            + "   	t1.`code` delivery_order_code,                                                                      "
            + "   	t1.id delivery_order_id,                                                                            "
            + "   	t1.type_name delivery_order_type_name,                                                              "
            + "   	t7.name as owner_name,                                                                              "
            + "   	t2.warehouse_name wc_warehouse_name,                                                                "
            + "   	t2.commodity_spec_code delivery_sku_code,                                                           "
            + "   	t2.commodity_name delivery_sku_name,                                                                "
            + "     t3.pm delivery_pm,                                                                                  "
            + "   	t2.commodity_spec delivery_spec,                                                                    "
            + "   	t2.type_gauge delivery_type_gauge,                                                                  "
            + "   	t2.qty delivery_qty,                                                                                "
            + "   	ifnull(t6.has_product_num, 0) + ifnull(t8.has_product_num, 0) has_product_num,                      "
            + "   	t2.unit_name delivery_unit_name,                                                                    "
            + "   	t.owner_id,                                                                                         "
            + "   	t.router_id,                                                                                        "
            + "   	t2.id delivery_order_detail_id,                                                                     "
            + "   	t11.name router_name,                                                                               "
            + "   	t11.code router_code,                                                                               "
            + "   	t.c_time                                                                                            "
            + "   FROM b_wo t                                                                                           "
            + "   LEFT JOIN b_release_order t1 ON t.delivery_order_id = t1.id                                           "
            + "   LEFT JOIN b_release_order_detail t2 ON t2.release_order_id = t1.id                                    "
            + "   LEFT JOIN m_goods_spec t3 ON t2.commodity_spec_code = t3.`code`                                       "
            + "   left join (select t4.delivery_order_detail_id, sum(t5.wo_qty) has_product_num from b_wo t4 left join b_wo_product t5 on t4.id = t5.wo_id where (t4.status = '"+ DictConstant.DICT_B_WO_STATUS_3 +"' or t4.status = '"+ DictConstant.DICT_B_WO_STATUS_2 +"')  and t5.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"' group by t4.delivery_order_detail_id) t6"
            + "   ON t6.delivery_order_detail_id = t2.id"
            + "   left join (select t4.delivery_order_detail_id, sum(t5.wo_qty) has_product_num from b_rt_wo t4 left join b_rt_wo_product t5 on t4.id = t5.wo_id where (t4.status = '"+ DictConstant.DICT_B_WO_STATUS_3 +"' or  t4.status = '"+ DictConstant.DICT_B_WO_STATUS_2 +"') and t5.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"' group by t4.delivery_order_detail_id) t8"
            + "   ON t8.delivery_order_detail_id = t2.id"
            + "   left join m_owner t7 ON t7.id = t.owner_id                                                            "
            + " LEFT JOIN b_wo_router t11 ON t.router_id = t11.id                                                       "
            + "   WHERE t.id = #{id}                                                                                    "
            + "   and (t.delivery_order_detail_id = t2.id or t.delivery_order_detail_id is null)                        "
    )
//    @Results({
//            @Result(property = "material_list", column = "json_material_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
//            @Result(property = "product_list", column = "json_product_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
//    })
    BWoVo getDetail(Integer id);

    @Select("<script>"
            +  " ${p1.params.dataScopeAnnotation_with}                                                                  "
            +  "  SELECT                                                                                                "
            +  "    t.code,                                                                                             "
            +  "    @row_num:= @row_num+ 1 as no,                                                                      "
            +  "    t.delivery_order_code,                                                                              "
            +  "    t10.wc_warehouse_name,                                                                              "
            +  "    t1.label status_name,                                                                               "
            +  "  	t.json_product_list,                                                                                "
            +  "  	t.json_material_list,                                                                               "
            +  "    t6.remark,                                                                                          "
            +  "    t.c_time,                                                                                           "
            +  "    t2.name c_name,                                                                                     "
            +  "    t3.name u_name,                                                                                     "
            +  "    t4.name e_name,                                                                                     "
            +  "    t.e_time,                                                                                           "
            +  "    t.u_time,                                                                                           "
            + "   t11.name router_name,                                                                                  "
            + "   t11.code router_code                                                                                  "
            +  "  FROM b_wo t                                                                                           "
            +  " LEFT JOIN s_dict_data t1 ON t.status = t1.dict_value AND t1.code = '"+ DictConstant.DICT_B_WO_STATUS + "'"
            + " LEFT JOIN m_staff t2 ON t2.id = t.c_id                                                                  "
            + " LEFT JOIN m_staff t3 ON t3.id = t.u_id                                                                  "
            + " LEFT JOIN m_staff t4 ON t4.id = t.e_id                                                                  "
            +  " LEFT JOIN m_cancel t6 ON t6.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_WO +"' AND t6.serial_id = t.id"
            +  " LEFT JOIN (SELECT t10.name wc_warehouse_name, t8.warehouse_id, t7.id                                   "
            +  "            FROM b_wo t7                                                                                "
            +  "            LEFT JOIN b_wo_product t8 ON t7.id = t8.wo_id and t8.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            +  "            LEFT JOIN m_warehouse t10 ON t10.id = t8.warehouse_id group by t7.id                        "
            +  "            ) t10 ON t10.id = t.id                                                                      "
            +  " LEFT JOIN b_wo_router t11 ON t.router_id = t11.id                                                      "
            +  "left join b_pp t13 on t13.id = t.pp_id                                            "
            +  "   ,(select @row_num:=0) t12                                                                            "
            +  " where true                                                                                             "
            + " AND (t.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')                 "
            + " AND (t.delivery_order_code like concat('%', #{p1.delivery_order_code}, '%') or #{p1.delivery_order_code} is null or #{p1.delivery_order_code} = '') "
            + " AND (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                              "
            + " AND (t10.warehouse_id = #{p1.wc_warehouse_id} or #{p1.wc_warehouse_id} is null)                         "
            + "   and (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)"
            + "   and (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)  "
            + " AND (date_format(t.c_time, '%Y-%m-%d') &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')    "
            + " AND (concat(t11.name, t11.code) like concat('%', #{p1.router_code}, '%') or #{p1.router_code} is null or #{p1.router_code} = '')    "
            + " AND (t13.code = #{p1.pp_code} or #{p1.pp_code} is null or #{p1.pp_code} = '')    "

            // 多选状态
            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                                                        "
            + "    and t.status in                                                                                                                                                                        "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                                                     "
            + "         #{item}                                                                                                                                                                           "
            + "        </foreach>                                                                                                                                                                         "
            + "   </if>                                                                                                                                                                                   "

            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_product sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id                "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.product_goods_name}, '%') or #{p1.product_goods_name} is null or #{p1.product_goods_name} = '')"
            + "     and sub2.wo_id = t.id)                                                                              "
            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_material sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id               "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.material_goods_name}, '%') or #{p1.material_goods_name} is null or #{p1.material_goods_name} = '')"
            + "     and sub2.wo_id = t.id)                                                                              "
            + "   <if test='p1.ids != null and p1.ids.length != 0' >                                                    "
            + "    and t.id in                                                                                          "
            + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>         "
            + "         #{item}                                                                                         "
            + "        </foreach>                                                                                       "
            + "   </if>                                                                                                 "
            + "     ${p1.params.dataScopeAnnotation}                                                                    "
            + "     </script>                                                                                           "
    )
    List<BWoVo> exportList(@Param("p1") BWoVo param);

    /**
     * 导出条数查询
     * @param param
     * @return
     */
    @Select("<script>"
            +  " ${p1.params.dataScopeAnnotation_with}                                                                  "
            +  "  SELECT                                                                                                "
            +  "    count(1)                                                                                            "
            +  "  FROM b_wo t                                                                                           "
            +  " LEFT JOIN (SELECT t10.name wc_warehouse_name, t8.warehouse_id, t7.id                                   "
            +  "            FROM b_wo t7                                                                                "
            +  "            LEFT JOIN b_wo_product t8 ON t7.id = t8.wo_id and t8.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            +  "            LEFT JOIN m_warehouse t10 ON t10.id = t8.warehouse_id group by t7.id                        "
            +  "            ) t10 ON t10.id = t.id                                                                      "
            +  " LEFT JOIN b_wo_router t11 ON t.router_id = t11.id                                                      "
            +  "left join b_pp t13 on t13.id = t.pp_id                                            "
            +  " where true                                                                                             "
            + " AND (t.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')                 "
            + " AND (t.delivery_order_code like concat('%', #{p1.delivery_order_code}, '%') or #{p1.delivery_order_code} is null or #{p1.delivery_order_code} = '') "
            + " AND (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                              "
            + " AND (t10.warehouse_id = #{p1.wc_warehouse_id} or #{p1.wc_warehouse_id} is null)                         "
            + "   and (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)"
            + "   and (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)  "
            + " AND (date_format(t.c_time, '%Y-%m-%d') &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')    "
            + " AND (concat(t11.name, t11.code) like concat('%', #{p1.router_code}, '%') or #{p1.router_code} is null or #{p1.router_code} = '')    "
            + " AND (t13.code = #{p1.pp_code} or #{p1.pp_code} is null or #{p1.pp_code} = '')    "

            // 多选状态
            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                                                        "
            + "    and t.status in                                                                                                                                                                        "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                                                     "
            + "         #{item}                                                                                                                                                                           "
            + "        </foreach>                                                                                                                                                                         "
            + "   </if>                                                                                                                                                                                   "

            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_product sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id                "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.product_goods_name}, '%') or #{p1.product_goods_name} is null or #{p1.product_goods_name} = '')"
            + "     and sub2.wo_id = t.id)                                                                              "
            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_material sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id               "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.material_goods_name}, '%') or #{p1.material_goods_name} is null or #{p1.material_goods_name} = '')"
            + "     and sub2.wo_id = t.id)                                                                              "
            + "     ${p1.params.dataScopeAnnotation}                                                                    "
            + "     </script>                                                                                           "
    )
    Long selectExportCount(@Param("p1") BWoVo param);

    /**
     * 查询待办数量
     * @param param
     * @return
     */
    @Select("<script>"
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + " SELECT                                                                                                  "
            + "   count(t.id)                                                                                               "
            + "  FROM                                                                                                   "
            + "	  b_todo subt1                                                                                     "
            + "	  INNER JOIN v_permission_operation_all subt2 ON subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}  "
            + "	  AND subt1.position_id = subt2.position_id                                                        "
            + "	  AND subt2.operation_perms = subt1.perms                                                          "
            + "	  AND subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_WO + "'                               "
            + "	  AND subt1.STATUS = '" + DictConstant.DICT_B_TODO_STATUS_TODO + "'                                "
            + "	  INNER JOIN b_wo t ON t.id = subt1.serial_id                                                      "
            + " LEFT JOIN m_owner t5 ON t5.id = t.owner_id                                                              "
            + " LEFT JOIN (SELECT t10.name wc_warehouse_name, t8.warehouse_id, t7.id                                    "
            + "            FROM b_wo t7                                                                                 "
            + "            LEFT JOIN b_wo_product t8 ON t7.id = t8.wo_id and t8.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            + "            LEFT JOIN m_warehouse t10 ON t10.id = t8.warehouse_id group by t7.id                                       "
            + "            ) t10 ON t10.id = t.id                                  "
            +  " LEFT JOIN b_wo_router t11 ON t.router_id = t11.id                                                      "
            +  "left join b_pp t13 on t13.id = t.pp_id                                            "
            + " WHERE TRUE                                                                                              "
            + " AND (t.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')                 "
            + " AND (t.delivery_order_code like concat('%', #{p1.delivery_order_code}, '%') or #{p1.delivery_order_code} is null or #{p1.delivery_order_code} = '') "
            + " AND (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                              "
            + " AND (t10.warehouse_id = #{p1.wc_warehouse_id} or #{p1.wc_warehouse_id} is null)                              "
            + " AND (concat(ifnull(t5.name, ''), '_', ifnull(t5.short_name, '')) like concat('%', #{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')                              "
            + "   and (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)"
            + "   and (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)  "
            + " AND (date_format(t.c_time, '%Y-%m-%d') &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')    "
            + " AND (concat(t11.name, t11.code) like concat('%', #{p1.router_code}, '%') or #{p1.router_code} is null or #{p1.router_code} = '')    "
            + " AND (t13.code = #{p1.pp_code} or #{p1.pp_code} is null or #{p1.pp_code} = '')    "

            // 多选状态
            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                                                        "
            + "    and t.status in                                                                                                                                                                        "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                                                     "
            + "         #{item}                                                                                                                                                                           "
            + "        </foreach>                                                                                                                                                                         "
            + "   </if>                                                                                                                                                                                   "

            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_product sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id                "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.product_goods_name}, '%') or #{p1.product_goods_name} is null or #{p1.product_goods_name} = '')"
            + "     and sub2.wo_id = t.id)                                                                              "
            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_material sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id               "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.material_goods_name}, '%') or #{p1.material_goods_name} is null or #{p1.material_goods_name} = '')"
            + "     and sub2.wo_id = t.id)                                                                              "
            + "     ${p1.params.dataScopeAnnotation}                                                                                                 "
            + "     </script>                                                                                           "
    )
    Integer selectTodoCount(@Param("p1") BWoVo param);

    /**
     * 查询 合计
     * @param param
     * @return
     */
    @Select("<script>"
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            +  "  SELECT                                                                                                                            "
            +  "  	SUM( t2.wo_qty ) material_actual,                                                                                                               "
            +  "  	SUM( t3.wo_qty ) product_actual                                                                                                                "
            +  "  FROM                                                                                                                              "
            +  "  	b_wo t                                                                                                                          "
            +  "  LEFT JOIN ( SELECT SUM( tt1.wo_qty ) wo_qty, tt1.wo_id FROM b_wo_material tt1 GROUP BY tt1.wo_id ) t2 ON t2.wo_id = t.id          "
            +  "  LEFT JOIN ( SELECT SUM( tt2.wo_qty ) wo_qty, tt2.wo_id FROM b_wo_product tt2 GROUP BY tt2.wo_id ) t3 ON t3.wo_id = t.id           "
            + " LEFT JOIN b_wo_router t11 ON t.router_id = t11.id                                                       "
            + " LEFT JOIN m_owner t5 ON t5.id = t.owner_id                                                              "
            +  " LEFT JOIN (SELECT t10.name wc_warehouse_name, t8.warehouse_id, t7.id"
            +  "            FROM b_wo t7                                                                                "
            +  "            LEFT JOIN b_wo_product t8 ON t7.id = t8.wo_id and t8.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            +  "            LEFT JOIN m_warehouse t10 ON t10.id = t8.warehouse_id group by t7.id                                       "
            +  "            ) t10 ON t10.id = t.id                                  "
            +  "left join b_pp t12 on t12.id = t.pp_id                                            "
            + " where (t.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')                 "
            + " AND t.status != '"+ DictConstant.DICT_B_WO_STATUS_5 +"' "
            + " AND (t.delivery_order_code like concat('%', #{p1.delivery_order_code}, '%') or #{p1.delivery_order_code} is null or #{p1.delivery_order_code} = '') "
            + " AND (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                              "
            + " AND (t10.warehouse_id = #{p1.wc_warehouse_id} or #{p1.wc_warehouse_id} is null)                         "
            + " AND (concat(ifnull(t5.name, ''), '_', ifnull(t5.short_name, '')) like concat('%', #{p1.owner_name}, '%') or #{p1.owner_name} is null or #{p1.owner_name} = '')"
            + " AND (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)"
            + " AND (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)  "
            + " AND (date_format(t.c_time, '%Y-%m-%d') &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')    "
            + " AND (concat(t11.name, t11.code) like concat('%', #{p1.router_code}, '%') or #{p1.router_code} is null or #{p1.router_code} = '')    "
            + " AND (t12.code = #{p1.pp_code} or #{p1.pp_code} is null or #{p1.pp_code} = '')    "

            // 多选状态
            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                                                        "
            + "    and t.status in                                                                                                                                                                        "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                                                     "
            + "         #{item}                                                                                                                                                                           "
            + "        </foreach>                                                                                                                                                                         "
            + "   </if>                                                                                                                                                                                   "

            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_product sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id                "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.product_goods_name}, '%') or #{p1.product_goods_name} is null or #{p1.product_goods_name} = '')"
            + "     and sub2.wo_id = t.id)                                                                              "
            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_material sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id               "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.material_goods_name}, '%') or #{p1.material_goods_name} is null or #{p1.material_goods_name} = '')"
            + "     and sub2.wo_id = t.id)                                                                              "

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
            + "				AND subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_WO+"'                                                                                        "
            + "				AND subt1.STATUS = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                                                                                       "
            + "				GROUP BY subt1.serial_id, subt1.serial_type                                                                                                                         "
            + "  )                                                                                                                                                                              "
            + "      </if>                                                                                                                                                                      "

            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                   "
            + "      and exists (                                                                                       "
            + "             select 1                                                                                    "
            + "               from b_already_do subt1                                                                   "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_WO + "'                       "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                     "
            + "                and serial_id = t.id                                                                     "
            + "				GROUP BY subt1.serial_id, subt1.serial_type                                                 "
            + "       )                                                                                                 "
            + "      </if>                                                                                              "

            + "     ${p1.params.dataScopeAnnotation}                                                                    "
            + " </script>"
            +  "")
    BWoVo selectListSum(@Param("p1") BWoVo param);


//    @Select(""
//            +  "  SELECT                                                                                                "
//            +  "    t.code,                                                                                             "
//            +  "    t11.id product_id,                                                                                  "
//            +  "    t12.id material_id,                                                                                 "
//            +  "    t.delivery_order_code,                                                                              "
//            +  "    t10.wc_warehouse_name,                                                                              "
//            +  "    t1.label status_name,                                                                               "
//            +  "    t13.name product_goods_name,                                                                  "
//            +  "    t13.spec product_spec,                                                                              "
//            +  "    t11.wo_qty product_wo_qty,                                                                          "
//            +  "    t14.name material_goods_name,                                                                 "
//            +  "    t14.spec material_spec,                                                                             "
//            +  "    t12.wo_qty material_wo_qty,                                                                         "
//            +  "    t12.wo_router material_wo_router,                                                                   "
//            +  "    t6.remark,                                                                                          "
//            +  "    t.c_time,                                                                                           "
//            +  "    t2.name c_name,                                                                                     "
//            +  "    t3.name u_name,                                                                                     "
//            +  "    t4.name e_name,                                                                                     "
//            +  "    t.e_time,                                                                                           "
//            +  "    concat(t.id, '_', t11.sku_id) wo_product_id,                                                            "
//            +  "    concat(t.id, '_', t12.sku_id) wo_material_id,                                                           "
//            +  "    t.u_time                                                                                            "
//            +  "  FROM b_wo t                                                   "
//            +  " LEFT JOIN s_dict_data t1 ON t.status = t1.dict_value AND t1.code = '"+ DictConstant.DICT_B_WO_STATUS + "'"
//            + " LEFT JOIN m_staff t2 ON t2.id = t.c_id                                                                  "
//            + " LEFT JOIN m_staff t3 ON t3.id = t.u_id                                                                  "
//            + " LEFT JOIN m_staff t4 ON t4.id = t.e_id                                                                  "
//            +  " LEFT JOIN m_cancel t6 ON t6.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_WO +"' AND t6.serial_id = t.id"
//            +  " LEFT JOIN (SELECT t10.name wc_warehouse_name, t8.warehouse_id, t7.id"
//            +  "            FROM b_wo t7                                                                                "
//            +  "            LEFT JOIN b_wo_product t8 ON t7.id = t8.wo_id and t8.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
//            +  "            LEFT JOIN m_warehouse t10 ON t10.id = t8.warehouse_id group by t7.id                                       "
//            +  "            ) t10 ON t10.id = t.id                                                                      "
//            +  "  inner JOIN b_wo_product t11 ON t.id = t11.wo_id                                                        "
//            +  "  LEFT JOIN m_goods_spec t13 ON t11.sku_id = t13.id                                                     "
//            +  "  inner JOIN b_wo_material t12 ON t12.wo_id = t.id                                                       "
//            +  "  LEFT JOIN m_goods_spec t14 ON t12.sku_id = t14.id                                                     "
//
//    )
//    List<BWoExportVo> exportList(BWoVo param);
}
