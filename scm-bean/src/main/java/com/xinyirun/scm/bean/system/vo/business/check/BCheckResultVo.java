package com.xinyirun.scm.bean.system.vo.business.check;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 盘盈盘亏
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
//@ApiModel(value = "盘盈盘亏", description = "盘盈盘亏")
public class BCheckResultVo implements Serializable {

    private static final long serialVersionUID = -7342373982744060831L;
    /**
     * id
     */
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 货主id
     */
    private Integer ownerId;

    /**
     * 货主code
     */
    private String ownerCode;

    /**
     * 仓库id
     */
    private Integer warehouseId;

    /**
     * 仓库code
     */
    private String warehouse_code;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 盘点操作id
     */
    private Integer check_operate_id;

    /**
     * 审核人id
     */
    private Integer e_id;

    /**
     * 审核时间
     */
    private LocalDateTime e_time;

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
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;


}
