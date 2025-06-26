package com.xinyirun.scm.bean.entity.master.org;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 集团与企业关系表，集团->企业关系
 * </p>
 *
 * @author zxh
 * @since 2020-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_org_group_company")
public class MOrgGroupCompanyEntity implements Serializable {

    private static final long serialVersionUID = -5610725210258326258L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 组织id
     */
    @TableField("org_id")
    private Long org_id;

    /**
     * 组织parent id
     */
    @TableField("org_parent_id")
    private Long org_parent_id;

    /**
     * 企业id
     */
    @TableField("current_id")
    private Long current_id;

    /**
     * 所属集团id
     */
    @TableField("parent_id")
    private Long parent_id;

    /**
     * 所属父结点类型
     */
    @TableField("parent_type")
    private String parent_type;

    /**
     * 根集团id
     */
    @TableField("root_id")
    private Long root_id;

    /**
     * 根集团id
     */
    @TableField("root_group_id")
    private Long root_group_id;

    /**
     * 嵌套时会>1
     */
    @TableField("counts")
    private Integer counts;

    /**
     * 嵌套时的排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 租户id
     */
//    @TableField("tenant_id")
//    private Long tenant_id;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

}
