package com.xinyirun.scm.core.system.config.mybatis.typehandlers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundSourceVo;
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
 * @ClassName: PoContractDetailListTypeHandler
 * @Description: mybatis处理 List<BApRefundSourceVo> 类型
 * @Author: AI Assistant
 * @date: 2025/06/02
 * @Version: 1.0
 */
@MappedTypes(value = {List.class})
@MappedJdbcTypes(value = {JdbcType.VARCHAR}, includeNullJdbcType = true)
public class ApDetailListTypeHandler extends BaseTypeHandler<List<BApReFundSourceVo>> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<BApReFundSourceVo> jsonList, JdbcType jdbcType) throws SQLException {
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
    public List<BApReFundSourceVo> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return getJsonList(resultSet.getString(s));
    }

    @Override
    public List<BApReFundSourceVo> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return getJsonList(resultSet.getString(i));
    }

    @Override
    public List<BApReFundSourceVo> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return getJsonList(callableStatement.getString(i));
    }

    private List<BApReFundSourceVo> getJsonList(String content) {
        List<BApReFundSourceVo> jsonResult = new ArrayList<>();

        if (StringUtils.isNotBlank(content)) {
            try {
                List<BApReFundSourceVo> jsonList = JSON.parseObject(content, new TypeReference<List<BApReFundSourceVo>>(){});
                if (!NullUtil.isNull(jsonList)) {
                    jsonResult.addAll(jsonList);
                }
            } catch (Exception e) {
                // 如果解析失败，记录日志但不抛出异常，返回空列表
                System.err.println("Failed to parse JSON to List<BApRefundSourceVo>: " + content);
                e.printStackTrace();
            }
        }

        return jsonResult;
    }
}
