package com.xinyirun.scm.core.system.mapper.sys.table;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigDetailEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 表格列配置详情表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-08
 */
@Repository
public interface STableColumnConfigDetailMapper extends BaseMapper<STableColumnConfigDetailEntity> {

    /**
     * 根据config_id查询详情列表
     */
    @Select("SELECT id, config_id, table_code, table_id, name, label, is_enable, is_delete " +
            "FROM s_table_column_config_detail " +
            "WHERE config_id = #{configId} " +
            "ORDER BY id")
    List<STableColumnConfigDetailVo> listByConfigId(@Param("configId") Integer configId);

    /**
     * 根据多个config_id查询详情列表
     */
    @Select("SELECT id, config_id, table_code, table_id, name, label, is_enable, is_delete " +
            "FROM s_table_column_config_detail " +
            "WHERE config_id IN " +
            "<foreach collection='configIds' item='configId' open='(' separator=',' close=')'>" +
            "#{configId}" +
            "</foreach> " +
            "ORDER BY config_id, id")
    List<STableColumnConfigDetailVo> listByConfigIds(@Param("configIds") List<Integer> configIds);
}