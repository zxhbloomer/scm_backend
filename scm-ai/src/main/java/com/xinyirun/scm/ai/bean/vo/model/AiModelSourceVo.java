package com.xinyirun.scm.ai.bean.vo.model;

import com.xinyirun.scm.ai.bean.vo.request.AdvSettingVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI模型源业务视图对象
 *
 * 用于业务逻辑处理的AI模型源数据传输对象
 * 包含模型源的详细信息和相关的业务数据
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
public class AiModelSourceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 模型名称
     */
    private String model_name;

    /**
     * 模型编码
     */
    private String model_code;

    /**
     * 模型类型（大语言/视觉/音频）
     */
    private String type;

    /**
     * 提供商
     */
    private String provider;

    /**
     * API密钥
     */
    private String api_key;

    /**
     * API地址
     */
    private String api_url;

    /**
     * 模型连接状态：true-启用，false-禁用
     */
    private Boolean status;

    /**
     * 模型类型（公有/私有）
     */
    private String permission_type;

    /**
     * 模型拥有者
     */
    private String owner;

    /**
     * 模型拥有者类型（个人/企业）
     */
    private String owner_type;

    /**
     * 基础名称
     */
    private String base_name;

    /**
     * 上下文窗口大小（总token容量）
     */
    private Integer context_window;

    /**
     * 最大输入token数（用于判断用户问题是否过长）
     */
    private Integer max_input_tokens;

    /**
     * 最大输出token数
     */
    private Integer max_output_tokens;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 创建人(操作人）
     */
    private String c_name;

    /**
     * 创建人名称（用于列表展示）
     */
    private String createUserName;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 高级设置参数列表
     */
    private List<AdvSettingVo> advSettingVoList;

    // 兼容方法，用于保持与备份代码的一致性

    /**
     * 获取提供商名称（兼容方法）
     */
    public String getProviderName() {
        return this.provider;
    }

    /**
     * 获取基础名称（兼容方法）
     */
    public String getBaseName() {
        return this.base_name;
    }

    /**
     * 获取应用密钥（兼容方法）
     */
    public String getAppKey() {
        return this.api_key;
    }

    /**
     * 获取API地址（兼容方法）
     */
    public String getApiUrl() {
        return this.api_url;
    }

    /**
     * 获取上下文窗口大小（兼容方法）
     */
    public Integer getContextWindow() {
        return this.context_window;
    }

    /**
     * 获取最大输入token数（兼容方法）
     */
    public Integer getMaxInputTokens() {
        return this.max_input_tokens;
    }

    /**
     * 获取最大输出token数（兼容方法）
     */
    public Integer getMaxOutputTokens() {
        return this.max_output_tokens;
    }
}