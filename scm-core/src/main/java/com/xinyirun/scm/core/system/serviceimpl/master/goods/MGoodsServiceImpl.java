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
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MGoodsAutoCodeServiceImpl;
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

    @Autowired
    private MGoodsAutoCodeServiceImpl autoCodeService;

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
        
        // 自动生成物料编码
        if (entity.getCode() == null || entity.getCode().trim().isEmpty()) {
            entity.setCode(autoCodeService.autoCode().getCode());
        }
        
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
            Long inventoryCount = (Long) associations.get("inventory_count");
            if (inventoryCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条库存记录，无法删除", entity.getName(), inventoryCount));
            }
            
            // L2: 入库记录检查  
            Long inboundCount = (Long) associations.get("inbound_count");
            if (inboundCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条入库记录，无法删除", entity.getName(), inboundCount));
            }
            
            // L3: 出库记录检查
            Long outboundCount = (Long) associations.get("outbound_count");
            if (outboundCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条出库记录，无法删除", entity.getName(), outboundCount));
            }
            
            // L4: 采购订单检查
            Long purchaseOrderCount = (Long) associations.get("purchase_order_count");
            if (purchaseOrderCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条采购订单记录，无法删除", entity.getName(), purchaseOrderCount));
            }
            
            // L5: 销售订单检查
            Long salesOrderCount = (Long) associations.get("sales_order_count");
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
     * 启用物料并返回更新后的数据
     * @param goodsVo 物料对象
     * @return 更新后的物料数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MGoodsVo enabledById(MGoodsVo goodsVo) {
        // 根据ID查询实体
        MGoodsEntity entity = this.getById(goodsVo.getId());
        if (entity == null) {
            throw new BusinessException("物料不存在，启用失败");
        }
        
        // 执行启用操作
        entity.setEnable(Boolean.TRUE);
        boolean updateResult = this.updateById(entity);
        
        if (!updateResult) {
            throw new BusinessException("物料启用失败，请重试");
        }
        
        // 查询并返回更新后的完整数据
        return mapper.selectId(goodsVo.getId());
    }

    /**
     * 停用物料并返回更新后的数据 - 包含业务关联校验
     * @param goodsVo 物料对象
     * @return 更新后的物料数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MGoodsVo disabledById(MGoodsVo goodsVo) {
        // 根据ID查询实体
        MGoodsEntity entity = this.getById(goodsVo.getId());
        if (entity == null) {
            throw new BusinessException("物料不存在，停用失败");
        }
        
        // 如果当前是启用状态，需要进行L1-L5多层业务校验
        if (entity.getEnable() != null && entity.getEnable()) {
            // 综合检查业务关联情况
            java.util.Map<String, Object> associations = mapper.checkGoodsBusinessAssociations(entity.getId());
            
            // L1: 库存检查
            Long inventoryCount = (Long) associations.get("inventory_count");
            if (inventoryCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条库存记录，无法停用", entity.getName(), inventoryCount));
            }
            
            // L2: 入库记录检查  
            Long inboundCount = (Long) associations.get("inbound_count");
            if (inboundCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条入库记录，无法停用", entity.getName(), inboundCount));
            }
            
            // L3: 出库记录检查
            Long outboundCount = (Long) associations.get("outbound_count");
            if (outboundCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条出库记录，无法停用", entity.getName(), outboundCount));
            }
            
            // L4: 采购订单检查
            Long purchaseOrderCount = (Long) associations.get("purchase_order_count");
            if (purchaseOrderCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条采购订单记录，无法停用", entity.getName(), purchaseOrderCount));
            }
            
            // L5: 销售订单检查
            Long salesOrderCount = (Long) associations.get("sales_order_count");
            if (salesOrderCount > 0) {
                throw new BusinessException(String.format("物料【%s】存在 %d 条销售订单记录，无法停用", entity.getName(), salesOrderCount));
            }
        }
        
        // 执行停用操作
        entity.setEnable(Boolean.FALSE);
        boolean updateResult = this.updateById(entity);
        
        if (!updateResult) {
            throw new BusinessException("物料停用失败，请重试");
        }
        
        // 查询并返回更新后的完整数据
        return mapper.selectId(goodsVo.getId());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(MGoodsVo searchCondition) {
        // 1. 查询物料实体
        MGoodsEntity goods = this.getById(searchCondition.getId());
        if (goods == null) {
            throw new BusinessException("物料不存在，删除失败");
        }

        // 2. 执行删除前校验（按照仓库删除标准模式）
        CheckResultAo cr = checkLogic(goods, CheckResultAo.DELETE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 3. 校验通过，执行删除逻辑 - 切换删除状态（复原逻辑）
        goods.setIs_del(!goods.getIs_del());
        boolean updateResult = this.updateById(goods);
        
        if (!updateResult) {
            throw new BusinessException("物料删除失败，请重试");
        }
    }

    /**
     * check逻辑（兼容性方法 - 将VO转换为Entity后调用统一校验）
     */
    public CheckResultAo checkLogic(MGoodsVo vo, String moduleType) {
        // 转换VO到Entity以使用统一的校验逻辑
        MGoodsEntity entity = (MGoodsEntity) BeanUtilsSupport.copyProperties(vo, MGoodsEntity.class);
        return checkLogic(entity, moduleType);
    }

    /**
     * 统一校验逻辑
     */
    public CheckResultAo checkLogic(MGoodsEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.DELETE_CHECK_TYPE:
                /** 如果逻辑删除为true，表示已经删除，无需校验 */
                if(entity.getIs_del() != null && entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                
                // 校验物料是否被业务使用（库存、入库、出库、采购订单、销售订单）
                CheckResultAo businessCheck = checkGoodsBusinessAssociations(entity.getId().longValue());
                if (!businessCheck.isSuccess()) {
                    return businessCheck;
                }
                break;
                
            // 其他校验类型...
            default:
                break;
        }
        return CheckResultUtil.OK();
    }

    /**
     * 校验物料业务关联（删除前检查）
     * @param goodsId 物料ID
     * @return 校验结果
     */
    private CheckResultAo checkGoodsBusinessAssociations(Long goodsId) {
        // 将Long转为Integer以匹配Mapper方法签名
        Integer goodsIdInt = goodsId.intValue();
        
        // L1级别：检查库存
        Long inventoryCount = mapper.checkInventoryExists(goodsIdInt).longValue();
        if (inventoryCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该物料在库存中存在 %d 条记录，请先清空库存", inventoryCount));
        }
        
        // L2级别：检查入库业务
        Long inboundCount = mapper.checkInboundExists(goodsIdInt).longValue();
        if (inboundCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该物料存在 %d 条入库记录，无法删除", inboundCount));
        }
        
        // L3级别：检查出库业务  
        Long outboundCount = mapper.checkOutboundExists(goodsIdInt).longValue();
        if (outboundCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该物料存在 %d 条出库记录，无法删除", outboundCount));
        }
        
        // L4级别：检查采购订单
        Long poOrderCount = mapper.checkPurchaseOrderExists(goodsIdInt).longValue();
        if (poOrderCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该物料被 %d 个采购订单使用，无法删除", poOrderCount));
        }
        
        // L5级别：检查销售订单
        Long soOrderCount = mapper.checkSalesOrderExists(goodsIdInt).longValue();
        if (soOrderCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该物料被 %d 个销售订单使用，无法删除", soOrderCount));
        }
        
        return CheckResultUtil.OK();
    }

}
