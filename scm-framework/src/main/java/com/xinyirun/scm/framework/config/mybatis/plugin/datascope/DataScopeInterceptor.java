package com.xinyirun.scm.framework.config.mybatis.plugin.datascope;


import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.xinyirun.scm.common.utils.collection.CollectionKit;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 数据范围的拦截器，数据权限
 *
 * @author zxh
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataScopeInterceptor implements Interceptor {

//    private static final String preState="/*!mycat:datanode=";
//    private static final String afterState="*/";

//    @Autowired
//    RedisUtil redisUtil;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");

        if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }

        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
        String originalSql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();

        //查找参数中包含DataScope类型的参数
        DataScope dataScope = findDataScopeObject(parameterObject);

//        String sql=(String)metaStatementHandler.getValue("delegate.boundSql.sql");
//        String node = (String)redisUtil.get("dnNode");
//        MUserEntity userinfo = null;
//        String node = null;
//        if (null != ServletUtil.getUserSession()) {
//            userinfo = ServletUtil.getUserSession().getUser_info();
//            if (null != userinfo.getAcc_name() && !"".equals(userinfo.getAcc_name())) {
//                node = (String) redisUtil.get("dnnode-"+userinfo.getAcc_name());
//            } else {
//                node = null;
//            }
//        }
//        if(node==null || "".equals(node)) {
//            node = "dn1";
//        }
//        sql = preState + node + afterState + sql;
//        System.out.println("sql is "+sql);
//        metaStatementHandler.setValue("delegate.boundSql.sql",sql);

        if (dataScope == null) {
            return invocation.proceed();
        } else {
            String scopeName = dataScope.getScopeName();
            List<Integer> deptIds = dataScope.getDeptIds();
            String join = CollectionKit.join(deptIds, ",");
            originalSql = "select * from (" + originalSql + ") temp_data_scope where temp_data_scope." + scopeName + " in (" + join + ")";
            metaStatementHandler.setValue("delegate.boundSql.sql", originalSql);
            return invocation.proceed();
        }
    }

    /**
     * 查找参数是否包括DataScope对象
     */
    public DataScope findDataScopeObject(Object parameterObj) {
        if (parameterObj instanceof DataScope) {
            return (DataScope) parameterObj;
        } else if (parameterObj instanceof Map) {
            for (Object val : ((Map<?, ?>) parameterObj).values()) {
                if (val instanceof DataScope) {
                    return (DataScope) val;
                }
            }
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
