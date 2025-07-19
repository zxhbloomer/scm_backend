package com.xinyirun.scm.bean.system.vo.business.wms.inventory;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class BContractReportVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = -4780207164119631157L;

    private Integer no;

    /**
     * 货主
     */
    private String owner;
    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 物料名称
     */
    private String goods_name;

    private String goods_code;

    /**
     * 合同类型， 采购合同， 销售合同
     */
    private String contract_type;

    /**
     * 商品类型
     */
    private String type;
    /**
     * 数量
     */
    private BigDecimal qty;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 字符串类型 id
     */
    private String id;

    /**
     * id
     */
    private Integer id_num;

    /**
     * 查询类型， 1为损耗报表， 2为在途报表
     */
    private Integer query_type;

    /**
     * 商品属性
     */
    private String goods_prop;

    /**
     * 供应商名称
     */
    private String supplier_name;

    /**
     * 供应商ID
     */
    private Integer supplier_id;

    private Long staff_id;

    /**
     * 实际出库数量
     */
    private BigDecimal actual_count;

    /**
     * 合同单号
     */
    private String contract_no;

    /**
     * 是否是中林环境
     */
    private Boolean showTips;

    /**
     * 开始时间, 批次
     */
    private String batch;

    /**
     * 存货数量
     */
    private BigDecimal qty_inventory;

    /**
     * 退货数量
     */
    private BigDecimal return_qty;

    /**
     * 合计
     */
    private BigDecimal count_qty;

    public BContractReportVo(String type, BigDecimal qty, String goods_prop, String contract_type) {
        this.type = type;
        this.qty = qty;
        this.goods_prop = goods_prop;
        this.contract_type = contract_type;
    }
}
