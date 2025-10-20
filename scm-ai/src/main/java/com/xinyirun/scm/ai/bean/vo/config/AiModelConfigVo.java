package com.xinyirun.scm.ai.bean.vo.config;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI模型配置VO类
 */
@Data
public class AiModelConfigVo {
    private Long id;
    private String name;
    private String modelName;
    private String modelType;
    private String provider;
    private String apiKey;
    private String baseUrl;
    private String deploymentName;
    private BigDecimal temperature;
    private Integer maxTokens;
    private BigDecimal topP;
    private Integer timeout;
    private Boolean enabled;
    private Boolean supportChat;
    private Boolean supportVision;
    private Boolean supportEmbedding;
    private LocalDateTime cTime;
    private LocalDateTime uTime;
    private Long cId;
    private Long uId;
    private String createUserName;
    private String updateUserName;
    private Integer dbversion;
}
