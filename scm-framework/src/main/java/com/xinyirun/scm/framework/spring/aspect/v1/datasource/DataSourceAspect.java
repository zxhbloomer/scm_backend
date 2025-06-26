package com.xinyirun.scm.framework.spring.aspect.v1.datasource;

import com.xinyirun.scm.common.annotations.DataSourceAnnotion;
import com.xinyirun.scm.common.config.datasource.DynamicDataSourceContextHolder;
import com.xinyirun.scm.common.utils.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 多数据源处理
 *
 * @author
 */
@Aspect
@Order(1)
@Component
@Slf4j
public class DataSourceAspect {

    @Pointcut("@annotation(com.xinyirun.scm.common.annotations.DataSourceAnnotion)")
    public void dsPointCut() {

    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
//        MethodSignature signature = (MethodSignature)point.getSignature();
//
//        Method method = signature.getMethod();
//
//        DataSourceAnnotion dataSource = method.getAnnotation(DataSourceAnnotion.class);

        DataSourceAnnotion dataSource = getDataSource(point);

        if (StringUtils.isNotNull(dataSource)) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }

        try {
            return point.proceed();
        } finally {
            // 销毁数据源 在执行方法之后
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }


    /**
     * 获取需要切换的数据源
     */
    public DataSourceAnnotion getDataSource(ProceedingJoinPoint point)
    {
        MethodSignature signature = (MethodSignature) point.getSignature();
        DataSourceAnnotion dataSource = AnnotationUtils.findAnnotation(signature.getMethod(), DataSourceAnnotion.class);
        if (Objects.nonNull(dataSource))
        {
            return dataSource;
        }

        return AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSourceAnnotion.class);
    }
}
