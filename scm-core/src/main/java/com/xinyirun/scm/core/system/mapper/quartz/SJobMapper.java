package com.xinyirun.scm.core.system.mapper.quartz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.org.MGroupEntity;
import com.xinyirun.scm.bean.entity.quartz.SJobEntity;
import com.xinyirun.scm.bean.system.vo.quartz.SJobVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2019-07-04
 */
@Repository
public interface SJobMapper extends BaseMapper<SJobEntity> {

    String common_column = "  "
            + "     select                                            "
            + "            t.*,                                       "
            + "            c_staff.name as c_name,                    "
            + "            u_staff.name as u_name                     "
            + "       from s_job t                                    "
            + "  LEFT JOIN m_staff c_staff ON t.c_id = c_staff.id     "
            + "  LEFT JOIN m_staff u_staff ON t.u_id = u_staff.id     "
            ;



    @Select("    "
        + common_column
        + "   where true                                                                                                                         "
        + "     and t.is_effected = true                                  "
        + "     and not exists (                                          "
        + "              select 1                                         "
        + "                from s_job subt1                               "
        + "               where subt1.misfire_policy = 2                  "
        + "                 and subt1.run_times >= 1                      "
        + "                 and t.id = subt1.id                           "
        + "       )                                                       "
        + "                                                               "
        + "      ")
    List<SJobVo> selectJobAll();

    @Select("    "
        + common_column
        + "   where true                                                                                                             "
        + "    and (t.job_name like CONCAT ('%',#{p1.job_name,jdbcType=VARCHAR},'%') or #{p1.job_name,jdbcType=VARCHAR} is null)                    "
        + "    and (t.job_group_type like CONCAT ('%',#{p1.job_group_type,jdbcType=VARCHAR},'%') or #{p1.job_group_type,jdbcType=VARCHAR} is null)  "
        + "    and (t.is_effected  = #{p1.is_effected} or #{p1.is_effected} is null) "
        + "      ")
    IPage<SJobVo> selectJobList(Page page, @Param("p1") SJobVo searchCondition);

    @Select("                    "
        + common_column
        + "   where true         "
        + "    and (t.id = #{p1} or #{p1} is null) "
        + "                      ")
    SJobVo selectJobById(@Param("p1") Long id);

    @Select("                    "
        + common_column
        + "   where true         "
        + "    and t.job_serial_id = #{p1} "
        + "    and t.job_serial_type = #{p2} "
        + "                      ")
    SJobVo selectJobBySerialId(@Param("p1") Long id, @Param("p2") String serialType);


    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + " select t.*                                  "
            + "   from s_job t                              "
            + "  where true                                 "
            + "    and t.job_name =  #{p1}                  "
            + "    and (t.id  <>  #{p2} or #{p2} is null)   "
            + "      ")
    List<SJobVo> selectByName(@Param("p1") String name, @Param("p2") Long equal_id);

}
