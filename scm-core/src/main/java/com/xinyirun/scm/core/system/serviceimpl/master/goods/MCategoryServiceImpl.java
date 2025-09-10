package com.xinyirun.scm.core.system.serviceimpl.master.goods;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.MCategoryEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.category.MCategoryExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MCategoryVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.goods.MCategoryMapper;
import com.xinyirun.scm.core.system.service.master.goods.IMCategoryService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MCategoryAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private MCategoryAutoCodeServiceImpl autoCodeService;

    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MCategoryVo> selectPage(MCategoryVo searchCondition) {
        // 分页条件
        Page<MCategoryEntity> pageCondition =
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
        
        // 自动编号逻辑
        if (StringUtils.isEmpty(entity.getCode())) {
            // 若未填编号，自动生成
            entity.setCode(autoCodeService.autoCode().getCode());
        }
        
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(MCategoryVo searchCondition) {
        // 1. 查询类别实体
        MCategoryEntity category = this.getById(searchCondition.getId());
        if (category == null) {
            throw new BusinessException("类别不存在，删除失败");
        }

        // 2. 执行删除前校验（按照仓库删除标准模式）
        CheckResultAo cr = checkLogic(category, CheckResultAo.DELETE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 3. 校验通过，执行删除逻辑 - 切换删除状态（复原逻辑）
        category.setIs_del(!category.getIs_del());
        boolean updateResult = this.updateById(category);
        
        if (!updateResult) {
            throw new BusinessException("类别删除失败，请重试");
        }
    }

    /**
     * check逻辑（兼容性方法 - 将VO转换为Entity后调用统一校验）
     */
    public CheckResultAo checkLogic(MCategoryVo vo, String moduleType) {
        // 转换VO到Entity以使用统一的校验逻辑
        MCategoryEntity entity = (MCategoryEntity) BeanUtilsSupport.copyProperties(vo, MCategoryEntity.class);
        return checkLogic(entity, moduleType);
    }

    /**
     * check逻辑（旧版本兼容）
     */
    public CheckResultAo checkLogic(String name, String moduleType) {
        MCategoryEntity entity = new MCategoryEntity();
        entity.setName(name);
        return checkLogic(entity, moduleType);
    }

    /**
     * 统一校验逻辑（按照仓库删除标准模式实现）
     * @param entity 类别实体
     * @param moduleType 操作类型
     * @return CheckResultAo 校验结果
     */
    public CheckResultAo checkLogic(MCategoryEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，检查名称、编码不能重复
                List<MCategoryEntity> nameList_insertCheck = mapper.selectByName(entity.getName(), entity.getId());
                if (nameList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：类别名称【"+ entity.getName() +"】出现重复");
                }
                if (entity.getCode() != null) {
                    List<MCategoryEntity> codeList_insertCheck = mapper.selectByCode(entity.getCode(), entity.getId());
                    if (codeList_insertCheck.size() >= 1) {
                        return CheckResultUtil.NG("新增保存出错：类别编码【"+ entity.getCode() +"】出现重复");
                    }
                }
                break;
                
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，检查名称、编码不能重复
                List<MCategoryEntity> nameList_updCheck = mapper.selectByName(entity.getName(), entity.getId());
                if (nameList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：类别名称【"+ entity.getName() +"】出现重复");
                }
                if (entity.getCode() != null) {
                    List<MCategoryEntity> codeList_updCheck = mapper.selectByCode(entity.getCode(), entity.getId());
                    if (codeList_updCheck.size() >= 1) {
                        return CheckResultUtil.NG("更新保存出错：类别编码【"+ entity.getCode() +"】出现重复");
                    }
                }
                break;
                
            case CheckResultAo.DELETE_CHECK_TYPE:
                /** 如果逻辑删除为true，表示已经删除，无需校验 */
                if(entity.getIs_del() != null && entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                
                // 校验类别下是否有商品
                Integer goodsCount = mapper.checkGoodsExists(entity.getId());
                if (goodsCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该类别下存在%d种商品，请先删除商品或转移到其他类别", goodsCount));
                }
                break;
                
            case CheckResultAo.UNDELETE_CHECK_TYPE:
                /** 如果逻辑删除为false，表示未删除，无需恢复 */
                if(entity.getIs_del() == null || !entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                // 恢复场合，检查编码不能重复
                if (entity.getCode() != null) {
                    List<MCategoryEntity> codeList_undelete_Check = mapper.selectByCode(entity.getCode(), entity.getId());
                    if (codeList_undelete_Check.size() >= 1) {
                        return CheckResultUtil.NG("恢复失败：类别编码【"+ entity.getCode() +"】已存在，请先修改编码后再恢复");
                    }
                }
                break;
                
            default:
        }
        return CheckResultUtil.OK();
    }

}
