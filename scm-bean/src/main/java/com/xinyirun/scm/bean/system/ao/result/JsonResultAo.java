package com.xinyirun.scm.bean.system.ao.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zxh
 *
 */
@Data
@Builder
@AllArgsConstructor
public class JsonResultAo<T> implements Serializable {

    private static final long serialVersionUID = 647057971276510639L;

    private String timestamp;
    /** 返回状态: http status */
    private Integer http_status;
    /** 返回消息：返回的system_code */
    private int system_code;
    private String system_message;
    /** 返回消息：返回的消息 */
    private String message;
    /** 调用路径：路径 */
    private String path;
    /** 调用方法：post，get */
    private String method;
    /** 是否成功[true:成功;false:失败]，默认失败 */
    private boolean success;
    /** json 导出是否把null也输出*/
    @Builder.Default
    private int json_result_type = 0;
    /** 返回数据，如果类型是数组且为null，返回[] */
    private T data;
}
