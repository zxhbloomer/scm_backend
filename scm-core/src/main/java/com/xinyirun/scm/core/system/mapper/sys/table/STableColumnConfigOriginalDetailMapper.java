package com.xinyirun.scm.core.system.mapper.sys.table;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigOriginalDetailEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 原始表格列配置详情 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
@Repository
public interface STableColumnConfigOriginalDetailMapper extends BaseMapper<STableColumnConfigOriginalDetailEntity> {

    /**
     * 根据原始配置ID查询详情数据
     */
    @Select("""
        SELECT 
            t1.id,
            t1.original_id,
            t1.table_code,
            t1.table_id,
            t1.NAME,
            t1.label,
            t1.sort,
            t1.is_enable,
            t1.is_delete
        FROM 
            s_table_column_config_original_detail t1
        WHERE 
            t1.original_id = #{originalId,jdbcType=INTEGER}
        ORDER BY t1.sort ASC
        """)
    List<STableColumnConfigVo> listByOriginalId(@Param("originalId") Integer originalId);

}