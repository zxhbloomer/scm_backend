package com.xinyirun.scm.ai.common.exception;

import com.xinyirun.scm.ai.core.exception.KnowledgeBaseBreakSearchException;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.enums.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AiExceptionHandler {

    @ExceptionHandler(AiServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public JsonResultAo<String> handleAiServiceUnavailable(AiServiceUnavailableException e) {
        log.error("AI服务不可用: {}", e.getMessage());
        return ResultUtil.OK("AI服务暂时不可用，请稍后重试", ResultEnum.SYSTEM_BUSINESS_ERROR);
    }

    /**
     * 处理知识库严格模式异常
     *
     * <p>对标aideepin：ErrorEnum.B_BREAK_SEARCH("B0013", "中断搜索")</p>
     *
     * <p>异常场景：</p>
     * <ul>
     *   <li>严格模式判断点1：用户问题Token过长，无空间检索文档</li>
     *   <li>严格模式判断点2：检索结果为空，知识库中无相关答案</li>
     * </ul>
     *
     * <p>返回HTTP 200（业务规则，非系统错误）：</p>
     * <ul>
     *   <li>这是业务规则限制，不是系统错误</li>
     *   <li>前端需要友好提示用户调整问题或补充知识库</li>
     * </ul>
     *
     * @param e 知识库严格模式异常
     * @return 错误响应（包含用户友好的错误消息）
     */
    @ExceptionHandler(KnowledgeBaseBreakSearchException.class)
    @ResponseStatus(HttpStatus.OK)
    public JsonResultAo<String> handleKnowledgeBaseBreakSearch(KnowledgeBaseBreakSearchException e) {
        log.warn("知识库严格模式检查失败，kbUuid: {}, 原因: {}", e.getKbUuid(), e.getMessage());
        return ResultUtil.OK(e.getMessage(), ResultEnum.SYSTEM_BUSINESS_ERROR);
    }
}