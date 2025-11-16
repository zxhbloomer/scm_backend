package com.xinyirun.scm.ai.mcp.P00000025.tools;

import com.xinyirun.scm.ai.mcp.P00000025.service.LocationAiService;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

import java.util.Map;

/**
 * 库区管理MCP工具集
 *
 * 提供自然语言访问库区管理功能的MCP工具，对应页面代码 P00000025
 *
 * 主要功能包括：
 * 1. 库区信息查询 - 支持多种条件的库区查询
 * 2. 库区详情获取 - 查看库区的完整信息
 * 3. 仓库库区查询 - 查询指定仓库下的所有库区
 * 4. 库区编码查找 - 按编码精确查找库区
 *
 * 设计原则：
 * - 所有工具都是只读操作，不进行数据修改
 * - 返回JSON格式的结构化数据，便于AI理解和用户查看
 * - 提供详细的操作日志，便于问题追踪
 * - 参数验证和异常处理，确保稳定性
 *
 * @author zzxxhh
 * @since 2025-11-04
 */
@Slf4j
@Component
public class LocationMcpTools {

    @Autowired
    private LocationAiService locationAiService;

    /**
     * 查询库区信息
     *
     * 支持按仓库、编码、名称、状态等多种条件查询库区。
     * 这是最常用的库区查询工具。
     *
     * 使用场景：
     * - "查询所有库区"
     * - "查询1号仓库的库区"
     * - "查找编码为LOC001的库区"
     * - "查询启用状态的库区"
     * - "查找名称包含'A区'的库区"
     *
     * @param tenantCode 租户ID（必填）
     * @param warehouseId 仓库ID（可选，用于查询指定仓库的库区）
     * @param code 库区编码（可选，支持模糊匹配）
     * @param name 库区名称（可选，支持模糊匹配）
     * @param status 状态（可选，ENABLED-启用，DISABLED-停用）
     * @param isDefault 是否默认库区（可选，true=默认，false=非默认）
     * @return JSON格式的库区查询结果
     */
    @McpTool(description = "查询库区信息，支持按仓库、编码、名称、状态等条件查询库区列表，用于库区信息的查找和浏览")
    public String queryLocations(
            @McpToolParam(description = "租户编码，用于数据权限控制") String tenantCode,
            @McpToolParam(description = "仓库ID，用于查询指定仓库的库区") Integer warehouseId,
            @McpToolParam(description = "库区编码，支持模糊查询") String code,
            @McpToolParam(description = "库区名称，支持模糊查询") String name,
            @McpToolParam(description = "状态，ENABLED-启用，DISABLED-停用") String status,
            @McpToolParam(description = "是否默认库区，true=默认，false=非默认") Boolean isDefault) {

        log.info("MCP工具调用 - 查询库区信息: 租户={}, 仓库ID={}, 编码={}, 名称={}, 状态={}, 默认={}",
                tenantCode, warehouseId, code, name, status, isDefault);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = locationAiService.queryLocations(
                    warehouseId, code, name, status, isDefault);

            // 添加调用信息
            result.put("tenantCode", tenantCode);
            result.put("toolName", "query_locations");
            java.util.Map<String, Object> queryConditions = new java.util.HashMap<>();
            queryConditions.put("warehouseId", warehouseId != null ? warehouseId : "");
            queryConditions.put("code", code != null ? code : "");
            queryConditions.put("name", name != null ? name : "");
            queryConditions.put("status", status != null ? status : "");
            queryConditions.put("isDefault", isDefault != null ? isDefault : "");
            result.put("queryConditions", queryConditions);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 查询库区信息: 租户={}, 错误={}", tenantCode, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "查询库区信息失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "toolName", "query_locations",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 获取库区详细信息
     *
     * 查询指定库区的完整信息，包括基础信息和关联的库位统计数据。
     *
     * 使用场景：
     * - "查看5号库区的详细信息"
     * - "这个库区有多少个库位？"
     * - "库区ID为10的详细情况"
     *
     * @param tenantCode 租户ID
     * @param locationId 库区ID（必填）
     * @return JSON格式的库区详细信息
     */
    @McpTool(description = "获取指定库区的详细信息，包括基础信息和关联的库位统计数据")
    public String getLocationDetail(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "库区ID，数字类型") int locationId) {

        log.info("MCP工具调用 - 获取库区详细信息: 租户={}, 库区ID={}", tenantCode, locationId);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = locationAiService.getLocationDetail(locationId);

            result.put("tenantCode", tenantCode);
            result.put("toolName", "get_location_detail");
            result.put("locationId", locationId);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 获取库区详细信息: 租户={}, 库区ID={}, 错误={}",
                    tenantCode, locationId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "获取库区详细信息失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "locationId", locationId,
                    "toolName", "get_location_detail",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 查询指定仓库下的所有库区
     *
     * 用于查看某个仓库的库区布局和结构。
     *
     * 使用场景：
     * - "查询1号仓库的所有库区"
     * - "这个仓库有哪些库区？"
     * - "仓库的库区分布情况"
     *
     * @param tenantCode 租户ID
     * @param warehouseId 仓库ID（必填）
     * @return JSON格式的库区列表
     */
    @McpTool(description = "查询指定仓库下的所有库区，用于查看仓库的库区布局")
    public String queryLocationsByWarehouse(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "仓库ID，数字类型") int warehouseId) {

        log.info("MCP工具调用 - 按仓库查询库区: 租户={}, 仓库ID={}", tenantCode, warehouseId);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = locationAiService.queryLocationsByWarehouse(warehouseId);

            result.put("tenantCode", tenantCode);
            result.put("toolName", "query_locations_by_warehouse");
            result.put("warehouseId", warehouseId);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 按仓库查询库区: 租户={}, 仓库ID={}, 错误={}",
                    tenantCode, warehouseId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "按仓库查询库区失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "warehouseId", warehouseId,
                    "toolName", "query_locations_by_warehouse",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 按库区编码精确查找库区
     *
     * 精确匹配库区编码，快速查找特定库区。
     *
     * 使用场景：
     * - "查找编码为LOC001的库区"
     * - "A-ZONE这个库区的信息"
     * - "编码为AREA-01的库区存在吗？"
     *
     * @param tenantCode 租户ID
     * @param code 库区编码（必填，精确匹配）
     * @param warehouseId 仓库ID（可选，0表示不限定仓库）
     * @return JSON格式的库区信息
     */
    @McpTool(description = "通过库区编码精确查找库区信息，用于快速定位特定编码的库区")
    public String findLocationByCode(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "库区编码，如'LOC001'、'A-ZONE'等") String code,
            @McpToolParam(description = "仓库ID，0表示不限定仓库") Integer warehouseId) {

        log.info("MCP工具调用 - 按编码查找库区: 租户={}, 编码={}, 仓库ID={}", tenantCode, code, warehouseId);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            // 如果未指定仓库，默认为0(不限定)
            int whId = (warehouseId != null) ? warehouseId : 0;

            Map<String, Object> result = locationAiService.findLocationByCode(code, whId);

            result.put("tenantCode", tenantCode);
            result.put("toolName", "find_location_by_code");
            result.put("searchCode", code);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 按编码查找库区: 租户={}, 编码={}, 错误={}",
                    tenantCode, code, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "按编码查找库区失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "searchCode", code,
                    "toolName", "find_location_by_code",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 按库区名称模糊查找库区
     *
     * 支持库区名称的模糊匹配，适用于用户只知道部分名称的情况。
     *
     * 使用场景：
     * - "找一下A区"
     * - "包含'成品'的库区有哪些？"
     * - "原材料相关的库区"
     *
     * @param tenantCode 租户ID
     * @param name 库区名称（必填，支持模糊匹配）
     * @param warehouseId 仓库ID（可选，null表示不限定仓库）
     * @return JSON格式的匹配库区列表
     */
    @McpTool(description = "通过库区名称模糊查找库区，支持部分名称匹配，用于按名称搜索库区")
    public String findLocationsByName(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "库区名称，支持部分匹配，如'A区'、'成品'等") String name,
            @McpToolParam(description = "仓库ID，null表示不限定仓库") Integer warehouseId) {

        log.info("MCP工具调用 - 按名称查找库区: 租户={}, 名称={}, 仓库ID={}", tenantCode, name, warehouseId);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = locationAiService.findLocationsByName(name, warehouseId);

            result.put("tenantCode", tenantCode);
            result.put("toolName", "find_locations_by_name");
            result.put("searchName", name);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 按名称查找库区: 租户={}, 名称={}, 错误={}",
                    tenantCode, name, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "按名称查找库区失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "searchName", name,
                    "toolName", "find_locations_by_name",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }
}
