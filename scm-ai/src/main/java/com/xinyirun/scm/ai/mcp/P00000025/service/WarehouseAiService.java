package com.xinyirun.scm.ai.mcp.P00000025.service;

import com.xinyirun.scm.bean.system.vo.master.warehouse.MWarehouseVo;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MWarehouseLocationBinVo;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库管理AI服务类
 * 
 * 为AI工具提供仓库相关业务逻辑支持，包括：
 * 1. 仓库基础信息查询和管理
 * 2. 库区库位信息查询
 * 3. 仓库状态管理（启用/停用）
 * 4. 仓库权限和分组管理
 * 
 * 注意：这是对现有IMWarehouseService的AI友好封装
 * 主要目的是为MCP工具提供更简洁、更适合自然语言交互的接口
 * 
 * @author zxh
 * @since 1.0.0
 */
@Slf4j
@Service
public class WarehouseAiService {

    @Autowired
    private IMWarehouseService warehouseService;

    /**
     * 查询仓库列表
     *
     * 提供灵活的仓库查询功能，支持按多种条件过滤
     *
     * @param code 仓库编码（可选，支持模糊查询）
     * @param name 仓库名称（可选，支持模糊查询）
     * @param shortName 仓库简称（可选，支持模糊查询）
     * @param province 省份（可选）
     * @param city 城市（可选）
     * @param status 状态（可选）：ENABLED-启用，DISABLED-停用
     * @param warehouseType 仓库类型（可选）
     * @param enableLocation 是否启用库区（可选）
     * @param enableBin 是否启用库位（可选）
     * @return 仓库查询结果
     */
    public Map<String, Object> queryWarehouses(String code, String name, String shortName,
                                                String province, String city, String status,
                                                String warehouseType, Boolean enableLocation,
                                                Boolean enableBin) {
        log.info("AI查询仓库列表 - 编码: {}, 名称: {}, 简称: {}, 省份: {}, 城市: {}, 状态: {}, 类型: {}, 启用库区: {}, 启用库位: {}",
                code, name, shortName, province, city, status, warehouseType, enableLocation, enableBin);

        try {
            // 构建查询条件
            MWarehouseVo searchCondition = new MWarehouseVo();
            if (code != null && !code.trim().isEmpty()) {
                searchCondition.setCode(code.trim());
            }
            if (name != null && !name.trim().isEmpty()) {
                searchCondition.setName(name.trim());
            }
            if (shortName != null && !shortName.trim().isEmpty()) {
                searchCondition.setShort_name(shortName.trim());
            }
            if (province != null && !province.trim().isEmpty()) {
                searchCondition.setProvince(province.trim());
            }
            if (city != null && !city.trim().isEmpty()) {
                searchCondition.setCity(city.trim());
            }
            if (status != null && !status.trim().isEmpty()) {
                // 将AI友好的状态转换为实际状态值
                if ("ENABLED".equalsIgnoreCase(status) || "启用".equals(status)) {
                    searchCondition.setEnable(true);
                } else if ("DISABLED".equalsIgnoreCase(status) || "停用".equals(status)) {
                    searchCondition.setEnable(false);
                }
            }
            if (warehouseType != null && !warehouseType.trim().isEmpty()) {
                searchCondition.setWarehouse_type(warehouseType.trim());
            }
            if (enableLocation != null) {
                searchCondition.setEnable_location(enableLocation);
            }
            if (enableBin != null) {
                searchCondition.setEnable_bin(enableBin);
            }
            
            // 调用现有服务查询
            List<MWarehouseVo> warehouses = warehouseService.selectList(searchCondition);
            
            // 构建AI友好的返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "仓库查询成功");
            result.put("total", warehouses.size());
            result.put("warehouses", warehouses);
            
            // 添加统计信息
            result.put("summary", calculateWarehouseSummary(warehouses));
            
            log.info("仓库查询完成，找到 {} 个仓库", warehouses.size());
            return result;
            
        } catch (Exception e) {
            log.error("仓库查询异常 - 错误: {}", e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "仓库查询失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 获取仓库详细信息
     * 
     * @param warehouseId 仓库ID
     * @return 仓库详细信息
     */
    public Map<String, Object> getWarehouseDetail(int warehouseId) {
        log.info("AI查询仓库详细信息 - 仓库ID: {}", warehouseId);
        
        try {
            // 查询仓库基础信息
            MWarehouseVo warehouse = warehouseService.selectById(warehouseId);
            
            if (warehouse == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "未找到仓库信息，仓库ID: " + warehouseId);
                return result;
            }
            
            // 查询库区库位信息
            MWarehouseLocationBinVo locationBin = null;
            try {
                locationBin = warehouseService.selectWarehouseLocationBin(warehouseId);
            } catch (Exception e) {
                log.warn("查询库区库位信息异常，仓库ID: {} - 错误: {}", warehouseId, e.getMessage());
            }
            
            // 构建详细信息
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "仓库详细信息查询成功");
            result.put("warehouse", warehouse);
            result.put("locationBin", locationBin);
            result.put("hasLocationBin", locationBin != null);
            
            log.info("仓库详细信息查询完成 - 仓库: {} [{}]", warehouse.getName(), warehouse.getCode());
            return result;
            
        } catch (Exception e) {
            log.error("仓库详细信息查询异常 - 仓库ID: {}, 错误: {}", warehouseId, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "仓库详细信息查询失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按编码查询仓库
     * 
     * @param code 仓库编码（精确匹配）
     * @return 仓库信息
     */
    public Map<String, Object> findWarehouseByCode(String code) {
        log.info("AI按编码查询仓库 - 编码: {}", code);
        
        if (code == null || code.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "仓库编码不能为空");
            return result;
        }
        
        try {
            List<com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity> warehouses = 
                warehouseService.selectByCode(code.trim(), null);
            
            Map<String, Object> result = new HashMap<>();
            
            if (warehouses.isEmpty()) {
                result.put("success", false);
                result.put("message", "未找到仓库，编码: " + code);
                result.put("warehouse", null);
            } else {
                // 转换为VO对象
                com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity entity = warehouses.get(0);
                MWarehouseVo warehouse = warehouseService.selectById(entity.getId());
                
                result.put("success", true);
                result.put("message", "仓库查询成功");
                result.put("warehouse", warehouse);
                
                if (warehouses.size() > 1) {
                    result.put("warning", "找到多个相同编码的仓库，返回第一个");
                }
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("按编码查询仓库异常 - 编码: {}, 错误: {}", code, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "按编码查询仓库失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按名称模糊查询仓库
     * 
     * @param name 仓库名称（支持模糊查询）
     * @return 匹配的仓库列表
     */
    public Map<String, Object> findWarehousesByName(String name) {
        log.info("AI按名称模糊查询仓库 - 名称: {}", name);
        
        if (name == null || name.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "仓库名称不能为空");
            return result;
        }
        
        try {
            // 使用名称模糊查询
            MWarehouseVo searchCondition = new MWarehouseVo();
            searchCondition.setName(name.trim());
            
            List<MWarehouseVo> warehouses = warehouseService.selectList(searchCondition);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "仓库名称查询完成");
            result.put("total", warehouses.size());
            result.put("warehouses", warehouses);
            result.put("searchName", name.trim());
            
            if (warehouses.isEmpty()) {
                result.put("message", "未找到匹配的仓库，名称包含: " + name);
            } else {
                log.info("找到 {} 个匹配的仓库", warehouses.size());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("按名称查询仓库异常 - 名称: {}, 错误: {}", name, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "按名称查询仓库失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 检查仓库状态
     * 
     * @param warehouseId 仓库ID
     * @return 仓库状态信息
     */
    public Map<String, Object> checkWarehouseStatus(int warehouseId) {
        log.info("AI检查仓库状态 - 仓库ID: {}", warehouseId);
        
        try {
            MWarehouseVo warehouse = warehouseService.selectById(warehouseId);
            
            Map<String, Object> result = new HashMap<>();
            
            if (warehouse == null) {
                result.put("success", false);
                result.put("message", "未找到仓库信息，仓库ID: " + warehouseId);
                return result;
            }
            
            // 分析仓库状态
            result.put("success", true);
            result.put("warehouseId", warehouseId);
            result.put("warehouseCode", warehouse.getCode());
            result.put("warehouseName", warehouse.getName());
            result.put("status", warehouse.getEnable());
            result.put("statusName", Boolean.TRUE.equals(warehouse.getEnable()) ? "启用" : "停用");
            result.put("isActive", Boolean.TRUE.equals(warehouse.getEnable()));
            result.put("canUse", Boolean.TRUE.equals(warehouse.getEnable()));
            
            // 添加状态描述
            if (Boolean.TRUE.equals(warehouse.getEnable())) {
                result.put("statusDescription", "仓库处于启用状态，可以正常使用");
            } else {
                result.put("statusDescription", "仓库处于停用状态，无法使用");
                result.put("recommendation", "如需使用此仓库，请先启用");
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("检查仓库状态异常 - 仓库ID: {}, 错误: {}", warehouseId, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "检查仓库状态失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 获取所有启用的仓库
     * 
     * @return 启用的仓库列表
     */
    public Map<String, Object> getEnabledWarehouses() {
        log.info("AI查询所有启用的仓库");
        
        try {
            // 查询启用状态的仓库
            MWarehouseVo searchCondition = new MWarehouseVo();
            searchCondition.setEnable(true); // true表示启用
            
            List<MWarehouseVo> warehouses = warehouseService.selectList(searchCondition);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "启用仓库查询完成");
            result.put("total", warehouses.size());
            result.put("warehouses", warehouses);
            result.put("statusFilter", "只显示启用状态的仓库");
            
            log.info("查询到 {} 个启用的仓库", warehouses.size());
            return result;
            
        } catch (Exception e) {
            log.error("查询启用仓库异常 - 错误: {}", e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询启用仓库失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 计算仓库统计信息
     * 
     * @param warehouses 仓库列表
     * @return 统计信息
     */
    private Map<String, Object> calculateWarehouseSummary(List<MWarehouseVo> warehouses) {
        Map<String, Object> summary = new HashMap<>();
        
        if (warehouses == null || warehouses.isEmpty()) {
            summary.put("total", 0);
            summary.put("enabled", 0);
            summary.put("disabled", 0);
            return summary;
        }
        
        long enabledCount = warehouses.stream().filter(w -> Boolean.TRUE.equals(w.getEnable())).count();
        long disabledCount = warehouses.stream().filter(w -> !Boolean.TRUE.equals(w.getEnable())).count();
        
        summary.put("total", warehouses.size());
        summary.put("enabled", enabledCount);
        summary.put("disabled", disabledCount);
        summary.put("enabledPercent", warehouses.size() > 0 ? (enabledCount * 100.0 / warehouses.size()) : 0);

        // 按仓库类型统计
        Map<String, Long> typeCount = new HashMap<>();
        for (MWarehouseVo warehouse : warehouses) {
            String type = warehouse.getWarehouse_type();
            if (type != null && !type.trim().isEmpty()) {
                typeCount.put(type.trim(), typeCount.getOrDefault(type.trim(), 0L) + 1);
            } else {
                typeCount.put("未分类", typeCount.getOrDefault("未分类", 0L) + 1);
            }
        }
        summary.put("typeDistribution", typeCount);

        return summary;
    }

    /**
     * 按仓库类型查询
     *
     * @param warehouseType 仓库类型（必填）
     * @return 匹配该类型的仓库列表
     */
    public Map<String, Object> getWarehousesByType(String warehouseType) {
        log.info("AI按类型查询仓库 - 类型: {}", warehouseType);

        if (warehouseType == null || warehouseType.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "仓库类型不能为空");
            return result;
        }

        try {
            MWarehouseVo searchCondition = new MWarehouseVo();
            searchCondition.setWarehouse_type(warehouseType.trim());

            List<MWarehouseVo> warehouses = warehouseService.selectList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "按类型查询仓库成功");
            result.put("total", warehouses.size());
            result.put("warehouses", warehouses);
            result.put("searchType", warehouseType.trim());
            result.put("summary", calculateWarehouseSummary(warehouses));

            log.info("找到 {} 个类型为 '{}' 的仓库", warehouses.size(), warehouseType);
            return result;

        } catch (Exception e) {
            log.error("按类型查询仓库异常 - 类型: {}, 错误: {}", warehouseType, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "按类型查询仓库失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按省份查询仓库
     *
     * @param province 省份名称（必填）
     * @return 该省份的仓库列表
     */
    public Map<String, Object> getWarehousesByProvince(String province) {
        log.info("AI按省份查询仓库 - 省份: {}", province);

        if (province == null || province.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "省份名称不能为空");
            return result;
        }

        try {
            MWarehouseVo searchCondition = new MWarehouseVo();
            searchCondition.setProvince(province.trim());

            List<MWarehouseVo> warehouses = warehouseService.selectList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "按省份查询仓库成功");
            result.put("total", warehouses.size());
            result.put("warehouses", warehouses);
            result.put("searchProvince", province.trim());
            result.put("summary", calculateWarehouseSummary(warehouses));

            log.info("在省份 '{}' 找到 {} 个仓库", province, warehouses.size());
            return result;

        } catch (Exception e) {
            log.error("按省份查询仓库异常 - 省份: {}, 错误: {}", province, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "按省份查询仓库失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按城市查询仓库
     *
     * @param city 城市名称（必填）
     * @return 该城市的仓库列表
     */
    public Map<String, Object> getWarehousesByCity(String city) {
        log.info("AI按城市查询仓库 - 城市: {}", city);

        if (city == null || city.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "城市名称不能为空");
            return result;
        }

        try {
            MWarehouseVo searchCondition = new MWarehouseVo();
            searchCondition.setCity(city.trim());

            List<MWarehouseVo> warehouses = warehouseService.selectList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "按城市查询仓库成功");
            result.put("total", warehouses.size());
            result.put("warehouses", warehouses);
            result.put("searchCity", city.trim());
            result.put("summary", calculateWarehouseSummary(warehouses));

            log.info("在城市 '{}' 找到 {} 个仓库", city, warehouses.size());
            return result;

        } catch (Exception e) {
            log.error("按城市查询仓库异常 - 城市: {}, 错误: {}", city, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "按城市查询仓库失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 构建仓库状态描述（用于AI理解）
     *
     * @param warehouse 仓库对象
     * @return 状态描述文本
     */
    private String buildWarehouseStatusDescription(MWarehouseVo warehouse) {
        if (warehouse == null) {
            return "仓库信息不存在";
        }

        StringBuilder description = new StringBuilder();

        description.append("仓库 ").append(warehouse.getName())
                .append(" (").append(warehouse.getCode()).append(")");

        if (Boolean.TRUE.equals(warehouse.getEnable())) {
            description.append(", 状态: 启用中");
        } else {
            description.append(", 状态: 已停用");
        }

        if (warehouse.getWarehouse_type() != null && !warehouse.getWarehouse_type().trim().isEmpty()) {
            description.append(", 类型: ").append(warehouse.getWarehouse_type());
        }

        if (Boolean.TRUE.equals(warehouse.getEnable_location())) {
            description.append(", 启用库区管理");
        }

        if (Boolean.TRUE.equals(warehouse.getEnable_bin())) {
            description.append(", 启用库位管理");
        }

        if (warehouse.getProvince() != null || warehouse.getCity() != null) {
            description.append(", 位置: ");
            if (warehouse.getProvince() != null) {
                description.append(warehouse.getProvince());
            }
            if (warehouse.getCity() != null) {
                description.append(warehouse.getCity());
            }
        }

        return description.toString();
    }
}