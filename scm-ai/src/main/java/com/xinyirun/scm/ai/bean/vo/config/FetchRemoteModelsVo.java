package com.xinyirun.scm.ai.bean.vo.config;

import lombok.Data;

/**
 * 获取远程模型列表请求VO
 */
@Data
public class FetchRemoteModelsVo {
    /**
     * API地址
     */
    private String baseUrl;

    /**
     * API密钥
     */
    private String apiKey;
}
