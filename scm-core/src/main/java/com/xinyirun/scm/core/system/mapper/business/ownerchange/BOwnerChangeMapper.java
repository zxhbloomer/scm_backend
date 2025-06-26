package com.xinyirun.scm.core.system.mapper.business.ownerchange;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.ownerchange.BOwnerChangeDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.ownerchange.BOwnerChangeEntity;
import com.xinyirun.scm.bean.system.vo.business.ownerchange.BOwnerChangeVo;
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
public interface BOwnerChangeMapper extends BaseMapper<BOwnerChangeEntity> {

    String common_select = "  "
            + "      SELECT                                                                                                                     "
            + "             t.*,                                                                                                      			"
            + "             tt.idx,                                                                                                      		"
            + "             t4.code ,                                                                                                 			"
            + "             t4.files ,                                                                                                 			"
            + "             t4.id owner_change_id ,                                                                                             "
            + "             t4.out_owner_id ,                                                                                             		"
            + "             t4.in_owner_id ,                                                                                             		"
            + "             t4.out_owner_code ,                                                                                                 "
            + "             t4.in_owner_code ,                                                                                             		"
            + "             t4.remark ,                                                                                               			"
            + "             t4.change_time ,                                                                                                  	"
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
            + "             t1.name as c_name,                                                                                        			"
            + "             t3.name as e_name,                                                                                        			"
            + "             t2.name as u_name                                                                                         			"
            + "        FROM                                                                                                           			"
            + "   	       b_owner_change_detail t                                                                                        		"
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                              			"
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                              			"
            + "   LEFT JOIN m_staff t3 ON t.e_id = t3.id                                                                              			"
            + "   LEFT JOIN b_owner_change t4 ON t.owner_change_id = t4.id                                                                    	"
            + "   LEFT JOIN m_owner t5 ON t4.out_owner_id = t5.id                                                                         		"
            + "   LEFT JOIN m_customer t6 ON t4.out_consignor_id = t6.id                                                                        "
            + "   LEFT JOIN m_warehouse t8 ON t4.out_warehouse_id = t8.id                                                             			"
            + "   LEFT JOIN m_warehouse t9 ON t4.in_warehouse_id = t9.id                                                              			"
            + "   LEFT JOIN m_goods_spec t10 ON t.sku_id = t10.id                                                                     			"
            + "   LEFT JOIN m_goods t11 ON t10.goods_id = t11.id                                                                      			"
            + "   LEFT JOIN m_owner t13 ON t4.in_owner_id = t13.id                                                                         		"
            + "   LEFT JOIN m_customer t14 ON t4.in_consignor_id = t14.id                                                                       "
            + "   LEFT JOIN v_dict_info t15 ON t15.dict_value = t.status and t15.code = '"+DictConstant.DICT_B_OWNER_CHANGE_STATUS+"'          	"
            + "   INNER JOIN (                                                                                                                  "
            + "   			  select row_number() over(partition by t.owner_change_id                                                           "
            + "   			                               order by t.c_time asc) as idx,                                                       "
            + "   				t.id                                                                                                            "
            + "   			from b_owner_change_detail t                                                                                        "
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
            + "   where true                                                                                            "
            + "   and (t4.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')              "
            + "   and (t4.out_owner_id = #{p1.out_owner_id} or #{p1.out_owner_id} is null)                              "
            + "   and (t4.in_owner_id = #{p1.in_owner_id} or #{p1.in_owner_id} is null)                                 "
            // 待办
            + "      <if test='p1.todo_status == 0' >                                                                                                                           "
            + " and exists (                                                                                                                                                    "
            + "		 select 1                                                                                                                                                   "
            + "			 from b_todo subt1                                                                                                                                      "
            + "			where t.id = subt1.serial_id                                                                                                                            "
            + "				and subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_OWNERCHANGE_DETAIL +"'                                                                        "
            + "             and subt1.status = '"+DictConstant.DICT_B_TODO_STATUS_TODO+"'                                                                                       "
            + "				and exists (                                                                                                                                        "
            + "						select 1                                                                                                                                    "
            + "							from v_permission_operation_all subt2                                                                                                   "
            + "						 where subt2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                                     "
            + "							 and subt2.operation_perms = subt1.perms                                                                                                "
            + "				)                                                                                                                                                   "
            + "  )                                                                                                                                                              "
            + "      </if>                                                                                                                                                      "

            // 已办
            + "      <if test='p1.todo_status == 1' >                                                                                                                           "
            + "      and exists (                                                                                                                                               "
            + "             select 1                                                                                                                                            "
            + "               from b_already_do subt1                                                                                                                           "
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_OWNERCHANGE_DETAIL + "'                                                                "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}"
            + "                and serial_id = t.id                                                                                                                             "
            + "       )                                                                                                                                                         "
            + "      </if>                                                                                                                                                      "

            + "    </script>   ")
    IPage<BOwnerChangeVo> selectPage(Page page, @Param("p1") BOwnerChangeVo searchCondition);

    /**
     * 按入库明细id查询
     * @param id
     * @return
     */
    @Select("                                                   "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}           "
            + "      ")
    BOwnerChangeVo get(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    BOwnerChangeVo selectId(@Param("p1") int id);

    /**
     * 没有分页，按id筛选条件
     */
    @Select("   <script>                                                                                        "
            + "  SELECT  t.*  from b_owner_change_detail t                                                          "
            + "  where t.id in                                                                                  "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>     "
            + "         #{item.id,jdbcType=INTEGER}                                                             "
            + "        </foreach>                                                                               "
            + "  </script>    ")
    List<BOwnerChangeDetailEntity> selectIds(@Param("p1") List<BOwnerChangeVo> searchCondition);

}
