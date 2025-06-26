package com.xinyirun.scm.bean.system.vo.master.customer;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 客户仓库关联
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "客户仓库关联", description = "客户仓库关联")
public class MCustomerWarehouseVo implements Serializable {


    private static final long serialVersionUID = -4203623879504784363L;

    /**
     * 主键id
     */
    private Integer id;


    /**
     * 客户id
     */
    private Integer customer_id;


    /**
     * 仓库Id
     */
    private Integer warehouse_id;


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
