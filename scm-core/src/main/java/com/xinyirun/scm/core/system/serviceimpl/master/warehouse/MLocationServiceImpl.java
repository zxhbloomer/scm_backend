package com.xinyirun.scm.core.system.serviceimpl.master.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MLocationExportVo;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MLocationVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MLocationMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMLocationService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MLocationAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
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
public class MLocationServiceImpl extends BaseServiceImpl<MLocationMapper, MLocationEntity> implements IMLocationService {

    @Autowired
    private MLocationMapper mapper;

    @Autowired
    private MWarehouseMapper warehouseMapper;

    @Autowired
    private MLocationAutoCodeServiceImpl autoCodeService;

    @Autowired
    private IMInventoryService inventoryService;

    @Override
    public IPage<MLocationVo> selectPage(MLocationVo searchCondition) {
        // 分页条件
        Page<MLocationEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<MLocationVo> selectList(MLocationVo searchCondition) {
        return mapper.selectList(searchCondition);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MLocationVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(),vo.getShort_name(), vo.getWarehouse_id(), CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MLocationEntity entity = (MLocationEntity) BeanUtilsSupport.copyProperties(vo, MLocationEntity.class);
        // 使用前端传入的enable值
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
    public UpdateResultAo<Integer> update(MLocationVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(),vo.getShort_name(), vo.getWarehouse_id(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);
        MLocationEntity entity = (MLocationEntity) BeanUtilsSupport.copyProperties(vo, MLocationEntity.class);
        // 使用前端传入的enable值
        // 设置拼音
        this.setPinyin(entity);

        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    public List<MLocationEntity> selectByName(String name,int warehouse_id) {
        // 查询 数据
        return mapper.selectByName(name,warehouse_id);
    }

    @Override
    public List<MLocationEntity> selectByCode(String code,int warehouse_id) {
        // 查询 数据
        return mapper.selectByCode(code,warehouse_id);
    }

    @Override
    public List<MLocationEntity> selectByShortName(String shortName,int warehouse_id) {
        // 查询 数据
        return mapper.selectByShortName(shortName,warehouse_id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MLocationVo enabledByIdsIn(MLocationVo locationVo) {
        // 1. 验证库区存在性
        MLocationEntity entity = this.getById(locationVo.getId());
        if (entity == null) {
            throw new BusinessException("库区不存在，启用失败");
        }

        // 2. 更新启用状态
        entity.setEnable(Boolean.TRUE);
        boolean updateResult = this.updateById(entity);
        if (!updateResult) {
            throw new BusinessException("库区启用失败，请重试");
        }

        // 3. 查询并返回最新数据
        return mapper.selectId(locationVo.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MLocationVo disSabledByIdsIn(MLocationVo locationVo) {
        // 1. 验证库区存在性
        MLocationEntity entity = this.getById(locationVo.getId());
        if (entity == null) {
            throw new BusinessException("库区不存在，停用失败");
        }

        // 2. 库存校验（保留原有业务逻辑）
        List<MLocationVo> checkList = List.of(locationVo);
        checkLocationInventory(checkList);

        // 3. 更新停用状态
        entity.setEnable(Boolean.FALSE);
        boolean updateResult = this.updateById(entity);
        if (!updateResult) {
            throw new BusinessException("库区停用失败，请重试");
        }

        // 4. 查询并返回最新数据
        return mapper.selectId(locationVo.getId());
    }


    @Override
    public MLocationVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 导出专用查询方法，支持动态排序
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MLocationExportVo> selectExportList(MLocationVo searchCondition) {
        // 处理动态排序
        String orderByClause = "";
        if (searchCondition.getPageCondition() != null && StringUtils.isNotEmpty(searchCondition.getPageCondition().getSort())) {
            String sort = searchCondition.getPageCondition().getSort();
            String field = sort.startsWith("-") ? sort.substring(1) : sort;
            
            // 正则验证：只允许字母、数字、下划线，防止SQL注入
            if (!field.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                throw new BusinessException("非法的排序字段格式");
            }
            
            if (sort.startsWith("-")) {
                // 降序：去掉前缀-，添加DESC
                orderByClause = " ORDER BY " + field + " DESC";
            } else {
                // 升序：直接使用字段名，添加ASC
                orderByClause = " ORDER BY " + sort + " ASC";
            }
        }
        
        return mapper.selectExportList(searchCondition, orderByClause);
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(String name, String code, String shortName, int warehouse_id , String moduleType) {
        // 数据库查询是否存在库区名重复数据
        List<MLocationEntity> selectByName = selectByName(name,warehouse_id);
        // 数据库查询是否存在库区编码重复数据
        List<MLocationEntity> selectByKey = selectByCode(code,warehouse_id);
        // 数据库查询是否存在库区简称重复数据
        List<MLocationEntity> selectByShortName = selectByShortName(shortName,warehouse_id);
        // 查询仓库数据
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
                if (selectByShortName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：简称出现重复", selectByShortName);
                }
//                if (warehouseEntity.getEnable_location() == Boolean.FALSE) {
//                    return CheckResultUtil.NG("新增保存出错：该仓库库区状态未启用");
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
                if (selectByShortName.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：简称出现重复", selectByShortName);
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                // 删除操作不需要重复性校验
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 统一校验逻辑
     */
    public CheckResultAo checkLogic(MLocationEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，检查编码不能重复
                List<MLocationEntity> codeList_insertCheck = selectByCode(entity.getCode(), entity.getWarehouse_id());
                if (codeList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：库区编码【"+ entity.getCode() +"】出现重复");
                }
                break;
                
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，检查编码不能重复
                List<MLocationEntity> codeList_updCheck = selectByCode(entity.getCode(), entity.getWarehouse_id());
                if (codeList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：库区编码【"+ entity.getCode() +"】出现重复");
                }
                break;
                
            case CheckResultAo.DELETE_CHECK_TYPE:
                return checkDeleteLogic(entity);
                
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 删除操作专用校验逻辑
     * 采用四级校验策略：库存数据 → 库位配置 → 入库记录 → 出库记录
     * 
     * @param entity 库区实体
     * @return 校验结果
     */
    private CheckResultAo checkDeleteLogic(MLocationEntity entity) {
        // 如果已经是删除状态，无需校验直接允许恢复
        if (entity.getIs_del()) {
            return CheckResultUtil.OK();
        }
        
        // L1: 库存数据校验（最高优先级）
        Integer inventoryCount = mapper.checkInventoryExists(entity.getId());
        if (inventoryCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该库区存在%d条库存数据，请先清空库存或转移库存到其他库区", inventoryCount));
        }
        
        // L2: 库位配置校验
        Integer binCount = mapper.checkBinExists(entity.getId());
        if (binCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该库区配置了%d个库位，请先删除或转移库位配置", binCount));
        }
        
        // L3: 入库业务校验
        Integer inboundCount = mapper.checkInboundExists(entity.getId());
        if (inboundCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该库区存在%d条入库记录，请先处理入库相关业务或联系系统管理员", inboundCount));
        }
        
        // L4: 出库业务校验
        Integer outboundCount = mapper.checkOutboundExists(entity.getId());
        if (outboundCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该库区存在%d条出库记录，请先处理出库相关业务或联系系统管理员", outboundCount));
        }
        
        return CheckResultUtil.OK();
    }

    private void setPinyin(MLocationEntity entity) {
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
    public void delete(MLocationVo searchCondition) {
        // 1. 查询库区实体
        MLocationEntity location = mapper.selectById(searchCondition.getId());
        if (location == null) {
            throw new BusinessException("库区不存在，删除失败");
        }

        // 2. 执行删除前校验（按照岗位删除标准模式）
        CheckResultAo cr = checkLogic(location, CheckResultAo.DELETE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 3. 校验通过，执行删除逻辑 - 切换删除状态（复原逻辑）
        location.setIs_del(!location.getIs_del());
        boolean updateResult = updateById(location);
        if (!updateResult) {
            throw new UpdateErrorException("删除失败：数据已被修改，请刷新后重试");
        }
    }

    /**
     * 检查库区库存数量是否为 0
     * 不为0，抛出异常
     */
    private void checkLocationInventory(List<MLocationVo> searchCondition) {
        List<MInventoryVo> locationInventoryList = inventoryService.selectInventoryByLocation(searchCondition);
        if (!CollectionUtils.isEmpty(locationInventoryList) && locationInventoryList.size() > 0) {
            // 计算库区总库存数量（可用库存 + 锁定库存）
            BigDecimal totalInventory = locationInventoryList.stream()
                    .map(vo -> {
                        BigDecimal available = vo.getQty_avaible() != null ? vo.getQty_avaible() : BigDecimal.ZERO;
                        BigDecimal locked = vo.getQty_lock() != null ? vo.getQty_lock() : BigDecimal.ZERO;
                        return available.add(locked);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 格式化库存数量显示
            String formattedInventory = formatInventoryQuantity(totalInventory);
            
            throw new BusinessException(String.format("停用失败：该库区内还有商品库存 %s，请先清空库存或转移到其他库区", formattedInventory));
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
}
