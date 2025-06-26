package com.xinyirun.scm.bean.bpm.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 流程审批结果状态枚举
 * @author : willian fu
 * @date : 2023/8/16
 */
public enum ProcessResultEnum {
    //审批中
    RUNNING("审批进行中"),

    //审批通过
    PASS("审批通过"),

    //审批驳回
    REFUSE("审批被驳回"),

    //审批被撤销
    CANCEL("审批被撤销"),

    COMPLETE("审批结束");

    @Getter
    private String desc;

    ProcessResultEnum(String desc) {
        this.desc = desc;
    }

    public static ProcessResultEnum resolveResult(String endActId){
        if (Objects.isNull(endActId)){
          return ProcessResultEnum.RUNNING;
        } else if ("cancel-end".equals(endActId)){
            return ProcessResultEnum.CANCEL;
        } else if ("process-end".equals(endActId) || endActId.contains("-timeoutTask")) {
            return ProcessResultEnum.PASS;
        } else if ("refuse-end".equals(endActId)) {
            return ProcessResultEnum.REFUSE;
        } else {
            return ProcessResultEnum.COMPLETE;
        }
    }

}
