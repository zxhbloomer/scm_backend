package com.xinyirun.scm.ai.bean.vo.statistics;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI Token使用业务视图对象
 *
 * 用于业务逻辑处理的Token使用数据传输对象
 * 包含Token使用的详细信息和相关的业务数据
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
public class AiTokenUsageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    private Integer id;

    /**
     * 对话ID
     */
    private String conversation_id;

    /**
     * 用户ID
     */
    private String user_id;

    /**
     * 模型源ID
     */
    private Integer model_source_id;

    /**
     * 关联的消息ID
     */
    private String conversation_content_id;

    /**
     * 输入Token数
     */
    private Integer prompt_tokens;

    /**
     * 输出Token数
     */
    private Integer completion_tokens;

    /**
     * 总Token数
     */
    private Integer total_tokens;

    /**
     * 使用时间
     */
    private LocalDateTime usage_time;


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