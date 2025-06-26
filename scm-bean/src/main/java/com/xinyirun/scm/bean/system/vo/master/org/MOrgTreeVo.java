package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.vo.common.component.TreeNode;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 生成组织架构数据的接收类
 *
 * @author zxh
 * @date 2019年 10月03日 11:55:24
 */
@Data
// @ApiModel(value = "生成组织架构数据的接收类", description = "生成组织架构数据的接收类")
@EqualsAndHashCode(callSuper=false)
public class MOrgTreeVo extends TreeNode implements Serializable {

    private static final long serialVersionUID = -3350299230362690901L;

    private Long id;


    /**
     * 租户id，根结点
     */
//    private Long tenant_id;

    /**
     * 关联单号
     */
    private Long serial_id;

    /**
     * 关联单号类型
     */
    private String serial_type;

    /**
     * 编号，00010001..
     */
    private String code;
    private String current_code;

    /**
     * 儿子个数
     */
    private Integer son_count;

    /**
     * 类型：10（租户）、20（集团）、30（公司）、40（部门）、50（岗位）、60（人员）
     */
    private String type;
    private String type_text;

    private String depth_id;
//    public List<Long> getDepth_id(){
//        List<Long> rtn = new ArrayList<>();
//        if(depth_id == null){
//            return null;
//        }
//        String[] split = depth_id.split(",");
//        for (int i = 0; i < split.length; i++) {
//            rtn.add(Long.valueOf(split[i]));
//        }
//        return rtn;
//    }

    /**
     * 全称
     */
    private String name;

    /**
     * 简称
     */
    private String simple_name;

    private String label;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

    /**
     * s_tenant、m_group、m_company、m_position
     */
    private String [] codes;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}
