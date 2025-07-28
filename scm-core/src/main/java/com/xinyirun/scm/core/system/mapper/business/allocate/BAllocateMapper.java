package com.xinyirun.scm.core.system.mapper.business.allocate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.allocate.BAllocateDetailEntity;
import com.xinyirun.scm.bean.entity.business.allocate.BAllocateEntity;
import com.xinyirun.scm.bean.system.vo.business.allocate.BAllocateVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inplan.BInPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inplan.BInPlanVo;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 库存调整 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Repository
public interface BAllocateMapper extends BaseMapper<BAllocateEntity> {

    String common_select = "  "
            + "      SELECT                                                                                                                     "
            + "             t.*,                                                                                                      			"
            + "             tt.idx,                                                                                                      		"
            + "             t4.code ,                                                                                                 			"
            + "             t4.order_id ,                                                                                             			"
            + "             t4.auto ,                                                                                                 			"
            + "             t4.out_owner_id ,                                                                                             		"
            + "             t4.remark ,                                                                                               			"
            + "             t4.allocate_time ,                                                                                               	"
            + "             t15.label as status_name,                                                                                 			"
            + "             ifnull(t5.short_name,t5.name) as out_owner_name,                                                              		"
            + "             ifnull(t6.short_name,t6.name) as out_consignor_name,                                                              	"
            + "             ifnull(t13.short_name,t13.name) as in_owner_name,                                                              		"
            + "             ifnull(t14.short_name,t14.name) as in_consignor_name,                                                              	"
            + "             ifnull(t8.short_name,t8.name) as out_warehouse_name,                                                      			"
            + "             t8.code as out_warehouse_code,                                                                                     	"
            + "             t8.id as out_warehouse_id,                                                                                     		"
            + "             ifnull(t9.short_name,t9.name) as in_warehouse_name,                                                       			"
            + "             t9.code as in_warehouse_code,                                                                                     	"
            + "             t9.id as in_warehouse_id,                                                                                     		"
            + "             t10.spec,                                                                                                 			"
            + "             t10.pm,                                                                                                   			"
            + "             t10.code as sku_code,                                                                                     			"
            + "             t11.name as goods_name,                                                                                   			"
            + "             t12.contract_no,                                                                                   			        "
            + "             t12.order_no,                                                                                   			        "
            + "             t1.name as c_name,                                                                                        			"
            + "             t3.name as e_name,                                                                                        			"
            + "             t2.name as u_name                                                                                         			"
            + "        FROM                                                                                                           			"
            + "   	       b_allocate_detail t                                                                                        			"
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                              			"
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                              			"
            + "   LEFT JOIN m_staff t3 ON t.e_id = t3.id                                                                              			"
            + "   LEFT JOIN b_allocate t4 ON t.allocate_id = t4.id                                                                    			"
            + "   LEFT JOIN m_owner t5 ON t4.out_owner_id = t5.id                                                                         		"
            + "   LEFT JOIN m_customer t6 ON t4.out_consignor_id = t6.id                                                                        "
            + "   LEFT JOIN m_warehouse t8 ON t4.out_warehouse_id = t8.id                                                             			"
            + "   LEFT JOIN m_warehouse t9 ON t4.in_warehouse_id = t9.id                                                              			"
            + "   LEFT JOIN m_goods_spec t10 ON t.sku_id = t10.id                                                                     			"
            + "   LEFT JOIN m_goods t11 ON t10.goods_id = t11.id                                                                      			"
            + "   LEFT JOIN b_allocate_order t12 ON t4.order_id = t12.id                                                                      	"
            + "   LEFT JOIN m_owner t13 ON t4.in_owner_id = t13.id                                                                         		"
            + "   LEFT JOIN m_customer t14 ON t4.in_consignor_id = t14.id                                                                       "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id           			"
            + "              where tab2.code = '"+ DictConstant.DICT_B_ALLOCATE_STATUS +"') t15 ON t15.dict_value = t.status          			"
            + "   INNER JOIN (                                                                                                                  "
            + "   			  select row_number() over(partition by t.allocate_id                                                               "
            + "   			                               order by t.c_time asc) as idx,                                                       "
            + "   				t.id                                                                                                            "
            + "   			from b_allocate_detail t                                                                                            "
            + "     ) tt on tt.id = t.id                                                                                                        "
            + "                                                                                                                       		    "
    ;

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("  <script>  "
            + common_select
            + "   where true                                                                                           "

            // 待办
            + "      <if test='p1.todo_status == 0' >                                                                                                                           "
            + " and exists (                                                                                                                                                    "
            + "		 select 1                                                                                                                                                   "
            + "			 from b_todo subt1                                                                                                                                      "
            + "			where t.id = subt1.serial_id                                                                                                                            "
            + "				and subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_ALLOCATE_DETAIL+"'                                                                        "
            + "             and subt1.status = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                              "
            + "				and exists (                                                                               "
            + "						select 1                                                                           "
            + "							from v_permission_operation_all subt2                                          "
            + "						 where subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                            "
            + "							 and subt2.operation_perms = subt1.perms                                       "
            + "				)                                                                                          "
            + "  )                                                                                                     "
            + "      </if>                                                                                             "

            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                  "
            + "      and exists (                                                                                      "
            + "             select 1                                                                                   "
            + "               from b_already_do subt1                                                                  "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_ALLOCATE_DETAIL + "'         "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}"
            + "                and serial_id = t.id                                                                    "
            + "       )                                                                                                "
            + "      </if>                                                                                             "

            + "    <if test='p1.code != null and p1.code != \"\"'>                                                     "
            + "        and t4.code LIKE CONCAT('%', #{p1.code}, '%')                                                   "
            + "    </if>                                                                                               "

            + "    <if test='p1.in_consignor_id != null'>                                                              "
            + "        and t4.in_consignor_id = #{p1.in_consignor_id}                                                  "
            + "    </if>                                                                                               "

            + "    <if test='p1.out_consignor_id != null'>                                                             "
            + "        and t4.out_consignor_id = #{p1.out_consignor_id}                                                 "
            + "    </if>                                                                                               "

            + "    <if test='p1.in_owner_id != null'>                                                                  "
            + "        and t4.in_owner_id = #{p1.in_owner_id}                                                          "
            + "    </if>                                                                                               "

            + "    <if test='p1.out_owner_id != null'>                                                                 "
            + "        and t4.out_owner_id = #{p1.out_owner_id}                                                        "
            + "    </if>                                                                                               "

            + "    <if test='p1.out_warehouse_id != null'>                                                             "
            + "        and t4.out_warehouse_id = #{p1.out_warehouse_id}                                                "
            + "    </if>                                                                                               "

            + "    <if test='p1.in_warehouse_id != null'>                                                              "
            + "        and t4.in_warehouse_id = #{p1.in_warehouse_id}                                                 "
            + "    </if>                                                                                               "

            + "    <if test='p1.goods_name != null and p1.goods_name != \"\"'>                                         "
            + "        and (t11.name LIKE CONCAT('%', #{p1.goods_name}, '%') or t11.code LIKE CONCAT('%', #{p1.goods_name}, '%') or t10.spec LIKE CONCAT('%', #{p1.goods_name}, '%') or t10.code LIKE CONCAT('%', #{p1.goods_name}, '%') )"
            + "    </if>                                                                                               "

            + "    <if test='p1.contract_no != null and p1.contract_no != \"\"'>                                       "
            + "        and (t12.contract_no LIKE CONCAT('%', #{p1.contract_no}, '%'))                                  "
            + "    </if>                                                                                               "

            + "    <if test='p1.status != null and p1.status != \"\"'>                                                 "
            + "        and t.status = #{p1.status}                                                                     "
            + "    </if>                                                                                               "

            + "    <if test='p1.start_time != null and p1.over_time != null'>"
            + "      and DATE_FORMAT(t.c_time, '%Y%m%d' ) &gt;= DATE_FORMAT(#{p1.start_time,jdbcType=DATE}, '%Y%m%d' ) "
            + "      and DATE_FORMAT(t.c_time, '%Y%m%d' ) &lt;= DATE_FORMAT(#{p1.over_time,jdbcType=DATE}, '%Y%m%d' )  "
            + "    </if>                                                                                               "

            + "    </script>   ")
    IPage<BAllocateVo> selectPage(Page page, @Param("p1") BAllocateVo searchCondition);

    /**
     * 按入库明细id查询
     * @param id
     * @return
     */
    @Select("                                                   "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}           "
            + "      ")
    BAllocateVo get(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    BAllocateVo selectId(@Param("p1") int id);

    /**
     * 没有分页，按id筛选条件
     */
    @Select("   <script>                                                                                        "
            + "  SELECT  t.*  from b_allocate_detail t                                                          "
            + "  where t.id in                                                                                  "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>     "
            + "         #{item.id,jdbcType=INTEGER}                                                             "
            + "        </foreach>                                                                               "
            + "  </script>    ")
    List<BAllocateDetailEntity> selectIds(@Param("p1") List<BAllocateVo> searchCondition);


    @Select(""
            +"		SELECT                                                      									                                                    "
            +"			t1.c_time plan_time,                                    									                                                    "
            +"			"+DictConstant.DICT_B_IN_TYPE_ONE+" AS type,                                              	                                                    "
            +"			"+DictConstant.DICT_B_IN_PLAN_STATUS_ZERO+" AS status,                                                                                         "
            +"			t1.sku_id,                                              									                                                    "
            +"			t1.sku_code,                                            									                                                    "
            +"			t2.in_owner_id owner_id,                                            									                                        "
            +"			t2.in_owner_code owner_code,                                          									                                        "
            +"			t2.in_consignor_id consignor_id,                                            									                                "
            +"			t2.in_consignor_code consignor_code,                                          									                                "
            +"			t2.in_warehouse_id warehouse_id,                                        					                                                    "
            +"			t1.qty AS count,                                        									                                                    "
            +"			t1.qty AS weight,                                       									                                                    "
            +"			t3.id AS location_id,                                       									                                                "
            +"			t4.id AS bin_id                                       									                                                        "
            +"		FROM                                                        									                                                    "
            +"			b_allocate_detail t1                                    									                                                    "
            +"			JOIN b_allocate t2 ON t1.allocate_id = t2.id            									                                                    "
//            +"			LEFT JOIN m_unit t3 ON t3.CODE = '"+DictConstant.DICT_API_UNIT_CODE+"'                                                                      "
            +"          LEFT JOIN m_location t3 ON t3.warehouse_id = t2.out_warehouse_id AND t3.enable = '"+ SystemConstants.ENABLE_TRUE + "'                           "
            +"          LEFT JOIN m_bin t4 ON t4.warehouse_id = t2.out_warehouse_id  AND t4.location_id = t3.id AND t4.enable = '"+ SystemConstants.ENABLE_TRUE + "'    "
            +"         WHERE t1.id =  #{p1,jdbcType=INTEGER}                                                                                                            "
    )
    BInPlanVo getInPlanFromAllocate(@Param("p1") int id);

    @Select(""
            +"		SELECT                                                      									                                                    "
            +"			t1.c_time plan_time,                                    									                                                    "
            +"			"+DictConstant.DICT_B_IN_PLAN_TYPE_ONE+" AS type,                                                                                                "
            +"			"+DictConstant.DICT_B_IN_PLAN_STATUS_TWO +" AS status,                                                                                        "
            +"			t1.sku_id,                                              									                                                    "
            +"			t1.sku_code,                                            									                                                    "
            +"			t2.in_owner_id owner_id,                                            									                                        "
            +"			t2.in_owner_code owner_code,                                          									                                        "
            +"			t2.in_consignor_id consignor_id,                                            									                                "
            +"			t2.in_consignor_code consignor_code,                                          									                                "
            +"			t2.in_warehouse_id warehouse_id,                                        					                                                    "
            +"			t1.qty AS count,                                        									                                                    "
            +"			t1.qty AS weight,                                       									                                                    "
            +"			t3.id AS location_id,                                       									                                                "
            +"			t4.id AS bin_id                                       									                                                        "
            +"		FROM                                                        									                                                    "
            +"			b_allocate_detail t1                                    									                                                    "
            +"			LEFT JOIN b_allocate t2 ON t1.allocate_id = t2.id            							                                                        "
            +"          LEFT JOIN m_location t3 ON t3.warehouse_id = t2.out_warehouse_id AND t3.enable = '"+ SystemConstants.ENABLE_TRUE + "'                           "
            +"          LEFT JOIN m_bin t4 ON t4.warehouse_id = t2.out_warehouse_id  AND t4.location_id = t3.id AND t4.enable = '"+ SystemConstants.ENABLE_TRUE + "'    "
            +"         WHERE t1.id =  #{p1,jdbcType=INTEGER}                                                                                                            "
    )
    List<BInPlanDetailVo> getInPlanDetailListFromAllocate(@Param("p1") int id);

    @Select(""
            + "			SELECT                                                                                                  "
            + "				t1.c_time plan_time,                                                                                "
            + "				1 AS type,                                                                                          "
            + "				0 AS STATUS,                                                                                        "
            + "				t1.sku_id,                                                                                          "
            + "				t1.sku_code,                                                                                        "
            + "				t2.in_owner_id owner_id,                                                                            "
            + "				t2.in_owner_code owner_code,                                                                        "
            + "				t2.in_consignor_id consignor_id,                                                                    "
            + "				t2.in_consignor_code consignor_code,                                                                "
            + "				t2.in_warehouse_id warehouse_id,                                                                    "
            + "				t3.id AS location_id,                                                                               "
            + "				t4.id AS bin_id,                                                                                    "
            + "				t1.qty AS plan_count,                                                                               "
            + "				t1.qty AS plan_weight,                                                                              "
            + "				0 AS plan_volume,                                                                                   "
            + "				t1.qty AS acutal_count,                                                                             "
            + "				t1.qty AS acutal_weight,                                                                            "
            + "				0 AS acutal_volume,                                                                                 "
            + "				0 AS price,                                                                                         "
            + "				0 AS amount,                                                                                        "
            + "				t5.src_unit_id unit_id                                                                              "
            + "				t5.calc,                                                                                            "
            + "			FROM                                                                                                    "
            + "				b_allocate_detail t1                                                                                "
            + "				JOIN b_allocate t2 ON t1.allocate_id = t2.id                                                        "
            + "				LEFT JOIN m_location t3 ON t3.warehouse_id = t2.out_warehouse_id                                    "
            + "				AND t3.ENABLE =  '"+ SystemConstants.ENABLE_TRUE + "'                                               "
            + "				LEFT JOIN m_bin t4 ON t4.warehouse_id = t2.out_warehouse_id                                    "
            + "				AND t4.location_id = t3.id                                                                          "
            + "				LEFT JOIN m_goods_unit_calc t5 ON t5.sku_id = t1.sku_id and t5.src_unit = '吨'                      "
            + "				AND t4.ENABLE = '"+ SystemConstants.ENABLE_TRUE + "'                                                "
    )
    List<BInPlanDetailVo> getInListFromAllocate(@Param("p1") int id);

    @Select(""
            +"		SELECT                                                      									                                                    "
            +"			t1.c_time plan_time,                                    									                                                    "
            +"			"+DictConstant.DICT_B_OUT_PLAN_TYPE_ONE+" AS type,                                                                                               "
//            +"			"+DictConstant.DICT_B_OUT_PLAN_STATUS_SAVED+" AS status,                                                                                        "
            +"			t1.sku_id,                                              									                                                    "
            +"			t1.sku_code,                                            									                                                    "
            +"			t2.out_owner_id owner_id,                                            									                                        "
            +"			t2.out_owner_code owner_code,                                          									                                        "
            +"			t2.out_consignor_id consignor_id,                                            									                                "
            +"			t2.out_consignor_code consignor_code,                                          									                                "
            +"			t2.out_warehouse_id warehouse_id,                                        					                                                    "
            +"			t1.qty AS count,                                        									                                                    "
            +"			t1.qty AS weight,                                       									                                                    "
            +"			t3.id AS location_id,                                       									                                                "
            +"			t4.id AS bin_id                                       									                                                        "
            +"		FROM                                                        									                                                    "
            +"			b_allocate_detail t1                                    									                                                    "
            +"			JOIN b_allocate t2 ON t1.allocate_id = t2.id            									                                                    "
            +"          LEFT JOIN m_location t3 ON t3.warehouse_id = t2.out_warehouse_id AND t3.enable = '"+ SystemConstants.ENABLE_TRUE + "'                           "
            +"          LEFT JOIN m_bin t4 ON t4.warehouse_id = t2.out_warehouse_id  AND t4.location_id = t3.id AND t4.enable = '"+ SystemConstants.ENABLE_TRUE + "'    "
//            +"			LEFT JOIN m_unit t3 ON t3.CODE = '"+DictConstant.DICT_API_UNIT_CODE+"'                                                                      "
            +"          WHERE t1.id =  #{p1,jdbcType=INTEGER}                                                                                                           "
    )
    BOutPlanVo getOutPlanFromAllocate(@Param("p1") int id);

    @Select(""
            +"		SELECT                                                      									                                                    "
            +"			t1.c_time plan_time,                                    									                                                    "
            +"			"+DictConstant.DICT_B_OUT_PLAN_TYPE_ONE+" AS type,                                                                                               "
//            +"			"+DictConstant.DICT_B_OUT_PLAN_STATUS_PASSED+" AS status,                                                                                       "
            +"			t1.sku_id,                                              									                                                    "
            +"			t1.sku_code,                                            									                                                    "
            +"			t2.out_owner_id owner_id,                                            									                                        "
            +"			t2.out_owner_code owner_code,                                          									                                        "
            +"			t2.out_consignor_id consignor_id,                                            									                                "
            +"			t2.out_consignor_code consignor_code,                                          									                                "
            +"			t2.out_warehouse_id warehouse_id,                                        					                                                    "
            +"			t1.qty AS count,                                        									                                                    "
            +"			t1.qty AS weight,                                       									                                                    "
            +"			t3.id AS location_id,                                       									                                                "
            +"			t4.id AS bin_id                                       									                                                        "
            +"		FROM                                                        									                                                    "
            +"			b_allocate_detail t1                                    									                                                    "
            +"			LEFT JOIN b_allocate t2 ON t1.allocate_id = t2.id            								                                                    "
            +"          LEFT JOIN m_location t3 ON t3.warehouse_id = t2.out_warehouse_id AND t3.enable = '"+ SystemConstants.ENABLE_TRUE + "'                           "
            +"          LEFT JOIN m_bin t4 ON t4.warehouse_id = t2.out_warehouse_id  AND t4.location_id = t3.id AND t4.enable = '"+ SystemConstants.ENABLE_TRUE + "'    "
            +"          WHERE t1.id =  #{p1,jdbcType=INTEGER}                                                       "
    )
    List<BOutPlanDetailVo> getOutPlanDetailListFromAllocate(@Param("p1") int id);
}
