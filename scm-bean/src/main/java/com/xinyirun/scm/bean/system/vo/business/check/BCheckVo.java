package com.xinyirun.scm.bean.system.vo.business.check;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 盘点
 * </p>
 *
 * @author wwl
 * @since 2021-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "盘点任务", description = "盘点任务")
public class BCheckVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -8377125193005782551L;

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
    private Integer owner_id;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 货主名称
     */
    private String owner_name;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库code
     */
    private String warehouse_code;

    /**
     * 单据状态
     */
    private String status;


    /**
     * 单据状态
     */
    private String status_name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 明细列表
     */
    private List<BCheckDetailVo> detailList;

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
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 创建
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 审核人
     */
    private String e_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

}
