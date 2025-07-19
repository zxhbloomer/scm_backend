package com.xinyirun.scm.bean.system.vo.business.wms.out;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 出库操作
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "出库操作", description = "出库操作")
public class BOutPlanOperateVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -1830935726651898253L;

    /**
     * 出库计划明细id
     */
    private Integer id;

    /**
     * 出库计划id
     */
    private Integer plan_id;

    /**
     * 规格id
     */
    private Integer sku_id;

    /**
     * 计划明细单号
     */
    private String code;

    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 单据状态值
     */
    private String status_name;

    /**
     * 单据类型值
     */
    private String bill_type_name;

    /**
     * 出库类型
     */
    private String type;

    /**
     * 出库类型值
     */
    private String type_name;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 物料名
     */
    private String goods_name;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 品名
     */
    private String pm;

    /**
     * 物料规格
     */
    private String spec;

    /**
     * 合同编号
     */
    private String contract_no;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 货值
     */
    private BigDecimal amount;

    /**
     * 订单id
     */
    private Integer order_id;

    /**
     * 订单类型
     */
    private String order_type;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 库存计量单位
     */
    private String unit;

    /**
     * 数量
     */
    private BigDecimal count;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 客户名
     */
    private String client_name;

    /**
     * 委托方id
     */
    private Integer consignor_id;

    /**
     * 委托方code
     */
    private String consignor_code;

    /**
     * 委托方名
     */
    private String consignor_name;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 货主名
     */
    private String owner_name;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库名
     */
    private String warehouse_name;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区名
     */
    private String location_name;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库位名
     */
    private String bin_name;

    /**
     * 可用库存
     */
    private BigDecimal qty_avaible;

    /**
     * 出库时间
     */
    private LocalDateTime outbound_time;

    /**
     * 实际数量
     */
    private BigDecimal actual_count;

    /**
     * 实际重量
     */
    private BigDecimal actual_weight;

    /**
     * 原发数量
     */
    private BigDecimal primary_quantity;

    /**
     * 实收车数
     */
    private Integer car_count;


    /**
     * 换算单位信息
     */
    private MGoodsUnitCalcVo unitData;

    /**
     * 磅单文件
     */
    private List<SFileInfoVo> pound_files;

    /**
     * 出库照片附件
     */
    private List<SFileInfoVo> out_photo_files;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 皮重
     */
    private BigDecimal tare_weight;

    /**
     * 毛重
     */
    private BigDecimal gross_weight;

    /**
     * 备注
     */
    private String detail_remark;
}
