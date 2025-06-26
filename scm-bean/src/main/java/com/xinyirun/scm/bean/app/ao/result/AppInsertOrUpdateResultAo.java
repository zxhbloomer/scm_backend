package com.xinyirun.scm.bean.app.ao.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Data
@Builder
@AllArgsConstructor
public class AppInsertOrUpdateResultAo<T> implements Serializable {
    private static final long serialVersionUID = -6338186654625335530L;

    /** 返回消息：返回的消息 */
    private String message;

    /** 是否成功[true:成功;false:失败]，默认失败 */
    private boolean success;

    /** 返回数据 */
    private T data;
}
