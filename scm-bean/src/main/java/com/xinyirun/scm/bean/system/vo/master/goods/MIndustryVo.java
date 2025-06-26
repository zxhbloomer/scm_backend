package com.xinyirun.scm.bean.system.vo.master.goods;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 行业
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "行业", description = "行业")
public class MIndustryVo implements Serializable {

    private static final long serialVersionUID = 4965438456618332343L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 行业名
     */
    private String name;

    /**
     * 板块名
     */
    private String business_name;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 编号
     */
    private String code;

    /**
     * 板块id
     */
    private Integer business_id;

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
     * 创建人姓名
     */
    private String c_name;

    /**
     * 修改人姓名
     */
    private String u_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 子类别集合
     */
    private List<MCategoryVo> categoryVo;

    /**
     * id集合
     */
    private List<Integer> ids;
}
