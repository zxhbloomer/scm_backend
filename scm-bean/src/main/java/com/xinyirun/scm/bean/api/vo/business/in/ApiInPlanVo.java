package com.xinyirun.scm.bean.api.vo.business.in;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 入库计划
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "入库计划", description = "入库计划")
public class ApiInPlanVo implements Serializable {

    private static final long serialVersionUID = 8358071133686851333L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 外部系统单号
     */
    private String extra_code;

    /**
     * 计划时间
     */
    private LocalDateTime plan_time;

    /**
     * 入库类型：0采购入库
     */
    private String type;

    /**
     * 单据类型：0供应链业务 1代理业务 2直销业务 3废钢业务
     */
    private String bill_type;

    /**
     * 备注
     */
    private String remark;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 委托方code
     */
    private String consignor_code;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 入库计划详情集合
     */
    private List<ApiInPlanDetailVo> detailList;

    /**
     * 入库订单对象
     */
    private ApiInOrderVo orderVo;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

}
