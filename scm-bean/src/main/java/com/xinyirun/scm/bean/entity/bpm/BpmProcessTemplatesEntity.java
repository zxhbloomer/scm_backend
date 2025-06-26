package com.xinyirun.scm.bean.entity.bpm;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * process_templates 审批流程模板
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_process_templates")
public class BpmProcessTemplatesEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -2575490627670986468L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    /**
     * 版本，0开始每次发布后累加1
     */
    @TableField("version")
    private Integer version;

    /**
     * 模板类型（表名：m_enterprise……）
     */
    private String type;

    /**
     * 页面code
     */
    @TableField("page_code")
    private String page_code;

    /**
     * 审批模板ID
     */
    @TableField("template_id")
    private String template_id;

    /**
     * 部署ID
     */
    @TableField("deployment_id")
    private String deployment_id;

    /**
     * 流程模板编号
     */
    @TableField("code")
    private String code;

    /**
     * 模板名称
     */
    @TableField("name")
    private String name;

    /**
     * 基础设置
     */
    @TableField("settings")
    private String settings;

    /**
     * 摸板表单
     */
    @TableField("form_items")
    private String form_items;

    /**
     * process
     */
    @TableField("process")
    private String process;

    /**
     * 图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 图标背景色
     */
    @TableField("background")
    private String background;

    /**
     * notify
     */
    @TableField("notify")
    private String notify;

    /**
     * 谁能提交
     */
    @TableField("who_commit")
    private String who_commit;

    /**
     * 谁能编辑
     */
    @TableField("who_edit")
    private String who_edit;

    /**
     * 谁能导出数据
     */
    @TableField("who_export")
    private String who_export;

    /**
     * remark
     */
    @TableField("remark")
    private String remark;

    /**
     * 冗余分组id
     */
    @TableField("group_id")
    private Integer group_id;

    /**
     * 是否已停用
     */
    @TableField("is_stop")
    private Boolean is_stop;

    /**
     * 回调class
     */
    @TableField("call_back_class")
    private String call_back_class;

    /**
     * 回调审批流建立时调用方法
     */
    @TableField("call_back_create_method")
    private String call_back_create_method;

    /**
     * 回调审批流建立时调用方法参数定义
     */
    @TableField("call_back_create_param")
    private String call_back_create_param;

    /**
     * 回调同意通过方法名（最后通过调用）
     */
    @TableField("call_back_approved_method")
    private String call_back_approved_method;

    /**
     * 回调同意通过方法参数定义（最后通过）
     */
    @TableField("call_back_approved_param")
    private String call_back_approved_param;

    /**
     * 回调拒绝方法名
     */
    @TableField("call_back_refuse_method")
    private String call_back_refuse_method;

    /**
     * 回调拒绝方法参数定义
     */
    @TableField("call_back_refuse_param")
    private String call_back_refuse_param;

    /**
     * 回调撤销方法名
     */
    @TableField("call_back_cancel_method")
    private String call_back_cancel_method;

    /**
     * 回调撤销方法参数定义
     */
    @TableField("call_back_cancel_param")
    private String call_back_cancel_param;

    /**
     * 回调更新方法名
     */
    @TableField("call_back_save_method")
    private String call_back_save_method;

    /**
     * 回调更新方法参数定义
     */
    @TableField("call_back_save_param")
    private String call_back_save_param;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;



}
