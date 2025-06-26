package com.xinyirun.scm.bean.api.vo.business.borroworder;

import com.xinyirun.scm.bean.api.vo.business.file.ApiFileVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBBorrowOrderVo implements Serializable {

    
    private static final long serialVersionUID = 712394600275142264L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 外部单号
     */
    private String extra_code;

    /**
     * 类型名称
     */
    private String type_name;

    /**
     * 业务板块
     */
    private String business_plate_name;

    /**
     * 业务类型名称
     */
    private String business_type_name;

    /**
     * 合同编号
     */
    private String contract_code;

    /**
     * 订单编号
     */
    private String order_code;

    /**
     * 采购退货单编号
     */
    private String purchase_order_return_code;

    /**
     * 客户名称
     */
    private String customer_name;

    /**
     * 客户code
     */
    private String customer_code;

    /**
     * 委托方名称
     */
    private String consignor_name;

    /**
     * 委托方code
     */
    private String consignor_code;

    /**
     * 货主名称
     */
    private String owner_name;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 放货指令信息
     */
    private String direct_info;

    /**
     * 日期
     */
    private LocalDateTime out_time;

    /**
     * 计划时间
     */
    private LocalDateTime plan_time;

    /**
     * 是否配置数量浮动
     */
    private Boolean float_controled;

    /**
     * 上浮百分比
     */
    private BigDecimal float_up;

    /**
     * 下浮百分比
     */
    private BigDecimal float_down;

    /**
     * 总金额
     */
    private BigDecimal total_amount;

    /**
     * 账户余额(企业预收款余额)
     */
    private BigDecimal balance;

    /**
     * 是否已用印上传(0否 1是)
     */
    private Boolean use_sealed;

    /**
     * 状态名称
     */
    private String status_name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 更新人
     */
    private String u_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 附件列表
     */
    private List<ApiFileVo> files;

    /**
     * 明细列表
     */
    List<ApiBBorrowOrderDetailVo> detailList;
}
