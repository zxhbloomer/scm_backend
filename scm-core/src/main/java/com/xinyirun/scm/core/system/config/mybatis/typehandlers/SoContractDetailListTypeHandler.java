package com.xinyirun.scm.core.system.config.mybatis.typehandlers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractDetailVo;
import com.xinyirun.scm.common.utils.NullUtil;
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
 * @ClassName: SoContractDetailListTypeHandler
 * @Description: mybatis处理 List<BSoContractDetailVo> 类型
 * @Author: AI Assistant
 * @date: 2025/01/22
 * @Version: 1.0
 */
@MappedTypes(value = {List.class})
@MappedJdbcTypes(value = {JdbcType.VARCHAR}, includeNullJdbcType = true)
public class SoContractDetailListTypeHandler extends BaseTypeHandler<List<BSoContractDetailVo>> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<BSoContractDetailVo> jsonList, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,
                JSON.toJSONString(jsonList,
                        JSONWriter.Feature.WriteNullStringAsEmpty,
                        JSONWriter.Feature.WriteNullNumberAsZero,
                        JSONWriter.Feature.WriteNullBooleanAsFalse,
                        JSONWriter.Feature.WriteNullListAsEmpty,
                        JSONWriter.Feature.ReferenceDetection,
                        JSONWriter.Feature.WriteEnumUsingToString,
                        JSONWriter.Feature.WriteClassName)
        );
    }

    @Override
    public List<BSoContractDetailVo> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return getJsonList(resultSet.getString(s));
    }

    @Override
    public List<BSoContractDetailVo> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return getJsonList(resultSet.getString(i));
    }

    @Override
    public List<BSoContractDetailVo> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return getJsonList(callableStatement.getString(i));
    }

    private List<BSoContractDetailVo> getJsonList(String content) {
        List<BSoContractDetailVo> jsonResult = new ArrayList<>();

        if (StringUtils.isNotBlank(content)) {
            try {
                List<BSoContractDetailVo> jsonList = JSON.parseObject(content, new TypeReference<List<BSoContractDetailVo>>(){});
                if (!NullUtil.isNull(jsonList)) {
                    jsonResult.addAll(jsonList);
                }
            } catch (Exception e) {
                // 如果解析失败，记录日志但不抛出异常，返回空列表
                System.err.println("Failed to parse JSON to List<BSoContractDetailVo>: " + content);
                e.printStackTrace();
            }
        }

        return jsonResult;
    }
}
