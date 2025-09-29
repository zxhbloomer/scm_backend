package com.xinyirun.scm.ai.bean.vo.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI配置业务视图对象
 *
 * 用于业务逻辑处理的AI配置数据传输对象
 * 包含配置的详细信息和相关的业务数据
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
public class AiConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 配置名称
     */
    private String config_name;

    /**
     * 配置键
     */
    private String config_key;

    /**
     * 配置值
     */
    private String config_value;

    /**
     * 配置描述
     */
    private String config_desc;

    /**
     * 是否启用：1-启用，0-禁用
     */
    private Integer is_enabled;

    /**
     * 配置分组
     */
    private String config_group;

    /**
     * 排序序号
     */
    private Integer sort_order;


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
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
}