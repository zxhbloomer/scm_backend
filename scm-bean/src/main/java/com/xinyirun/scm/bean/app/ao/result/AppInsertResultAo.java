package com.xinyirun.scm.bean.app.ao.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class AppInsertResultAo<T> implements Serializable {
    private static final long serialVersionUID = -8213535868279232671L;

    /** 返回消息：返回的消息 */
    private String message;

    /** 是否成功[true:成功;false:失败]，默认失败 */
    private boolean success;

    /** 返回数据 */
    private T data;
}
