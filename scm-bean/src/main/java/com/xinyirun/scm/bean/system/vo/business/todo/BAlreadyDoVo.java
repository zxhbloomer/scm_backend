package com.xinyirun.scm.bean.system.vo.business.todo;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 已办事项
 * </p>
 *
 * @author wwl
 * @since 2021-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "已办事项", description = "已办事项")
public class BAlreadyDoVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 7047049202564679531L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 待办id
     */
    private Integer todo_id;

    /**
     * 业务类型
     */
    private String serial_name;

    /**
     * 单据code
     */
    private String serial_code;

    /**
     * 单据id
     */
    private Integer serial_id;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 路径
     */
    private String path;

    /**
     * 关联单号类型
     */
    private String serial_type;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 页面id
     */
    private Long page_id;

    /**
     * 页面code
     */
    private String page_code;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 货主
     */
    private String owner_name;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * sku_name
     */
    private String sku_name;

    /**
     * 数量
     */
    private String qty;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 单位名称
     */
    private String unit_name;

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
}
