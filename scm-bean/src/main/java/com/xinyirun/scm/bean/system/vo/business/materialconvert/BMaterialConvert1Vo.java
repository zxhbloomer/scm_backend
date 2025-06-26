package com.xinyirun.scm.bean.system.vo.business.materialconvert;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Wang Qianfeng
 * @date 2022/11/22 14:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMaterialConvert1Vo implements Serializable {


    private static final long serialVersionUID = 6926874948155045130L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * detail 表 Id
     */
    private Integer detail_id;

    /**
     * 单号
     */
    private String code;

    /**
     * 名称
     */
    private String name;
    private String convert_name;

    /**
     * 明细数量
     */
    private Integer count;

    /**
     * 版本号
     */
    private String data_version;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库ID
     */
    private Integer warehouse_id;

    /**
     * 货主名称
     */
    private String owner_name;

    /**
     * 转换类型
     */
    private String type_name;

    /**
     * 新物料名称
     */
    private String target_goods_name;

    /**
     * 新物料名称
     */
    private String target_goods_code;

    /**
     * 新物料code
     */
    private String target_spec;

    /**
     * 单据状态
     */
    private Integer is_effective;

    private LocalDateTime convert_time;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;
    private String c_name;

    /**
     * 修改人id
     */
    private Long u_id;
    private String u_name;

    /**
     * 源物料编码
     */
    private String source_goods_code;

    /**
     * 源物料名称
     */
    private String source_goods_name;

    /**
     * 源物料编码
     */
    private String source_spec;

    private BigDecimal calc;

    /**
     * 装换前 sku_id
     */
    private Integer source_sku_id;

    /**
     * 转换后 sku_id
     */
    private Integer sku_id;

    /**
     * 货主
     */
    private Integer owner_id;

}
