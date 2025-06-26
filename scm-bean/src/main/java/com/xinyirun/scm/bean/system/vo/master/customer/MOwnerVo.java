package com.xinyirun.scm.bean.system.vo.master.customer;

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
 * 货主
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "货主", description = "货主")
public class MOwnerVo implements Serializable {

    private static final long serialVersionUID = 682364108282757122L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 简称
     */
    private String short_name;

    /**
     * 客户名称拼音
     */
    private String name_pinyin;

    /**
     * 客户简称拼音
     */
    private String short_name_pinyin;

    /**
     * 板块
     */
    private String business_type;

    /**
     * 社会信用代码证
     */
    private String credit_no;

    /**
     * 是否启用 0否 1是
     */
    private Boolean enable;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人姓名
     */
    private String c_name;

    /**
     * 修改人姓名
     */
    private String u_name;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * id 集合
     */
    private List<Integer> ids;
}
