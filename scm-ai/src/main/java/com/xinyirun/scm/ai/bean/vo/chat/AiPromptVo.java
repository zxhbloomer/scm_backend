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
     * 主键ID
     */
    private String id;

    /**
     * 编号
     */
    private String code;

    /**
     * 简称
     */
    private String nickname;

    /**
     * 描述
     */
    private String desc;

    /**
     * 提示词类型：1-客服提示词，2-知识库提示词
     */
    private Integer type;

    /**
     * 提示词内容，文本格式存储
     */
    private String prompt;


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