package com.xinyirun.scm.ai.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.Embedding;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 硅基流动（SiliconFlow）嵌入模型实现
 * 使用 BAAI/bge-m3 模型生成 1024 维向量
 *
 *
 * 特性：
 * - 向量维度：1024
 * - 支持语言：100+ 语言（包括中英文）
 * - 上下文长度：8192 tokens
 * - 免费额度：RPM 2,000 / TPM 500,000
 *
 * @author SCM AI Team
 * @since 2025-10-10
 */
@Slf4j
public class SiliconFlowEmbeddingModel implements EmbeddingModel {

    private final String baseUrl;
    private final String apiKey;
    private final String model;
    private final RestTemplate restTemplate;
    private final int dimensions;

    public SiliconFlowEmbeddingModel(String baseUrl, String apiKey, String model, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.restTemplate = restTemplate;
        this.dimensions = 1024; // BAAI/bge-m3 固定为 1024 维
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        try {
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("input", request.getInstructions());
            requestBody.put("encoding_format", "float");

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);

            // 调用硅基流动 API
            String url = baseUrl ;
            log.debug("调用硅基流动嵌入API: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                // 解析响应
                JSONObject responseBody = JSON.parseObject(response.getBody());
                List<Embedding> embeddings = new ArrayList<>();

                if (responseBody.containsKey("data")) {
                    var dataArray = responseBody.getJSONArray("data");
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject item = dataArray.getJSONObject(i);
                        List<Double> embeddingData = item.getList("embedding", Double.class);

                        // 转换为 float 数组
                        float[] embeddingArray = new float[embeddingData.size()];
                        for (int j = 0; j < embeddingData.size(); j++) {
                            embeddingArray[j] = embeddingData.get(j).floatValue();
                        }

                        embeddings.add(new Embedding(embeddingArray, i));
                    }
                }

                log.debug("成功生成 {} 个嵌入向量，维度: {}", embeddings.size(), dimensions);
                return new EmbeddingResponse(embeddings);
            } else {
                throw new RuntimeException("硅基流动API调用失败: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("调用硅基流动嵌入API失败", e);
            throw new RuntimeException("生成嵌入向量失败: " + e.getMessage(), e);
        }
    }

    @Override
    public float[] embed(String text) {
        EmbeddingRequest request = new EmbeddingRequest(Collections.singletonList(text), null);
        EmbeddingResponse response = call(request);

        if (response.getResults() != null && !response.getResults().isEmpty()) {
            return response.getResults().get(0).getOutput();
        }

        throw new RuntimeException("未能生成嵌入向量");
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getText());
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        EmbeddingRequest request = new EmbeddingRequest(texts, null);
        EmbeddingResponse response = call(request);

        List<float[]> results = new ArrayList<>();
        for (Embedding embedding : response.getResults()) {
            results.add(embedding.getOutput());
        }

        return results;
    }

    @Override
    public int dimensions() {
        return dimensions;
    }
}
