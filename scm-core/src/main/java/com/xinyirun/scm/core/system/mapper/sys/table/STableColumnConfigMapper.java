package com.xinyirun.scm.core.system.mapper.sys.table;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.TableColumnDetailListTypeHandler;
import org.apache.ibatis.annotations.*;
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
public interface STableColumnConfigMapper extends BaseMapper<STableColumnConfigEntity> {

    /**
     * 查看页面查询列表
     */
    @Select("""
        SELECT 
            t1.id,
            t1.NAME,
            t1.label,
            t1.sort,
            t1.fix,
            t2.NAME table_name,
            t2.page_code,
            t2.type,
            t2.start_column_index,
            t2.staff_id,
            t1.is_enable,
            t1.is_delete,
            t3.max_sort,
            t3.min_sort,
            t1.is_group
        FROM 
            s_table_column_config t1
            INNER JOIN s_table_config t2 ON t1.table_code = t2.CODE
            INNER JOIN (
                SELECT 
                    max(subt1.sort) AS max_sort,
                    min(subt1.sort) AS min_sort,
                    subt2.staff_id,
                    subt1.table_code
                FROM 
                    s_table_column_config subt1
                    INNER JOIN s_table_config subt2 ON subt1.table_code = subt2.CODE
                WHERE subt1.fix = FALSE
                GROUP BY subt1.table_code, subt2.staff_id
            ) t3 ON t1.table_code = t3.table_code AND t2.staff_id = t3.staff_id
        WHERE TRUE
            AND t2.staff_id = #{p1.staff_id,jdbcType=INTEGER}
            AND t2.page_code = #{p1.page_code,jdbcType=VARCHAR}
            AND (t1.is_enable = #{p1.is_enable,jdbcType=BOOLEAN} or #{p1.is_enable,jdbcType=BOOLEAN} is null)
        ORDER BY t1.sort
        """)
    List<STableColumnConfigVo> list(@Param("p1") STableColumnConfigVo searchCondition);

    /**
     * 删除列表数据
     */
    @Select("""
        DELETE t1
        FROM 
            s_table_column_config t1
            INNER JOIN s_table_config t2 ON t1.table_code = t2.CODE
        WHERE TRUE
            AND t2.staff_id = #{p1.staff_id,jdbcType=INTEGER}
            AND t2.page_code = #{p1.page_code,jdbcType=VARCHAR}
        """)
    void delete(@Param("p1") STableColumnConfigVo searchCondition);

    /**
     * check用户数据是否与original数据一致
     */
    @Select("""
        SELECT 
            tab1.count1 = tab2.count2 flag
        FROM 
            (
                SELECT 
                    count(1) count1
                FROM 
                    s_table_column_config t1
                    INNER JOIN s_table_config t3 ON t1.table_id = t3.id
                    INNER JOIN s_table_column_config_original t2 ON t1.NAME = t2.NAME
                        AND t1.label = t2.label
                        AND t1.fix = t2.fix
                        AND t3.page_code = t2.page_code
                WHERE 
                    t3.staff_id = #{p1.staff_id,jdbcType=INTEGER}
                    AND t3.page_code = #{p1.page_code,jdbcType=VARCHAR}
            ) tab1,
            (
                SELECT count(1) count2 
                FROM s_table_column_config_original t1
                WHERE t1.page_code = #{p1.page_code,jdbcType=VARCHAR}
            ) tab2
        """)
    Boolean check(@Param("p1") STableColumnConfigVo condition);


    /**
     * 查询指定分组的详情数据
     */
    @Select("""
        SELECT 
            t1.id,
            t1.config_id,
            t1.table_code,
            t1.table_id,
            t1.NAME,
            t1.label,
            t1.is_enable,
            t1.is_delete,
            t1.sort
        FROM 
            s_table_column_config_detail t1
            INNER JOIN s_table_column_config t2 ON t1.config_id = t2.id
            INNER JOIN s_table_config t3 ON t2.table_code = t3.CODE
        WHERE TRUE
            AND t3.staff_id = #{staffId,jdbcType=INTEGER}
            AND t3.page_code = #{pageCode,jdbcType=VARCHAR}
            AND t2.id = #{configId,jdbcType=INTEGER}
        ORDER BY t1.sort
        """)
    List<STableColumnConfigVo> listGroupChildren(@Param("staffId") Integer staffId, 
                                                 @Param("pageCode") String pageCode, 
                                                 @Param("configId") Integer configId);

    /**
     * 查询页面列配置主表数据
     * 分组详情数据在ServiceImpl中单独查询和设置
     */
    @Select("""
        SELECT 
            t1.id,
            t1.NAME,
            t1.label,
            t1.sort,
            t1.fix,
            t2.NAME table_name,
            t2.page_code,
            t2.type,
            t2.start_column_index,
            t2.staff_id,
            t1.is_enable,
            t1.is_delete,
            t1.is_group
        FROM 
            s_table_column_config t1
            INNER JOIN s_table_config t2 ON t1.table_code = t2.CODE
        WHERE TRUE
            AND t2.staff_id = #{p1.staff_id,jdbcType=INTEGER}
            AND t2.page_code = #{p1.page_code,jdbcType=VARCHAR}
            AND (t1.is_enable = #{p1.is_enable,jdbcType=BOOLEAN} or #{p1.is_enable,jdbcType=BOOLEAN} is null)
        ORDER BY t1.sort
        """)
    List<STableColumnConfigVo> listWithDetails(@Param("p1") STableColumnConfigVo searchCondition);

}
