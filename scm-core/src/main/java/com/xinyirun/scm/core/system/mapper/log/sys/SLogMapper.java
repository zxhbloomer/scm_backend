package com.xinyirun.scm.core.system.mapper.log.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.log.sys.SLogSysEntity;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogSysVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
@Repository
public interface SLogMapper extends BaseMapper<SLogSysEntity> {

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + " select * from s_log_sys t                                                                                                       "
            + "   where true                                                                                                                    "
            + "      and (t.type like CONCAT ('%',#{p1.type,jdbcType=VARCHAR},'%') or #{p1.type,jdbcType=VARCHAR} is null)                      "
            + "      and (t.url like CONCAT ('%',#{p1.url,jdbcType=VARCHAR},'%') or #{p1.url,jdbcType=VARCHAR} is null)                         "
            + "      and (t.operation like CONCAT ('%',#{p1.operation,jdbcType=VARCHAR},'%') or #{p1.operation,jdbcType=VARCHAR} is null)       "
            + "      and (t.c_time >= #{p1.start_time,jdbcType=DATE} or #{p1.start_time,jdbcType=DATE} is null)                                 "
            + "      and (t.c_time <= #{p1.over_time,jdbcType=DATE} or #{p1.over_time,jdbcType=DATE} is null)                                   "
            + "       ")
    IPage<SLogSysVo> selectPage(Page page, @Param("p1") SLogSysVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + " select count(*) as c from  (                                                                                                    "
            + " select t.id from s_log_sys t                                                                                                    "
            + "   where true                                                                                                                    "
            + "      and (t.type like CONCAT ('%',#{p1.type,jdbcType=VARCHAR},'%') or #{p1.type,jdbcType=VARCHAR} is null)                      "
            + "      and (t.url like CONCAT ('%',#{p1.url,jdbcType=VARCHAR},'%') or #{p1.url,jdbcType=VARCHAR} is null)                         "
            + "      and (t.operation like CONCAT ('%',#{p1.operation,jdbcType=VARCHAR},'%') or #{p1.operation,jdbcType=VARCHAR} is null)       "
            + "      and (t.c_time >= #{p1.start_time,jdbcType=DATE} or #{p1.start_time,jdbcType=DATE} is null)                                 "
            + "      and (t.c_time <= #{p1.over_time,jdbcType=DATE} or #{p1.over_time,jdbcType=DATE} is null)                                   "
            + "     limit ${(p1.pageCondition.current-1)*p1.pageCondition.size},  ${(p1.pageCondition.limit_count)}                             "
            + "     )  sub "
            + "       ")
    Integer getLimitCount(@Param("p1") SLogSysVo searchCondition);

}
