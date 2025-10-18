package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Token验证结果消息VO
 *
 * 
 *
 * <p>用途：</p>
 * <ul>
 *   <li>存储用户问题的Token计算结果</li>
 *   <li>标识Token是否超限（用于严格模式判断）</li>
 * </ul>
 *
 * @author SCM AI Team
 * @since 2025-10-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputAdaptorMsg implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Token未超限标志
     */
    public static final int TOKEN_TOO_MUCH_NOT = 0;

    /**
     * 用户问题Token超限标志
     */
    public static final int TOKEN_TOO_MUCH_QUESTION = 1;

    /**
     * Token超限状态
     *
     * <ul>
     *   <li>0 - TOKEN_TOO_MUCH_NOT：未超限，可以正常检索知识库</li>
     *   <li>1 - TOKEN_TOO_MUCH_QUESTION：用户问题超限，无空间检索文档</li>
     * </ul>
     */
    private int tokenTooMuch;

    /**
     * 用户问题的Token数量
     *
     * <p>用于日志记录和问题诊断</p>
     */
    private int userQuestionTokenCount;
}
