package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程定义(act_re_model)
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmProcessVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -8521715224298998246L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 页面code
     */
    private String page_code;

    /**
     * 审批模板ID
     */
    private String template_id;

    /**
     * 流程模板编号
     */
    private String code;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 基础设置
     */
    private String settings;

    /**
     * 摸板表单
     */
    private String form_items;

    /**
     * 流程json
     */
    private String process;

    /**
     * 图标
     */
    private String icon;

    /**
     * 图标背景色
     */
    private String background;

    /**
     * notify
     */
    private String notify;

    /**
     * 谁能提交
     */
    private String who_commit;

    /**
     * 谁能编辑
     */
    private String who_edit;

    /**
     * 谁能导出数据
     */
    private String who_export;

    /**
     * 版本，0开始每次发布后累加1
     */
    private Integer version;

    /**
     * remark
     */
    private String remark;

    /**
     * 冗余分组id
     */
    private Integer group_id;

    /**
     * 是否已停用
     */
    private Boolean is_stop;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    private Integer c_id;

    /**
     * 修改人ID
     */
    private Integer u_id;


    /**
     * 创建人id
     */
    private String c_name;

    /**
     * 修改人id
     */
    private String u_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    private PageCondition pageCondition;

    /**
     * 表名
     */
    private String tab_name;

    /**
     * 流程定义ID（引擎流生成）
     */
    private String process_definition_id;

    /**
     * 用户code
     */
    private String user_code;

    /**
     * 租户code
     */
    private String tenant_code;

    /**
     * 业务表单数据
     */
    private Object form_json;

    /**
     * 业务表单数据 class
     */
    private String form_class;

    /**
     * 表单数据
     */
    private JSONObject form_data;

    /**
     * 自选数据
     */
    private Map<String, List<OrgUserVo>> process_users;

    /**
     * 表ID
     */
    private Integer serial_id;

    /**
     * 表类型
     */
    private String serial_type;

    /**
     * 模板流程数据 格式（{"process":{"id":"root","parentId":null,"type":"ROOT","name":"发起人","desc":"任何人","props":{"assignedUser":[],"formPerms":[{"id":"field9edee91d-5318-4c7d-ae44-d0135c424631","title":"出库类型","required":true,"perm":"R"},{"id":"fieldc51dc175-7f82-449d-9224-e4ed9a9b38a8","title":"出库数量","required":true,"perm":"R"}]},"children":{"id":"node_679413643036","parentId":"root","props":{"assignedType":"ASSIGN_USER","mode":"AND","sign":false,"nobody":{"handler":"TO_PASS","assignedUser":[]},"timeLimit":{"timeout":{"unit":"H","value":0},"handler":{"type":"REFUSE","notify":{"once":true,"hour":1}}},"assignedUser":[{"id":39,"code":"E202203050001","name":"王东明","type":"user"}],"formPerms":[{"id":"field9edee91d-5318-4c7d-ae44-d0135c424631","title":"出库类型","required":true,"perm":"R"},{"id":"fieldc51dc175-7f82-449d-9224-e4ed9a9b38a8","title":"出库数量","required":true,"perm":"R"}],"selfSelect":{"multiple":false},"leaderTop":{"endCondition":"TOP","endLevel":1},"leader":{"level":1},"role":[],"refuse":{"type":"TO_END","target":""},"formUser":""},"type":"APPROVAL","name":"审批人","children":{"id":"node_892238107942","parentId":"node_679413643036","props":{"shouldAdd":false,"assignedUser":[{"id":42,"code":"E202203050004","name":"王广玉","type":"user"}],"formPerms":[{"id":"field9edee91d-5318-4c7d-ae44-d0135c424631","title":"出库类型","required":true,"perm":"R"},{"id":"fieldc51dc175-7f82-449d-9224-e4ed9a9b38a8","title":"出库数量","required":true,"perm":"R"}]},"type":"CC","name":"抄送人","children":{}}}}}）
     */
    private String process_data;

    /**
     * 允许发起审批流的用户
     */
    private List<OrgUserVo> orgUserVoList;

    /**
     * 发起人信息
     */
    private OrgUserVo orgUserVo;

    /**
     * 初始流程节点
     */
    private String initial_process;

    /**
     * 流程状态
     */
    private String next_approve_name;
}
