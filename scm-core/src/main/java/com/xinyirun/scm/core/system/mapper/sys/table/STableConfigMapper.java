package com.xinyirun.scm.core.system.mapper.sys.table;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.table.STableConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;
import com.xinyirun.scm.bean.system.vo.sys.table.STableConfigVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
@Repository
public interface STableConfigMapper extends BaseMapper<STableConfigEntity> {

    /**
     * 查看页面查询列表
     */
    @Select("    "
            + "		SELECT                                                                                             "
            + "         *                                                                                              "
            + "		FROM                                                                                               "
            + "			s_table_config t1                                                                              "
            + "			WHERE TRUE                                                                                     "
            + "         AND t1.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                              "
            + "         AND t1.page_code = #{p1.page_code,jdbcType=VARCHAR}                                            "
            + "      ")
    STableConfigVo get(@Param("p1") STableColumnConfigVo searchCondition);

    @Select("SELECT                                                                                                    "
            + "    t.id,                                                                                               "
            + "    t.`code`,                                                                                           "
            + "    t.`name`,                                                                                           "
            + "    t.page_code,                                                                                        "
            + "    t.c_time,                                                                                           "
            + "    t.u_time,                                                                                           "
            + "    t1.name c_name,                                                                                     "
            + "    t2.name u_name                                                                                      "
            + "  FROM s_table_config t                                                                                 "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            + "  WHERE (t.`name` like concat('%', #{p1.name}, '%') or #{p1.name} is null or #{p1.name} = '')           "
            + "  AND (t.page_code like concat('%', #{p1.page_code}, '%') or #{p1.page_code} is null or #{p1.page_code} = '')"
    )
    IPage<STableConfigVo> selectPageList(Page<STableConfigVo> page, @Param("p1") STableConfigVo param);
}
