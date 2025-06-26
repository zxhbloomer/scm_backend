package com.xinyirun.scm.bean.system.vo.business.materialconvert;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Wang Qianfeng
 * @date 2022/11/23 16:38
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BConvertRecordVo implements Serializable {
    private static final long serialVersionUID = -2447117667128234703L;

    /**
     * 转换时间
     */
    private LocalDateTime c_time;

    /**
     * 转换单号
     */
    private String convert_code;

    /**
     * 版本号
     */
    private Integer data_version;

    /**
     * 转换名称
     */
    private String convert_name;

    /**
     * 货主ID
     */
    private Integer owner_id;

    /**
     * 货主名称
     */
    private String owner_name;

    /**
     * 仓库ID
     */
    private Integer warehouse_id;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 单据状态
     */
    private Boolean is_effective;

    /**
     * 0-单次任务 1-定时任务, 转换类型
     */
    private String type_name;

    private String type;

    /**
     * 源物料code
     */
    private String source_sku_code;

    /**
     * 源物料规格名称
     */
    private String source_sku_name;

    /**
     * 源物料名称
     */
    private String source_goods_name;

    /**
     * 新物料规格名称
     */
    private String target_sku_name;

    /**
     * 新物料规格编码
     */
    private String target_sku_code;

    /**
     * 新物料名称
     */
    private String target_goods_name;

    /**
     * 转换后比例
     */
    private BigDecimal calc;

    /**
     * 转换源物料可用库存
     */
    private BigDecimal source_qty;

    /**
     * 转换物料转换库存
     */
    private BigDecimal target_qty;

    /**
     * 分页参数
     */
    private PageCondition pageCondition;

    /**
     * 开始时间
     */
    private String start_time;

    /**
     * 结束时间
     */
    private String end_time;
}
