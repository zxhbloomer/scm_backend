package com.xinyirun.scm.core.system.mapper.master.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.vehicle.MVehicleEntity;
import com.xinyirun.scm.bean.system.vo.master.vehicle.MVehicleVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface MVehicleMapper extends BaseMapper<MVehicleEntity> {

    String common_select = "  "
            + "     SELECT                                                                                              "
            + "            t.*,                                                                                         "
            + "            t1.name as c_name,                                                                           "
            + "            t2.name as u_name,                                                                           "
            + "            t3.label as no_color_str,                                                                    "
            + "            ifnull(t4.label, '未知') as validate_status_name                                              "
            + "       FROM                                                                                              "
            + "  	       m_vehicle t                                                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
            + "  LEFT JOIN s_dict_data t3 ON t.no_color = t3.dict_value and t3.code='m_vehicle_no_color'                "
            + "  LEFT JOIN s_dict_data t4 ON t.validate_status = t4.dict_value and t4.code='m_vehicle_validate_status'  "

            + "                                                                        "
            ;

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true                                                                                                                   "
            + "    and (t.no like CONCAT ('%',#{p1.no,jdbcType=VARCHAR},'%') or #{p1.no,jdbcType=VARCHAR} is null)                            "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                      "
            + "    and (t.is_del = #{p1.is_del,jdbcType=BOOLEAN} or #{p1.is_del,jdbcType=BOOLEAN} is null)                                    "
            + "      ")
    IPage<MVehicleVo> selectPage(Page page, @Param("p1") MVehicleVo searchCondition);


    /**
     * 查询详情
     */
    @Select("    "
            + "     SELECT                                                                                              "
            + "            t.*,                                                                                         "
            + "            t1.name as c_name,                                                                           "
            + "            t2.name as u_name                                                                            "
            + "       FROM                                                                                              "
            + "  	       m_vehicle t                                                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
            + "  where true                                                                                             "
            + "    and (t.id = #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                          "
            + "      ")
    MVehicleVo getDetail(@Param("p1") MVehicleVo searchCondition);


    /**
     * 车牌号查询list
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and (t.is_del = 0)                                                                                   "
            + "    and (t.id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                         "
            + "    and t.no = #{p1.no,jdbcType=VARCHAR}                                                                 "
            + "      ")
    List<MVehicleEntity> selectByCarNo(@Param("p1") MVehicleVo searchCondition);

    /**
     * 车牌号查询list
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.no = #{p1,jdbcType=VARCHAR} )                                                                 "
            + "    and (t.is_del = 0 )                                                                                  "
            + "      ")
    MVehicleEntity selectByNo(@Param("p1") String carNo);


    /**
     * 车牌号查询list
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.id = #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                           "
            + "      ")
    MVehicleVo selectId(@Param("p1") Integer id);

}
