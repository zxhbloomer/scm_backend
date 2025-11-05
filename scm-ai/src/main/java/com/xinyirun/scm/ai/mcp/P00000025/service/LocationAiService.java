package com.xinyirun.scm.ai.mcp.P00000025.service;

import com.xinyirun.scm.bean.system.vo.master.warehouse.MLocationVo;
import com.xinyirun.scm.core.system.service.master.warehouse.IMLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库区管理AI服务类
 *
 * 为AI工具提供库区（Location）相关业务逻辑支持，包括：
 * 1. 库区基础信息查询和管理
 * 2. 按仓库查询库区列表
 * 3. 库区状态管理（启用/停用）
 * 4. 默认库区标识管理
 * 5. 盘点锁定状态查询
 *
 * 注意：这是对现有IMLocationService的AI友好封装
 * 主要目的是为MCP工具提供更简洁、更适合自然语言交互的接口
 *
 * @author zxh
 * @since 1.0.0
 */
@Slf4j
@Service
public class LocationAiService {

    @Autowired
    private IMLocationService locationService;

    /**
     * 查询库区列表
     *
     * 提供灵活的库区查询功能，支持按多种条件过滤
     *
     * @param warehouseId 所属仓库ID（可选）
     * @param code 库区编码（可选，支持模糊查询）
     * @param name 库区名称（可选，支持模糊查询）
     * @param status 状态（可选）：ENABLED-启用，DISABLED-停用
     * @param isDefault 是否默认库区（可选）
     * @return 库区查询结果
     */
    public Map<String, Object> queryLocations(
            Integer warehouseId,
            String code,
            String name,
            String status,
            Boolean isDefault) {

        log.info("AI查询库区列表 - 仓库ID: {}, 编码: {}, 名称: {}, 状态: {}, 是否默认: {}",
                warehouseId, code, name, status, isDefault);

        try {
            // 构建查询条件
            MLocationVo searchCondition = new MLocationVo();

            if (warehouseId != null) {
                searchCondition.setWarehouse_id(warehouseId);
            }
            if (code != null && !code.trim().isEmpty()) {
                searchCondition.setCode(code.trim());
            }
            if (name != null && !name.trim().isEmpty()) {
                searchCondition.setName(name.trim());
            }
            if (status != null && !status.trim().isEmpty()) {
                if ("ENABLED".equalsIgnoreCase(status) || "启用".equals(status)) {
                    searchCondition.setEnable(true);
                } else if ("DISABLED".equalsIgnoreCase(status) || "停用".equals(status)) {
                    searchCondition.setEnable(false);
                }
            }
            if (isDefault != null) {
                searchCondition.setIs_default(isDefault);
            }

            // 调用现有服务查询
            List<MLocationVo> locations = locationService.selectList(searchCondition);

            // 构建AI友好的返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "库区查询成功");
            result.put("total", locations.size());
            result.put("locations", locations);

            // 添加统计信息
            result.put("summary", calculateLocationSummary(locations));

            log.info("库区查询完成，找到 {} 个库区", locations.size());
            return result;

        } catch (Exception e) {
            log.error("库区查询异常 - 错误: {}", e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "库区查询失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 获取库区详细信息
     *
     * @param locationId 库区ID
     * @return 库区详细信息
     */
    public Map<String, Object> getLocationDetail(int locationId) {
        log.info("AI查询库区详细信息 - 库区ID: {}", locationId);

        try {
            MLocationVo location = locationService.selectById(locationId);

            if (location == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "未找到库区信息，库区ID: " + locationId);
                return result;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "库区详细信息查询成功");
            result.put("location", location);

            // 添加额外的状态描述
            result.put("statusDescription", buildLocationStatusDescription(location));

            log.info("库区详细信息查询完成 - 库区: {} [{}]", location.getName(), location.getCode());
            return result;

        } catch (Exception e) {
            log.error("库区详细信息查询异常 - 库区ID: {}, 错误: {}", locationId, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "库区详细信息查询失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按仓库查询库区
     *
     * 查询指定仓库下的所有库区
     *
     * @param warehouseId 仓库ID（必填）
     * @return 仓库下的库区列表
     */
    public Map<String, Object> queryLocationsByWarehouse(int warehouseId) {
        log.info("AI查询仓库库区 - 仓库ID: {}", warehouseId);

        try {
            MLocationVo searchCondition = new MLocationVo();
            searchCondition.setWarehouse_id(warehouseId);

            List<MLocationVo> locations = locationService.selectList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "仓库库区查询成功");
            result.put("warehouseId", warehouseId);
            result.put("total", locations.size());
            result.put("locations", locations);

            // 查找默认库区
            MLocationVo defaultLocation = locations.stream()
                .filter(loc -> Boolean.TRUE.equals(loc.getIs_default()))
                .findFirst()
                .orElse(null);

            result.put("hasDefaultLocation", defaultLocation != null);
            if (defaultLocation != null) {
                result.put("defaultLocation", Map.of(
                    "id", defaultLocation.getId(),
                    "code", defaultLocation.getCode(),
                    "name", defaultLocation.getName()
                ));
            }

            log.info("仓库库区查询完成，仓库ID: {}, 找到 {} 个库区", warehouseId, locations.size());
            return result;

        } catch (Exception e) {
            log.error("仓库库区查询异常 - 仓库ID: {}, 错误: {}", warehouseId, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "仓库库区查询失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按编码查询库区
     *
     * @param code 库区编码（精确匹配）
     * @param warehouseId 所属仓库ID（必填）
     * @return 库区信息
     */
    public Map<String, Object> findLocationByCode(String code, int warehouseId) {
        log.info("AI按编码查询库区 - 编码: {}, 仓库ID: {}", code, warehouseId);

        if (code == null || code.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "库区编码不能为空");
            return result;
        }

        try {
            List<com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity> locations =
                locationService.selectByCode(code.trim(), warehouseId);

            Map<String, Object> result = new HashMap<>();

            if (locations.isEmpty()) {
                result.put("success", false);
                result.put("message", "未找到库区，编码: " + code + ", 仓库ID: " + warehouseId);
                result.put("location", null);
            } else {
                // 转换为VO对象
                com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity entity = locations.get(0);
                MLocationVo location = locationService.selectById(entity.getId());

                result.put("success", true);
                result.put("message", "库区查询成功");
                result.put("location", location);

                if (locations.size() > 1) {
                    result.put("warning", "找到多个相同编码的库区，返回第一个");
                }
            }

            return result;

        } catch (Exception e) {
            log.error("按编码查询库区异常 - 编码: {}, 仓库ID: {}, 错误: {}", code, warehouseId, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "按编码查询库区失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按名称模糊查询库区
     *
     * @param name 库区名称（支持模糊查询）
     * @param warehouseId 所属仓库ID（可选，不传则查询所有仓库）
     * @return 匹配的库区列表
     */
    public Map<String, Object> findLocationsByName(String name, Integer warehouseId) {
        log.info("AI按名称模糊查询库区 - 名称: {}, 仓库ID: {}", name, warehouseId);

        if (name == null || name.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "库区名称不能为空");
            return result;
        }

        try {
            MLocationVo searchCondition = new MLocationVo();
            searchCondition.setName(name.trim());
            if (warehouseId != null) {
                searchCondition.setWarehouse_id(warehouseId);
            }

            List<MLocationVo> locations = locationService.selectList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "库区名称查询完成");
            result.put("total", locations.size());
            result.put("locations", locations);
            result.put("searchName", name.trim());
            result.put("searchWarehouseId", warehouseId);

            if (locations.isEmpty()) {
                result.put("message", "未找到匹配的库区，名称包含: " + name);
            } else {
                log.info("找到 {} 个匹配的库区", locations.size());
            }

            return result;

        } catch (Exception e) {
            log.error("按名称查询库区异常 - 名称: {}, 仓库ID: {}, 错误: {}", name, warehouseId, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "按名称查询库区失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 检查库区状态
     *
     * @param locationId 库区ID
     * @return 库区状态信息
     */
    public Map<String, Object> checkLocationStatus(int locationId) {
        log.info("AI检查库区状态 - 库区ID: {}", locationId);

        try {
            MLocationVo location = locationService.selectById(locationId);

            Map<String, Object> result = new HashMap<>();

            if (location == null) {
                result.put("success", false);
                result.put("message", "未找到库区信息，库区ID: " + locationId);
                return result;
            }

            // 分析库区状态
            result.put("success", true);
            result.put("locationId", locationId);
            result.put("locationCode", location.getCode());
            result.put("locationName", location.getName());
            result.put("warehouseId", location.getWarehouse_id());

            // 启用状态
            result.put("enable", location.getEnable());
            result.put("enableName", Boolean.TRUE.equals(location.getEnable()) ? "启用" : "停用");
            result.put("isActive", Boolean.TRUE.equals(location.getEnable()));
            result.put("canUse", Boolean.TRUE.equals(location.getEnable()));

            // 默认库区标识
            result.put("isDefault", Boolean.TRUE.equals(location.getIs_default()));

            // 盘点锁定状态
            result.put("inventoryLocked", Boolean.TRUE.equals(location.getInventory()));

            // 添加状态描述
            result.put("statusDescription", buildLocationStatusDescription(location));

            return result;

        } catch (Exception e) {
            log.error("检查库区状态异常 - 库区ID: {}, 错误: {}", locationId, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "检查库区状态失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 获取所有启用的库区
     *
     * @param warehouseId 仓库ID（可选）
     * @return 启用的库区列表
     */
    public Map<String, Object> getEnabledLocations(Integer warehouseId) {
        log.info("AI查询所有启用的库区 - 仓库ID: {}", warehouseId);

        try {
            MLocationVo searchCondition = new MLocationVo();
            searchCondition.setEnable(true);
            if (warehouseId != null) {
                searchCondition.setWarehouse_id(warehouseId);
            }

            List<MLocationVo> locations = locationService.selectList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "启用库区查询完成");
            result.put("total", locations.size());
            result.put("locations", locations);
            result.put("statusFilter", "只显示启用状态的库区");
            if (warehouseId != null) {
                result.put("warehouseFilter", "仓库ID: " + warehouseId);
            }

            log.info("查询到 {} 个启用的库区", locations.size());
            return result;

        } catch (Exception e) {
            log.error("查询启用库区异常 - 仓库ID: {}, 错误: {}", warehouseId, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询启用库区失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 获取默认库区
     *
     * @param warehouseId 仓库ID（必填）
     * @return 默认库区信息
     */
    public Map<String, Object> getDefaultLocation(int warehouseId) {
        log.info("AI查询默认库区 - 仓库ID: {}", warehouseId);

        try {
            MLocationVo searchCondition = new MLocationVo();
            searchCondition.setWarehouse_id(warehouseId);
            searchCondition.setIs_default(true);

            List<MLocationVo> locations = locationService.selectList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("warehouseId", warehouseId);

            if (locations.isEmpty()) {
                result.put("success", false);
                result.put("message", "该仓库未设置默认库区");
                result.put("defaultLocation", null);
            } else {
                MLocationVo defaultLocation = locations.get(0);
                result.put("success", true);
                result.put("message", "默认库区查询成功");
                result.put("defaultLocation", defaultLocation);

                if (locations.size() > 1) {
                    result.put("warning", "发现多个默认库区，返回第一个。建议检查数据配置。");
                }
            }

            return result;

        } catch (Exception e) {
            log.error("查询默认库区异常 - 仓库ID: {}, 错误: {}", warehouseId, e.getMessage(), e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询默认库区失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 计算库区统计信息
     *
     * @param locations 库区列表
     * @return 统计信息
     */
    private Map<String, Object> calculateLocationSummary(List<MLocationVo> locations) {
        Map<String, Object> summary = new HashMap<>();

        if (locations == null || locations.isEmpty()) {
            summary.put("total", 0);
            summary.put("enabled", 0);
            summary.put("disabled", 0);
            summary.put("defaultCount", 0);
            summary.put("inventoryLockedCount", 0);
            return summary;
        }

        long enabledCount = locations.stream().filter(l -> Boolean.TRUE.equals(l.getEnable())).count();
        long disabledCount = locations.stream().filter(l -> !Boolean.TRUE.equals(l.getEnable())).count();
        long defaultCount = locations.stream().filter(l -> Boolean.TRUE.equals(l.getIs_default())).count();
        long inventoryLockedCount = locations.stream().filter(l -> Boolean.TRUE.equals(l.getInventory())).count();

        summary.put("total", locations.size());
        summary.put("enabled", enabledCount);
        summary.put("disabled", disabledCount);
        summary.put("defaultCount", defaultCount);
        summary.put("inventoryLockedCount", inventoryLockedCount);
        summary.put("enabledPercent", locations.size() > 0 ? (enabledCount * 100.0 / locations.size()) : 0);

        // 按仓库分组统计
        Map<Integer, Long> warehouseCount = locations.stream()
            .collect(Collectors.groupingBy(MLocationVo::getWarehouse_id, Collectors.counting()));
        summary.put("warehouseDistribution", warehouseCount);

        return summary;
    }

    /**
     * 构建库区状态描述
     *
     * @param location 库区对象
     * @return 状态描述文本
     */
    private String buildLocationStatusDescription(MLocationVo location) {
        StringBuilder desc = new StringBuilder();

        // 启用状态
        if (Boolean.TRUE.equals(location.getEnable())) {
            desc.append("库区处于启用状态");
        } else {
            desc.append("库区处于停用状态");
        }

        // 默认库区
        if (Boolean.TRUE.equals(location.getIs_default())) {
            desc.append("，这是默认库区");
        }

        // 盘点锁定
        if (Boolean.TRUE.equals(location.getInventory())) {
            desc.append("，当前处于盘点锁定状态，部分操作受限");
        }

        // 使用建议
        if (!Boolean.TRUE.equals(location.getEnable())) {
            desc.append("。如需使用此库区，请先启用");
        } else if (Boolean.TRUE.equals(location.getInventory())) {
            desc.append("。盘点期间请谨慎操作");
        } else {
            desc.append("，可以正常使用");
        }

        return desc.toString();
    }
}
