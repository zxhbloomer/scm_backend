package com.xinyirun.scm.core.system.config.mybatis.typehandlers.pagesetting;

import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.serializer.JSONWriter.Feature;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000128Vo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ClassName: RP00000128TypeHandler
 * @Description: MyBatis处理JSON array类型，支持List<String>字段
 * @Author: zxh
 * @date: 2020/4/13
 * @Version: 1.0
 */
@MappedTypes(value = {P00000128Vo.class})
@MappedJdbcTypes(value = {JdbcType.VARCHAR}, includeNullJdbcType = true)
public class RP00000128TypeHandler<T> extends BaseTypeHandler<T> {

    private Class<T> clazz;

    public RP00000128TypeHandler(Class<T> clazz) {
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
//                        JSONWriter.Feature.QuoteFieldNames,
//                        JSONWriter.Feature.WriteDateUseDateFormat,
//                        JSONWriter.Feature.DisableCircularReferenceDetect,
                        JSONWriter.Feature.ReferenceDetection,
                        JSONWriter.Feature.WriteEnumUsingToString,
                        JSONWriter.Feature.WriteClassName)
        );
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String s) throws SQLException {
        try {
            return getObjectByJson(resultSet.getString(s));
        } catch (IllegalAccessException | InstantiationException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public T getNullableResult(ResultSet resultSet, int i) throws SQLException {
        try {
            return getObjectByJson(resultSet.getString(i));
        } catch (IllegalAccessException | InstantiationException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        try {
            return getObjectByJson(callableStatement.getString(i));
        } catch (IllegalAccessException | InstantiationException e) {
            throw new SQLException(e);
        }
    }

    private T getObjectByJson(String content) throws IllegalAccessException, InstantiationException {
        T result = JSON.parseObject(content, clazz);
//        if (result instanceof P00000128Vo) {
//            P00000128Vo vo = (P00000128Vo) result;
//            List<String> stringList = vo.getColumns_four();
//            if (stringList != null) {
//                for (int i = 0; i < stringList.size(); i++) {
//                    String value = stringList.get(i);
//                    if (value == null) {
//                        stringList.set(i, ""); // Replace null values with empty string
//                    }
//                }
//            }
//        }
        return result;
    }
}
