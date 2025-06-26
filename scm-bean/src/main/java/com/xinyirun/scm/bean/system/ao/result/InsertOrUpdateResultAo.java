package com.xinyirun.scm.bean.system.ao.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zxh
 * @date 2019/9/2
 */
@Data
@Builder
@AllArgsConstructor
public class InsertOrUpdateResultAo<T> implements Serializable {

    private static final long serialVersionUID = -6186622215109211106L;

    /** 返回消息：返回的消息 */
    private String message;

    /** 是否成功[true:成功;false:失败]，默认失败 */
    private boolean success;

    /** 返回数据 */
    private T data;
}
