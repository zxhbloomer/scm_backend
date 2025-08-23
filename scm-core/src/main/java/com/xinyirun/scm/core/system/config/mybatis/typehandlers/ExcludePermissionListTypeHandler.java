package com.xinyirun.scm.core.system.config.mybatis.typehandlers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo.PermissionItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 排除权限列表类型处理器
 * 用于处理MStaffVo中excludePermissionList字段的JSON序列化和反序列化
 * 
 * @author Claude Code
 * @date 2025-08-23
 */
@MappedTypes(value = {List.class})
@MappedJdbcTypes(value = {JdbcType.VARCHAR}, includeNullJdbcType = true)
public class ExcludePermissionListTypeHandler extends BaseTypeHandler<List<PermissionItem>> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<PermissionItem> permissionList, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,
                JSON.toJSONString(permissionList,
                        JSONWriter.Feature.WriteNullStringAsEmpty,
                        JSONWriter.Feature.WriteNullNumberAsZero,
                        JSONWriter.Feature.WriteNullBooleanAsFalse,
                        JSONWriter.Feature.WriteNullListAsEmpty,
                        JSONWriter.Feature.ReferenceDetection,
                        JSONWriter.Feature.WriteEnumUsingToString)
        );
    }

    @Override
    public List<PermissionItem> getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return parseExcludePermissionList(resultSet.getString(columnName));
    }

    @Override
    public List<PermissionItem> getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return parseExcludePermissionList(resultSet.getString(columnIndex));
    }

    @Override
    public List<PermissionItem> getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return parseExcludePermissionList(callableStatement.getString(columnIndex));
    }

    /**
     * 解析JSON字符串为排除权限列表
     * @param jsonContent JSON内容
     * @return 排除权限列表
     */
    private List<PermissionItem> parseExcludePermissionList(String jsonContent) {
        List<PermissionItem> permissionList = new ArrayList<>();
        
        if (StringUtils.isNotBlank(jsonContent)) {
            try {
                List<PermissionItem> parsedList = JSON.parseObject(jsonContent, new TypeReference<List<PermissionItem>>(){});
                if (parsedList != null) {
                    permissionList.addAll(parsedList);
                }
            } catch (Exception e) {
                // 记录异常但不抛出，返回空列表
                System.err.println("解析排除权限列表JSON失败: " + jsonContent + ", 错误: " + e.getMessage());
            }
        }
        
        return permissionList;
    }
}