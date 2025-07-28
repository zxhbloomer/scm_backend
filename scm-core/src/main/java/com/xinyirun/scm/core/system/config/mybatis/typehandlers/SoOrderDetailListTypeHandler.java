package com.xinyirun.scm.core.system.config.mybatis.typehandlers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderDetailVo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 专门处理 List<BSoOrderDetailVo> 类型的 TypeHandler
 * 解决 MyBatis 中泛型类型擦除导致的类型转换问题
 * 
 * @author System
 * @since 2025-06-02
 */
public class SoOrderDetailListTypeHandler extends BaseTypeHandler<List<BSoOrderDetailVo>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<BSoOrderDetailVo> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public List<BSoOrderDetailVo> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonString = rs.getString(columnName);
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<List<BSoOrderDetailVo>>() {});
    }

    @Override
    public List<BSoOrderDetailVo> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonString = rs.getString(columnIndex);
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<List<BSoOrderDetailVo>>() {});
    }

    @Override
    public List<BSoOrderDetailVo> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonString = cs.getString(columnIndex);
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<List<BSoOrderDetailVo>>() {});
    }
}
