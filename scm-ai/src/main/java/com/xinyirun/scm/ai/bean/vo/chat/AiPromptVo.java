package com.xinyirun.scm.ai.bean.vo.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI提示词业务视图对象
 *
 * 用于业务逻辑处理的提示词数据传输对象
 * 包含提示词的详细信息和相关的业务数据
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
public class AiPromptVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    private Integer id;

    /**
     * 提示词名称
     */
    private String prompt_name;

    /**
     * 提示词内容
     */
    private String prompt_content;

    /**
     * 提示词类型
     */
    private String prompt_type;

    /**
     * 是否系统内置：1-是，0-否
     */
    private Integer is_system;

    /**
     * 租户标识
     */
    private String tenant;

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