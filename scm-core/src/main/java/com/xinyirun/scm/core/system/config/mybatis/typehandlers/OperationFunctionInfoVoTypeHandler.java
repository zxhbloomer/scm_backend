package com.xinyirun.scm.core.system.config.mybatis.typehandlers;

import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.serializer.JSONWriter.Feature;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationFunctionInfoVo;
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
 * @ClassName: JsonHandler
 * @Description: mybatis处理JSON array类型
 * @Author: zxh
 * @date: 2020/4/13
 * @Version: 1.0
 */
@MappedTypes(value = {List.class})
@MappedJdbcTypes(value = {JdbcType.VARCHAR}, includeNullJdbcType = true)
public class OperationFunctionInfoVoTypeHandler<T> extends BaseTypeHandler<List<T>> {

    private Class<T> clazz;

    public OperationFunctionInfoVoTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<T> jsonList, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,
                JSON.toJSONString(jsonList,
                        JSONWriter.Feature.WriteNullStringAsEmpty,
                        JSONWriter.Feature.WriteNullNumberAsZero,
                        JSONWriter.Feature.WriteNullBooleanAsFalse,
                        JSONWriter.Feature.WriteNullListAsEmpty,
//                JSONWriter.Feature.QuoteFieldNames,
//                JSONWriter.Feature.WriteDateUseDateFormat,
//                JSONWriter.Feature.DisableCircularReferenceDetect,
                        JSONWriter.Feature.ReferenceDetection,
                        JSONWriter.Feature.WriteEnumUsingToString,
                        JSONWriter.Feature.WriteClassName)
        );
    }

    @Override
    public List<T> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return getJsonList(resultSet.getString(s));
    }

    @Override
    public List<T> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return getJsonList(resultSet.getString(i));
    }

    @Override
    public List<T> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return getJsonList(callableStatement.getString(i));
    }

    private List<T> getJsonList(String content) {
        List<T> jsonResult = new ArrayList<>();

        if (StringUtils.isNotBlank(content)) {
            List<T> jsonList = JSON.parseObject(content, new com.alibaba.fastjson2.TypeReference<List<T>>(){});
            if (!NullUtil.isNull(jsonList)) {
                jsonResult.addAll(jsonList);
            }
//            jsonResult = JSON.parseArray(content, clazz);
        }

        return jsonResult;
    }
}
