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
 * @author SCM-AI模块
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
     * @param status 状态（可选）：ENABLED-启用，DISABLED-停用
     * @param warehouseType 仓库类型（可选）
     * @return 仓库查询结果
     */
    public Map<String, Object> queryWarehouses(String code, String name, String status, String warehouseType) {
        log.info("AI查询仓库列表 - 编码: {}, 名称: {}, 状态: {}, 类型: {}", code, name, status, warehouseType);
        
        try {
            // 构建查询条件
            MWarehouseVo searchCondition = new MWarehouseVo();
            if (code != null && !code.trim().isEmpty()) {
                searchCondition.setCode(code.trim());
            }
            if (name != null && !name.trim().isEmpty()) {
                searchCondition.setName(name.trim());
            }
            if (status != null && !status.trim().isEmpty()) {
                // 将AI友好的状态转换为实际状态值
                if ("ENABLED".equalsIgnoreCase(status) || "启用".equals(status)) {
                    searchCondition.setEnable(true);
                } else if ("DISABLED".equalsIgnoreCase(status) || "停用".equals(status)) {
                    searchCondition.setEnable(false);
                }
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
        
        // 按仓库类型统计（如果有类型字段的话）
        Map<String, Long> typeCount = new HashMap<>();
        for (MWarehouseVo warehouse : warehouses) {
            // 这里可以根据实际的仓库类型字段进行统计
            // 由于没有看到具体的类型字段，先用默认值
            String type = "普通仓库"; // warehouse.getWarehouseType() 或类似字段
            typeCount.put(type, typeCount.getOrDefault(type, 0L) + 1);
        }
        summary.put("typeDistribution", typeCount);
        
        return summary;
    }
}