package com.xinyirun.scm.bean.system.vo.master.enterprise;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import com.xinyirun.scm.common.annotations.bpm.FieldMeta;
import com.xinyirun.scm.common.constant.SystemConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业管理VO类
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MEnterpriseVo implements Serializable {

    private static final long serialVersionUID = 8179466261414373498L;

    // ================================
    // 基础字段
    // ================================

    /**
     * 主键id
     */
    private Integer id;

    /**
     * id 集
     */
    private Integer[] ids;

    /**
     * 统一社会信用代码
     */
    private String uscc;

    /**
     * 企业编码
     */
    private String code;

    /**
     * 版本，0开始每次审批通过后累加1
     */
    private Integer version;

    /**
     * 修改理由，在单据完成审批后，修改时需要记录修改理由
     */
    private String modify_reason;

    // ================================
    // 企业信息字段
    // ================================

    /**
     * 企业名称
     */
    private String name;

    /**
     * 企业名称全拼
     */
    private String name_pinyin;

    /**
     * 企业名称简拼
     */
    private String name_short_pinyin;

    /**
     * 注册资本
     */
    private BigDecimal registration_capital;

    /**
     * 企业类型：1客户 2供应商 3仓储方 4承运商 5加工厂 6运营企业 7监管企业
     */
    private String type;

    /**
     * 法人代表
     */
    private String legal_person;

    /**
     * 法人代表 全拼
     */
    private String legal_person_pinyin;

    /**
     * 法人代表 简拼
     */
    private String legal_person_short_pinyin;

    /**
     * 成立时间
     */
    private LocalDateTime est_date;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contact_person;

    /**
     * 联系电话
     */
    private String contact_phone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 曾用名
     */
    private String former_name;

    /**
     * 曾用名全拼
     */
    private String former_name_pinyin;

    /**
     * 曾用名简拼
     */
    private String former_name_short_pinyin;

    // ================================
    // 状态控制字段
    // ================================

    /**
     * 删除0-未删除，1-已删除
     */
    private Boolean is_del;

    /**
     * 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回 5-已作废
     */
    private Integer status;

    /**
     * 作废理由
     */
    private String cancel_reason;

    /**
     * 置顶排序时间
     */
    private LocalDateTime top_time;

    // ================================
    // BPM审批流程字段
    // ================================

    /**
     * 流程实例ID
     */
    private Integer bpm_instance_id;

    /**
     * 流程实例code
     */
    private String bpm_instance_code;

    /**
     * 审批流程名称：企业新增审批
     */
    private String bpm_process_name;

    /**
     * 流程状态
     */
    private String next_approve_name;

    /**
     * 作废审批流程实例ID
     */
    private Integer bpm_cancel_instance_id;

    /**
     * 作废审批流程实例code
     */
    private String bpm_cancel_instance_code;

    /**
     * 作废审批流程名称：企业作废审批
     */
    private String bpm_cancel_process_name;

    // ================================
    // 主体企业相关字段
    // ================================

    /**
     * 主体企业：0-false（不是）、1-true（是）
     */
    private Boolean is_sys_company;

    /**
     * 主体企业编号
     */
    private String sys_company_code;

    /**
     * 主体企业id
     */
    private Integer sys_company_id;

    // ================================
    // 审计字段
    // ================================

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    // ================================
    // 页面控制字段
    // ================================

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 企业类型
     */
    private String[] type_ids;

    private String type_ids_str;

    /**
     * 企业类型名称
     */
    private String type_names;

    // ================================
    // 文件附件字段
    // ================================

    /**
     * logo文件
     */
    private Integer logo_file;
    private List<SFileInfoVo> logo_files;

    /**
     * 营业执照文件
     */
    private Integer license_att_file;
    private List<SFileInfoVo> license_att_files;

    /**
     * 身份证正面照文件
     */
    private Integer lr_id_front_att_file;
    private List<SFileInfoVo> lr_id_front_att_files;

    /**
     * 身份证反面照文件
     */
    private Integer lr_id_back_att_file;
    private List<SFileInfoVo> lr_id_back_att_files;

    /**
     * 其他文件
     */
    private Integer doc_att_file;
    private List<SFileInfoVo> doc_att_files;

    /**
     * 营业执照文件（备用字段）
     */
    private Integer file_id;

    // ================================
    // BPM表单相关字段
    // ================================

    /**
     * 初始化审批流程
     */
    private String initial_process;

    /**
     * 表单数据
     */
    private JSONObject form_data;

    /**
     * 自选数据
     */
    private Map<String, List<OrgUserVo>> process_users;

    /**
     * 校验类型
     */
    private String check_type;

    /**
     * 审批流实例化code
     */
    private String process_code;

    // ================================
    // 组件返回字段
    // ================================

    /**
     * 组件返回—企业类型id
     */
    private String dict_id;

    /**
     * 组件返回—企业类型名称
     */
    private String dict_label;

    // ================================
    // 显示用字段
    // ================================

    /**
     * 审核状态名称
     */
    private String status_name;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 报表编号
     */
    private String print_url;

    /**
     * 二维码
     */
    private String qr_code;

    /**
     * 综合名称：全称，拼音，简拼
     */
    private String combine_search_condition;

    // ================================
    // 业务判断字段
    // ================================

    /**
     * 是否为供应商
     */
    private Boolean isSupplier;

    /**
     * 是否为客户
     */
    private Boolean isCustomer;

    /**
     * 是否为主体企业
     */
    private Boolean isSysCompany;
}