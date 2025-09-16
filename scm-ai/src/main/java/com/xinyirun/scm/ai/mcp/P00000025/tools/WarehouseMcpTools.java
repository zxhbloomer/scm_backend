package com.xinyirun.scm.ai.mcp.P00000025.tools;

import com.xinyirun.scm.ai.mcp.P00000025.service.WarehouseAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

import java.util.Map;

/**
 * 仓库管理MCP工具集
 * 
 * 提供自然语言访问仓库管理功能的MCP工具，对应页面代码 P00000025
 * 
 * 主要功能包括：
 * 1. 仓库信息查询 - 支持多种条件的仓库查询
 * 2. 仓库状态检查 - 检查仓库的启用/停用状态
 * 3. 库区库位查询 - 查看仓库的存储结构
 * 4. 仓库推荐 - 根据条件推荐合适的仓库
 * 
 * 设计原则：
 * - 所有工具都是只读操作，不进行数据修改
 * - 返回JSON格式的结构化数据，便于AI理解和用户查看
 * - 提供详细的操作日志，便于问题追踪
 * - 参数验证和异常处理，确保稳定性
 * 
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Slf4j
@Component
public class WarehouseMcpTools {

    @Autowired
    private WarehouseAiService warehouseAiService;

    /**
     * 查询仓库信息
     * 
     * 这是最常用的仓库查询工具，支持多种查询条件的组合使用。
     * 用户可以通过仓库编码、名称、状态等条件来查找仓库信息。
     * 
     * 使用场景：
     * - "查询所有仓库"
     * - "找一下主仓库"
     * - "查询编码为WH001的仓库"
     * - "查询启用状态的仓库"
     * - "查找名称包含'成品'的仓库"
     * 
     * @param tenantId 租户ID（必填，用于多租户数据隔离）
     * @param code 仓库编码（可选，支持模糊匹配）
     * @param name 仓库名称（可选，支持模糊匹配）
     * @param status 仓库状态（可选，ENABLED-启用/DISABLED-停用）
     * @param warehouseType 仓库类型（可选，根据业务定义）
     * @return JSON格式的仓库查询结果
     */
    @Tool(description = "查询仓库信息，支持按编码、名称、状态等条件查询仓库列表，用于仓库信息的查找和浏览")
    public String queryWarehouses(
            @ToolParam(description = "租户ID，用于数据权限控制") String tenantId,
            @ToolParam(description = "仓库编码，支持模糊查询，如'WH001'") String code,
            @ToolParam(description = "仓库名称，支持模糊查询，如'主仓库'") String name,
            @ToolParam(description = "仓库状态：ENABLED-启用，DISABLED-停用") String status,
            @ToolParam(description = "仓库类型，如'成品仓'、'原料仓'等") String warehouseType) {
        
        log.info("MCP工具调用 - 查询仓库信息: 租户={}, 编码={}, 名称={}, 状态={}, 类型={}", 
                tenantId, code, name, status, warehouseType);
        
        try {
            // 调用AI服务执行查询
            Map<String, Object> result = warehouseAiService.queryWarehouses(code, name, status, warehouseType);
            
            // 添加调用信息
            result.put("tenantId", tenantId);
            result.put("toolName", "query_warehouses");
            result.put("queryConditions", Map.of(
                "code", code != null ? code : "",
                "name", name != null ? name : "",
                "status", status != null ? status : "",
                "warehouseType", warehouseType != null ? warehouseType : ""
            ));
            
            // 转换为JSON字符串返回
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);
            
        } catch (Exception e) {
            log.error("MCP工具异常 - 查询仓库信息: 租户={}, 错误={}", tenantId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "message", "查询仓库信息失败: " + e.getMessage(),
                "tenantId", tenantId,
                "toolName", "query_warehouses",
                "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        }
    }

    /**
     * 获取仓库详细信息
     * 
     * 查询指定仓库的完整信息，包括基础信息和库区库位结构。
     * 当用户需要了解某个仓库的详细情况时使用。
     * 
     * 使用场景：
     * - "查看1号仓库的详细信息"
     * - "这个仓库有哪些库区库位？"
     * - "仓库ID为5的详细情况"
     * 
     * @param tenantId 租户ID
     * @param warehouseId 仓库ID（必填）
     * @return JSON格式的仓库详细信息
     */
    @Tool(description = "获取指定仓库的详细信息，包括基础信息、库区库位结构等完整数据")
    public String getWarehouseDetail(
            @ToolParam(description = "租户ID") String tenantId,
            @ToolParam(description = "仓库ID，数字类型") int warehouseId) {
        
        log.info("MCP工具调用 - 获取仓库详细信息: 租户={}, 仓库ID={}", tenantId, warehouseId);
        
        try {
            Map<String, Object> result = warehouseAiService.getWarehouseDetail(warehouseId);
            
            // 添加调用信息
            result.put("tenantId", tenantId);
            result.put("toolName", "get_warehouse_detail");
            result.put("warehouseId", warehouseId);
            
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);
            
        } catch (Exception e) {
            log.error("MCP工具异常 - 获取仓库详细信息: 租户={}, 仓库ID={}, 错误={}", 
                     tenantId, warehouseId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "message", "获取仓库详细信息失败: " + e.getMessage(),
                "tenantId", tenantId,
                "warehouseId", warehouseId,
                "toolName", "get_warehouse_detail",
                "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        }
    }

    /**
     * 按仓库编码查找仓库
     * 
     * 精确匹配仓库编码，快速查找特定仓库。
     * 仓库编码通常是唯一的，此工具用于精确查找。
     * 
     * 使用场景：
     * - "查找编码WH001的仓库"
     * - "WH-MAIN这个仓库的信息"
     * - "编码为STORE-01的仓库存在吗？"
     * 
     * @param tenantId 租户ID
     * @param code 仓库编码（必填，精确匹配）
     * @return JSON格式的仓库信息
     */
    @Tool(description = "通过仓库编码精确查找仓库信息，用于快速定位特定编码的仓库")
    public String findWarehouseByCode(
            @ToolParam(description = "租户ID") String tenantId,
            @ToolParam(description = "仓库编码，如'WH001'、'MAIN_STORE'等") String code) {
        
        log.info("MCP工具调用 - 按编码查找仓库: 租户={}, 编码={}", tenantId, code);
        
        try {
            Map<String, Object> result = warehouseAiService.findWarehouseByCode(code);
            
            result.put("tenantId", tenantId);
            result.put("toolName", "find_warehouse_by_code");
            result.put("searchCode", code);
            
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);
            
        } catch (Exception e) {
            log.error("MCP工具异常 - 按编码查找仓库: 租户={}, 编码={}, 错误={}", 
                     tenantId, code, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "message", "按编码查找仓库失败: " + e.getMessage(),
                "tenantId", tenantId,
                "searchCode", code,
                "toolName", "find_warehouse_by_code",
                "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        }
    }

    /**
     * 按仓库名称模糊查找仓库
     * 
     * 支持仓库名称的模糊匹配，适用于用户只知道部分名称的情况。
     * 
     * 使用场景：
     * - "找一下主仓库"
     * - "包含'成品'的仓库有哪些？"
     * - "原材料相关的仓库"
     * 
     * @param tenantId 租户ID
     * @param name 仓库名称（必填，支持模糊匹配）
     * @return JSON格式的匹配仓库列表
     */
    @Tool(description = "通过仓库名称模糊查找仓库，支持部分名称匹配，用于按名称搜索仓库")
    public String findWarehousesByName(
            @ToolParam(description = "租户ID") String tenantId,
            @ToolParam(description = "仓库名称，支持部分匹配，如'主仓'、'成品'等") String name) {
        
        log.info("MCP工具调用 - 按名称查找仓库: 租户={}, 名称={}", tenantId, name);
        
        try {
            Map<String, Object> result = warehouseAiService.findWarehousesByName(name);
            
            result.put("tenantId", tenantId);
            result.put("toolName", "find_warehouses_by_name");
            result.put("searchName", name);
            
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);
            
        } catch (Exception e) {
            log.error("MCP工具异常 - 按名称查找仓库: 租户={}, 名称={}, 错误={}", 
                     tenantId, name, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "message", "按名称查找仓库失败: " + e.getMessage(),
                "tenantId", tenantId,
                "searchName", name,
                "toolName", "find_warehouses_by_name",
                "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        }
    }

    /**
     * 检查仓库状态
     * 
     * 检查指定仓库的当前状态，包括是否启用、是否可用等信息。
     * 用于确认仓库是否可以正常使用。
     * 
     * 使用场景：
     * - "检查5号仓库的状态"
     * - "这个仓库现在能用吗？"
     * - "仓库的启用状态如何？"
     * 
     * @param tenantId 租户ID
     * @param warehouseId 仓库ID（必填）
     * @return JSON格式的仓库状态信息
     */
    @Tool(description = "检查仓库的当前状态，包括启用/停用状态，是否可以使用等信息")
    public String checkWarehouseStatus(
            @ToolParam(description = "租户ID") String tenantId,
            @ToolParam(description = "仓库ID") int warehouseId) {
        
        log.info("MCP工具调用 - 检查仓库状态: 租户={}, 仓库ID={}", tenantId, warehouseId);
        
        try {
            Map<String, Object> result = warehouseAiService.checkWarehouseStatus(warehouseId);
            
            result.put("tenantId", tenantId);
            result.put("toolName", "check_warehouse_status");
            
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);
            
        } catch (Exception e) {
            log.error("MCP工具异常 - 检查仓库状态: 租户={}, 仓库ID={}, 错误={}", 
                     tenantId, warehouseId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "message", "检查仓库状态失败: " + e.getMessage(),
                "tenantId", tenantId,
                "warehouseId", warehouseId,
                "toolName", "check_warehouse_status",
                "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        }
    }

    /**
     * 获取所有可用仓库
     * 
     * 查询所有处于启用状态的仓库，用于获取当前可用的仓库列表。
     * 这是一个快捷查询，相当于状态过滤为"启用"的仓库查询。
     * 
     * 使用场景：
     * - "有哪些仓库可以使用？"
     * - "列出所有启用的仓库"
     * - "当前可用的仓库列表"
     * 
     * @param tenantId 租户ID
     * @return JSON格式的启用仓库列表
     */
    @Tool(description = "获取所有启用状态的仓库列表，用于查看当前可以使用的仓库")
    public String getAvailableWarehouses(
            @ToolParam(description = "租户ID") String tenantId) {
        
        log.info("MCP工具调用 - 获取可用仓库: 租户={}", tenantId);
        
        try {
            Map<String, Object> result = warehouseAiService.getEnabledWarehouses();
            
            result.put("tenantId", tenantId);
            result.put("toolName", "get_available_warehouses");
            result.put("filterDescription", "只显示启用状态的仓库");
            
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);
            
        } catch (Exception e) {
            log.error("MCP工具异常 - 获取可用仓库: 租户={}, 错误={}", tenantId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "message", "获取可用仓库失败: " + e.getMessage(),
                "tenantId", tenantId,
                "toolName", "get_available_warehouses",
                "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        }
    }

    /**
     * 仓库推荐工具
     * 
     * 根据指定条件推荐合适的仓库。这是一个智能推荐工具，
     * 可以根据用户的需求（如状态要求、类型偏好等）推荐最适合的仓库。
     * 
     * 使用场景：
     * - "推荐一个可用的仓库"
     * - "给我推荐存放成品的仓库"
     * - "需要一个启用状态的仓库"
     * 
     * @param tenantId 租户ID
     * @param requirement 需求描述（可选）
     * @param preferredType 偏好类型（可选）
     * @param mustEnabled 是否必须启用（可选，默认true）
     * @return JSON格式的仓库推荐结果
     */
    @Tool(description = "根据指定条件推荐合适的仓库，提供智能化的仓库选择建议")
    public String recommendWarehouses(
            @ToolParam(description = "租户ID") String tenantId,
            @ToolParam(description = "需求描述，如'存放成品'、'临时存储'等") String requirement,
            @ToolParam(description = "偏好的仓库类型") String preferredType,
            @ToolParam(description = "是否必须启用，true或false") Boolean mustEnabled) {
        
        log.info("MCP工具调用 - 仓库推荐: 租户={}, 需求={}, 偏好类型={}, 必须启用={}", 
                tenantId, requirement, preferredType, mustEnabled);
        
        try {
            // 根据推荐条件查询仓库
            String status = (mustEnabled == null || mustEnabled) ? "ENABLED" : null;
            Map<String, Object> result = warehouseAiService.queryWarehouses(null, null, status, preferredType);
            
            // 添加推荐逻辑的结果信息
            result.put("tenantId", tenantId);
            result.put("toolName", "recommend_warehouses");
            result.put("recommendationCriteria", Map.of(
                "requirement", requirement != null ? requirement : "无特殊要求",
                "preferredType", preferredType != null ? preferredType : "无类型偏好",
                "mustEnabled", mustEnabled != null ? mustEnabled : true
            ));
            
            // 如果有结果，添加推荐说明
            if ((Boolean) result.get("success") && result.containsKey("warehouses")) {
                result.put("recommendationNote", "根据您的需求，以下仓库符合条件。建议优先选择启用状态的仓库。");
            }
            
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);
            
        } catch (Exception e) {
            log.error("MCP工具异常 - 仓库推荐: 租户={}, 错误={}", tenantId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "message", "仓库推荐失败: " + e.getMessage(),
                "tenantId", tenantId,
                "toolName", "recommend_warehouses",
                "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        }
    }
}