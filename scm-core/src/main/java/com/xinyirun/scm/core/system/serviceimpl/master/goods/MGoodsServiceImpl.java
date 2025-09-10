package com.xinyirun.scm.core.system.serviceimpl.master.goods;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsMapper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import com.xinyirun.scm.core.system.service.master.goods.IMGoodsService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MGoodsServiceImpl extends BaseServiceImpl<MGoodsMapper, MGoodsEntity> implements IMGoodsService {

    @Autowired
    private MGoodsMapper mapper;

    @Autowired
    private MGoodsSpecMapper specMapper;

    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MGoodsVo> selectPage(MGoodsVo searchCondition) {
        // 分页条件
        Page<MGoodsEntity> pageCondition =
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
    public InsertResultAo<Integer> insert(MGoodsVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(),  CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MGoodsEntity entity = (MGoodsEntity) BeanUtilsSupport.copyProperties(vo, MGoodsEntity.class);
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
    public UpdateResultAo<Integer> update(MGoodsVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(),  CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);

        MGoodsEntity entity = (MGoodsEntity) BeanUtilsSupport.copyProperties(vo, MGoodsEntity.class);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        // 同步修改规格中的商品名称
        List<MGoodsSpecEntity> specList = specMapper.selectByGoodsCode(entity.getCode());
        for (MGoodsSpecEntity spec : specList) {
            spec.setName(entity.getName());
            specMapper.updateById(spec);
        }
        return UpdateResultUtil.OK(updCount);
    }

    /**
     * 批量删除 - 包含业务关联校验
     * @param searchCondition
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> deleteByIdsIn(List<MGoodsVo> searchCondition) {
        List<MGoodsEntity> list = mapper.selectIdsIn(searchCondition);
        
        // L1-L5多层业务校验 - 删除前检查业务关联
        for(MGoodsEntity entity : list) {
            // 综合检查业务关联情况
            java.util.Map<String, Object> associations = mapper.checkGoodsBusinessAssociations(entity.getId());
            
            // L1: 库存检查
            Integer inventoryCount = (Integer) associations.get("inventory_count");
            if (inventoryCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条库存记录，无法删除", entity.getName(), inventoryCount));
            }
            
            // L2: 入库记录检查  
            Integer inboundCount = (Integer) associations.get("inbound_count");
            if (inboundCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条入库记录，无法删除", entity.getName(), inboundCount));
            }
            
            // L3: 出库记录检查
            Integer outboundCount = (Integer) associations.get("outbound_count");
            if (outboundCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条出库记录，无法删除", entity.getName(), outboundCount));
            }
            
            // L4: 采购订单检查
            Integer purchaseOrderCount = (Integer) associations.get("purchase_order_count");
            if (purchaseOrderCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条采购订单记录，无法删除", entity.getName(), purchaseOrderCount));
            }
            
            // L5: 销售订单检查
            Integer salesOrderCount = (Integer) associations.get("sales_order_count");
            if (salesOrderCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条销售订单记录，无法删除", entity.getName(), salesOrderCount));
            }
        }
        
        // 校验通过后执行物理删除
        List<Integer> ids = list.stream().map(MGoodsEntity::getId).collect(java.util.stream.Collectors.toList());
        int deletedCount = mapper.deleteBatchIds(ids);
        
        return DeleteResultUtil.OK(deletedCount);
    }

    /**
     * 启用
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MGoodsVo> searchCondition) {
        List<MGoodsEntity> list = mapper.selectIdsIn(searchCondition);
        for(MGoodsEntity entity : list) {
            entity.setEnable(Boolean.TRUE);
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 停用 - 包含业务关联校验
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disSabledByIdsIn(List<MGoodsVo> searchCondition) {
        List<MGoodsEntity> list = mapper.selectIdsIn(searchCondition);
        
        // L1-L5多层业务校验 - 停用前检查业务关联
        for(MGoodsEntity entity : list) {
            // 如果已经是停用状态，跳过校验
            if (entity.getEnable() == null || !entity.getEnable()) {
                continue;
            }
            
            // 综合检查业务关联情况
            java.util.Map<String, Object> associations = mapper.checkGoodsBusinessAssociations(entity.getId());
            
            // L1: 库存检查
            Integer inventoryCount = (Integer) associations.get("inventory_count");
            if (inventoryCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条库存记录，无法停用", entity.getName(), inventoryCount));
            }
            
            // L2: 入库记录检查  
            Integer inboundCount = (Integer) associations.get("inbound_count");
            if (inboundCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条入库记录，无法停用", entity.getName(), inboundCount));
            }
            
            // L3: 出库记录检查
            Integer outboundCount = (Integer) associations.get("outbound_count");
            if (outboundCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条出库记录，无法停用", entity.getName(), outboundCount));
            }
            
            // L4: 采购订单检查
            Integer purchaseOrderCount = (Integer) associations.get("purchase_order_count");
            if (purchaseOrderCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条采购订单记录，无法停用", entity.getName(), purchaseOrderCount));
            }
            
            // L5: 销售订单检查
            Integer salesOrderCount = (Integer) associations.get("sales_order_count");
            if (salesOrderCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条销售订单记录，无法停用", entity.getName(), salesOrderCount));
            }
        }
        
        // 校验通过后执行停用操作
        for(MGoodsEntity entity : list) {
            entity.setEnable(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 启用/停用切换 - 包含停用校验
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MGoodsVo> searchCondition) {
        List<MGoodsEntity> list = mapper.selectIdsIn(searchCondition);
        
        // 对即将切换为停用状态的物料进行业务关联校验
        for(MGoodsEntity entity : list) {
            // 如果当前是启用状态，即将切换为停用状态，需要校验
            if (entity.getEnable() != null && entity.getEnable()) {
                // 综合检查业务关联情况
                java.util.Map<String, Object> associations = mapper.checkGoodsBusinessAssociations(entity.getId());
                
                // L1-L5多层业务校验
                Integer inventoryCount = (Integer) associations.get("inventory_count");
                if (inventoryCount > 0) {
                    throw new BusinessException(String.format("物料【%s】存在 %d 条库存记录，无法停用", entity.getName(), inventoryCount));
                }
                
                Integer inboundCount = (Integer) associations.get("inbound_count");
                if (inboundCount > 0) {
                    throw new BusinessException(String.format("物料【%s】存在 %d 条入库记录，无法停用", entity.getName(), inboundCount));
                }
                
                Integer outboundCount = (Integer) associations.get("outbound_count");
                if (outboundCount > 0) {
                    throw new BusinessException(String.format("物料【%s】存在 %d 条出库记录，无法停用", entity.getName(), outboundCount));
                }
                
                Integer purchaseOrderCount = (Integer) associations.get("purchase_order_count");
                if (purchaseOrderCount > 0) {
                    throw new BusinessException(String.format("物料【%s】存在 %d 条采购订单记录，无法停用", entity.getName(), purchaseOrderCount));
                }
                
                Integer salesOrderCount = (Integer) associations.get("sales_order_count");
                if (salesOrderCount > 0) {
                    throw new BusinessException(String.format("物料【%s】存在 %d 条销售订单记录，无法停用", entity.getName(), salesOrderCount));
                }
            }
        }
        
        // 校验通过后执行状态切换
        for(MGoodsEntity entity : list) {
            entity.setEnable(!entity.getEnable());
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 导出
     *
     * @param searchConditionList 入参
     * @return List<MGoodsExportVo>
     */
    @Override
    public List<MGoodsExportVo> export(MGoodsVo searchConditionList) {
        return mapper.exportList(searchConditionList);
    }

    @Override
    public MGoodsVo selectById(int id) {
        return mapper.selectId(id);
    }

    @Override
    public List<MGoodsEntity> selectByName(String name) {
        // 查询 数据
        return mapper.selectByName(name);
    }

    /**
     * check逻辑
     *
     * @return
     */
    public CheckResultAo checkLogic(String name, String moduleType) {
        List<MGoodsEntity> selectByName = selectByName(name);
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
