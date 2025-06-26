package com.xinyirun.scm.bean.app.vo.master.enterprise;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import com.xinyirun.scm.common.annotations.bpm.FieldMeta;
import com.xinyirun.scm.common.constant.SystemConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AppMEnterpriseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5267608703231400580L;

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
     * 企业名称
     */
    private String name;

    /**
     * 企业名称全拼
     */
    private String name_pinyin;

    /**
     * 企业简称拼音
     */
    private String name_short_pinyin;

    /**
     * 注册资本
     */
    private BigDecimal registration_capital;

    /**
     * 企业类型：1客户 2供应商 3仓储方 4承运商 5加工厂
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

    /**
     * 删除0-未删除，1-已删除
     */
    @TableField("is_del")
    private Boolean is_del;

    /**
     * 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
     */
    private String status;

    /**
     * 置顶排序时间
     */
    private LocalDateTime top_time;

    /**
     * 流程实例ID
     */
    private Integer bpm_instance_id;

    /**
     * 流程实例code
     */
    private String bpm_instance_code;

    /**
     * 流程状态
     */
    private String next_approve_name;

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

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 企业类型
     */
    private String[] type_ids;
    @FieldMeta(title = "企业类型",
            required = true,
            fieldType = SystemConstants.BPM_FORM.FIELD_TYPE_SELECTINPUT ,
            valueType = SystemConstants.BPM_FORM.VALUE_TYPE_STRING
    )
    private String type_ids_str;

    /**
     * 企业类型名称
     */
    private String type_names;

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
     * 组件返回—企业类型id
     */
    private String dict_id;

    /**
     * 组件返回—企业类型名称
     */
    private String dict_label;

    /**
     * 审核状态名称
     */
    private String status_name;

    /**
     * 审批流实例化code
     */
    private String process_code;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;
}
