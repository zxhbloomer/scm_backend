package com.xinyirun.scm.core.system.mapper.business.todo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.todo.BTodoEntity;
import com.xinyirun.scm.bean.system.vo.business.todo.BTodoVo;
import com.xinyirun.scm.bean.system.vo.business.todo.TodoCountVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 待办 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2021-11-20
 */
@Repository
public interface BTodoMapper extends BaseMapper<BTodoEntity> {

    /**
     * 待办事项页面查询
     *
     * select wt2.serial_id,
     *        wt4.role_id ,
     *        wt6.permission_id
     *   from m_staff wt1
     * inner join m_staff_org wt2
     *     on wt1.id = wt2.staff_id
     *    and wt2.serial_type = 'm_position'
     * inner join m_position wt3
     *     on wt2.serial_id = wt3.id
     *    and wt3.is_del = false
     * inner join m_role_position wt4
     *     on wt2.serial_id = wt4.position_id
     * inner join s_role wt5
     *     on wt5.id = wt4.role_id
     *    and wt5.is_del = false
     *    and wt5.is_enable = true
     * inner join m_permission_role wt6
     *     on wt6.role_id = wt4.role_id
     * inner join m_permission_operation wt7
     *     on
     *     (case
     *      when 待办事项分类='提交类' then wt7.perms in ('','','')
     *      else
     *      end)
     *  where wt1.id = 28
     *
     *
     *
     *
     */
    @Select("     "
            + "	 	SELECT                                                                                                                                                                                                                                                                                          "
            + "	 		t2.label serial_name,                                                                                                                  "
            + "	 		t1.c_time,                                                                                                                             "
            + "	 		t3.name c_name,                                                                                                                        "
            + "	 		t4.path,                                                                                                                               "
            + "	 		t1.u_time,                                                                                                                             "
            + "         t5.code serial_code ,                                                                                                                  "
            + "	 		t6.short_name as owner_name,                                                                                                           "
            + "	 		t7.short_name as warehouse_name,                                                                                                             "
            + "	 		t8.name as sku_name,                                                                                                                   "
            + "	 		t5.qty,                                                                                                                                "
            + "	 		t5.unit_id,                                                                                                                            "
            + "	 		t9.name as unit_name                                                                                                                   "
            + "	 	FROM                                                                                                                                       "
            + "	 		b_todo t1                                                                                                                              "
            + "	 		LEFT JOIN v_dict_info t2 ON t1.serial_type = t2.dict_value and t2.code = 'todo_type'                                                   "
            + "	 		left join m_staff t3 on t1.u_id = t3.id                                                                                                "
            + "         left join s_pages_function t10 on t1.perms = t10.perms                                                                                 "
            + "	 		left join m_menu t4 on t10.page_id = t4.page_id                                                                                         "
            // 入库计划
            + "	 		LEFT JOIN (SELECT t1.id, t2.code, 'b_in_plan_detail' serial_type ,t2.owner_id,t1.warehouse_id,t1.sku_id,t1.count as qty,t1.unit_id     "
            + "	 		             FROM b_in_plan_detail t1                                                                                                  "
            + "	 		        left join b_in_plan t2 on t1.plan_id = t2.id                                                                                   "
            // 入库单
            + "	 		        union all                                                                                                                      "
            + "	 		           SELECT id, code, 'b_in' serial_type ,owner_id ,warehouse_id,sku_id,actual_count as qty,unit_id                              "
            + "	 		             FROM b_in                                                                                                                 "
            // 出库计划
            + "	 		        union all                                                                                                                      "
            + "	 		           SELECT t1.id, t2.code, 'b_out_plan_detail' serial_type ,t2.owner_id ,t1.warehouse_id,t1.sku_id,t1.count as qty,t1.unit_id   "
            + "	 		             FROM b_out_plan_detail t1                                                                                                 "
            + "	 		        left join b_out_plan t2 on t1.plan_id = t2.id                                                                                  "
            // 出库单
            + "	 		        union all                                                                                                                      "
            + "	 		           SELECT id, code, 'b_out' serial_type ,owner_id ,warehouse_id,sku_id,actual_count as qty,unit_id                             "
            + "	 		             FROM b_out                                                                                                                "

            // 库存调整
            + "	 		        union all                                                                                                                      "
            + "	 		           SELECT t1.id, t2.code, 'b_adjust_detail' serial_type ,t2.owner_id ,                                                         "
            + "                                 t1.warehouse_id,t1.sku_id,t1.qty_adjust as qty,''   as unit_id                                                 "
            + "	 		             FROM b_adjust_detail t1                                                                                                   "
            + "	 		        left join b_adjust t2 on t1.adjust_id = t2.id                                                                                  "

            // 库存调拨
            + "	 		        union all                                                                                                                      "
            + "	 		           SELECT t1.id, t2.code, 'b_allocate_detail' serial_type ,t2.out_owner_id                                                     "
            + "                 owner_id ,t2.out_warehouse_id warehouse_id,t1.sku_id,t1.qty as qty,''   as unit_id                                             "
            + "	 		             FROM b_allocate_detail t1                                                                                                 "
            + "	 		        left join b_allocate t2 on t1.allocate_id = t2.id                                                                              "

            // 货权转移
            + "	 		        union all                                                                                                                      "
            + "	 		           SELECT t1.id, t2.code, 'b_owner_change_detail' serial_type ,t2.out_owner_id                                                 "
            + "                 owner_id ,t2.out_warehouse_id warehouse_id,t1.sku_id,t1.qty as qty,''   as unit_id                                             "
            + "	 		             FROM b_owner_change_detail t1                                                                                             "
            + "	 		        left join b_owner_change t2 on t1.owner_change_id = t2.id                                                                      "

            + "	 		) t5 on t1.serial_type = t5.serial_type                                                                                                "
            + "	 		    and t1.serial_id = t5.id                                                                                                           "
            + "	 		left join m_owner t6 on t6.id = t5.owner_id                                                                                            "
            + "	 		left join m_warehouse t7 on t7.id = t5.warehouse_id                                                                                    "
            + "	 		left join m_goods_spec t8 on t5.sku_id = t8.id                                                                                         "
            + "  		left join m_unit t9 on t5.unit_id = t9.id                                                                                              "
            + "        where true                                                                                                                              "
            + " AND exists (                                                                                                                                   "
            + "      SELECT 1                                                                                                                                  "
            + "        FROM m_staff_org com_t1                                                                                                                 "
            + "  inner JOIN m_position com_t2                                                                                                                  "
            + "          ON com_t1.serial_id = com_t2.id                                                                                                       "
            + "         AND com_t1.serial_type = '"+ DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE +"'                                               "
            + "         where com_t1.staff_id =  #{p1.staff_id,jdbcType=INTEGER}            "
            + "         and com_t2.id = t1.position_id            "
            + "   )                     "
            + " AND t1.status = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'"
            + "     GROUP BY t1.serial_id,t1.serial_type                   "
            +"      ")
    IPage<BTodoVo> selectPage(Page page, @Param("p1") BTodoVo searchCondition);

    /**
     * 待办事项页面查询
     */
    @Select("  <script>   "
            + "select t.serial_id from  b_todo  t                                                                                  "
            + "   where true                                                                                                       "
            + "   and t.serial_type = #{p1,jdbcType=VARCHAR}                                                                       "
            + "   <if test='p2 != null and p2.length!=0' >                                                                         "
            + "         and t.position_id in                                                                                       "
            + "        <foreach collection='p2' item='item' index='index' open='(' separator=',' close=')'>                        "
            + "         #{item}                                                                                                    "
            + "        </foreach>                                                                                                  "
            + "   </if>                                                                                                            "
            + "   and (t.status = '0')                                                                                             "
            +"  </script>                                                                                                          ")
    List<Integer> selectTodoIdList(@Param("p1") String serial_type, @Param("p2") Long[] position_id);

    /**
     * 待办事项列表查询
     */
    @Select("    "
            + "select t.* from  b_todo  t                                                                                         "
            + "   where true                                                                                                      "
            + "   and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null)                         "
            + "   and (t.serial_type = #{p1.serial_type,jdbcType=VARCHAR} or #{p1.serial_type,jdbcType=VARCHAR} is null)          "
            + "   and (t.serial_id = #{p1.serial_id,jdbcType=INTEGER} or #{p1.serial_id,jdbcType=INTEGER} is null)                 "
            +"  ")
    List<BTodoVo> selectTodoList(@Param("p1") BTodoVo searchCondition);

    /**
     * 待办事项数量
     */
    @Select("    "
            +"  SELECT  count(1) todoCount  from(                                                                       "
            +"  SELECT                                                                                                  "
            +"   t2.*                                                                                                   "
            +"  FROM                                                                                                    "
            +"    b_todo t2                                                                                             "
            +"  WHERE  TRUE                                                                                             "
            + " AND exists (                                                                                            "
            + "      SELECT 1                                                                                           "
            + "        FROM m_staff_org com_t1                                                                          "
            + "  inner JOIN m_position com_t2                                                                           "
            + "          ON com_t1.serial_id = com_t2.id                                                                "
            + "         AND com_t1.serial_type = '"+ DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE +"'        "
            + "         where com_t1.staff_id =  #{p2,jdbcType=INTEGER}                                                 "
            + "         and com_t2.id = t2.position_id                                                                  "
            + "   )                                                                                                     "
            + " AND t2.status = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                              "
            + " AND t2.serial_type =  #{p1,jdbcType=VARCHAR}                                                            "
            + "     GROUP BY t2.serial_id,t2.serial_type                                                                "
            + "     )t                                                                                                  "
            +"  ")
    TodoCountVo selectTodoCount(@Param("p1") String serial_type, @Param("p2") Long staff_id);


    @Delete("<script>"
            + "delete from b_todo where serial_type = #{p2} and serial_id in                                            "
            + " <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                    "
            + "    #{item}                                                                                              "
            + " </foreach>                                                                                              "
            + "</script>"
    )
    void deleteByIdsAndSerialType(@Param("p1") List<Integer> serial_ids,@Param("p2") String serial_type);
}
