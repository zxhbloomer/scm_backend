package com.xinyirun.scm.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.report.entity.JimuReport;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 积木报表Mapper接口
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Mapper
public interface JimuReportMapper extends BaseMapper<JimuReport> {
    
    /**
     * 根据报表编码查询报表信息
     */
    @Select("""
            SELECT * FROM jimu_report 
            WHERE code = #{code} AND del_flag = 0
            """)
    JimuReport getByCode(@Param("code") String code);
    
    /**
     * 根据报表ID查询完整信息（包含JSON数据）
     */
    @Select("""
            SELECT id, code, name, note, status, type, json_str, api_url, thumb,
                   template, view_count, css_str, js_str, create_by, create_time,
                   update_by, update_time 
            FROM jimu_report 
            WHERE id = #{id} AND del_flag = 0
            """)
    JimuReport getFullById(@Param("id") String id);
    
    /**
     * 查询报表列表（不包含JSON等大字段）
     */
    @Select("""
            SELECT id, code, name, note, status, type, api_url, thumb, template,
                   view_count, create_by, create_time, update_by, update_time
            FROM jimu_report 
            WHERE del_flag = 0 
            ORDER BY update_time DESC
            """)
    List<JimuReport> getReportList();
    
    
    /**
     * 更新浏览次数
     */
    @Update("""
            UPDATE jimu_report 
            SET view_count = view_count + 1 
            WHERE id = #{id}
            """)
    int updateViewCount(@Param("id") String id);
}