package com.xinyirun.scm.core.system.serviceimpl.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictTypeEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictTypeExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictTypeVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.sys.config.dict.SDictTypeMapper;
import com.xinyirun.scm.core.system.service.sys.config.dict.ISDictTypeService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 字典类型表、字典主表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class SDictTypeServiceImpl extends BaseServiceImpl<SDictTypeMapper, SDictTypeEntity> implements ISDictTypeService {

    @Autowired
    private SDictTypeMapper mapper;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<SDictTypeEntity> selectPage(SDictTypeVo searchCondition) {
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
    public List<SDictTypeEntity> select(SDictTypeVo searchCondition) {
        // 查询 数据
        List<SDictTypeEntity> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<SDictTypeVo> selectIdsIn(List<SDictTypeVo> searchCondition) {
        // 查询 数据
        List<SDictTypeVo> list = mapper.selectIdsIn(searchCondition);
        return list;
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
    public boolean saveBatches(List<SDictTypeEntity> entityList) {
        return super.saveBatch(entityList, 500);
    }

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_DICT_TYPE, allEntries=true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIdsIn(List<SDictTypeVo> searchCondition) {
        List<SDictTypeVo> list = mapper.selectIdsIn(searchCondition);
        list.forEach(
            bean -> {
                bean.setIs_del(!bean.getIs_del());
            }
        );
        List<SDictTypeEntity> entities = BeanUtilsSupport.copyProperties(list, SDictTypeEntity.class);
        saveOrUpdateBatch(entities, 500);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_DICT_TYPE, 
                key = "T(com.xinyirun.scm.common.utils.datasource.DataSourceHelper).getCurrentDataSourceName() + '::' + #entity.code")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(SDictTypeEntity entity) {
        // 插入前check
        CheckResultAo cr = checkLogic(entity.getCode());
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        entity.setIs_del(false);
        // 插入逻辑保存
        return InsertResultUtil.OK(mapper.insert(entity));
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_DICT_TYPE, 
                key = "T(com.xinyirun.scm.common.utils.datasource.DataSourceHelper).getCurrentDataSourceName() + '::' + #entity.code")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(SDictTypeEntity entity) {
        // 更新前check
        CheckResultAo cr = checkLogic(entity.getCode());
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
     * @param code
     * @return
     */
    @Override
    public List<SDictTypeVo> selectByCode(String code) {
        // 查询 数据
        List<SDictTypeVo> list = mapper.selectByCode(code);
        return list;
    }

    /**
     * 为excel做的check，仅仅是为了尝试是否能够反射调用
     * @param vo
     * @return
     */
    @Override
    public Boolean testCheck(SDictTypeEntity vo) {
        return true;
    }

    /**
     * 部分 导出
     *
     * @param searchConditionList
     * @return
     */
    @Override
    public List<SDictTypeExportVo> selectListExport(List<SDictTypeVo> searchConditionList) {
        return mapper.selectListExport(searchConditionList);
    }

    /**
     * 全部导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<SDictTypeExportVo> selectAllExport(SDictTypeVo searchCondition) {
        return mapper.selectAllExport(searchCondition);
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkLogic(String _code){
        // code查重
        List<SDictTypeVo> list = selectByCode(_code);
        if(list.size() > 1){
            return CheckResultUtil.NG("字典类型出现重复", list);
        }

        return CheckResultUtil.OK();
    }
}
