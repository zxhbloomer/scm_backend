package com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation;

import com.xinyirun.scm.bean.system.vo.common.component.TreeNode;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 仓库分组关系数据的接收类
 *
 * @author zxh
 * @date
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class MRelationTreeVo extends TreeNode implements Serializable {

    private static final long serialVersionUID = -669325101852953387L;

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
     * 类型：1一级；2二级；3三级
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
    private String short_name;

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
     * check bo 是否启用
     */
    private Boolean is_enable;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 数组code 00010001
     */
    private List<MWarehouseRelationVo> relation_codes;
}
