package com.xinyirun.scm.core.system.serviceimpl.master.goods;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.MIndustryEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.goods.MIndustryExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MIndustryVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.goods.MIndustryMapper;
import com.xinyirun.scm.core.system.service.master.goods.IMIndustryService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  行业service服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MIndustryServiceImpl extends BaseServiceImpl<MIndustryMapper, MIndustryEntity> implements IMIndustryService {

    @Autowired
    private MIndustryMapper mapper;

    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MIndustryVo> selectPage(MIndustryVo searchCondition) {
        // 分页条件
        Page<MIndustryEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MIndustryVo> searchCondition) {
        List<MIndustryEntity> list = mapper.selectIdsIn(searchCondition);
        for(MIndustryEntity entity : list) {
            entity.setEnable(!entity.getEnable());
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 导出
     *
     * @param searchConditionList 入参
     * @return List<MIndustryExportVo>
     */
    @Override
    public List<MIndustryExportVo> export(MIndustryVo searchConditionList) {
        return mapper.exportList(searchConditionList);
    }


    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MIndustryVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(), CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MIndustryEntity entity = (MIndustryEntity) BeanUtilsSupport.copyProperties(vo, MIndustryEntity.class);
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
    public UpdateResultAo<Integer> update(MIndustryVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);
        MIndustryEntity entity = (MIndustryEntity) BeanUtilsSupport.copyProperties(vo, MIndustryEntity.class);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public DeleteResultAo<Integer> deleteByIdsIn(List<MIndustryVo> searchCondition) {
        return null;
    }

    /**
     * 启用
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MIndustryVo> searchCondition) {
        List<MIndustryEntity> list = mapper.selectIdsIn(searchCondition);
        for(MIndustryEntity entity : list) {
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
    public void disSabledByIdsIn(List<MIndustryVo> searchCondition) {
        List<MIndustryEntity> list = mapper.selectIdsIn(searchCondition);
        for(MIndustryEntity entity : list) {
            entity.setEnable(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    public MIndustryVo selectById(int id) {
        return mapper.selectId(id);
    }

    @Override
    public List<MIndustryEntity> selectByName(String name) {
        // 查询 数据
        return mapper.selectByName(name);
    }

    @Override
    public List<MIndustryEntity> selectByBusiness(int businessTypeId) {
        // 查询 数据
        return mapper.selectByBusiness(businessTypeId);
    }

    /**
     * check逻辑
     *
     * @return
     */
    public CheckResultAo checkLogic(String name, String moduleType) {
        List<MIndustryEntity> selectByName = selectByName(name);
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
