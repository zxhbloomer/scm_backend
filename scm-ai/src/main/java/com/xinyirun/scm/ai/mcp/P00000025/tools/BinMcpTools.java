package com.xinyirun.scm.ai.mcp.P00000025.tools;

import com.xinyirun.scm.ai.mcp.P00000025.service.BinAiService;
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
 * 库位管理MCP工具集
 *
 * 提供自然语言访问库位管理功能的MCP工具，对应页面代码 P00000025
 *
 * 主要功能包括：
 * 1. 库位信息查询 - 支持多种条件的库位查询
 * 2. 库位详情获取 - 查看库位的完整信息
 * 3. 库区/仓库库位查询 - 查询指定库区或仓库下的所有库位
 * 4. 库位编码查找 - 按编码精确查找库位
 * 5. 可用库位查询 - 查找空闲且启用的库位
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
public class BinMcpTools {

    @Autowired
    private BinAiService binAiService;

    /**
     * 查询库位信息
     *
     * 支持按仓库、库区、编码、状态等多种条件查询库位。
     * 这是最常用的库位查询工具。
     *
     * 使用场景：
     * - "查询所有库位"
     * - "查询1号仓库的库位"
     * - "查找编码为BIN001的库位"
     * - "查询空闲状态的库位"
     * - "查找5号库区的所有库位"
     *
     * @param tenantCode 租户ID（必填）
     * @param warehouseId 仓库ID（可选，用于查询指定仓库的库位）
     * @param locationId 库区ID（可选，用于查询指定库区的库位）
     * @param code 库位编码（可选，支持模糊匹配）
     * @param name 库位名称（可选，支持模糊匹配）
     * @param status 库位状态（可选，'0'=空闲，'1'=已分配，'2'=已占用）
     * @param goodsStatus 货品状态（可选，'0'=正常品，'1'=次品）
     * @param isDefault 是否默认库位（可选，true=默认，false=非默认）
     * @return JSON格式的库位查询结果
     */
    @McpTool(description = "查询库位信息，支持按仓库、库区、编码、状态等条件查询库位列表，用于库位信息的查找和浏览")
    public String queryBins(
            @McpToolParam(description = "租户编码，用于数据权限控制") String tenantCode,
            @McpToolParam(description = "仓库ID，用于查询指定仓库的库位") Integer warehouseId,
            @McpToolParam(description = "库区ID，用于查询指定库区的库位") Integer locationId,
            @McpToolParam(description = "库位编码，支持模糊查询") String code,
            @McpToolParam(description = "库位名称，支持模糊查询") String name,
            @McpToolParam(description = "库位状态，'0'=空闲，'1'=已分配，'2'=已占用") String status,
            @McpToolParam(description = "货品状态，'0'=正常品，'1'=次品") String goodsStatus,
            @McpToolParam(description = "是否默认库位，true=默认，false=非默认") Boolean isDefault) {

        log.info("MCP工具调用 - 查询库位信息: 租户={}, 仓库ID={}, 库区ID={}, 编码={}, 名称={}, 状态={}, 货品状态={}, 默认={}",
                tenantCode, warehouseId, locationId, code, name, status, goodsStatus, isDefault);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = binAiService.queryBins(
                    warehouseId, locationId, code, name, status, goodsStatus, isDefault);

            // 添加调用信息
            result.put("tenantCode", tenantCode);
            result.put("toolName", "query_bins");
            java.util.Map<String, Object> queryConditions = new java.util.HashMap<>();
            queryConditions.put("warehouseId", warehouseId != null ? warehouseId : "");
            queryConditions.put("locationId", locationId != null ? locationId : "");
            queryConditions.put("code", code != null ? code : "");
            queryConditions.put("name", name != null ? name : "");
            queryConditions.put("status", status != null ? status : "");
            queryConditions.put("goodsStatus", goodsStatus != null ? goodsStatus : "");
            queryConditions.put("isDefault", isDefault != null ? isDefault : "");
            result.put("queryConditions", queryConditions);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 查询库位信息: 租户={}, 错误={}", tenantCode, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "查询库位信息失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "toolName", "query_bins",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 获取库位详细信息
     *
     * 查询指定库位的完整信息，包括基础信息、货品信息和库存数据。
     *
     * 使用场景：
     * - "查看100号库位的详细信息"
     * - "这个库位存储了什么货品？"
     * - "库位ID为50的详细情况"
     *
     * @param tenantCode 租户编码
     * @param binId 库位ID（必填）
     * @return JSON格式的库位详细信息
     */
    @McpTool(description = "获取指定库位的详细信息，包括基础信息、货品信息和库存数据")
    public String getBinDetail(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "库位ID，数字类型") int binId) {

        log.info("MCP工具调用 - 获取库位详细信息: 租户={}, 库位ID={}", tenantCode, binId);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = binAiService.getBinDetail(binId);

            result.put("tenantCode", tenantCode);
            result.put("toolName", "get_bin_detail");
            result.put("binId", binId);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 获取库位详细信息: 租户={}, 库位ID={}, 错误={}",
                    tenantCode, binId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "获取库位详细信息失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "binId", binId,
                    "toolName", "get_bin_detail",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 查询指定库区下的所有库位
     *
     * 用于查看某个库区的库位布局和分布。
     *
     * 使用场景：
     * - "查询5号库区的所有库位"
     * - "这个库区有哪些库位？"
     * - "库区的库位分布情况"
     *
     * @param tenantCode 租户编码
     * @param locationId 库区ID（必填）
     * @return JSON格式的库位列表
     */
    @McpTool(description = "查询指定库区下的所有库位，用于查看库区的库位布局")
    public String queryBinsByLocation(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "库区ID，数字类型") int locationId) {

        log.info("MCP工具调用 - 按库区查询库位: 租户={}, 库区ID={}", tenantCode, locationId);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = binAiService.queryBinsByLocation(locationId);

            result.put("tenantCode", tenantCode);
            result.put("toolName", "query_bins_by_location");
            result.put("locationId", locationId);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 按库区查询库位: 租户={}, 库区ID={}, 错误={}",
                    tenantCode, locationId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "按库区查询库位失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "locationId", locationId,
                    "toolName", "query_bins_by_location",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 查询指定仓库下的所有库位
     *
     * 用于查看某个仓库的全部库位情况。
     *
     * 使用场景：
     * - "查询1号仓库的所有库位"
     * - "这个仓库有多少个库位？"
     * - "仓库的库位总览"
     *
     * @param tenantCode 租户编码
     * @param warehouseId 仓库ID（必填）
     * @return JSON格式的库位列表
     */
    @McpTool(description = "查询指定仓库下的所有库位，用于查看仓库的全部库位")
    public String queryBinsByWarehouse(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "仓库ID，数字类型") int warehouseId) {

        log.info("MCP工具调用 - 按仓库查询库位: 租户={}, 仓库ID={}", tenantCode, warehouseId);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = binAiService.queryBinsByWarehouse(warehouseId);

            result.put("tenantCode", tenantCode);
            result.put("toolName", "query_bins_by_warehouse");
            result.put("warehouseId", warehouseId);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 按仓库查询库位: 租户={}, 仓库ID={}, 错误={}",
                    tenantCode, warehouseId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "按仓库查询库位失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "warehouseId", warehouseId,
                    "toolName", "query_bins_by_warehouse",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 按库位编码精确查找库位
     *
     * 精确匹配库位编码，快速查找特定库位。
     *
     * 使用场景：
     * - "查找编码为BIN001的库位"
     * - "A01-01-01这个库位的信息"
     * - "编码为SLOT-100的库位存在吗？"
     *
     * @param tenantCode 租户编码
     * @param code 库位编码（必填，精确匹配）
     * @param warehouseId 仓库ID（可选，0表示不限定仓库）
     * @param locationId 库区ID（可选，0表示不限定库区）
     * @return JSON格式的库位信息
     */
    @McpTool(description = "通过库位编码精确查找库位信息，用于快速定位特定编码的库位")
    public String findBinByCode(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "库位编码，如'BIN001'、'A01-01-01'等") String code,
            @McpToolParam(description = "仓库ID，0表示不限定仓库") Integer warehouseId,
            @McpToolParam(description = "库区ID，0表示不限定库区") Integer locationId) {

        log.info("MCP工具调用 - 按编码查找库位: 租户={}, 编码={}, 仓库ID={}, 库区ID={}",
                tenantCode, code, warehouseId, locationId);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            // 如果未指定仓库和库区，默认为0（不限定）
            int whId = (warehouseId != null) ? warehouseId : 0;
            int locId = (locationId != null) ? locationId : 0;

            Map<String, Object> result = binAiService.findBinByCode(code, whId, locId);

            result.put("tenantCode", tenantCode);
            result.put("toolName", "find_bin_by_code");
            result.put("searchCode", code);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 按编码查找库位: 租户={}, 编码={}, 错误={}",
                    tenantCode, code, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "按编码查找库位失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "searchCode", code,
                    "toolName", "find_bin_by_code",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 获取可用库位列表
     *
     * 查询空闲且启用的库位，用于入库时查找可用库位。
     * 可以指定仓库或库区进行过滤。
     *
     * 使用场景：
     * - "有哪些库位可以使用？"
     * - "1号仓库有多少个可用库位？"
     * - "5号库区有空闲库位吗？"
     * - "查找可以入库的库位"
     *
     * @param tenantCode 租户编码
     * @param warehouseId 仓库ID（可选，不指定则查询所有仓库）
     * @param locationId 库区ID（可选，不指定则查询所有库区）
     * @return JSON格式的可用库位列表
     */
    @McpTool(description = "获取可用库位列表（空闲且启用的库位），用于入库时查找可用库位")
    public String getAvailableBins(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "仓库ID（可选），不指定则查询所有仓库") Integer warehouseId,
            @McpToolParam(description = "库区ID（可选），不指定则查询所有库区") Integer locationId) {

        log.info("MCP工具调用 - 获取可用库位: 租户={}, 仓库ID={}, 库区ID={}",
                tenantCode, warehouseId, locationId);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = binAiService.queryAvailableBins(warehouseId, locationId);

            result.put("tenantCode", tenantCode);
            result.put("toolName", "get_available_bins");
            result.put("filterDescription", "只显示空闲且启用状态的库位");
            if (warehouseId != null) {
                result.put("warehouseFilter", warehouseId);
            }
            if (locationId != null) {
                result.put("locationFilter", locationId);
            }

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 获取可用库位: 租户={}, 错误={}", tenantCode, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "获取可用库位失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "toolName", "get_available_bins",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }
}
