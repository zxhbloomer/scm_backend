package com.xinyirun.scm.core.system.mapper.sys.table;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigOriginalEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigOriginalVo;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
@Repository
public interface STableColumnConfigOriginalMapper extends BaseMapper<STableColumnConfigOriginalEntity> {

    /**
     * 查询列表
     */
    @Select("    "
            + "		SELECT                                                                                              "
            + "			t1.id,                                                                                          "
            + "			t1.NAME,                                                                                        "
            + "			t1.label,                                                                                       "
            + "			t1.sort,                                                                                        "
            + "			t1.fix,                                                                                         "
            + "			t1.is_delete,                                                                                   "
            + "			#{p1.table_code,jdbcType=VARCHAR} table_code,                                                   "
            + "			#{p1.table_id,jdbcType=INTEGER} table_id,                                                       "
            + "			t2.NAME table_name,                                                                             "
            + "			t2.page_code,                                                                                   "
            + "			t2.type,                                                                                        "
            + "			t2.start_column_index,                                                                          "
            + "			t2.staff_id,                                                                                    "
            + "			t1.is_enable,                                                                                   "
            + "			t1.is_group                                                                                     "
            + "		FROM                                                                                                "
            + "			s_table_column_config_original t1                                                               "
            + "			INNER JOIN s_table_config t2 ON t1.page_code = t2.page_code                                     "
            + "			WHERE TRUE                                                                                      "
            + "         AND t2.page_code = #{p1.page_code,jdbcType=VARCHAR}                                             "
            + "         AND t2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                               "
            + "         ORDER BY t1.sort                                                                                "
            + "      ")
    List<STableColumnConfigVo> list(@Param("p1") STableColumnConfigVo searchCondition);

    /**
     * 查询列表
     */
    @Select("    "
            + "		SELECT                                                                                              "
            + "			t1.id,                                                                                          "
            + "			t3.NAME,                                                                                        "
            + "			t3.label,                                                                                       "
            + "			t3.sort,                                                                                        "
            + "			t3.fix,                                                                                         "
            + "			t2.NAME table_name,                                                                             "
            + "			t1.is_delete,                                                                                   "
            + "			t2.page_code,                                                                                   "
            + "			t2.type,                                                                                        "
            + "			t2.start_column_index,                                                                          "
            + "			t2.staff_id,                                                                                    "
            + "			t3.is_enable                                                                                    "
            + "		FROM                                                                                                "
            + "			s_table_column_config t1                                                                        "
            + "			INNER JOIN s_table_config t2 ON t1.table_code = t2.code                                         "
            + "			INNER JOIN s_table_column_config_original t3 ON t3.NAME = t1.NAME                               "
            +"              AND t3.label = t1.label AND t3.page_code = t2.page_code                                     "
            + "			WHERE TRUE                                                                                      "
            + "         AND t2.page_code = #{p1.page_code,jdbcType=VARCHAR}                                             "
            + "         AND t2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                               "
//            + "         AND t3.is_delete = false                                                                      "
            + "         ORDER BY t3.sort                                                                                "
            + "      ")
    List<STableColumnConfigOriginalVo> originallist(@Param("p1") STableColumnConfigOriginalVo searchCondition);

}
