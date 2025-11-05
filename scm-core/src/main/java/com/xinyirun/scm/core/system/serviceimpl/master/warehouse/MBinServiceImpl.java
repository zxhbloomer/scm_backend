package com.xinyirun.scm.core.system.serviceimpl.master.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MBinExportVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MBinVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MBinMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMBinService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MBinAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * <p>
 * 库位 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MBinServiceImpl extends BaseServiceImpl<MBinMapper, MBinEntity> implements IMBinService {

    @Autowired
    private MBinMapper mapper;

    @Autowired
    private MWarehouseMapper warehouseMapper;

    @Autowired
    private MBinAutoCodeServiceImpl autoCodeService;

    @Autowired
    private IMInventoryService inventoryService;

    @Override
    public IPage<MBinVo> selectPage(MBinVo searchCondition) {
        // 分页条件
        Page<MBinEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        if(searchCondition.getCombine_search_condition() != null
                && searchCondition.getCombine_search_condition().split(SystemConstants.WAREHOUSE_LOCSTION_BIN_DELIMITER).length > 0) {
            searchCondition.setCombine_search_condition(searchCondition.getCombine_search_condition().split(SystemConstants.WAREHOUSE_LOCSTION_BIN_DELIMITER)[0].trim());
        }
        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<MBinVo> selecList(MBinVo searchCondition) {
        return mapper.selectList(searchCondition);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MBinVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(),vo.getLocation_id(), vo.getWarehouse_id(), CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MBinEntity entity = (MBinEntity) BeanUtilsSupport.copyProperties(vo, MBinEntity.class);
        entity.setEnable(Boolean.TRUE);
        // 设置拼音
        this.setPinyin(entity);
        // 自动生成编号
        entity.setCode(autoCodeService.autoCode().getCode());

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(MBinVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(),vo.getLocation_id(), vo.getWarehouse_id(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);
        MBinEntity entity = (MBinEntity) BeanUtilsSupport.copyProperties(vo, MBinEntity.class);
        // 设置拼音
        this.setPinyin(entity);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public List<MBinEntity> selectByName(String name,int warehouse_id,int location_id) {
        // 查询 数据
        return mapper.selectByName(name,warehouse_id,location_id);
    }

    @Override
    public List<MBinVo> selectByCode(String code,int warehouse_id,int location_id) {
        // 查询 数据
        return mapper.selectByCode(code,warehouse_id,location_id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> enabled(MBinVo vo) {
        // 更新前检查库位是否存在
        MBinEntity entity = mapper.selectById(vo.getId());
        if (entity == null) {
            throw new BusinessException("库位不存在，启用失败");
        }
        
        // 设置启用状态
        entity.setEnable(Boolean.TRUE);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> disabled(MBinVo vo) {
        // 更新前检查库位是否存在
        MBinEntity entity = mapper.selectById(vo.getId());
        if (entity == null) {
            throw new BusinessException("库位不存在，停用失败");
        }
        
        // 库存校验 - 检查库位是否存在库存（单条记录校验）
        List<MBinVo> singleBinList = new java.util.ArrayList<>();
        singleBinList.add(vo);
        CheckResultAo inventoryCheck = checkBinInventory(singleBinList);
        if (!inventoryCheck.isSuccess()) {
            throw new BusinessException(inventoryCheck.getMessage());
        }
        
        // 设置停用状态
        entity.setEnable(Boolean.FALSE);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }


    @Override
    public MBinVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 库位导出 全部
     *
     * @param searchCondition 查询参数
     * @return List<MBinExportVo>
     */
    @Override
    public List<MBinExportVo> exportAll(MBinVo searchCondition) {
        // 全部导出时，不设置ids参数
        searchCondition.setIds(null);
        return selectExportList(searchCondition);
    }

    /**
     * 库位导出 部分
     *
     * @param searchCondition 查询参数
     * @return List<MBinExportVo>
     */
    @Override
    public List<MBinExportVo> export(List<MBinVo> searchCondition) {
        if (searchCondition == null || searchCondition.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // 从选择的记录中提取ids
        Integer[] ids = searchCondition.stream()
                .filter(vo -> vo.getId() != null)
                .map(vo -> vo.getId())
                .toArray(Integer[]::new);
                
        if (ids.length == 0) {
            return new java.util.ArrayList<>();
        }
        
        // 创建查询条件，设置ids用于选择导出
        MBinVo exportCondition = new MBinVo();
        exportCondition.setIds(ids);
        
        return selectExportList(exportCondition);
    }

    /**
     * 导出专用查询方法 (完全按照仓库模式设计)
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     * @return List<MBinExportVo>
     */
    @Override
    public List<MBinExportVo> selectExportList(MBinVo searchCondition) {
        // 设置组合搜索条件处理
        if(searchCondition.getCombine_search_condition() != null
                && searchCondition.getCombine_search_condition().split(SystemConstants.WAREHOUSE_LOCSTION_BIN_DELIMITER).length > 0) {
            searchCondition.setCombine_search_condition(searchCondition.getCombine_search_condition().split(SystemConstants.WAREHOUSE_LOCSTION_BIN_DELIMITER)[0].trim());
        }
        
        return mapper.selectExportList(searchCondition);
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(String name, String code, int location_id, int warehouse_id , String moduleType) {
        // 数据查询库位名称是否重复
        List<MBinEntity> selectByName = selectByName(name,warehouse_id,location_id);
        // 数据查询库位编码是否重复
        List<MBinVo> selectByKey = selectByCode(code,warehouse_id,location_id);
        // 数据库查询仓库库位状态是否启用
        MWarehouseEntity warehouseEntity = warehouseMapper.selectById(warehouse_id);

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", name);
                }
                if (selectByKey.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", code);
                }
//                if (!warehouseEntity.getEnable_bin()) {
//                    return CheckResultUtil.NG("新增保存出错：仓库库位状态未启用", warehouseEntity.getName());
//                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", name);
                }
                if (selectByKey.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", code);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    private void setPinyin(MBinEntity entity) {
        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称拼音首字母
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setName_pinyin_initial(str.toString());
    }

    /**
     * 校验库位库存
     * 检查指定库位是否存在库存量大于0或锁定库存不等于0的情况
     * @param binVoList 库位列表
     * @return 校验结果和错误消息
     */
    private CheckResultAo checkBinInventory(List<MBinVo> binVoList) {
        try {
            // 查询库位库存
            List<MInventoryVo> inventoryList = inventoryService.selectInventoryByBinIds(binVoList);
            
            if (inventoryList != null && inventoryList.size() > 0) {
                StringBuilder errorMessage = new StringBuilder("停用失败，以下库位存在库存：\n");
                
                for (MInventoryVo inventory : inventoryList) {
                    // 获取库位名称
                    String binName = "库位ID:" + inventory.getBin_id();
                    for (MBinVo binVo : binVoList) {
                        if (binVo.getId().equals(inventory.getBin_id())) {
                            binName = binVo.getName();
                            break;
                        }
                    }
                    
                    errorMessage.append("- ").append(binName);
                    
                    // 格式化库存数量信息
                    if (inventory.getQty_avaible() != null && inventory.getQty_avaible().compareTo(BigDecimal.ZERO) > 0) {
                        errorMessage.append("：可用库存 ").append(formatInventoryQuantity(inventory.getQty_avaible()));
                    }
                    
                    if (inventory.getQty_lock() != null && inventory.getQty_lock().compareTo(BigDecimal.ZERO) != 0) {
                        errorMessage.append("，锁定库存 ").append(formatInventoryQuantity(inventory.getQty_lock()));
                    }
                    
                    errorMessage.append("\n");
                }
                
                return CheckResultUtil.NG(errorMessage.toString());
            }
            
            return CheckResultUtil.OK();
            
        } catch (Exception e) {
            return CheckResultUtil.NG("校验库位库存时发生异常：" + e.getMessage());
        }
    }

    /**
     * 格式化库存数量显示
     * @param quantity 库存数量
     * @return 格式化后的数量字符串
     */
    private String formatInventoryQuantity(BigDecimal quantity) {
        if (quantity == null) {
            return "0";
        }
        
        // 使用4位小数格式化，去除末尾的零
        DecimalFormat df = new DecimalFormat("#,##0.####");
        return df.format(quantity);
    }

    /**
     * 删除库位（逻辑删除）
     * 参考仓库管理删除模式，支持删除/恢复切换
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(MBinVo searchCondition) {
        // 1. 查询库位实体
        MBinEntity bin = this.getById(searchCondition.getId());
        if (bin == null) {
            throw new BusinessException("库位不存在，删除失败");
        }

        // 2. 执行删除前校验（按照仓库删除标准模式）
        CheckResultAo cr = checkLogic(bin, CheckResultAo.DELETE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 3. 校验通过，执行删除逻辑 - 切换删除状态（复原逻辑）
        bin.setIs_del(!bin.getIs_del());
        boolean updateResult = this.updateById(bin);
        
        if (!updateResult) {
            throw new BusinessException("库位删除失败，请重试");
        }
    }

    /**
     * 统一校验逻辑（按照仓库删除标准模式实现）
     * @param entity 库位实体
     * @param moduleType 操作类型
     * @return CheckResultAo 校验结果
     */
    public CheckResultAo checkLogic(MBinEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.DELETE_CHECK_TYPE:
                // 如果逻辑删除为true，表示已经删除，无需校验
                if(entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                
                // L1: 库存数据校验（最高优先级 - 数据完整性）
                Integer inventoryCount = mapper.checkInventoryExists(entity.getId());
                if (inventoryCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该库位存在%d条库存数据，请先清空库存或转移库存到其他库位", inventoryCount));
                }
                
                // L2: 入库业务校验（业务流程完整性）
                Integer inboundCount = mapper.checkInboundExists(entity.getId());
                if (inboundCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该库位存在%d条入库记录，请先处理入库相关业务或联系系统管理员", inboundCount));
                }
                
                // L3: 出库业务校验（业务流程完整性）
                Integer outboundCount = mapper.checkOutboundExists(entity.getId());
                if (outboundCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该库位存在%d条出库记录，请先处理出库相关业务或联系系统管理员", outboundCount));
                }
                
                break;
                
            case CheckResultAo.UNDELETE_CHECK_TYPE:
                // 如果逻辑删除为false，表示未删除，无需恢复
                if(!entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                // 恢复场合，检查编码不能重复
                List<MBinVo> codeList = selectByCode(entity.getCode(), entity.getWarehouse_id(), entity.getLocation_id());
                if(codeList.size() > 1) {
                    return CheckResultUtil.NG("恢复保存出错：库位编码【"+ entity.getCode() +"】出现重复");
                }
                break;
        }
        return CheckResultUtil.OK();
    }
}
