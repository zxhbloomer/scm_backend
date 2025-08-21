package com.xinyirun.scm.core.system.config.mybatis.typehandlers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleVo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 专门处理 List<SRoleVo.PermissionItem> 类型的 TypeHandler
 * 解决 MyBatis 中泛型类型擦除导致的类型转换问题
 * 
 * @author System
 * @since 2025-01-21
 */
public class PermissionItemListTypeHandler extends BaseTypeHandler<List<SRoleVo.PermissionItem>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<SRoleVo.PermissionItem> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public List<SRoleVo.PermissionItem> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonString = rs.getString(columnName);
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<List<SRoleVo.PermissionItem>>() {});
    }

    @Override
    public List<SRoleVo.PermissionItem> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonString = rs.getString(columnIndex);
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<List<SRoleVo.PermissionItem>>() {});
    }

    @Override
    public List<SRoleVo.PermissionItem> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonString = cs.getString(columnIndex);
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<List<SRoleVo.PermissionItem>>() {});
    }
}