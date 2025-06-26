package com.xinyirun.scm.bean.bpm.enums;

/**
 * @author : willian fu
 * @date : 2022/7/7
 */
public enum ApprovalTypeEnum {
    //指定用户
    ASSIGN_USER,
    //指定的部门的主管
    ASSIGN_LEADER,
    //发起人自选
    SELF_SELECT,
    //连续多级主管
    LEADER_TOP,
    //发起人指定级别主管
    LEADER,
    //指定角色
    ROLE,
    //发起人自己审批
    SELF,
    //系统自动驳回
    REFUSE,
    //表单内的用户
    FORM_USER,
    //表单内的部门的主管
    FORM_DEPT;
}
