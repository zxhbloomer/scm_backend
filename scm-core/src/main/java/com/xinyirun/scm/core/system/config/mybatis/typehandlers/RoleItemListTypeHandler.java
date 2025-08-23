package com.xinyirun.scm.core.system.config.mybatis.typehandlers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionVo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 专门处理 List<MPositionVo.RoleItem> 类型的 TypeHandler
 * 解决 MyBatis 中泛型类型擦除导致的类型转换问题
 * 用于岗位页面角色点击功能的JSON数据转换
 * 
 * @author System
 * @since 2025-01-22
 */
public class RoleItemListTypeHandler extends BaseTypeHandler<List<MPositionVo.RoleItem>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<MPositionVo.RoleItem> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public List<MPositionVo.RoleItem> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonString = rs.getString(columnName);
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<List<MPositionVo.RoleItem>>() {});
    }

    @Override
    public List<MPositionVo.RoleItem> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonString = rs.getString(columnIndex);
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<List<MPositionVo.RoleItem>>() {});
    }

    @Override
    public List<MPositionVo.RoleItem> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonString = cs.getString(columnIndex);
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<List<MPositionVo.RoleItem>>() {});
    }
}