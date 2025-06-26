package com.xinyirun.scm.core.system.serviceimpl.master.goods;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.MBusinessTypeEntity;
import com.xinyirun.scm.bean.entity.master.goods.MCategoryEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.goods.MCategoryExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MCategoryVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.goods.MCategoryMapper;
import com.xinyirun.scm.core.system.service.master.goods.IMCategoryService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  类别service服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MCategoryServiceImpl extends BaseServiceImpl<MCategoryMapper, MCategoryEntity> implements IMCategoryService {

    @Autowired
    private MCategoryMapper mapper;

    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MCategoryVo> selectPage(MCategoryVo searchCondition) {
        // 分页条件
        Page<MBusinessTypeEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MCategoryVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(),  CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MCategoryEntity entity = (MCategoryEntity) BeanUtilsSupport.copyProperties(vo, MCategoryEntity.class);
        entity.setEnable(Boolean.TRUE);
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
    public UpdateResultAo<Integer> update(MCategoryVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);

        MCategoryEntity entity = (MCategoryEntity) BeanUtilsSupport.copyProperties(vo, MCategoryEntity.class);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public DeleteResultAo<Integer> deleteByIdsIn(List<MCategoryVo> searchCondition) {
        return null;
    }

    /**
     * 启用
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MCategoryVo> searchCondition) {
        List<MCategoryEntity> list = mapper.selectIdsIn(searchCondition);
        for(MCategoryEntity entity : list) {
            entity.setEnable(Boolean.TRUE);
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 停用
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disSabledByIdsIn(List<MCategoryVo> searchCondition) {
        List<MCategoryEntity> list = mapper.selectIdsIn(searchCondition);
        for(MCategoryEntity entity : list) {
            entity.setEnable(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MCategoryVo> searchCondition) {
        List<MCategoryEntity> list = mapper.selectIdsIn(searchCondition);
        for(MCategoryEntity entity : list) {
            entity.setEnable(!entity.getEnable());
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 导出
     *
     * @param searchConditionList 入参
     * @return List<MCategoryExportVo>
     */
    @Override
    public List<MCategoryExportVo> export(MCategoryVo searchConditionList) {
        return mapper.exportList(searchConditionList);
    }

    @Override
    public MCategoryVo selectById(int id) {
        return mapper.selectId(id);
    }

    @Override
    public List<MCategoryEntity> selectByName(String name) {
        // 查询 数据
        return mapper.selectByName(name);
    }


    /**
     * check逻辑
     *
     * @return
     */
    public CheckResultAo checkLogic(String name, String moduleType) {
        List<MCategoryEntity> selectByName = selectByName(name);
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", name);
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", name);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

}
