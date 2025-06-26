package com.xinyirun.scm.core.system.serviceimpl.sys.config.dict;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictTypeEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictTypeExportVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.sys.config.dict.SDictDataMapper;
import com.xinyirun.scm.core.system.service.sys.config.dict.ISDictDataService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
public class SDictDataServiceImpl extends BaseServiceImpl<SDictDataMapper, SDictDataEntity> implements ISDictDataService {

    @Autowired
    private SDictDataMapper mapper;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<SDictDataVo> selectPage(SDictDataVo searchCondition) {
        // 分页条件
        Page<SDictTypeEntity> pageCondition =
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
    public List<SDictDataVo> select(SDictDataVo searchCondition) {
        // 查询 数据
        List<SDictDataVo> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<SDictDataVo> selectIdsIn(List<SDictDataVo> searchCondition) {
        // 查询 数据
        List<SDictDataVo> list = mapper.selectIdsIn(searchCondition);
        return list;
    }

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    @Override
    public SDictDataVo selectByid(Long id) {
        // 查询 数据
        return mapper.selectId(id);
    }

    /**
     * 批量导入逻辑
     *
     * @param entityList
     * @return
     */
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_DICT_TYPE, allEntries=true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatches(List<SDictDataEntity> entityList) {
        return super.saveBatch(entityList, 500);
    }

    /**
     * 批量删除复原
     * 
     * @param searchCondition
     * @return
     */
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_DICT_TYPE, allEntries=true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIdsIn(List<SDictDataVo> searchCondition) {
        List<SDictDataVo> list = mapper.selectIdsIn(searchCondition);
        list.forEach(bean -> {
            bean.setIs_del(!bean.getIs_del());
        });
        List<SDictDataEntity> entityList = BeanUtilsSupport.copyProperties(list, SDictDataEntity.class);
        super.saveOrUpdateBatch(entityList, 500);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * 
     * @param entity 实体对象
     * @return
     */
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_DICT_TYPE, allEntries=true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(SDictDataEntity entity) {
        // 插入前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        // 设置：字典键值和字典排序
        SDictDataEntity data = mapper.getSortNum(entity.getDict_type_id());
        if (null == data) {
            entity.setSort(0);
        } else {
            entity.setSort(data.getSort());
        }
        entity.setIs_del(false);
        // 插入逻辑保存
        return InsertResultUtil.OK(mapper.insert(entity));
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * 
     * @param entity 实体对象
     * @return
     */
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_DICT_TYPE, allEntries=true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(SDictDataEntity entity) {
        // 更新前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
//        entity.setU_id(null);
//        entity.setU_time(null);
        return UpdateResultUtil.OK(mapper.updateById(entity));
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param dict_value
     * @return
     */
    public List<SDictDataEntity> selectByDictValue(String dict_value, Long dict_type_id, Long equal_id) {
        // 查询 数据
        List<SDictDataEntity> list = mapper.selectByDictValue(dict_value, dict_type_id, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param label
     * @return
     */
    public List<SDictDataEntity> selectByLabel(String label, Long dict_type_id, Long equal_id) {
        // 查询 数据
        List<SDictDataEntity> list = mapper.selectByLabel(label, dict_type_id, equal_id);
        return list;
    }

    /**
     * check逻辑
     * 
     * @return
     */
    public CheckResultAo checkLogic(SDictDataEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                List<SDictDataEntity> listDictValue_insertCheck = selectByDictValue(entity.getDict_value(), entity.getDict_type_id(), null);
//                List<SDictDataEntity> listLable_insertCheck = selectByLabel(entity.getLabel(), entity.getDict_type_id(), null);
                // 新增场合，不能重复
                if (listDictValue_insertCheck.size() >= 1) {
                    // 模块编号不能重复
                    return CheckResultUtil.NG("新增保存出错：字典键值出现重复", listDictValue_insertCheck);
                }
//                if (listLable_insertCheck.size() >= 1) {
//                     模块名称不能重复
//                    return CheckResultUtil.NG("新增保存出错：字典标签出现重复", listLable_insertCheck);
//                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                List<SDictDataEntity> listDictValue_updCheck = selectByDictValue(entity.getDict_value(), entity.getDict_type_id(), entity.getId());
//                List<SDictDataEntity> listLable_updCheck = selectByLabel(entity.getLabel(), entity.getDict_type_id(),  entity.getId());
                // 更新场合，不能重复设置
                if (listDictValue_updCheck.size() >= 1) {
                    // 模块编号不能重复
                    return CheckResultUtil.NG("更新保存出错：字典键值出现重复", listDictValue_updCheck);
                }
//                if (listLable_updCheck.size() >= 1) {
                    // 模块名称不能重复
//                    return CheckResultUtil.NG("更新保存出错：字典标签出现重复", listLable_updCheck);
//                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * sort保存
     *
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<List<SDictDataVo>> saveList(List<SDictDataVo> data) {
        List<SDictDataVo> resultList = new ArrayList<>();
        // 乐观锁 dbversion
        for(SDictDataVo bean:data){
            // 复制到新的entity
            SDictDataEntity entity = (SDictDataEntity)BeanUtilsSupport.copyProperties(bean, SDictDataEntity.class);
            UpdateResultAo updateResultAo = this.update(entity);
            if(updateResultAo.isSuccess()){
                SDictDataVo searchData = this.selectByid(bean.getId());
                resultList.add(searchData);
            } else {
                throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
            }
        }
        return UpdateResultUtil.OK(resultList);
    }

    /**
     * 获取表名称，通过字典表
     *
     */
    @Override
    public List<SDictDataVo> selectColumnComment(SDictDataVo searchCondition) {
        List<SDictDataVo> resultList = mapper.selectColumnComment(searchCondition);
        return resultList;
    }

    /**
     * 更新顺序, 上移, sort 字段值互换
     *
     * @param bean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_DICT_TYPE, allEntries=true)
    public void updateSortUp(SDictDataVo bean) {
        LambdaQueryWrapper<SDictDataEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SDictDataEntity::getCode, bean.getDictTypeCode())
                .orderBy(true, true, SDictDataEntity::getSort);
        // 根据code 查询, 包括删除的
        List<SDictDataEntity> list = mapper.selectList(wrapper);
        int sourceIndex = 0;
        int targetIndex = 0;
        // 遍历, 获取 源 的索引
        for (int i = 0; i < list.size(); i++) {
            if (bean.getId().equals(list.get(i).getId())) {
                sourceIndex = i;
            }
        }
        // 获取 前一个的位置, i 是 0, 不可上移
        if (sourceIndex != 0) {
            targetIndex = sourceIndex - 1;
        } else {
            // 如果等于0, 就是第一个, 不用上移
            return;
        }
        // 交换 sort
        SDictDataEntity source = list.get(sourceIndex);
        Integer sort = source.getSort();
        SDictDataEntity target = list.get(targetIndex);
        source.setSort(target.getSort());
        list.set(targetIndex, source);
        target.setSort(sort);
        list.set(sourceIndex, target);

        // 更新
        this.updateBatchById(list);
    }

    /**
     * 更新顺序, 下移  sort 字段值互换
     *
     * @param bean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_DICT_TYPE, allEntries=true)
    public void updateSortDown(SDictDataVo bean) {
        LambdaQueryWrapper<SDictDataEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SDictDataEntity::getCode, bean.getDictTypeCode())
                .orderBy(true, true, SDictDataEntity::getSort);
        // 根据code 查询, 包括删除的
        List<SDictDataEntity> list = mapper.selectList(wrapper);
        int sourceIndex = 0;
        int targetIndex = 0;
        // 遍历, 获取 源 的索引
        for (int i = 0; i < list.size(); i++) {
            if (bean.getId().equals(list.get(i).getId())) {
                sourceIndex = i;
            }
        }
        // 获取 前一个的位置, i 是 最大的, 不可下移
        if (sourceIndex != list.size()) {
            targetIndex = sourceIndex + 1;
        } else {
            // 如果是最大的, 就是最后一个, 不用下移
            return;
        }
        // 交换 sort
        SDictDataEntity source = list.get(sourceIndex);
        Integer sort = source.getSort();
        SDictDataEntity target = list.get(targetIndex);
        source.setSort(target.getSort());
        list.set(targetIndex, source);
        target.setSort(sort);
        list.set(sourceIndex, target);

        // 更新
        this.updateBatchById(list);
//        System.out.println(list);


    }

    /**
     * 导出 数据
     *
     * @param searchConditionList
     * @return
     */
    @Override
    public List<SDictDataExportVo> selectListExport(List<SDictDataVo> searchConditionList) {
        return mapper.selectListExport(searchConditionList);
    }

    /**
     * 全部数据导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<SDictDataExportVo> selectAllExport(SDictDataVo searchCondition) {
        return mapper.selectAllExport(searchCondition);
    }


    /**
     * 获取字典数据
     */
    @Override
    public List<SDictDataVo> selectData(SDictDataVo searchCondition){
        return mapper.select(searchCondition);
    }
}
