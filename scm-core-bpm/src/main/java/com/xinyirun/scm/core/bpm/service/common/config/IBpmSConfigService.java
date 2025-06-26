package com.xinyirun.scm.core.bpm.service.common.config;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigVo;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务类
 * </p>
 *q
 * @author zxh
 * @since 2019-08-23
 */
public interface IBpmSConfigService extends IService<SConfigEntity> {

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    SConfigVo selectByid(Long id);

    /**
     * 通过name查询
     *
     */
    List<SConfigEntity> selectByName(String name);

    /**
     * 通过key查询
     *
     */
    SConfigEntity selectByKey(String key);

}
