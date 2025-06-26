package com.xinyirun.scm.bean.system.vo.business.out;

import com.xinyirun.scm.bean.system.ao.fs.UploadFileResultAo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 入库单导入
 * </p>
 *
 * @author wwl
 * @since 2022-04-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOutImportVo extends UploadFileResultAo implements Serializable {

    private static final long serialVersionUID = -2632562594753816432L;

    /**
     * 页面编码
     */
    private String page_code;

    /**
     * 订单编号
     */
    private String order_no;

    /**
     * 计划单号
     */
    private String plan_code;

    /**
     * 换算单位
     */
    private String unit_name;

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
     * 物料单价
     */
    private String price;

    /**
     * 总价
     */
    private String amount;

    /**
     * 数量
     */
    private String count;

    /**
     * 重量
     */
    private String weight;

    /**
     * 出库时间
     */
    private LocalDateTime outbound_time;

    /**
     * 实际重量
     */
    private String actual_weight;
    private Double actual_weight_double_value;

    /**
     * combine_key
     */
    private String combine_key;

}
