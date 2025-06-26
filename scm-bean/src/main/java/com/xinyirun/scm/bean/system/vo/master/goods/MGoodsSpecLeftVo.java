package com.xinyirun.scm.bean.system.vo.master.goods;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 物料规格左侧树状
 * </p>
 *
 * @author htt
 * @since 2021-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "物料规格左侧树状", description = "物料规格左侧树状")
public class MGoodsSpecLeftVo {

    private static final long serialVersionUID = 8810763526984183621L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 板块id
     */
    private Integer business_id;

    /**
     * 行业ID
     */
    private Integer industry_id;

    /**
     * 类别id
     */
    private Integer category_id;

    /**
     * 物料id
     */
    private Integer goods_id;

    /**
     * 物料名称
     */
    private String name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;




}
