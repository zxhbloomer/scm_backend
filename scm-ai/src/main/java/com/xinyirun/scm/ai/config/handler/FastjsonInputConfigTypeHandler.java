package com.xinyirun.scm.ai.config.handler;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeInputConfigVo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Fastjson2 InputConfig TypeHandler
 * 专门处理 AiWfNodeInputConfigVo 类型的序列化和反序列化
 *
 * 替代 MyBatis Plus 的 JacksonTypeHandler，统一使用 Fastjson2
 * 参考 aideepin 的 NodeInputConfigTypeHandler 实现
 *
 * @author SCM-AI团队
 * @since 2025-10-25
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(AiWfNodeInputConfigVo.class)
public class FastjsonInputConfigTypeHandler extends BaseTypeHandler<AiWfNodeInputConfigVo> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, AiWfNodeInputConfigVo parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public AiWfNodeInputConfigVo getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return json == null ? null : JSON.parseObject(json, AiWfNodeInputConfigVo.class);
    }

    @Override
    public AiWfNodeInputConfigVo getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return json == null ? null : JSON.parseObject(json, AiWfNodeInputConfigVo.class);
    }

    @Override
    public AiWfNodeInputConfigVo getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return json == null ? null : JSON.parseObject(json, AiWfNodeInputConfigVo.class);
    }
}
