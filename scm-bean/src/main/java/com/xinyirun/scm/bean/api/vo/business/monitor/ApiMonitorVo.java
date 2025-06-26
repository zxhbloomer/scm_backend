package com.xinyirun.scm.bean.api.vo.business.monitor;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 监管任务回单
 *
 * @Date 2023-3-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiMonitorVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = -4829897602069080551L;

	private Integer id;

	/**
	 * 编号
	 */
	private String code;

	/**
	 * 监管任务单号(WMS同步过来)
	 */
	private String wmsCode;

	/**
	 * 类型(1-承运 2-托运)
	 */
	private Integer type;

	/**
	 * 类型名称
	 */
	private String typeName;

	/**
	 * 订单id(在中林与青润 存储逻辑不一致)
	 */
	private Integer orderId;

	/**
	 * 订单编号(在中林与青润 存储逻辑不一致)
	 */
	private String orderCode;

	/**
	 * 合同id(在中林与青润 存储逻辑不一致)
	 */
	private Integer contractId;

	/**
	 * 合同编号(在中林与青润 存储逻辑不一致)
	 */
	private String contractCode;

	/**
	 * 托运人统一社会信用代码
	 */
	private String creditNo;

	/**
	 * 托运合同-托运人=企业id(company.id)
	 */
	private Integer companyId;

	/**
	 * 托运人=企业名称
	 */
	private String companyName;

	/**
	 * 托运人=企业简称
	 */
	private String companyAbbr;

	/**
	 * 承运人=组织主体名称
	 */
	private String orgName;

	/**
	 * 承运人=组织主体简称
	 */
	private String orgAbbr;

	/**
	 * 车牌号
	 */
	private String plateNum;

	/**
	 * 车辆数
	 */
	private String plateCount = "1";

	/**
	 * 运输方式
	 */
	private String modeTransportName;

	/**
	 * 发货地
	 */
	private String sendAddress;

	/**
	 * 收货地
	 */
	private String receiptAddress;

	/**
	 * 审批状态名称(30-已审批 enum.id)
	 */
	private Integer statusId;

	/**
	 * 审批状态名称
	 */
	private String statusName;

	/**
	 * 发货数量
	 */
	private BigDecimal sendCount;

	/**
	 * 收货数量
	 */
	private BigDecimal receiptCount;

	/**
	 * 损耗
	 */
	private BigDecimal lossCount;

	/**
	 * 日期
	 */
	private LocalDate inputDate;

	/**
	 * 日期
	 */
	private String inputDateStr;

	/**
	 * 结算数据id(10-按发货数量 20-按收货数量 enum.id)
	 */
	private Integer balanceTypeId;

	/**
	 * 结算数据名称
	 */
	private String balanceTypeName;

	/**
	 * 回单类型id(10-公运回单-监管任务 enum.id)
	 */
	private Integer receiptTypeId = 10;

	/**
	 * 回单类型名称
	 */
	private String receiptTypeName = "公运回单-监管任务";

	/**
	 * 收货地类型
	 */
	private String inWarehouseType;

	/**
	 * 发货地类型
	 */
	private String outWarehouseTypes;

	/**
	 * 收货地
	 */
	private String inWarehouseName;

	/**
	 * 收货地code
	 */
	private String inWarehouseCode;

	/**
	 * 出库/提货时间
	 */
	private LocalDateTime outTime;

	/**
	 * 采购/销售合同号
	 */
	private String contractNo;

	/**
	 * 备注
	 */
	private String remarks;

	/**
	 * 商品明细
	 */
	private List<ApiMonitorItemVo> items;

	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 商品编码
	 */
	private String goodsCode;

	/**
	 * 商品单位
	 */
	private String unitName = "吨";

	/**
	 * 监管任务状态
	 */
	private String monitorStatusName;

	/**
	 * 监管任务状态
	 */
	private String monitorStatus;

}
