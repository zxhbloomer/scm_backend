package com.xinyirun.scm.bean.api.vo.business.monitor;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 监管任务回单明细
 *
 * @Date 2023-3-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiMonitorItemVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = 4913913557339872264L;

	/**
	 * 监管任务回单id(receipt_supervise.id)
	 */
	private Integer receiptSuperviseId;

	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 商品名称
	 */
	private String goodsCode;


}
