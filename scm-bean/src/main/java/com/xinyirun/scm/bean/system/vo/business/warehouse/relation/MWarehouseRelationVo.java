package com.xinyirun.scm.bean.system.vo.business.warehouse.relation;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MWarehouseRelationVo implements Serializable {

    private static final long serialVersionUID = -5188602944495404785L;

    private Integer id;

    /**
     * 编号，00010001..
     */
    private String code;

    /**
     * 儿子个数
     */
    private Integer son_count;

    /**
     * 上级组织，null为根节点
     */
    private Long parent_id;

    /**
     * 根结点
     */
    private Long sys_id;

    /**
     * 关联单号
     */
    private Long serial_id;

    /**
     * 关联单号类型
     */
    private String serial_type;

    /**
     * 类型：10（一级）、20（二级）、30（三级）
     */
    private String type;

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
    private Long c_id;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    private Integer dbversion;

}
