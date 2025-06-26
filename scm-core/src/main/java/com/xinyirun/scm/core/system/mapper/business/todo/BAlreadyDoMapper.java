package com.xinyirun.scm.core.system.mapper.business.todo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.todo.BAlreadyDoEntity;
import com.xinyirun.scm.bean.system.vo.business.todo.BAlreadyDoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 已办 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2021-11-20
 */
@Repository
public interface BAlreadyDoMapper extends BaseMapper<BAlreadyDoEntity> {

    /**
     * 已办事项页面查             询
     * @param searchCondition
     * @return
     */
    @Select("    "
            + "	  	SELECT                                                                                                                                      "
            + "	  		t2.label serial_name,                                                                                                                   "
            + "	  		t1.c_time,                                                                                                                              "
            + "	  		t3.name c_name,                                                                                                                         "
            + "	  		t4.path,                                                                                                                                "
            + "	  		t1.u_time,                                                                                                                              "
            + "	  		t5.code serial_code ,                                                                                                                   "
            + "	  		t6.short_name as owner_name,                                                                                                            "
            + "	  		t7.short_name as warehouse_name,                                                                                                        "
            + "	  		t8.name as sku_name,                                                                                                                    "
            + "	  		t5.qty,                                                                                                                                 "
            + "	  		t5.unit_id,                                                                                                                             "
            + "	  		t9.name as unit_name	FROM                                                                                                            "
            + "	  		b_already_do t1                                                                                                                         "
            + "	  LEFT JOIN v_dict_info t2 ON t1.serial_type = t2.dict_value and t2.code = 'todo_type'                                                          "
            + "	  		left join m_staff t3 on t1.u_id = t3.id                                                                                                 "
            + "	  		left join m_menu t4 on t1.page_code = t4.page_code                                                                                      "

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


            + "	  		) t5 on t1.serial_type = t5.serial_type                                                                                                 "
            + "	  		    and t1.serial_id = t5.id                                                                                                            "
            + "	  		left join m_owner t6 on t6.id = t5.owner_id                                                                                             "
            + "	  		left join m_warehouse t7 on t7.id = t5.warehouse_id                                                                                     "
            + "	  		left join m_goods_spec t8 on t5.sku_id = t8.id                                                                                          "
            + "	  		left join m_unit t9 on t5.unit_id = t9.id                                                                                               "
            + "	   where true                                                                                                                                   "
            + "	     and t1.staff_id = #{p2,jdbcType=BIGINT}                                                                                                    "
            + "                                             ")
    IPage<BAlreadyDoVo> selectPage(Page page, @Param("p1") BAlreadyDoVo searchCondition, @Param("p2") Long staff_id);

    /**
     * 已办事项页面查询
     */
    @Select("    "
            + "select t.serial_id from  b_already_do  t                                                          "
            + "   where true                                                                                     "
            + "   and (t.serial_type = #{p1,jdbcType=VARCHAR}  or #{p1,jdbcType=VARCHAR} is null)                "
            + "   and t.staff_id = #{p2,jdbcType=BIGINT}                                                         "
            + "   ")
    List<Integer> selectAlreadyDoIdList(@Param("p1") String serial_type, @Param("p2") Long staff_id );


}
