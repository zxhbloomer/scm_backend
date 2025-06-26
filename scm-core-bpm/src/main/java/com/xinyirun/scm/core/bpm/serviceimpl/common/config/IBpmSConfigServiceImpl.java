package com.xinyirun.scm.core.bpm.serviceimpl.common.config;

import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigVo;
import com.xinyirun.scm.common.utils.redis.RedisUtil;
import com.xinyirun.scm.core.bpm.mapper.common.config.BpmSConfigMapper;
import com.xinyirun.scm.core.bpm.service.common.config.IBpmSConfigService;
import com.xinyirun.scm.core.bpm.serviceimpl.base.v1.BpmBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class IBpmSConfigServiceImpl extends BpmBaseServiceImpl<BpmSConfigMapper, SConfigEntity> implements IBpmSConfigService {

    @Autowired
    private BpmSConfigMapper mapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    @Override
    public SConfigVo selectByid(Long id) {
        // 查询 数据
        return mapper.selectId(id);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    @Override
    public List<SConfigEntity> selectByName(String name) {
        // 查询 数据
        List<SConfigEntity> list = mapper.selectByName(name);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param key
     * @return
     */
    @Override
    public SConfigEntity selectByKey(String key) {
        // 查询 数据
        return mapper.selectByKey(key);
    }
}
