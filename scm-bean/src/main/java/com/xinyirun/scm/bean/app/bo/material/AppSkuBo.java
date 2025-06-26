package com.xinyirun.scm.bean.app.bo.material;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppSkuBo implements Serializable {


    private static final long serialVersionUID = -7842977389192152598L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 物料编码
     */
    private String code;

    /**
     * 物料名称
     */
    private String name;

    /**
     * 单位(米/支、码/支)
     */
    private String unit;

    /**
     * 规格
     */
    private String spec;

    /**
     * 是否启用（1是0否）
     */
    private Boolean enable;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 是否删除
     */
    private Boolean deleteflag;

    /**
     * 物料类型
     */
    private Integer goods_type;

    /**
     * 净重
     */
    private BigDecimal net_weight;

    /**
     * 毛重
     */
    private BigDecimal rough_weight;

    /**
     * 体积
     */
    private BigDecimal volume;

    /**
     * 产地
     */
    private String orgin;

    /**
     * 商品id
     */
    private Integer goods_id;

    /**
     * 品名
     */
    private String pm;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    private Integer dbversion;
}
