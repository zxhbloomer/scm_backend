package com.xinyirun.scm.ai.mcp.utils.master.goods;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecVo;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品查询MCP工具集（通用）
 * 支持按结构化JSON关键词或单关键词查询商品规格列表
 */
@Slf4j
@Component
public class GoodsQueryMcpTools {

    @Autowired
    private MGoodsSpecMapper goodsSpecMapper;

    /**
     * 按结构化JSON关键词查询商品（商品名、规格参数、编码独立模糊匹配）
     */
    @McpTool(description = """
        按结构化关键词查询商品（物料规格），支持商品名、规格参数、编码分别独立模糊匹配，精度优于单关键词查询。
        keywordJson 格式：{"goods_name":"商品名","spec":"规格参数如30-80mm","code":"编码"}，字段为空时传空字符串。
        返回启用状态商品列表，包含 sku_id、sku_code、sku_name、goods_id、goods_code、goods_name、unit 等字段。
        total 字段表示查询到的商品数量。
        """)
    public String queryGoodsByKeywordJson(
            @McpToolParam(description = "租户编码，用于数据权限控制") String tenantCode,
            @McpToolParam(description = "结构化关键词JSON，格式：{\"goods_name\":\"\",\"spec\":\"\",\"code\":\"\"}") String keywordJson) {

        log.info("MCP工具调用 - 查询商品(JSON): 租户={}, keywordJson={}", tenantCode, keywordJson);

        try {
            DataSourceHelper.use(tenantCode);

            if (!StringUtils.hasText(keywordJson)) {
                return JSON.toJSONString(Map.of(
                    "success", false, "total", 0, "list", List.of(),
                    "message", "keywordJson 不能为空"
                ), JSONWriter.Feature.PrettyFormat);
            }

            JSONObject kw = JSON.parseObject(keywordJson);
            MGoodsSpecVo condition = new MGoodsSpecVo();
            String goodsName = kw.getString("goods_name");
            String spec      = kw.getString("spec");
            String code      = kw.getString("code");
            condition.setAi_goods_name(StringUtils.hasText(goodsName) ? goodsName : null);
            condition.setAi_spec(StringUtils.hasText(spec)            ? spec      : null);
            condition.setAi_code(StringUtils.hasText(code)            ? code      : null);

            Page<MGoodsSpecVo> page = new Page<>(1, 50);
            IPage<MGoodsSpecVo> pageResult = goodsSpecMapper.selectPageForAi(page, condition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("total", pageResult.getTotal());
            result.put("list", pageResult.getRecords());
            result.put("keywordJson", keywordJson);
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 查询商品(JSON): {}", e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "total", 0,
                "list", List.of(),
                "message", "查询商品失败: " + e.getMessage()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            DataSourceHelper.close();
        }
    }

    /**
     * 按关键词查询商品（模糊匹配商品名称、规格、编码）
     */
    @McpTool(description = """
        按关键词查询商品（物料规格），支持模糊匹配商品名称、规格名称、商品编码。
        返回匹配的启用状态商品列表，包含 sku_id、sku_code、sku_name、goods_id、goods_code、goods_name、unit 等字段。
        total 字段表示查询到的商品数量。
        """)
    public String queryGoodsByName(
            @McpToolParam(description = "租户编码，用于数据权限控制") String tenantCode,
            @McpToolParam(description = "商品名称或规格关键词，支持模糊查询") String keyword) {

        log.info("MCP工具调用 - 查询商品: 租户={}, 关键词={}", tenantCode, keyword);

        try {
            DataSourceHelper.use(tenantCode);

            MGoodsSpecVo condition = new MGoodsSpecVo();
            condition.setKeyword(keyword);

            Page<MGoodsSpecVo> page = new Page<>(1, 50);
            IPage<MGoodsSpecVo> pageResult = goodsSpecMapper.selectPageForAiByKeyword(page, condition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("total", pageResult.getTotal());
            result.put("list", pageResult.getRecords());
            result.put("keyword", keyword);
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 查询商品: {}", e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "total", 0,
                "list", List.of(),
                "message", "查询商品失败: " + e.getMessage()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            DataSourceHelper.close();
        }
    }
}
