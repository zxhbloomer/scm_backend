package com.xinyirun.scm.core.app.serviceimpl.sys.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.app.vo.sys.config.AppSConfigVo;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.app.mapper.sys.config.AppSConfigMapper;
import com.xinyirun.scm.core.app.service.sys.config.AppISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
@Service
public class AppSConfigServiceImpl extends BaseServiceImpl<AppSConfigMapper, SConfigEntity> implements AppISConfigService {

    @Autowired
    private AppSConfigMapper mapper;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<AppSConfigVo> selectPage(AppSConfigVo searchCondition) {
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
    public List<AppSConfigVo> select(AppSConfigVo searchCondition) {
        // 查询 数据
        List<AppSConfigVo> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<SConfigEntity> selectIdsIn(List<AppSConfigVo> searchCondition) {
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
    public List<AppSConfigVo> selectIdsInForExport(List<AppSConfigVo> searchCondition) {
        // 查询 数据
        List<AppSConfigVo> list = mapper.selectIdsInForExport(searchCondition);
        return list;
    }

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    @Override
    public AppSConfigVo selectByid(Long id) {
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
    public InsertResultAo<Integer> insert(AppSConfigVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getConfig_key(), CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new AppBusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        SConfigEntity entity = (SConfigEntity) BeanUtilsSupport.copyProperties(vo, SConfigEntity.class);
        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

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
    public UpdateResultAo<Integer> update(AppSConfigVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getConfig_key(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new AppBusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);

        SConfigEntity entity = (SConfigEntity) BeanUtilsSupport.copyProperties(vo, SConfigEntity.class);
        return UpdateResultUtil.OK(mapper.updateById(entity));
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
    public AppSConfigVo selectByKey(String key) {
        // 查询 数据
        return mapper.selectByKey(key);
    }

    @Override
    public AppSConfigVo getByKey(AppSConfigVo searchCondition) {
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
    public DeleteResultAo<Integer> realDeleteByIdsIn(List<AppSConfigVo> searchCondition) {
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
    public void enabledByIdsIn(List<AppSConfigVo> searchCondition) {
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

}
