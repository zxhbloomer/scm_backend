package com.xinyirun.scm.core.system.serviceimpl.master.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.business.wms.warehouse.BWarehouseGroupRelationEntity;
import com.xinyirun.scm.bean.entity.business.wms.warehouse.relation.BWarehouseRelationEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.*;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.service.business.warehouse.IBWarehouseGroupRelationService;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IBWarehouseRelationService;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MWarehouseAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
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
public class MWarehouseServiceImpl extends BaseServiceImpl<MWarehouseMapper, MWarehouseEntity> implements IMWarehouseService {

    @Autowired
    private MWarehouseMapper mapper;

    @Autowired
    private MLocationServiceImpl locationService;

    @Autowired
    private MBinServiceImpl binService;

    @Autowired
    private MWarehouseAutoCodeServiceImpl autoCodeService;

    @Autowired
    private IMInventoryService inventoryService;

    @Autowired
    private IBWarehouseGroupRelationService ibWarehouseGroupRelationService;

    @Autowired
    private IBWarehouseRelationService ibWarehouseRelationService;

    @Override
    public IPage<MWarehouseVo> selectPage(MWarehouseVo searchCondition) {
        // 分页条件
        Page<MWarehouseEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<MWarehouseVo> selectList(MWarehouseVo searchCondition) {
        return mapper.selectList(searchCondition);
    }

    /**
     * 获取仓库库区库位信息
     */
    @Override
    public MWarehouseLocationBinVo selectWarehouseLocationBin(int warehouse_id) {
        return mapper.selectWarehouseLocationBin(warehouse_id);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MWarehouseVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MWarehouseEntity entity = (MWarehouseEntity) BeanUtilsSupport.copyProperties(vo, MWarehouseEntity.class);
        entity.setEnable(Boolean.TRUE);
        // 设置拼音
        this.setPinyin(entity);
        if (StringUtils.isEmpty(entity.getCode())) {
            // 若未填编号，自动生成
            entity.setCode(autoCodeService.autoCode().getCode());
        }

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());
        // 生成默认库区
        MLocationVo location  = new MLocationVo();
        if (entity.getEnable_location() == null || entity.getEnable() == Boolean.FALSE) {
            entity.setEnable_location(Boolean.FALSE);
            location.setName(SystemConstants.DEFAULT_LOCATION);
            location.setShort_name(SystemConstants.DEFAULT_LOCATION);
            location.setWarehouse_id(entity.getId());
            location.setIs_default(Boolean.TRUE);
            locationService.insert(location);
        } else {
            entity.setEnable_location(Boolean.TRUE);
        }

        if (entity.getEnable_bin() == null || entity.getEnable() == Boolean.FALSE) {
            entity.setEnable_bin(Boolean.FALSE);
            MBinVo bin = new MBinVo();
            bin.setName(SystemConstants.DEFAULT_BIN);
            bin.setLocation_id(location.getId());
            bin.setWarehouse_id(entity.getId());
            binService.insert(bin);
        } else {
            entity.setEnable_bin(Boolean.TRUE);
        }

        mapper.updateById(entity);

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(MWarehouseVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);
        MWarehouseEntity entity = (MWarehouseEntity) BeanUtilsSupport.copyProperties(vo, MWarehouseEntity.class);
        // 设置拼音
        this.setPinyin(entity);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public List<MWarehouseEntity> selectByName(String name, Integer id) {
        // 查询 数据
        return mapper.selectByName(name, id);
    }

    @Override
    public List<MWarehouseEntity> selectByCode(String code, Integer id) {
        // 查询 数据
        return mapper.selectByCode(code, id);
    }

    @Override
    public List<MWarehouseEntity> selectByShortName(String shortName, Integer id) {
        // 查询 数据
        return mapper.selectByShortName(shortName, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MWarehouseVo> searchCondition) {
        List<MWarehouseEntity> list = mapper.selectIdsIn(searchCondition);
        for(MWarehouseEntity entity : list) {
            entity.setEnable(Boolean.TRUE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disSabledByIdsIn(List<MWarehouseVo> searchCondition) {
        List<MWarehouseEntity> list = mapper.selectIdsIn(searchCondition);
        // 根据 仓库ID 查询仓库库存量大于0, 锁定库存不等于0 的库存, 如果集合大于0, 则有仓库库存不为0, 无法禁用
        checkInventory(searchCondition);
        for(MWarehouseEntity entity : list) {
            entity.setEnable(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MWarehouseVo> searchCondition) {
        List<MWarehouseEntity> list = mapper.selectIdsIn(searchCondition);
        for(MWarehouseEntity entity : list) {
            if (entity.getEnable()) {
                // 禁用, 需判断仓库商品库存数量是否为0
                checkInventory(searchCondition);
            }
            entity.setEnable(!entity.getEnable());
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    public MWarehouseVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 仓库导出 全部
     *
     * @param searchCondition 查询参数
     * @return List<MWarehouseExportVo>
     */
    @Override
    public List<MWarehouseExportVo> exportAll(MWarehouseVo searchCondition) {
        return mapper.exportAll(searchCondition);
    }

    /**
     * 仓库导出 部分
     *
     * @param searchCondition 查询参数
     * @return List<MWarehouseExportVo>
     */
    @Override
    public List<MWarehouseExportVo> export(List<MWarehouseVo> searchCondition) {
        return mapper.export(searchCondition);
    }

    /**
     * 导出专用查询方法 (完全按照岗位模式设计)
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     */
    @Override
    public List<MWarehouseExportVo> selectExportList(MWarehouseVo searchCondition) {
        // 处理动态排序
        String orderByClause = "";
        if (searchCondition.getPageCondition() != null && StringUtils.isNotEmpty(searchCondition.getPageCondition().getSort())) {
            String sortField = searchCondition.getPageCondition().getSort();
            if (sortField.startsWith("-")) {
                // 降序：去掉-号，加上DESC
                orderByClause = "ORDER BY " + sortField.substring(1) + " DESC";
            } else {
                // 升序：直接加上ASC
                orderByClause = "ORDER BY " + sortField + " ASC";
            }
            
            // SQL注入防护：验证字段名
            if (!orderByClause.matches("ORDER BY [a-zA-Z_][a-zA-Z0-9_]* (ASC|DESC)")) {
                orderByClause = "";
            }
        }
        
        return mapper.selectExportList(searchCondition, orderByClause);
    }

    /**
     * 根据 仓库 code 查询三大件
     *
     * @param warehouse_code
     * @return
     */
    @Override
    public List<MBLWBo> selectBLWByCode(String warehouse_code) {
        return mapper.selectBLWByCode(warehouse_code);
    }

    @Override
    public MWarehouseGroupTransferVo getWarehouseStaffTransferList(MWGroupTransferVo searchCondition) {

        MWarehouseGroupTransferVo vo = new MWarehouseGroupTransferVo();
        // 获取全部仓库组
        vo.setWarehouse_group_all(mapper.getAllWarehouseGroupTransferList(searchCondition));
        // 获取该该关系已经设置过的仓库组
        List<Long> rtnList = mapper.getWarehouseGroupTransferList(searchCondition);
        vo.setWarehouse_groups(rtnList.toArray(new Long[rtnList.size()]));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String setWarehouseGroupTransfer(MWGroupTransferVo bean) {
        // 删除剔除的仓库
        mapper.deleteWarehouseGroupRelationByWarehouseId(bean);
        // 增加选择的仓库
        List<BWarehouseGroupRelationEntity> lists = new ArrayList<>();
        for(int i=0; i < bean.getWarehouse_groups().length; i++){
            BWarehouseGroupRelationEntity entity = new BWarehouseGroupRelationEntity();
            entity.setWarehouse_id(bean.getWarehouse_id());
            entity.setWarehouse_group_id(bean.getWarehouse_groups()[i]);
            lists.add(entity);
        }

        ibWarehouseGroupRelationService.saveBatch(lists);
        return "OK";
    }

    @Override
    public MWarehouseStaffTransferVo getWarehouseStaffTransferList(MWStaffTransferVo searchCondition) {
        MWarehouseStaffTransferVo vo = new MWarehouseStaffTransferVo();
        // 获取全部仓库组
        vo.setWarehouse_all(mapper.getAllWarehouseStaffTransferList(searchCondition));
        // 获取该该关系已经设置过的仓库组
        List<Long> rtnList = mapper.getWarehouseStaffTransferList(searchCondition);
        vo.setWarehouses(rtnList.toArray(new Long[rtnList.size()]));
        return vo;
    }

    @Override
    public String setWarehouseStaffTransfer(MWStaffTransferVo bean) {
        // 删除剔除的仓库
        mapper.deleteWarehouseStaffRelationByStaffId(bean);
        // 增加选择的仓库
        List<BWarehouseRelationEntity> lists = new ArrayList<>();
        for(int i=0; i < bean.getWarehouse_ids().length; i++){
            BWarehouseRelationEntity entity = new BWarehouseRelationEntity();
            entity.setSerial_id(bean.getWarehouse_ids()[i]);
            entity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_WAREHOUSE);
            entity.setStaff_id(bean.getStaff_id());
            lists.add(entity);
        }

        ibWarehouseRelationService.saveBatch(lists);
        return "OK";
    }

    /**
     * 检查商品库存数量是否为 0
     * 不为0, 抛出异常
     */
    private void checkInventory(List<MWarehouseVo> searchCondition) {
        List<MInventoryVo> mInventoryVos = inventoryService.selectInventoryByWarehouse(searchCondition);
        if (!CollectionUtils.isEmpty(mInventoryVos) && mInventoryVos.size() > 0) {
            // 计算总库存数量（可用库存 + 锁定库存）
            BigDecimal totalInventory = mInventoryVos.stream()
                    .map(vo -> {
                        BigDecimal available = vo.getQty_avaible() != null ? vo.getQty_avaible() : BigDecimal.ZERO;
                        BigDecimal locked = vo.getQty_lock() != null ? vo.getQty_lock() : BigDecimal.ZERO;
                        return available.add(locked);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 格式化库存数量显示
            String formattedInventory = formatInventoryQuantity(totalInventory);
            
            throw new BusinessException(String.format("停用失败：该仓库内还有商品库存 %s，请先清空库存或转移到其他仓库", formattedInventory));
        }
    }

    /**
     * 格式化库存数量显示，格式：9,999.9999吨
     */
    private String formatInventoryQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) == 0) {
            return "0吨";
        }
        
        // 使用DecimalFormat格式化数字，保留4位小数，添加千分位分隔符
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.0000");
        return df.format(quantity) + "吨";
    }

    /**
     * check逻辑（兼容性方法 - 将VO转换为Entity后调用统一校验）
     */
    public CheckResultAo checkLogic(MWarehouseVo vo, String moduleType) {
        // 转换VO到Entity以使用统一的校验逻辑
        MWarehouseEntity entity = (MWarehouseEntity) BeanUtilsSupport.copyProperties(vo, MWarehouseEntity.class);
        return checkLogic(entity, moduleType);
    }

    private void setPinyin(MWarehouseEntity entity) {
        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称拼音首字母
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setName_pinyin_initial(str.toString());

        // 简称全拼
        entity.setShort_name_pinyin(Pinyin.toPinyin(entity.getShort_name(), ""));
        // 名称拼音首字母
        str = new StringBuilder("");
        for (char c: entity.getShort_name().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setShort_name_pinyin_initial(str.toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(MWarehouseVo searchCondition) {
        // 1. 查询仓库实体
        MWarehouseEntity warehouse = this.getById(searchCondition.getId());
        if (warehouse == null) {
            throw new BusinessException("仓库不存在，删除失败");
        }

        // 2. 执行删除前校验（按照岗位删除标准模式）
        CheckResultAo cr = checkLogic(warehouse, CheckResultAo.DELETE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 3. 校验通过，执行删除逻辑 - 切换删除状态（复原逻辑）
        warehouse.setIs_del(!warehouse.getIs_del());
        boolean updateResult = this.updateById(warehouse);
        
        if (!updateResult) {
            throw new BusinessException("仓库删除失败，请重试");
        }
    }

    /**
     * 统一校验逻辑（按照岗位删除标准模式实现）
     * @param entity 仓库实体
     * @param moduleType 操作类型
     * @return CheckResultAo 校验结果
     */
    public CheckResultAo checkLogic(MWarehouseEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，检查名称、编码、简称不能重复
                List<MWarehouseEntity> nameList_insertCheck = selectByName(entity.getName(), entity.getId());
                if (nameList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：仓库名称【"+ entity.getName() +"】出现重复");
                }
                List<MWarehouseEntity> codeList_insertCheck = selectByCode(entity.getCode(), entity.getId());
                if (codeList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：仓库编码【"+ entity.getCode() +"】出现重复");
                }
                List<MWarehouseEntity> shortNameList_insertCheck = selectByShortName(entity.getShort_name(), entity.getId());
                if (shortNameList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：仓库简称【"+ entity.getShort_name() +"】出现重复");
                }
                break;
                
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，检查名称、编码、简称不能重复
                List<MWarehouseEntity> nameList_updCheck = selectByName(entity.getName(), entity.getId());
                if (nameList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：仓库名称【"+ entity.getName() +"】出现重复");
                }
                List<MWarehouseEntity> codeList_updCheck = selectByCode(entity.getCode(), entity.getId());
                if (codeList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：仓库编码【"+ entity.getCode() +"】出现重复");
                }
                List<MWarehouseEntity> shortNameList_updCheck = selectByShortName(entity.getShort_name(), entity.getId());
                if (shortNameList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：仓库简称【"+ entity.getShort_name() +"】出现重复");
                }
                break;
                
            case CheckResultAo.DELETE_CHECK_TYPE:
                /** 如果逻辑删除为true，表示已经删除，无需校验 */
                if(entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                
                // L1: 库存数据校验（最高优先级 - 数据完整性）
                Integer inventoryCount = baseMapper.checkInventoryExists(entity.getId());
                if (inventoryCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该仓库存在%d条库存数据，请先清空库存或转移库存到其他仓库", inventoryCount));
                }
                
                // L2: 入库业务校验（业务流程完整性）
                Integer inboundCount = baseMapper.checkInboundExists(entity.getId());
                if (inboundCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该仓库存在%d条入库记录，请先处理入库相关业务或联系系统管理员", inboundCount));
                }
                
                // L3: 出库业务校验（业务流程完整性）
                Integer outboundCount = baseMapper.checkOutboundExists(entity.getId());
                if (outboundCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该仓库存在%d条出库记录，请先处理出库相关业务或联系系统管理员", outboundCount));
                }
                
                // L4: 库区配置校验（基础设施完整性）
                Integer locationCount = baseMapper.checkLocationExists(entity.getId());
                if (locationCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该仓库配置了%d个库区，请先删除或转移库区配置", locationCount));
                }
                
                // L5: 库位配置校验（基础设施完整性）
                Integer binCount = baseMapper.checkBinExists(entity.getId());
                if (binCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该仓库配置了%d个库位，请先删除或转移库位配置", binCount));
                }
                break;
                
            case CheckResultAo.UNDELETE_CHECK_TYPE:
                /** 如果逻辑删除为false，表示未删除，无需恢复 */
                if(!entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                // 恢复场合，检查编码不能重复
                List<MWarehouseEntity> codeList_undelete_Check = selectByCode(entity.getCode(), entity.getId());
                if (codeList_undelete_Check.size() >= 1) {
                    return CheckResultUtil.NG("恢复失败：仓库编码【"+ entity.getCode() +"】已存在，请先修改编码后再恢复");
                }
                break;
                
            default:
        }
        return CheckResultUtil.OK();
    }

}
