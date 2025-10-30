package com.xinyirun.scm.framework.config.mybatis.plugin.datachange;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeMainClickHouseVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogVo;
import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.MpUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.redis.RedisUtil;
import com.xinyirun.scm.common.utils.reflection.ReflectionUtil;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.framework.config.event.define.DataChangeEvent;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.datachange.LogDataChangeProducer;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 数据变更拦截器：
 * 1、entity实体类加入注解@@DataChangeEntityAnnotation，type为main时，表示主要的数据，不需要记录变更
 * 2、拦截被注解的实体类的增删改操作，记录变更
 * 3、需要注意：拦截的是sql，不能去查数据，因为存在事务
 * 4、需要注意：在新增时，可能存在关联主表还没有被事务提交，
 *    所以需要考虑异步处理：先记录对比的两个bean的数据变更点，更新至mongodb，
 *                       提交1分钟后的定时任务，根据id，查询相应的ordercode，并更新到mongodb
 * 5、需要注意：在保存数据时，基本上每张表都有c_id、u_id，每张表都需要查询这两个id并获取名称。
 *
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class DataChangeInterceptor implements Interceptor {

    @Autowired
    LogDataChangeProducer producer;

    /**  发布事件 */
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    @Lazy
    private ISConfigService isConfigService;

//    /**
//     * 打破循环依赖
//     * https://www.baeldung.com/circular-dependencies-in-spring
//     */
//    @Autowired
//    public DataChangeInterceptor(
//            @Lazy ISConfigService isConfigService
//    ){
//        this.isConfigService = isConfigService;
//    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        DataChangeEntityAnnotation dataChange = isDataChangeEntityAnnotation(invocation);

        if(dataChange != null) {
            // 需要自行记录数据变更

            // 在 SQL 执行之前的逻辑：获取原始数据
            SDataChangeLogVo dataChangeVoBefore = beforeProcess(invocation, dataChange);

            // 执行 SQL
            Object returnValue = invocation.proceed();

            // 在 SQL 执行之后的逻辑
            SDataChangeLogVo dataChangeVoAfter = afterProcess(invocation, dataChange);
            // 设置数据变更的信息
            if(!Objects.isNull(dataChangeVoBefore) ){
                dataChangeVoAfter.setBeforeVo(dataChangeVoBefore.getBeforeVo());
            }

            // 从请求中获取requestId
            String requestId = getRequestId();

//            SConfigEntity config = isConfigService.selectByKey(SystemConstants.LOG_DATA_CHANGE);
            SConfigEntity config = null;
            // 现获取redis缓存
            String jsonList = redisUtil.getString(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_CONFIG);
            if (StringUtils.isNotEmpty(jsonList)) {
                List<SConfigEntity> list = JSONArray.parseArray(jsonList, SConfigEntity.class);
                // 从list中获取config_key为LOG_DATA_CHANGE的数据
                config = list.stream().filter(item -> item.getConfig_key().equals(SystemConstants.LOG_DATA_CHANGE)).findFirst().orElse(null);
            } else {
                // 如果redis中没有，则从数据库中获取
                config = isConfigService.selectByKey(SystemConstants.LOG_DATA_CHANGE);
            }

//        if ("1".equals(SystemConstants.LOG_DATA_CHANGE_OPEN)) {
            if (config != null && "1".equals(config.getValue())) {
                // 发送mq，异步处理
                if("main".equals(dataChange.type())){

                    Object[] args = invocation.getArgs();
                    MappedStatement ms = (MappedStatement) args[0];
                    SqlCommandType sqlCommandType = ms.getSqlCommandType();
                    // 获取 SQL 语句的细节
                    BoundSql boundSql = ms.getBoundSql(args[1]);
                    SLogDataChangeMainClickHouseVo dataChangeMain = afterProcessInsertMain(boundSql, dataChange, sqlCommandType.toString());
                    dataChangeMain.setRequest_id(requestId);
                    applicationEventPublisher.publishEvent(new DataChangeEvent(this, dataChangeMain));
                } else {
                    if (dataChangeVoAfter != null) {
                        dataChangeVoAfter.setRequest_id(requestId);
                        applicationEventPublisher.publishEvent(new DataChangeEvent(this, dataChangeVoAfter));
                    }
                }
            }

            return returnValue;
        } else {
            // 不需要自行记录数据变更

            // 执行 SQL
            return invocation.proceed();
        }
    }
    /**
     * 执行前的操作
     * @param invocation
     */
    private SDataChangeLogVo beforeProcess(Invocation invocation, DataChangeEntityAnnotation dataChange) {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        // 获取 SQL 语句的细节
        BoundSql boundSql = ms.getBoundSql(args[1]);

        // 您的后处理逻辑
        // 比如记录数据库变动
        if (sqlCommandType == SqlCommandType.INSERT ||
                sqlCommandType == SqlCommandType.UPDATE ||
                sqlCommandType == SqlCommandType.DELETE) {

            try {
                Statement statement = null;
                try {
                    // 预处理SQL：压缩连续空白为单个空格，提高解析成功率
                    String cleanedSql = boundSql.getSql().replaceAll("\\s+", " ").trim();
                    statement = CCJSqlParserUtil.parse(cleanedSql, parser -> {
                        parser.withAllowComplexParsing(true);
                    });
                } catch (JSQLParserException e) {
                    log.error("sql无法解析,可能是DDL相关语句, SQL语句:{}", boundSql.getSql());
                }

                if (statement instanceof Insert) {
                    log.debug("新增的数据，在数据变化表中的前步骤可以不考虑");
                    return null;
                } else if (statement instanceof Update) {
                    return beforeProcessUpdate(boundSql, dataChange, sqlCommandType.toString());
                } else if (statement instanceof Delete) {
                    return beforeProcessDelete(boundSql, dataChange, sqlCommandType.toString());
                }
            } catch (Exception e) {
                log.error("Unexpected error for mappedStatement={}, sql={}", ms.getId(), boundSql.getSql(), e);
            }
        }
        return null;
    }


    /**
     * 执行后的操作
     * @param invocation
     */
    private SDataChangeLogVo afterProcess(Invocation invocation, DataChangeEntityAnnotation dataChange) {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        // 获取 SQL 语句的细节
        BoundSql boundSql = ms.getBoundSql(args[1]);

        // 您的后处理逻辑
        // 比如记录数据库变动
        if (sqlCommandType == SqlCommandType.INSERT ||
                sqlCommandType == SqlCommandType.UPDATE ||
                sqlCommandType == SqlCommandType.DELETE) {
            try {
                Statement statement = null;
                try {
                    // 预处理SQL：压缩连续空白为单个空格，提高解析成功率
                    String cleanedSql = boundSql.getSql().replaceAll("\\s+", " ").trim();
                    statement = CCJSqlParserUtil.parse(cleanedSql, parser -> {
                        parser.withAllowComplexParsing(true);
                    });
                } catch (JSQLParserException e) {
                    log.error("sql无法解析,可能是DDL相关语句, SQL语句:{}", boundSql.getSql());
                }
                if (statement instanceof Insert) {
                    return afterProcessInsert(boundSql, dataChange, sqlCommandType.toString());
                } else if (statement instanceof Update) {
                    return afterProcessUpdate(boundSql, dataChange, sqlCommandType.toString());
                } else if (statement instanceof Delete) {
                    log.debug("删除处理");
                    return new SDataChangeLogVo();
//                    return null;
                }
            } catch (Exception e) {
                log.error("Unexpected error for mappedStatement={}, sql={}", ms.getId(), boundSql.getSql(), e);
            }
        }
        return null;
    }

    /**
     * 判断哪些SQL需要处理
     */
    protected DataChangeEntityAnnotation getDataVersionAnno(Statement statement) {
        String tableName;
        if (statement instanceof Insert) {
            tableName = ((Insert) statement).getTable().getName();
        } else if (statement instanceof Update) {
            tableName = ((Update) statement).getTable().getName();
        } else if (statement instanceof Delete) {
            tableName = ((Delete) statement).getTable().getName();
        } else {
            return null;
        }

        log.debug("解析sql，得到更新的表名：{}",tableName);

        TableInfo tableInfo = MpUtil.getTableInfo(tableName);
        if (Objects.isNull(tableInfo)) {
            return null;
        }
        return tableInfo.getEntityType().getAnnotation(DataChangeEntityAnnotation.class);
    }

    /**
     * 执行前(更新)取数据的逻辑
     * @param boundSql
     * @param dataChange
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    public SDataChangeLogVo beforeProcessUpdate(BoundSql boundSql, DataChangeEntityAnnotation dataChange, String sqlCommandType) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if("main".equals(dataChange.type())){
            return null;
        } else {
            SDataChangeLogVo dataChangeVo = new SDataChangeLogVo();
            String _class_name = dataChange.type();
            String _functionName = "getDataChangeVoByUpdateBefore";
            Object arg1 = boundSql;
            SDataChangeLogDetailVo beforeVo = new SDataChangeLogDetailVo();
            beforeVo = (SDataChangeLogDetailVo) ReflectionUtil.invokex(_class_name, _functionName, arg1);
            dataChangeVo.setBeforeVo(beforeVo);
            dataChangeVo.setSqlCommandType(sqlCommandType);
            dataChangeVo.setName(dataChange.value());
            if(beforeVo != null) {
                dataChangeVo.setTableColumns(beforeVo.getTableColumns());
                dataChangeVo.setClass_name(beforeVo.getClass_name());
                dataChangeVo.setEntity_name(beforeVo.getEntity_name());
                dataChangeVo.setTable_id(beforeVo.getTable_id());
                dataChangeVo.setOrder_code(beforeVo.getOrder_code());
                dataChangeVo.setTable_name(beforeVo.getTableColumns().getTable_name());
            }
            return dataChangeVo;
        }
    }

    /**
     * 执行前(删除)取数据的逻辑
     * @param boundSql
     * @param dataChange
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    public SDataChangeLogVo beforeProcessDelete(BoundSql boundSql, DataChangeEntityAnnotation dataChange, String sqlCommandType) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if("main".equals(dataChange.type())){
            // 不存在这个可能
            return null;
        } else {
            SDataChangeLogVo dataChangeVo = new SDataChangeLogVo();
            String _class_name = dataChange.type();
            String _functionName = "getDataChangeVoByDelete";
            Object arg1 = boundSql;
            SDataChangeLogDetailVo beforeVo = new SDataChangeLogDetailVo();
            beforeVo = (SDataChangeLogDetailVo) ReflectionUtil.invokex(_class_name, _functionName, arg1);
            dataChangeVo.setBeforeVo(beforeVo);
            dataChangeVo.setSqlCommandType(sqlCommandType);
            dataChangeVo.setName(dataChange.value());
            if(beforeVo != null) {
                dataChangeVo.setTableColumns(beforeVo.getTableColumns());
                dataChangeVo.setClass_name(beforeVo.getClass_name());
                dataChangeVo.setEntity_name(beforeVo.getEntity_name());
                dataChangeVo.setTable_id(beforeVo.getTable_id());
                dataChangeVo.setOrder_code(beforeVo.getOrder_code());
                dataChangeVo.setTable_name(beforeVo.getTableColumns().getTable_name());
            }
            return dataChangeVo;
        }
    }


    /**
     * 执行后（新增）取数据的逻辑
     * @param boundSql
     * @param dataChange
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    public SLogDataChangeMainClickHouseVo afterProcessInsertMain(BoundSql boundSql, DataChangeEntityAnnotation dataChange, String sqlCommandType) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if("main".equals(dataChange.type())){
            // 主要的数据：编码表：生成编号时
            SLogDataChangeMainClickHouseVo dataChangeMain = new SLogDataChangeMainClickHouseVo();
            String _code = getSpecifiedColumnValue(boundSql,"CODE").toString();
            String _type = getSpecifiedColumnValue(boundSql,"TYPE").toString();
            String _name = getSpecifiedColumnValue(boundSql,"NAME").toString();
            LocalDateTime _c_time = convertToLocalDateTime(getSpecifiedColumnValue(boundSql,"C_TIME"));
            LocalDateTime _u_time = convertToLocalDateTime(getSpecifiedColumnValue(boundSql,"U_TIME"));
            String _u_id =  getSpecifiedColumnValue(boundSql,"u_id").toString();
            dataChangeMain.setOrder_code(_code);
            dataChangeMain.setOrder_type(_type);
            dataChangeMain.setName(_name);
            dataChangeMain.setC_time(_c_time);
            dataChangeMain.setU_time(_u_time);
            dataChangeMain.setU_id(_u_id);
            // 从请求中获取requestId
//            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//            String requestId = (String) attr.getRequest().getAttribute(SystemConstants.REQUEST_ID);
            String requestId = getRequestId();
            dataChangeMain.setRequest_id(requestId);
            return dataChangeMain;
        } else {
            return null;
        }
    }

    public SDataChangeLogVo afterProcessInsert(BoundSql boundSql, DataChangeEntityAnnotation dataChange, String sqlCommandType) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if("main".equals(dataChange.type())){
            return null;
        } else {
            SDataChangeLogVo dataChangeVo = new SDataChangeLogVo();
            String _class_name = dataChange.type();
            String _functionName = "getDataChangeVoByInsert";
            Object arg1 = boundSql;
            SDataChangeLogDetailVo afterVo = new SDataChangeLogDetailVo();
            afterVo = (SDataChangeLogDetailVo) ReflectionUtil.invokex(_class_name, _functionName, arg1);
            dataChangeVo.setAfterVo(afterVo);
            dataChangeVo.setSqlCommandType(sqlCommandType);
            dataChangeVo.setName(dataChange.value());
            if(afterVo != null) {
                dataChangeVo.setTableColumns(afterVo.getTableColumns());
                dataChangeVo.setClass_name(afterVo.getClass_name());
                dataChangeVo.setEntity_name(afterVo.getEntity_name());
                dataChangeVo.setTable_id(afterVo.getTable_id());
                dataChangeVo.setOrder_code(afterVo.getOrder_code());
                dataChangeVo.setTable_name(afterVo.getTableColumns().getTable_name());
            }
            return dataChangeVo;
        }
    }

    /**
     * 执行后（更新）取数据的逻辑
     * @param boundSql
     * @param dataChange
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    public SDataChangeLogVo afterProcessUpdate(BoundSql boundSql, DataChangeEntityAnnotation dataChange, String sqlCommandType) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if("main".equals(dataChange.type())){
            // 不存在这个可能
            return null;
        } else {
            SDataChangeLogVo dataChangeVo = new SDataChangeLogVo();
            String _class_name = dataChange.type();
            String _functionName = "getDataChangeVoByUpdateAfter";
            Object arg1 = boundSql;
            SDataChangeLogDetailVo afterVo = new SDataChangeLogDetailVo();
            afterVo = (SDataChangeLogDetailVo) ReflectionUtil.invokex(_class_name, _functionName, arg1);
            dataChangeVo.setAfterVo(afterVo);
            dataChangeVo.setSqlCommandType(sqlCommandType);
            dataChangeVo.setName(dataChange.value());
            if(afterVo != null) {
                dataChangeVo.setTableColumns(afterVo.getTableColumns());
                dataChangeVo.setClass_name(afterVo.getClass_name());
                dataChangeVo.setEntity_name(afterVo.getEntity_name());
                dataChangeVo.setTable_id(afterVo.getTable_id());
                dataChangeVo.setOrder_code(afterVo.getOrder_code());
                dataChangeVo.setTable_name(afterVo.getTableColumns().getTable_name());
            }
            return dataChangeVo;
        }
    }

    /**
     * 安全地将对象转换为LocalDateTime
     */
    private LocalDateTime convertToLocalDateTime(Object value) {
        if (value == null || "".equals(value)) {
            return null;
        }
        
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.trim().isEmpty()) {
                return null;
            }
            try {
                // 尝试解析常见的日期时间格式
                if (strValue.length() == 19) { // yyyy-MM-dd HH:mm:ss
                    return LocalDateTime.parse(strValue, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } else if (strValue.length() > 19) { // 包含毫秒的格式
                    return LocalDateTime.parse(strValue.substring(0, 19), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                // 其他ISO格式
                return LocalDateTime.parse(strValue);
            } catch (Exception e) {
                log.warn("无法解析日期时间字符串: {}", strValue);
                return null;
            }
        }
        
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        
        if (value instanceof java.util.Date) {
            return LocalDateTime.ofInstant(((java.util.Date) value).toInstant(), java.time.ZoneId.systemDefault());
        }
        
        log.warn("无法将类型 {} 转换为 LocalDateTime: {}", value.getClass().getName(), value);
        return null;
    }

    /**
     * 获取指定列的值
     */
    private Object getSpecifiedColumnValue(BoundSql boundSql, String columnName) {
        MetaObject metaObject = SystemMetaObject.forObject(boundSql.getParameterObject());

        // 遍历 BoundSql 中的每个 ParameterMapping
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            String propertyName = parameterMapping.getProperty();

            // 跳过特殊的 MyBatis-Plus 参数
            if (propertyName.startsWith("ew.paramNameValuePairs")) {
                continue;
            }

            // 分割 propertyName 并处理
            String[] arr = propertyName.split("\\.");
            String propertyNameTrim = arr[arr.length - 1].toUpperCase();

            // 如果处理后的 propertyNameTrim 等于指定列名，返回相应的值
            if (columnName.toUpperCase().equals(propertyNameTrim)) {
                Object value = metaObject.getValue(propertyName);
                return value == null ? "" : value; // 直接返回对象，不进行字符串转换
            }
        }

        return "";
    }

    /**
     * 判断是否需要进行处理
     *
     * @param invocation
     * @return
     */
    private DataChangeEntityAnnotation isDataChangeEntityAnnotation(Invocation invocation) {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        // 获取 SQL 语句的细节
        BoundSql boundSql = ms.getBoundSql(args[1]);

        Statement statement = null;
        try {
            // 预处理SQL：压缩连续空白为单个空格，提高解析成功率
            String cleanedSql = boundSql.getSql().replaceAll("\\s+", " ").trim();
            statement = CCJSqlParserUtil.parse(cleanedSql, parser -> {
                parser.withAllowComplexParsing(true);
            });
        } catch (JSQLParserException e) {
            log.warn("sql无法解析,可能是DDL相关语句, SQL语句:{}", boundSql.getSql());
            log.error("isDataChangeEntityAnnotation error", e);
            return null;
        }
        DataChangeEntityAnnotation dataChange;
        // 判断是否需要进行处理
        dataChange = this.getDataVersionAnno(statement);
        if (Objects.isNull(dataChange)) {
            return null;
        }
        return dataChange;
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 设置属性
    }

    /**
     * 获取 requestId
     * @return
     */
    private String getRequestId() {
        String requestId = null;
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            requestId = (String) attr.getRequest().getAttribute(SystemConstants.REQUEST_ID);
            return requestId;
        } catch (Exception e) {
            log.debug("定时任务无法获取requestId, 此错误定义为常量,");
            requestId = "定时任务常量";
            return requestId;
        }

    }
}
