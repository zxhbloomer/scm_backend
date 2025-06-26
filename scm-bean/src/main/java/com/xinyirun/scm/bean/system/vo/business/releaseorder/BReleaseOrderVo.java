package com.xinyirun.scm.bean.system.vo.business.releaseorder;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
public class BReleaseOrderVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = -5510526083375287899L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 明细id
     */
    private Integer detail_id;


    /**
     * 编号
     */
    private String code;

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

    private Integer owner_id;

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
    private List<BReleaseFilesVo> files;

    /**
     * 明细列表
     */
    private List<BReleaseOrderDetailVo> detailList;

    /**
     * 分页数据
     */
    private PageCondition pageCondition;

    /**
     * 商品编号
     */
    private String commodity_code;

    /**
     * 商品名称
     */
    private String commodity_name;

    /**
     * 商品规格
     */
    private String commodity_spec;

    /**
     * 规格code
     */
    private String commodity_spec_code;

    /**
     * 单价(含税)(订单商品单价)
     */
    private BigDecimal price;

    /**
     * 金额(含税)=单价*放货数量
     */
    private BigDecimal amount;

    /**
     * 放货数量
     * @param id
     */
    private BigDecimal qty;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库 ID
     */
    private Integer warehouse_id;

    /**
     * 仓库编码
     */
    private String warehouse_code;

    /**
     * 型规
     */
    private String type_gauge;

    /**
     * 序号
     */
    private Integer no;

    /**
     * 品名
     */
    private String pm;

    /**
     * 单位
     */
    private String unit_name;

    /**
     * 已生产数量
     */
    private BigDecimal has_product_num;

    /**
     * 是否是生产
     * @param id
     */
    private Boolean is_wo;

    /**
     * 配方生产管理 已生产数量
     * @param id
     */
    private BigDecimal rt_has_product_num;

    /**
     * 来源
     * @param id
     */
    private String source_type;

    /**
     * 0-放货指令 1-借货指令
     */
    private String order_type;

    public BReleaseOrderVo(Integer id) {
        this.id = id;
    }
}
