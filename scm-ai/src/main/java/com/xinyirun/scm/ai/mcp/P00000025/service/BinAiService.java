package com.xinyirun.scm.ai.mcp.P00000025.service;

import com.xinyirun.scm.bean.system.vo.master.warehouse.MBinVo;
import com.xinyirun.scm.core.system.service.master.warehouse.IMBinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库位AI服务
 * 提供库位相关的AI友好查询接口,包装底层的IMBinService
 *
 * 库位(Bin)是仓库管理的最小存储单元:
 * - 归属于特定的仓库(warehouse)和库区(location)
 * - 具有精确的位置信息(道/列/排/层)
 * - 支持货物状态管理(空库位/预分配/有货)
 * - 支持混放控制(供应商混放/批次混放/货物混放)
 *
 * @author AI Service Generator
 * @date 2025-11-04
 */
@Slf4j
@Service
public class BinAiService {

    @Autowired
    private IMBinService binService;

    /**
     * 查询库位列表
     * 支持多条件组合查询,包括仓库、库区、编码、名称、状态等
     *
     * @param warehouseId 仓库ID,可选
     * @param locationId 库区ID,可选
     * @param code 库位编码,支持模糊查询
     * @param name 库位名称,支持模糊查询
     * @param status 启用状态: ENABLED-启用, DISABLED-停用
     * @param goodsStatus 货物状态: EMPTY-空库位(0), ALLOCATED-预分配(1), OCCUPIED-有货(2)
     * @param isDefault 是否默认库位
     * @return 包含库位列表和统计信息的Map
     */
    public Map<String, Object> queryBins(Integer warehouseId, Integer locationId,
                                         String code, String name,
                                         String status, String goodsStatus,
                                         Boolean isDefault) {
        try {
            log.info("查询库位列表: warehouseId={}, locationId={}, code={}, name={}, status={}, goodsStatus={}, isDefault={}",
                    warehouseId, locationId, code, name, status, goodsStatus, isDefault);

            // 构建查询条件
            MBinVo searchCondition = new MBinVo();

            if (warehouseId != null) {
                searchCondition.setWarehouse_id(warehouseId);
            }

            if (locationId != null) {
                searchCondition.setLocation_id(locationId);
            }

            if (code != null && !code.trim().isEmpty()) {
                searchCondition.setCode(code.trim());
            }

            if (name != null && !name.trim().isEmpty()) {
                searchCondition.setName(name.trim());
            }

            if (status != null && !status.isEmpty()) {
                if ("ENABLED".equalsIgnoreCase(status)) {
                    searchCondition.setEnable(true);
                } else if ("DISABLED".equalsIgnoreCase(status)) {
                    searchCondition.setEnable(false);
                }
            }

            if (goodsStatus != null && !goodsStatus.isEmpty()) {
                if ("EMPTY".equalsIgnoreCase(goodsStatus)) {
                    searchCondition.setGoods_status(false); // 0-空库位
                } else if ("ALLOCATED".equalsIgnoreCase(goodsStatus)) {
                    // 预分配状态需要特殊处理,这里暂时不设置
                } else if ("OCCUPIED".equalsIgnoreCase(goodsStatus)) {
                    searchCondition.setGoods_status(true); // 有货
                }
            }

            if (isDefault != null) {
                searchCondition.setIs_default(isDefault);
            }

            // 执行查询
            List<MBinVo> bins = binService.selecList(searchCondition);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "库位查询成功");
            result.put("total", bins.size());
            result.put("bins", bins);
            result.put("summary", calculateBinSummary(bins));

            log.info("库位查询成功,共查询到{}个库位", bins.size());
            return result;

        } catch (Exception e) {
            log.error("查询库位列表失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询库位列表失败: " + e.getMessage());
            errorResult.put("total", 0);
            return errorResult;
        }
    }

    /**
     * 获取库位详细信息
     * 包含库位的完整属性,包括位置信息、混放规则等
     *
     * @param binId 库位ID
     * @return 包含库位详细信息的Map
     */
    public Map<String, Object> getBinDetail(int binId) {
        try {
            log.info("获取库位详情: binId={}", binId);

            MBinVo bin = binService.selectById(binId);

            Map<String, Object> result = new HashMap<>();
            if (bin != null) {
                result.put("success", true);
                result.put("message", "获取库位详情成功");
                result.put("bin", bin);
                result.put("statusDescription", buildBinStatusDescription(bin));
            } else {
                result.put("success", false);
                result.put("message", "库位不存在");
            }

            return result;

        } catch (Exception e) {
            log.error("获取库位详情失败: binId={}", binId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "获取库位详情失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按库区查询库位列表
     * 获取指定库区下的所有库位
     *
     * @param locationId 库区ID
     * @return 包含库位列表的Map
     */
    public Map<String, Object> queryBinsByLocation(int locationId) {
        try {
            log.info("按库区查询库位: locationId={}", locationId);

            MBinVo searchCondition = new MBinVo();
            searchCondition.setLocation_id(locationId);

            List<MBinVo> bins = binService.selecList(searchCondition);

            // 识别默认库位
            MBinVo defaultBin = bins.stream()
                    .filter(b -> Boolean.TRUE.equals(b.getIs_default()))
                    .findFirst()
                    .orElse(null);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "库区库位查询成功");
            result.put("locationId", locationId);
            result.put("total", bins.size());
            result.put("bins", bins);
            result.put("defaultBin", defaultBin);
            result.put("summary", calculateBinSummary(bins));

            log.info("库区库位查询成功,库区ID={},共{}个库位", locationId, bins.size());
            return result;

        } catch (Exception e) {
            log.error("按库区查询库位失败: locationId={}", locationId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询库区库位失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按仓库查询库位列表
     * 获取指定仓库下的所有库位
     *
     * @param warehouseId 仓库ID
     * @return 包含库位列表的Map
     */
    public Map<String, Object> queryBinsByWarehouse(int warehouseId) {
        try {
            log.info("按仓库查询库位: warehouseId={}", warehouseId);

            MBinVo searchCondition = new MBinVo();
            searchCondition.setWarehouse_id(warehouseId);

            List<MBinVo> bins = binService.selecList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "仓库库位查询成功");
            result.put("warehouseId", warehouseId);
            result.put("total", bins.size());
            result.put("bins", bins);
            result.put("summary", calculateBinSummary(bins));

            log.info("仓库库位查询成功,仓库ID={},共{}个库位", warehouseId, bins.size());
            return result;

        } catch (Exception e) {
            log.error("按仓库查询库位失败: warehouseId={}", warehouseId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询仓库库位失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按编码查询库位
     * 在指定仓库和库区内通过编码精确查找库位
     *
     * @param code 库位编码
     * @param warehouseId 仓库ID
     * @param locationId 库区ID
     * @return 包含库位信息的Map
     */
    public Map<String, Object> findBinByCode(String code, int warehouseId, int locationId) {
        try {
            log.info("按编码查询库位: code={}, warehouseId={}, locationId={}", code, warehouseId, locationId);

            List<MBinVo> binList = binService.selectByCode(code, warehouseId, locationId);
            MBinVo bin = (binList != null && !binList.isEmpty()) ? binList.get(0) : null;

            Map<String, Object> result = new HashMap<>();
            if (bin != null) {
                result.put("success", true);
                result.put("message", "库位查询成功");
                result.put("bin", bin);
                result.put("statusDescription", buildBinStatusDescription(bin));
            } else {
                result.put("success", false);
                result.put("message", "未找到编码为'" + code + "'的库位");
            }

            return result;

        } catch (Exception e) {
            log.error("按编码查询库位失败: code={}", code, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询库位失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按名称模糊查询库位
     *
     * @param name 库位名称,支持模糊查询
     * @param warehouseId 仓库ID,可选
     * @param locationId 库区ID,可选
     * @return 包含库位列表的Map
     */
    public Map<String, Object> findBinsByName(String name, Integer warehouseId, Integer locationId) {
        try {
            log.info("按名称查询库位: name={}, warehouseId={}, locationId={}", name, warehouseId, locationId);

            MBinVo searchCondition = new MBinVo();
            searchCondition.setName(name);

            if (warehouseId != null) {
                searchCondition.setWarehouse_id(warehouseId);
            }

            if (locationId != null) {
                searchCondition.setLocation_id(locationId);
            }

            List<MBinVo> bins = binService.selecList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "库位查询成功");
            result.put("total", bins.size());
            result.put("bins", bins);
            result.put("summary", calculateBinSummary(bins));

            log.info("按名称查询库位成功,共找到{}个库位", bins.size());
            return result;

        } catch (Exception e) {
            log.error("按名称查询库位失败: name={}", name, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询库位失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 检查库位状态
     * 返回库位的启用状态、默认标识、货物状态等信息
     *
     * @param binId 库位ID
     * @return 包含库位状态信息的Map
     */
    public Map<String, Object> checkBinStatus(int binId) {
        try {
            log.info("检查库位状态: binId={}", binId);

            MBinVo bin = binService.selectById(binId);

            Map<String, Object> result = new HashMap<>();
            if (bin != null) {
                result.put("success", true);
                result.put("binId", binId);
                result.put("code", bin.getCode());
                result.put("name", bin.getName());
                result.put("enabled", bin.getEnable());
                result.put("isDefault", bin.getIs_default());
                result.put("goodsStatus", bin.getGoods_status());
                result.put("goodsStatusText", getGoodsStatusText(bin.getGoods_status()));
                result.put("warehouseId", bin.getWarehouse_id());
                result.put("locationId", bin.getLocation_id());
                result.put("statusDescription", buildBinStatusDescription(bin));
            } else {
                result.put("success", false);
                result.put("message", "库位不存在");
            }

            return result;

        } catch (Exception e) {
            log.error("检查库位状态失败: binId={}", binId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "检查库位状态失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 获取所有启用的库位
     *
     * @param warehouseId 仓库ID,可选
     * @param locationId 库区ID,可选
     * @return 包含启用库位列表的Map
     */
    public Map<String, Object> getEnabledBins(Integer warehouseId, Integer locationId) {
        try {
            log.info("获取启用的库位: warehouseId={}, locationId={}", warehouseId, locationId);

            MBinVo searchCondition = new MBinVo();
            searchCondition.setEnable(true);

            if (warehouseId != null) {
                searchCondition.setWarehouse_id(warehouseId);
            }

            if (locationId != null) {
                searchCondition.setLocation_id(locationId);
            }

            List<MBinVo> bins = binService.selecList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "启用库位查询成功");
            result.put("total", bins.size());
            result.put("bins", bins);
            result.put("summary", calculateBinSummary(bins));

            log.info("启用库位查询成功,共{}个", bins.size());
            return result;

        } catch (Exception e) {
            log.error("获取启用库位失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询启用库位失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 获取默认库位
     * 获取指定库区的默认库位
     *
     * @param locationId 库区ID
     * @return 包含默认库位信息的Map
     */
    public Map<String, Object> getDefaultBin(int locationId) {
        try {
            log.info("获取默认库位: locationId={}", locationId);

            MBinVo searchCondition = new MBinVo();
            searchCondition.setLocation_id(locationId);
            searchCondition.setIs_default(true);

            List<MBinVo> bins = binService.selecList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            if (!bins.isEmpty()) {
                MBinVo defaultBin = bins.get(0);
                result.put("success", true);
                result.put("message", "找到默认库位");
                result.put("bin", defaultBin);
                result.put("statusDescription", buildBinStatusDescription(defaultBin));
            } else {
                result.put("success", false);
                result.put("message", "该库区没有设置默认库位");
            }

            return result;

        } catch (Exception e) {
            log.error("获取默认库位失败: locationId={}", locationId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "获取默认库位失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 按位置坐标查询库位
     * 根据道/列/排/层等位置属性查询库位
     *
     * @param warehouseId 仓库ID
     * @param locationId 库区ID,可选
     * @param lineCode 道编号
     * @param colCode 列编号
     * @param rowCode 排编号
     * @param levelCode 层号
     * @return 包含库位列表的Map
     */
    public Map<String, Object> queryBinsByPosition(Integer warehouseId, Integer locationId,
                                                   String lineCode, String colCode,
                                                   String rowCode, String levelCode) {
        try {
            log.info("按位置查询库位: warehouseId={}, locationId={}, line={}, col={}, row={}, level={}",
                    warehouseId, locationId, lineCode, colCode, rowCode, levelCode);

            MBinVo searchCondition = new MBinVo();

            if (warehouseId != null) {
                searchCondition.setWarehouse_id(warehouseId);
            }

            if (locationId != null) {
                searchCondition.setLocation_id(locationId);
            }

            if (lineCode != null && !lineCode.trim().isEmpty()) {
                searchCondition.setLine_code(lineCode.trim());
            }

            if (colCode != null && !colCode.trim().isEmpty()) {
                searchCondition.setCol_code(colCode.trim());
            }

            if (rowCode != null && !rowCode.trim().isEmpty()) {
                searchCondition.setRow_code(rowCode.trim());
            }

            if (levelCode != null && !levelCode.trim().isEmpty()) {
                searchCondition.setLevel_code(levelCode.trim());
            }

            List<MBinVo> bins = binService.selecList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "位置库位查询成功");
            result.put("total", bins.size());
            result.put("bins", bins);
            result.put("summary", calculateBinSummary(bins));

            log.info("位置库位查询成功,共{}个", bins.size());
            return result;

        } catch (Exception e) {
            log.error("按位置查询库位失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询位置库位失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 查询可用库位
     * 查询空库位(goods_status=0)且启用的库位
     *
     * @param warehouseId 仓库ID,可选
     * @param locationId 库区ID,可选
     * @return 包含可用库位列表的Map
     */
    public Map<String, Object> queryAvailableBins(Integer warehouseId, Integer locationId) {
        try {
            log.info("查询可用库位: warehouseId={}, locationId={}", warehouseId, locationId);

            MBinVo searchCondition = new MBinVo();
            searchCondition.setEnable(true);
            searchCondition.setGoods_status(false); // 0-空库位

            if (warehouseId != null) {
                searchCondition.setWarehouse_id(warehouseId);
            }

            if (locationId != null) {
                searchCondition.setLocation_id(locationId);
            }

            List<MBinVo> bins = binService.selecList(searchCondition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "可用库位查询成功");
            result.put("total", bins.size());
            result.put("bins", bins);
            result.put("summary", calculateBinSummary(bins));

            log.info("可用库位查询成功,共{}个空库位", bins.size());
            return result;

        } catch (Exception e) {
            log.error("查询可用库位失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查询可用库位失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 计算库位汇总统计
     *
     * @param bins 库位列表
     * @return 统计信息Map
     */
    private Map<String, Object> calculateBinSummary(List<MBinVo> bins) {
        Map<String, Object> summary = new HashMap<>();

        if (bins == null || bins.isEmpty()) {
            summary.put("total", 0);
            summary.put("enabled", 0);
            summary.put("disabled", 0);
            summary.put("empty", 0);
            summary.put("allocated", 0);
            summary.put("occupied", 0);
            summary.put("defaultCount", 0);
            return summary;
        }

        long enabledCount = bins.stream().filter(b -> Boolean.TRUE.equals(b.getEnable())).count();
        long disabledCount = bins.stream().filter(b -> Boolean.FALSE.equals(b.getEnable())).count();
        long defaultCount = bins.stream().filter(b -> Boolean.TRUE.equals(b.getIs_default())).count();

        // 货物状态统计 (0-空库位, 1-预分配, 2-有货)
        long emptyCount = bins.stream().filter(b -> Boolean.FALSE.equals(b.getGoods_status())).count();
        long occupiedCount = bins.stream().filter(b -> Boolean.TRUE.equals(b.getGoods_status())).count();

        summary.put("total", bins.size());
        summary.put("enabled", enabledCount);
        summary.put("disabled", disabledCount);
        summary.put("empty", emptyCount);
        summary.put("occupied", occupiedCount);
        summary.put("defaultCount", defaultCount);

        // 按库区分组统计
        Map<Integer, Long> locationDistribution = bins.stream()
                .collect(Collectors.groupingBy(MBinVo::getLocation_id, Collectors.counting()));
        summary.put("locationDistribution", locationDistribution);

        // 按仓库分组统计
        Map<Integer, Long> warehouseDistribution = bins.stream()
                .collect(Collectors.groupingBy(MBinVo::getWarehouse_id, Collectors.counting()));
        summary.put("warehouseDistribution", warehouseDistribution);

        return summary;
    }

    /**
     * 构建库位状态描述
     *
     * @param bin 库位信息
     * @return 状态描述文本
     */
    private String buildBinStatusDescription(MBinVo bin) {
        if (bin == null) {
            return "库位不存在";
        }

        StringBuilder desc = new StringBuilder();
        desc.append("库位'").append(bin.getName()).append("'(").append(bin.getCode()).append(") ");

        // 启用状态
        if (Boolean.TRUE.equals(bin.getEnable())) {
            desc.append("已启用");
        } else {
            desc.append("已停用");
        }

        // 默认库位
        if (Boolean.TRUE.equals(bin.getIs_default())) {
            desc.append(",默认库位");
        }

        // 货物状态
        desc.append(",").append(getGoodsStatusText(bin.getGoods_status()));

        // 位置信息
        if (bin.getLine_code() != null || bin.getCol_code() != null ||
            bin.getRow_code() != null || bin.getLevel_code() != null) {
            desc.append(",位置:");
            if (bin.getLine_code() != null) desc.append("道").append(bin.getLine_code());
            if (bin.getCol_code() != null) desc.append("列").append(bin.getCol_code());
            if (bin.getRow_code() != null) desc.append("排").append(bin.getRow_code());
            if (bin.getLevel_code() != null) desc.append("层").append(bin.getLevel_code());
        }

        return desc.toString();
    }

    /**
     * 获取货物状态文本
     *
     * @param goodsStatus 货物状态
     * @return 状态文本
     */
    private String getGoodsStatusText(Boolean goodsStatus) {
        if (goodsStatus == null) {
            return "状态未知";
        }
        if (Boolean.FALSE.equals(goodsStatus)) {
            return "空库位";
        } else {
            return "有货";
        }
    }
}
