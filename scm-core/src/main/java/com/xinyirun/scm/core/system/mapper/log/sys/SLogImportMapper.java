package com.xinyirun.scm.core.system.mapper.log.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.log.sys.SLogImportEntity;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogImportVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-04-07
 */
@Repository
public interface SLogImportMapper extends BaseMapper<SLogImportEntity> {

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + " select t.*,t1.name c_name, t2.name u_name from s_log_import t                                                                  "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                       "
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                       "
            + "   where true                                                                                                                   "
            + "      and (t.type like CONCAT ('%',#{p1.type,jdbcType=VARCHAR},'%') or #{p1.type,jdbcType=VARCHAR} is null)                     "
            + "      and (t.page_name like CONCAT ('%',#{p1.page_name,jdbcType=VARCHAR},'%') or #{p1.page_name,jdbcType=VARCHAR} is null)      "
            + "      and (t1.name like CONCAT ('%',#{p1.c_name,jdbcType=VARCHAR},'%') or #{p1.c_name,jdbcType=VARCHAR} is null)                "
            + "       ")
    IPage<SLogImportVo> selectPage(Page page, @Param("p1") SLogImportVo searchCondition);

    @Select(" select count(*) from (                                                                                                           "
            + " select t.id from s_log_import t                                                                                                "
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                       "
            + "   where true                                                                                                                   "
            + "      and (t.type like CONCAT ('%',#{p1.type,jdbcType=VARCHAR},'%') or #{p1.type,jdbcType=VARCHAR} is null)                     "
            + "      and (t.page_name like CONCAT ('%',#{p1.page_name,jdbcType=VARCHAR},'%') or #{p1.page_name,jdbcType=VARCHAR} is null)      "
            + "      and (t1.name like CONCAT ('%',#{p1.c_name,jdbcType=VARCHAR},'%') or #{p1.c_name,jdbcType=VARCHAR} is null)                "
            + "     limit ${(p1.pageCondition.current-1)*p1.pageCondition.size},  ${(p1.pageCondition.limit_count)}                            "
            + "       ) sub ")
    Integer getLimitCount(@Param("p1") SLogImportVo searchCondition);
}
