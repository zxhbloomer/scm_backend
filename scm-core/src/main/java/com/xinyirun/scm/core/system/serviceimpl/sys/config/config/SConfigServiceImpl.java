package com.xinyirun.scm.core.system.serviceimpl.sys.config.config;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigDataExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.redis.RedisUtil;
import com.xinyirun.scm.core.system.mapper.sys.config.config.SConfigMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.schedule.v5.ISBDUserPwdWarningV5Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 字典数据表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Slf4j
@Service
public class SConfigServiceImpl extends BaseServiceImpl<SConfigMapper, SConfigEntity> implements ISConfigService {

    @Autowired
    private SConfigMapper mapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    @Lazy
    private ISBDUserPwdWarningV5Service isbdUserPwdWarningV5Service;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<SConfigVo> selectPage(SConfigVo searchCondition) {
        // 分页条件
        Page<SConfigEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<SConfigDataExportVo> selectExportList(SConfigVo searchCondition) {
        // 查询 数据
        List<SConfigDataExportVo> list = mapper.selectExportList(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<SConfigEntity> selectIdsIn(List<SConfigVo> searchCondition) {
        // 查询 数据
        List<SConfigEntity> list = mapper.selectIdsIn(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<SConfigDataExportVo> selectIdsInForExport(List<SConfigVo> searchCondition) {
        // 查询 数据
        List<SConfigDataExportVo> list = mapper.selectIdsInForExport(searchCondition);
        return list;
    }

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
     * 批量导入逻辑
     *
     * @param entityList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatches(List<SConfigEntity> entityList) {
        return super.saveBatch(entityList, 500);
    }


    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(SConfigVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getConfig_key(), CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        SConfigEntity entity = (SConfigEntity) BeanUtilsSupport.copyProperties(vo, SConfigEntity.class);
        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        // 将数据库中所有SConfigEntity数据缓存到redis
        List<SConfigEntity> list = mapper.selectAllData();
        // 先删除缓存
        redisUtil.delete(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_CONFIG);
        // 更新缓存
        redisUtil.set(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_CONFIG, JSON.toJSONString(list));

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(SConfigVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getConfig_key(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);

        SConfigEntity entity = (SConfigEntity) BeanUtilsSupport.copyProperties(vo, SConfigEntity.class);

        // 将数据库中所有SConfigEntity数据缓存到redis
        List<SConfigEntity> list = mapper.selectAllData();
        // 先删除缓存
        redisUtil.delete(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_CONFIG);
        // 更新缓存
        redisUtil.set(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_CONFIG, JSON.toJSONString(list));

        int update = mapper.updateById(entity);

        // 调用用户密码预警
        if (SystemConstants.PWD_SWITCH.equals(vo.getConfig_key())){
            isbdUserPwdWarningV5Service.userPwdWarning(null,null);
        }
        return UpdateResultUtil.OK(update);
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

    @Override
    public SConfigVo getByKey(SConfigVo searchCondition) {
        return mapper.getByKey(searchCondition.getConfig_key());
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param value
     * @return
     */
    @Override
    public List<SConfigEntity> selectByValue(String value) {
        // 查询 数据
        List<SConfigEntity> list = mapper.selectByValue(value);
        return list;
    }


    /**
     * check逻辑
     *
     * @return
     */
    public CheckResultAo checkLogic(String name, String key, String moduleType) {
        List<SConfigEntity> selectByName = selectByName(name);
        List<SConfigEntity> selectByKey = mapper.selectListByKey(key);

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：参数名称出现重复", name);
                }
                if (selectByKey.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：参数键名出现重复", key);
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：参数名称出现重复", name);
                }
                if (selectByKey.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：参数键名出现重复", key);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }


    /**
     * 批量删除复原
     *
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DeleteResultAo<Integer> realDeleteByIdsIn(List<SConfigVo> searchCondition) {
        List<Long> idList = new ArrayList<>();
        searchCondition.forEach(bean -> {
            idList.add(bean.getId());
        });
        int result=mapper.deleteBatchIds(idList);
        return DeleteResultUtil.OK(result);
    }

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void enabledByIdsIn(List<SConfigVo> searchCondition) {
        List<SConfigEntity> list = mapper.selectIdsIn(searchCondition);
        for(SConfigEntity entity : list) {
//            CheckResultAo cr;
//            if(entity.getIs_enable()){
//                /** 如果逻辑删除为true，表示为：页面点击了复原操作 */
//                cr = checkLogic(entity, CheckResultAo.UNDELETE_CHECK_TYPE);
//            } else {
//                /** 如果逻辑删除为false，表示为：页面点击了删除操作 */
//                cr = checkLogic(entity, CheckResultAo.DELETE_CHECK_TYPE);
//            }
//            if (cr.isSuccess() == false) {
//                throw new BusinessException(cr.getMessage());
//            }
            entity.setIs_enable(!entity.getIs_enable());
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 初始化配置缓存
     * 1、删除缓存、2、查询数据、3、设置缓存
     */
    @Override
    public void initConfigCache() {
        try {
            String cacheKey = DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.CACHE_PC.CACHE_CONFIG;
            
            // 1、删除缓存
            redisUtil.delete(cacheKey);
            
            // 2、查询数据
            List<SConfigEntity> configList = mapper.selectAllData();
            
            // 3、设置缓存
            redisUtil.set(cacheKey, JSON.toJSONString(configList));
            log.debug("初始化配置缓存完成，共加载{}条配置", JSON.toJSONString(configList));
        } catch (Exception e) {
            log.warn("初始化配置缓存失败", e);
        }
    }

}
