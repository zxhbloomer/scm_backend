package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.xinyirun.scm.bean.bpm.vo.ProcessNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 审批流程的基础bean
 * 1、流程的json
 * 2、登录用户的node信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AppBBpmProcessJson implements Serializable {

    @Serial
    private static final long serialVersionUID = 7994471440797948278L;
    /**
     * root节点的json数据
     */
    private ProcessNode<?> root;

    /**
     * 登录用户的node信息
     */
    private AppStaffUserBpmInfoVo appStaffVo;
}
