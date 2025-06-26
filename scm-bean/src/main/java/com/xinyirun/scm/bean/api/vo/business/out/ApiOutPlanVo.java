package com.xinyirun.scm.bean.api.vo.business.out;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 出库计划
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库计划", description = "出库计划")
public class ApiOutPlanVo implements Serializable {

    private static final long serialVersionUID = 2117069183389247385L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 出库计划详情集合
     */
    private List<ApiOutPlanDetailVo> detailList;

    /**
     * 编号
     */
    private String code;

    /**
     * 计划时间
     */
    private LocalDateTime plan_time;

    /**
     * 出库类型：0销售出库 1=退货出库 2=直采出库
     */
    private String type;

    /**
     * 外部系统单号
     */
    private String extra_code;

    /**
     * 放货指令编号
     */
    private String release_order_code;

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
     * 出库订单对象
     */
    private ApiOutOrderVo orderVo;


}
