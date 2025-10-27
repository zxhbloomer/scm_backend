package com.xinyirun.scm.ai.workflow.node.httprequest;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 工作流HTTP请求节点配置
 * 参考 aideepin: com.moyz.adi.common.workflow.node.httprequest.HttpRequestNodeConfig
 * 
 * 转换说明：
 * - @JsonProperty → @JSONField
 * - JsonNode → JSONObject (Fastjson2)
 */
@Data
public class HttpRequestNodeConfig {

    /**
     * 请求方法：GET、POST等
     */
    @NotBlank
    private String method;

    /**
     * 请求URL地址
     */
    @NotBlank
    private String url;

    /**
     * 请求头参数
     */
    @NotNull
    private List<Param> headers;

    /**
     * URL查询参数
     */
    private List<Param> params;

    /**
     * 内容类型
     * 支持：text/plain、application/json、multipart/form-data、application/x-www-form-urlencoded
     */
    @NotNull
    @JSONField(name = "content_type")
    private String contentType;

    /**
     * 纯文本请求体
     */
    @JSONField(name = "text_body")
    private String textBody;

    /**
     * 表单数据请求体
     */
    @JSONField(name = "form_data_body")
    private List<Param> formDataBody;

    /**
     * URL编码表单请求体
     */
    @JSONField(name = "form_urlencoded_body")
    private List<Param> formUrlencodedBody;

    /**
     * JSON请求体
     * 转换：JsonNode → JSONObject (Fastjson2)
     */
    @JSONField(name = "json_body")
    private JSONObject jsonBody;

    /**
     * 请求超时时间（秒）
     */
    @NotNull
    private Integer timeout;

    /**
     * 重试次数
     */
    @NotNull
    @JSONField(name = "retry_times")
    private Integer retryTimes;

    /**
     * 是否清理HTML标签（仅提取文本内容）
     */
    @JSONField(name = "clear_html")
    private Boolean clearHtml;

    /**
     * HTTP请求参数
     */
    @Data
    public static class Param {
        /**
         * 参数名
         */
        private String name;

        /**
         * 参数值
         */
        private Object value;
    }
}
