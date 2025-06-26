package com.xinyirun.scm.core.system.config.mybatis.sqlinjector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.xinyirun.scm.core.system.config.mybatis.sqlinjector.methods.SystemUpdateById;

import java.util.List;

/**
 * 自定义Sql注入
 */
public class SystemSqlInjector extends DefaultSqlInjector {
    /**
     * 如果只需增加方法，保留MP自带方法
     * 可以super.getMethodList() 再add
     * @return
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        //增加自定义方法
        methodList.add(new SystemUpdateById());
        return methodList;
    }
}
