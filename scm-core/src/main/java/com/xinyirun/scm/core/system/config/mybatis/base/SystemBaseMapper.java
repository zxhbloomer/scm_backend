package com.xinyirun.scm.core.system.config.mybatis.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

/**
 * 自定义basemapper
 * @param <T>
 */
public interface SystemBaseMapper<T> extends BaseMapper<T> {

    /**
     * 根据 ID 修改，wms自定义
     *
     * @param entity 实体对象
     */
    int systemUpdateById(@Param(Constants.ENTITY) T entity);
}
