package com.xinyirun.scm.bean.system.vo.master.inventory;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;


/**
 * 库存流水 EXPORT
 *
 * @author wqf
 * @since 2024-1-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MInventoryAccountExportVo implements Serializable {

    
    private static final long serialVersionUID = -7209098001766385654L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "业务单号", index = 1)
    private String business_code;

    @ExcelProperty(value = "订单编号", index = 2)
    private String order_no;

    @ExcelProperty(value = "合同编号", index = 3)
    private String contract_no;

    @ExcelProperty(value = "货主", index = 4)
    private String owner_name;

    @ExcelProperty(value = "物料编码", index = 5)
    private String sku_code;

    @ExcelProperty(value = "业务板块", index = 6)
    private String business_name;

    @ExcelProperty(value = "行业", index = 7)
    private String industry_name;

    @ExcelProperty(value = "物料类别", index = 8)
    private String category_name;

    @ExcelProperty(value = "物料名称", index = 9)
    private String goods_name;

    @ExcelProperty(value = "品名", index = 10)
    private String pm;

    @ExcelProperty(value = "规格", index = 11)
    private String spec;

    @ExcelProperty(value = "库存单位", index = 12)
    private String unit;

    @ExcelProperty(value = "单据类型", index = 13)
    private String serial_type_name;

    @ExcelProperty(value = "业务类型", index = 14)
    private String business_type_name;

    @ExcelProperty(value = "仓库", index = 15)
    private String warehouse_name;

    @ExcelProperty(value = "仓库类型", index = 16)
    private String warehouse_type_name;

    @ExcelProperty(value = "入出库数量", index = 17)
    private String qty;

    @ExcelProperty(value = "库存余额", index = 18)
    private String qty_inventory_total;

    @ExcelProperty(value = "变更时间", index = 19)
    private String u_time;

    @ExcelProperty(value = "操作人", index = 20)
    private String operator;
}
