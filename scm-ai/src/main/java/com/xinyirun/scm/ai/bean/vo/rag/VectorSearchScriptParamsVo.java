package com.xinyirun.scm.ai.bean.vo.rag;

import co.elastic.clients.json.JsonData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Elasticsearch 向量检索脚本参数 VO
 *
 * <p>用于封装 script_score 查询的参数</p>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VectorSearchScriptParamsVo {

    /**
     * 查询向量（384维）
     */
    private float[] queryVector;

    /**
     * 转换为 Elasticsearch Script 所需的参数格式
     *
     * @return Map&lt;String, JsonData&gt; 格式的参数
     */
    public Map<String, JsonData> toScriptParams() {
        Map<String, JsonData> params = new HashMap<>();
        params.put("query_vector", JsonData.of(queryVector));
        return params;
    }

    /**
     * 静态工厂方法：从查询向量创建参数对象
     *
     * @param queryVector 查询向量
     * @return VectorSearchScriptParamsVo 实例
     */
    public static VectorSearchScriptParamsVo of(float[] queryVector) {
        return new VectorSearchScriptParamsVo(queryVector);
    }
}
