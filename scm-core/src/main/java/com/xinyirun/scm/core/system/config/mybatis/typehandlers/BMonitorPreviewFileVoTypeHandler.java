package com.xinyirun.scm.core.system.config.mybatis.typehandlers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorPreviewFileVo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ClassName: JsonHandler
 * @Description: mybatis处理JSON array类型
 * @Author: zxh
 * @date: 2020/4/13
 * @Version: 1.0
 */
@MappedTypes(value = {BMonitorPreviewFileVo.class})
@MappedJdbcTypes(value = {JdbcType.VARCHAR}, includeNullJdbcType = true)
public class BMonitorPreviewFileVoTypeHandler<T> extends BaseTypeHandler<T> {

    private Class<T> clazz;

    public BMonitorPreviewFileVoTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, T parameter, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,
                JSON.toJSONString(parameter,
                        JSONWriter.Feature.WriteNullStringAsEmpty,
                        JSONWriter.Feature.WriteNullNumberAsZero,
                        JSONWriter.Feature.WriteNullBooleanAsFalse,
                        JSONWriter.Feature.WriteNullListAsEmpty,
//                    JSONWriter.Feature.QuoteFieldNames,
//                    JSONWriter.Feature.WriteDateUseDateFormat,
//                    JSONWriter.Feature.DisableCircularReferenceDetect,
                        JSONWriter.Feature.ReferenceDetection,
                        JSONWriter.Feature.WriteEnumUsingToString,
                        JSONWriter.Feature.WriteClassName)
        );
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String s) throws SQLException {
        try {
            return getObjcetByJson(resultSet.getString(s));
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        } catch (InstantiationException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public T getNullableResult(ResultSet resultSet, int i) throws SQLException {
        try {
            return getObjcetByJson(resultSet.getString(i));
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        } catch (InstantiationException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        try {
            return getObjcetByJson(callableStatement.getString(i));
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        } catch (InstantiationException e) {
            throw new SQLException(e);
        }
    }

    private T getObjcetByJson(String content) throws IllegalAccessException, InstantiationException {
        T rtn = JSON.parseObject(content,clazz);
        return rtn;
    }
}
