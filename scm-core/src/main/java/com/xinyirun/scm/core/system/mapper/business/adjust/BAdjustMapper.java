package com.xinyirun.scm.core.system.mapper.business.adjust;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.adjust.BAdjustDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.adjust.BAdjustEntity;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustVo;
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
public interface BAdjustMapper extends BaseMapper<BAdjustEntity> {

    String common_select = "  "
            + "      SELECT                                                                                                                       "
            + "             tt.idx,                                                                                                               "
            + "             t.*,                                                                                                                  "
            + "             t4.files_id ,                                                                                                         "
            + "             t4.code ,                                                                                                             "
            + "             t4.owner_id ,                                                                                                         "
            + "             t4.remark ,                                                                                                           "
            + "             t15.label as status_name,                                                                                             "
            + "             ifnull(t5.short_name,t5.name) as owner_name,                                                                          "
            + "             t9.name as warehouse_name,                                                                                            "
            + "             t9.short_name as warehouse_short_name,                                                                                "
            + "             t16.label type_name,                                                                                                  "
            + "             t4.type,                                                                                                              "
            + "             t7.name as bin_name,                                                                                                  "
            + "             t8.name as location_name,                                                                                             "
            + "             t10.spec,                                                                                                             "
            + "             t10.pm,                                                                                                            	  "
            + "             t10.code as sku_code,                                                                                           	  "
            + "             t11.name as goods_name,                                                                                               "
            + "             t1.name as c_name,                                                                                              	  "
            + "             t3.name as e_name,                                                                                             		  "
            + "             t2.name as u_name                                                                                              		  "
            + "        FROM                                                                                                                       "
            + "   	       b_adjust_detail t                                                                                                      "
            + "   LEFT JOIN (                                                                                                                     "
            + "        SELECT                                                                                                                     "
            + "          ( @i := CASE WHEN @now_adjust_id=t1.adjust_id THEN @i + 1 ELSE 1 END ) idx,                                              "
            + "          ( @now_adjust_id := t1.adjust_id ),                                                                                      "
            + "           t1.*                                                                                                                    "
            + "         FROM                                                                                                                      "
            + "            b_adjust_detail t1,                                                                                                    "
            + "            ( SELECT @i := 0, @now_adjust_id := '' ) AS a                                                                          "
            + "          ) tt on t.id = tt.id                                                                                                     "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                          "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                          "
            + "   LEFT JOIN m_staff t3 ON t.e_id = t3.id                                                                                          "
            + "   LEFT JOIN b_adjust t4 ON t.adjust_id = t4.id                                                                                    "
            + "   LEFT JOIN m_owner t5 ON t4.owner_id = t5.id                                                                                     "
            + "   LEFT JOIN m_bin t7 ON t.location_id = t7.id                                                                                     "
            + "   LEFT JOIN m_location t8 ON t.location_id = t8.id                                                                                "
            + "   LEFT JOIN m_warehouse t9 ON t.warehouse_id = t9.id                                                                              "
            + "   LEFT JOIN m_goods_spec t10 ON t.sku_id = t10.id                                                                             	  "
            + "   LEFT JOIN m_goods t11 ON t10.goods_id = t11.id                                                                             	  "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                       "
            + "              where tab2.code = '"+ DictConstant.DICT_B_ADJUST_STATUS +"') t15 ON t15.dict_value = t.status                        "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                       "
            + "              where tab2.code = '"+ DictConstant.DICT_B_ADJUST_TYPE +"') t16 ON t16.dict_value = t4.type                            "
            + "                                                                                                                                   "
            ;

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select(" <script>  "
            + common_select
            + "   where true                                                                                                                                                    "
            + "      and (t4.owner_id = #{p1.owner_id,jdbcType=INTEGER} or #{p1.owner_id,jdbcType=INTEGER} is null)                                                             "
            + "      and (t.warehouse_id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                  "
            + "      and (CONCAT(ifnull(t.code, ''), '_', ifnull(t4.code, '')) like CONCAT('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null or #{p1.code,jdbcType=VARCHAR} ='')                    "
            + "      and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} ='')                                     "
            + "      and (CONCAT (ifnull(t10.name,''),ifnull(t10.spec,''),ifnull(t10.code,''),ifnull(t11.name,''),ifnull(t11.code,'')) like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')  or#{p1.goods_name,jdbcType=VARCHAR} is null)                       "
            + "      and (DATE_FORMAT(t.c_time, '%Y%m%d' ) &gt;= DATE_FORMAT(#{p1.start_time,jdbcType=DATE}, '%Y%m%d' ) or #{p1.start_time,jdbcType=DATE} is null)                                                              "
            + "      and (DATE_FORMAT(t.c_time, '%Y%m%d' ) &lt;= DATE_FORMAT(#{p1.over_time,jdbcType=DATE}, '%Y%m%d' ) or #{p1.over_time,jdbcType=DATE} is null)                                                                "
            + "      and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) &gt;= #{p1.batch} or #{p1.batch} is null or #{p1.batch} = '')                                                              "
            // 待办
            + "      <if test='p1.todo_status == 0' >                                                                                                                           "
            + " and exists (                                                                                                                                                    "
            + "		 select 1                                                                                                                                                   "
            + "			 from b_todo subt1                                                                                                                                      "
            + "			where t.id = subt1.serial_id                                                                                                                            "
            + "				and subt1.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_ADJUST_DETAIL+"'                                                                          "
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
            + "              where subt1.serial_type = '" + SystemConstants.SERIAL_TYPE.B_ADJUST_DETAIL + "'                                                                    "
            + "                and subt1.staff_id = #{p1.staff_id,jdbcType=INTEGER}"
            + "                and serial_id = t.id                                                                                                                             "
            + "       )                                                                                                                                                         "
            + "      </if>                                                                                                                                                      "

            + "    </script>   ")
    IPage<BAdjustVo> selectPage(Page page, @Param("p1") BAdjustVo searchCondition);

    /**
     * 按入库明细id查询
     * @param id
     * @return
     */
    @Select("                                                   "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}           "
            + "      ")
    BAdjustVo get(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t4.id =  #{p1,jdbcType=INTEGER}"
            + "  order by t.u_time asc"
            + "      ")
    List<BAdjustVo> selectId(@Param("p1") int id);

    /**
     * 没有分页，按id筛选条件
     */
    @Select("   <script>                                                                                        "
            + "  SELECT  t.*  from b_adjust_detail t                                                           "
            + "  where t.id in                                                                                  "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>     "
            + "         #{item.id,jdbcType=INTEGER}                                                             "
            + "        </foreach>                                                                               "
            + "  </script>    ")
    List<BAdjustDetailEntity> selectIds(@Param("p1") List<BAdjustVo> searchCondition);

}
